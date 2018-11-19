package com.atguigu.atcrowdfunding.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.atguigu.atcrowdfunding.model.Permission;
import com.atguigu.atcrowdfunding.model.Role;
import com.atguigu.atcrowdfunding.model.User;
import com.atguigu.atcrowdfunding.service.PermissionService;
import com.atguigu.atcrowdfunding.service.RoleService;
import com.atguigu.atcrowdfunding.service.UserService;

public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private RoleService roleService;
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		System.out.println("[ShiroRealm] doGetAuthenticationInfo");
		
		//1. 把 AuthenticationToken 转换为 UsernamePasswordToken 
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		
		//2. 从 UsernamePasswordToken 中来获取 username
		String loginacct = upToken.getUsername();
		
		//3. 调用数据库的方法, 从数据库中查询 username 对应的用户记录
		System.out.println("从数据库中获取 username: " + loginacct + " 所对应的用户信息.");
		
		User user = userService.queryByUsername(loginacct);
		//4. 若用户不存在, 则可以抛出 UnknownAccountException 异常
		if(user == null){
			throw new UnknownAccountException("用户不存在!");
		}
		
		//6. 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为: SimpleAuthenticationInfo
		//以下信息是从数据库中获取的.
		//1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象. 
		Object principal = loginacct;
		//2). credentials: 密码. 
		Object credentials = user.getUserpswd(); 
		//3). 盐值. 
		ByteSource credentialsSalt = ByteSource.Util.bytes(loginacct);
		//4). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
		String realmName = getName();
		
		//SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, realmName);
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, credentials, credentialsSalt, realmName);
		return info;
	}


	//授权会被 shiro 回调的方法
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		//1. 从 PrincipalCollection 中来获取登录用户的信息
		User user = (User) principals.getPrimaryPrincipal();
		
		//2. 利用登录的用户的信息来用户当前用户的角色或权限(可能需要查询数据库)
		List<String> uRoles = userService.getUserRoles(user);
		List<Permission> uPermissions = permissionService.queryPermissionsByUser(user);
		
		Set<String> permissions = new HashSet<String>();
		for (Permission permission : uPermissions) {
			if(StringUtils.isBlank(permission.getUrl())) continue;
			permissions.add(permission.getUrl().substring(0, permission.getUrl().lastIndexOf("/"))+"/**");
		}
		//3. 创建 SimpleAuthorizationInfo, 并设置其 reles 属性.
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setRoles(new HashSet<String>(uRoles));
		info.setStringPermissions(permissions);
		return info;
	}
	
	
	
	public static void main(String[] args) {
		String hashAlgorithmName = "MD5";
		Object credentials = "123456";
		Object salt = ByteSource.Util.bytes("zhangsan");;
//		int hashIterations = 1024;//设置加密的次数
//		Object result = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
		Object result = new SimpleHash(hashAlgorithmName, credentials, salt);
		System.out.println(result);
	}
}

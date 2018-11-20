package com.atguigu.atcrowdfunding.web;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;

public class UrlPermissionResolver implements PermissionResolver {

	public Permission resolvePermission(String permissionString) {
		if(permissionString.startsWith("/")) {
			return new UrlPermission(permissionString);
		}
		return new WildcardPermission(permissionString);
	}

}

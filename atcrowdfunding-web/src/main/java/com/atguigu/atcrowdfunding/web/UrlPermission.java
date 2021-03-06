package com.atguigu.atcrowdfunding.web;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

public class UrlPermission implements Permission {

	private String url;

	public UrlPermission() {
	}

	public UrlPermission(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean implies(Permission p) {
		if (!(p instanceof UrlPermission)) {
			return false;
		}
		UrlPermission up = (UrlPermission) p;
		PatternMatcher patternMatcher = new AntPathMatcher();
		System.out.println(this.getUrl()+"<==========>"+up.getUrl());
		return patternMatcher.matches(this.getUrl(), up.getUrl());
	}

}

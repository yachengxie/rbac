package com.atguigu.atcrowdfunding.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.AccessControlFilter;

public class UrlAccessFilter extends AccessControlFilter {

	public static final String DEFAULT_ERROR_URL = "/unauthorized.jsp";

	private String errorUrl = DEFAULT_LOGIN_URL;

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		System.out.println("========>[UrlAccessFilter]");
		String url = this.getPathWithinApplication(request);
		return this.getSubject(request, response).isPermitted(url);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.sendRedirect(errorUrl);
		return false;
	}

}

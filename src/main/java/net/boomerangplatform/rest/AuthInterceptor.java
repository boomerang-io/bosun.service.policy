package net.boomerangplatform.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	private ThreadLocal<String> threadLocal;

	public AuthInterceptor(ThreadLocal<String> auth) {
		this.threadLocal = auth;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			this.threadLocal.set(authHeader);
		}
		return true;
	}

}
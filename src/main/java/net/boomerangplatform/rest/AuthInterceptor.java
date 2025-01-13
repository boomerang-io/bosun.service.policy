package net.boomerangplatform.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

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
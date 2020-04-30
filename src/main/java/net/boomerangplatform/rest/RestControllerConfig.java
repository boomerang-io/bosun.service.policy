package net.boomerangplatform.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(name = "boomerang.standalone", havingValue = "false")
public class RestControllerConfig implements WebMvcConfigurer {

	private ThreadLocal<String> authThreadLocal = new ThreadLocal<>();
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AuthInterceptor(authThreadLocal));
	}

	public String getCurrentAuthHeader() {
		return authThreadLocal.get();
	}
}
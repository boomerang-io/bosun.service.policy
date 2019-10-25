package net.boomerangplatform.rest;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(name = "bosun.external.teams", havingValue = "true")
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
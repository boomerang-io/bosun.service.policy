package net.boomerangplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
public class SecurityConfig {

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll().anyRequest().authenticated())
//				.exceptionHandling(
//						exceptionHandling -> exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
//							response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
//						}))
//				.httpBasic(Customizer.withDefaults());
//		return http.build();
//	}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Allow all requests
            )
            .formLogin(form -> form.disable()) // Disable form login
            .httpBasic(basic -> basic.disable()) // Disable basic authentication
            .anonymous(withDefaults -> {}); // Enable anonymous access

        return http.build();
    }

}
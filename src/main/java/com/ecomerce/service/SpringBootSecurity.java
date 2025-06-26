package com.ecomerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; 
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; 
 
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Opcional, por si usas @PreAuthorize, etc.
public class SpringBootSecurity       {
	
	 
	private UserDetailsService userDetailService;		
	private final CustomSuccessHandler customSuccessHandler;

	  

	  public SpringBootSecurity(UserDetailsService userDetailService, CustomSuccessHandler customSuccessHandler) {
		super();
		this.userDetailService = userDetailService;
		this.customSuccessHandler = customSuccessHandler;
	}

	@Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/administrador/**", "/productos/**").hasRole("ADMIN")
	                .anyRequest().permitAll()
	            )
	            .formLogin(login -> login
	                .loginPage("/usuario/login")
	                .successHandler(customSuccessHandler) // <- aquí
	                .permitAll()
	                .defaultSuccessUrl("/usuario/acceder", true))
	            	.logout(logout -> logout
	                    .logoutUrl("/logout")
	                    .logoutSuccessUrl("/usuario/login?logout")
	                
	            );

	        return http.build();
	    }

	    @Bean
	    public BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    // Esto reemplaza el método configure(AuthenticationManagerBuilder)
	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	        return authConfig.getAuthenticationManager();
	    }

}

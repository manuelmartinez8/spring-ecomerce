package com.ecomerce.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler  {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication
										) throws IOException, ServletException {
        // Obtener roles del usuario autenticado
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER"));

        if (isAdmin) {
            response.sendRedirect("/administrador"); // o /admin/home
        } else if (isUser) {
            response.sendRedirect("/"); // página principal
        } else {
            response.sendRedirect("/usuario/login?error=rol"); // fallback
        }
		
	}

}

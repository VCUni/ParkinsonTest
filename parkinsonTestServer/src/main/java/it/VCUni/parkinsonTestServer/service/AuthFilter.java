package it.VCUni.parkinsonTestServer.service;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// Filtro per le richieste web per permettere l'accesso ad alcune risorse solo agli utenti registrati

@Component
public class AuthFilter implements Filter{

	@Autowired
	Logger log;
	
	@Autowired
	TokenGenerator validator;

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
						
		HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

		UserDetails details = validator.validateToken(req);
			
		if (details != null) {
			 UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(details.getUsername(), null, details.getAuthorities());
			 SecurityContextHolder.getContext().setAuthentication(authentication);
		}
				
		chain.doFilter(req, res);
	}


}
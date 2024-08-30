package com.track.share.config.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.track.share.business.BusinessService;
import com.track.share.utility.JwtHelper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private BusinessService businessService;

	@Autowired
    private ApplicationContext context;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			// Authorization
			String requestHeader = request.getHeader("Authorization");

			String username = null;
			String token = null;

			if (requestHeader != null) {

				if (requestHeader.startsWith("Bearer ")) {
					// Extracting the token
					token = requestHeader.substring(7);
					logger.info("Received token: {} " + token);

					// Validating the token and extracting username
					username = this.jwtHelper.getUsernameFromToken(token);
					logger.info("Extracted username from token: {} " + username);

				} else {
					logger.info("Invalid Authorization Header");
				}

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

					// Fetch user details from username
					UserDetails userDetails;

					try {
						userDetails = context.getBean(UserDetailsServiceImpl.class)
	                            .loadUserByUsername(username);
					} catch (UsernameNotFoundException e) {
						userDetails = this.businessService.loadUserByUsername(username, token);
					}

					// Validate the token
					Boolean validateToken = userDetails.isEnabled()
							? this.jwtHelper.validateToken(token, userDetails.getUsername())
							: false;

//					logger.info("Validation Result: " + validateToken);

					if (validateToken) {
						// Set up authentication
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);
						logger.info("User authenticated successfully");

					} else {
						logger.info("Token validation fails");
					}

				} else {
					logger.info("Invalid or missing username");
				}
			}

		} catch (IllegalArgumentException e) {
			logger.error("Illegal Argument while processing the token: {}" + e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("Given JWT token is expired: {}" + e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid token: {}" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception during token processing: {}" + e.getMessage());
		}

		// Continue with the filter chain
		filterChain.doFilter(request, response);
	}
}

package com.track.share.utility;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.track.share.config.auth.AuthUserDetails;

@Component
public class Utility {

	@Autowired
	private SessionRegistry sessionRegistry;

	public LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails) {
				return ((UserDetails) principal).getUsername();
			}
		}

		// Return a default or handle the case where the username is not available
		return "unknownUser";
	}

	public void invalidateUserSession(String username) {
		List<Object> principals = sessionRegistry.getAllPrincipals();

		for (Object principal : principals) {
			if (principal instanceof AuthUserDetails) {
				AuthUserDetails userDetails = (AuthUserDetails) principal;
				if (userDetails.getUsername().trim().contentEquals(username.trim())) {
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
					for (SessionInformation session : sessions) {
						session.expireNow(); // Invalidate the session
					}
				}
			}
		}
	}
}

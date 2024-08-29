package com.track.share.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.track.share.user.UserService;
import com.track.share.user.Users;

class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userService.getUser(username);

		if (user == null) {
			throw new UsernameNotFoundException("Invalid Username Or Password");
		}

		return new AuthUserDetails(user);
	}
}

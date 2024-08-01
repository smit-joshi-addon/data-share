package com.track.share.config.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.track.share.user.Users;

public class AuthUserDetails implements  UserDetails {

	private final String  ROLE_PREFIX="ROLE_";

	private static final long serialVersionUID = 1L;

	private Users user;

	public AuthUserDetails(Users user) {
		this.user=user;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		 SimpleGrantedAuthority authority = new SimpleGrantedAuthority(ROLE_PREFIX);
	        return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getStatus();
	}


	@Override
	public int hashCode() {
		return Objects.hash(ROLE_PREFIX, user);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		AuthUserDetails other = (AuthUserDetails) obj;
		return Objects.equals(ROLE_PREFIX, other.ROLE_PREFIX) && Objects.equals(user, other.user);
	}

}

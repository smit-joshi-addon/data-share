package com.track.share.config.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.track.share.config.auth.dtos.AuthRequest;
import com.track.share.config.auth.dtos.AuthResponse;
import com.track.share.config.auth.dtos.SignUpRequest;
import com.track.share.user.UserService;
import com.track.share.user.Users;
import com.track.share.utility.JwtHelper;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
class ApiAuthController {

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtHelper jwtHelper;
	private final UserService userService;

	public ApiAuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService,
			JwtHelper jwtHelper, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtHelper = jwtHelper;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest,HttpServletRequest request) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.email());
		String token = jwtHelper.generateToken(userDetails.getUsername());
		return ResponseEntity.ok(new AuthResponse(token, jwtHelper.getExpirationDateFromToken(token)));
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> registerUser(@RequestBody SignUpRequest signUpRequest) {
		if (userService.createUser(Users.builder().email(signUpRequest.email()).password(signUpRequest.password())
				.name(signUpRequest.name()).build()) == null)
			return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
		UserDetails userDetails = userDetailsService.loadUserByUsername(signUpRequest.email());
		String token = jwtHelper.generateToken(userDetails.getUsername());
		return ResponseEntity.ok(new AuthResponse(token, jwtHelper.getExpirationDateFromToken(token)));
	}

	@GetMapping("/login")
	public ResponseEntity<String> authenticateUser() {
		return new ResponseEntity<>("Login Using Get request is not supported", HttpStatus.METHOD_NOT_ALLOWED);
	}

	@GetMapping("/register")
	public ResponseEntity<String> registerUser() {
		return new ResponseEntity<>("Registration Using Get request is not supported", HttpStatus.METHOD_NOT_ALLOWED);
	}
}
package com.track.share.utility;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {

	// requirement :
//	public static final long JWT_TOKEN_VALIDITY = 60L * 60L * 24L * 365L * 10L; //10 years
	public static final long JWT_TOKEN_VALIDITY = 60L * 5; // 5 minutes

	private String secret = "UBfqXorgJNzArZFdNLNSjABTBQqrTCJGmENCzYqvgVBnBDgdPOTkuMQpaLZYGQOcZftsSAlbJMKaoCCvnZmfeiUjQBPubpcfUUaGdjIRSPdWQrIpWjhqGvbNDTHENcXE";

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); // Generate SecretKey from secret

		return Jwts.parserBuilder().setSigningKey(key) // Use the SecretKey for parsing
				.build().parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// generate token for user
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, username);
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		// Generate a SecretKey from your secret string
		SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000L))
				.signWith(key, SignatureAlgorithm.HS512) // Use the updated signWith method
				.compact();
	}

	// validate token
	public Boolean validateToken(String token, String username) {
		final String exUsername = getUsernameFromToken(token);
		return (exUsername.equals(username) && !isTokenExpired(token));
	}

	public String refreshToken(String token) {
		if (isTokenExpired(token)) {
			final Claims claims = getAllClaimsFromToken(token);
			return doGenerateToken(claims, claims.getSubject());
		}
		return token;
	}
}

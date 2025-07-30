package com.example.security;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secretKeyString;

	private SecretKey secretKey;

	private static final long EXPIRATION_TIME = 1000 * 60 * 5;
	// In JwtUtil.java
	private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days
	private static final long ID_EXPIRATION_TIME = 1000 * 60 * 5; // 5 minutes or similar

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
	}

	public String generateRefreshToken(UserDetails userDetails) {
		return Jwts.builder().subject(userDetails.getUsername()).claim("aud", "AppUser").claim("iss", "Suchitra")
				.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
				.signWith(secretKey, Jwts.SIG.HS256).compact();
	}

	public String generateIdToken(UserDetails userDetails) {
		return Jwts.builder().subject(userDetails.getUsername()).claim("aud", "AppUser").claim("iss", "Suchitra")
				.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + ID_EXPIRATION_TIME))
				.signWith(secretKey, Jwts.SIG.HS256).compact();
	}

	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().subject(userDetails.getUsername()).claim("aud", "AppUser").claim("iss", "Suchitra")
				.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(secretKey, Jwts.SIG.HS256).compact();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		return extractUsername(token).equals(userDetails.getUsername());
	}
	
//	public boolean validateToken(String token, UserDetails userDetails) {
//	    Claims claims = Jwts.parser()
//	        .verifyWith(secretKey)
//	        .build()
//	        .parseSignedClaims(token)
//	        .getPayload();
//
//	    String subject = claims.getSubject();
//	    String audience = claims.get("aud", String.class);
//	    String issuer = claims.get("iss", String.class);
//
//	    return subject.equals(userDetails.getUsername()) &&
//	           "AppUser".equals(audience) &&
//	           "Suchitra".equals(issuer);
//	}

	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
	}

}

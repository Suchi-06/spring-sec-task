package com.example.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.entity.UserEntity;
import com.example.security.JwtUtil;
import com.example.services.CustomUserDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody UserEntity user) {
	    try {
	        Authentication authentication = authManager.authenticate(
	            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
	        );

	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

	        String accessToken = jwtUtil.generateToken(userDetails);
	        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
	        String idToken = jwtUtil.generateIdToken(userDetails);

	        return ResponseEntity.ok(Map.of(
	            "access_token", accessToken,
	            "refresh_token", refreshToken,
	            "id_token", idToken
	        ));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	            .body(Map.of("error", "Invalid username or password"));
	    }
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
	    String refreshToken = request.get("refresh_token");
	    try {
	        String username = jwtUtil.extractUsername(refreshToken);
	        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

	        if (jwtUtil.validateToken(refreshToken, userDetails)) {
	            String newAccessToken = jwtUtil.generateToken(userDetails);
	            return ResponseEntity.ok(Map.of("access_token", newAccessToken));
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
	    }
	}


}

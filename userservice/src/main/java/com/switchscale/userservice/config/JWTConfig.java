package com.switchscale.userservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import com.switchscale.userservice.model.UserModel;

import java.security.Key;
import java.util.Date;

@Component
public class JWTConfig {

    private static final String SECRET_KEY = "2026NewMeSuperSecretJwtKeyForHs256";
    
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    private Key getSignInKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(UserModel user){
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEMail(String token){
        return getClaims(token).getSubject();
    }

    public boolean validationToken(String token){
        try{
            return !getClaims(token).getExpiration().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

    private Claims getClaims(String token){
        String normalizedToken = normalizeToken(token);
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(normalizedToken)
                .getBody();
    }

    private String normalizeToken(String token) {
        if (token == null) {
            return "";
        }

        String trimmedToken = token.trim();
        if (trimmedToken.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmedToken.substring(7).trim();
        }
        return trimmedToken;
    }

}

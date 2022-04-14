package com.springboot.blog.security;

import com.springboot.blog.exception.BlogAPIException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationInMs;

    //GENERATE TOKEN
    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        System.out.println("USERNAME"+username);
        Date currentDate = new Date();
        System.out.println("CURRENT DATE"+currentDate);
        Date expireDate = new Date(currentDate.getTime()+jwtExpirationInMs);
        System.out.println("EXPIRY DATE"+expireDate);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();

        System.out.println("TOKEN"+token);
        return token;
    }

    //GET USERNAME FROM TOKEN
    public String getUsernameFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //VALIDATE TOKEN
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);

            return true;
        }catch (Exception e){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Error parsing JWT token");
        }
    }
}

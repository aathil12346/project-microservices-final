package com.bankingapp.account_services.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt.secret}")
    private String secret;
    @Value("${app.jwt.expiration.milliseconds}")
    private long expirationDate;


    public String generateJwt(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();

        Date expiryDate = new Date(currentDate.getTime() + expirationDate);

        return Jwts.builder().setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expiryDate)
                .signWith(generateKey())
                .compact();

    }

    public boolean validateToken(String token){

        try {
            Jwts.parserBuilder().setSigningKey(generateKey())
                    .build()
                    .parse(token);

            return true;
        }catch (MalformedJwtException exception){
            throw new MalformedJwtException("invalid jwt token");
        }catch (ExpiredJwtException exception){
            throw new ExpiredJwtException("Jwt expired");
        }catch (UnsupportedJwtException exception){
            throw new UnsupportedJwtException("unsupported jwt token");
        }catch (IllegalArgumentException exception){
            throw new IllegalArgumentException("jwt claims string is empty");
        }


    }
    private Key generateKey(){
         return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}

package com.bankingapp.account_services.security;

import com.bankingapp.account_services.exception.AppException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    public String getUsername(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(generateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token){

        try {
            Jwts.parserBuilder().setSigningKey(generateKey())
                    .build()
                    .parse(token);

            return true;
        }catch (MalformedJwtException exception){
            throw new AppException("invalid jwt token",HttpStatus.BAD_REQUEST);
        }catch (ExpiredJwtException exception){
            throw new AppException("Jwt expired",HttpStatus.BAD_REQUEST);
        }catch (UnsupportedJwtException exception){
            throw new AppException("unsupported jwt token",HttpStatus.BAD_REQUEST);
        }catch (IllegalArgumentException exception){
            throw new AppException("jwt claims string is empty",HttpStatus.BAD_REQUEST);
        }


    }

    public String extractJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7,bearerToken.length());
        }

        return null;
    }
    private Key generateKey(){
         return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}

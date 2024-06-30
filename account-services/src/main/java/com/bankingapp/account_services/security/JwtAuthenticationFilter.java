package com.bankingapp.account_services.security;

import com.bankingapp.account_services.exception.AppException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

      String token = jwtUtils.extractJwtFromRequest(request);
      try {
          if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {

              String username = jwtUtils.getUsername(token);

              UserDetails userDetails = userDetailsService.loadUserByUsername(username);

              UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                      userDetails, null, userDetails.getAuthorities()
              );

              authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

              SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          }
          filterChain.doFilter(request, response);
      }catch (MalformedJwtException exception){
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("Invalid Jwt");
      }catch (ExpiredJwtException exception){
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("Jwt Expired");
      }catch (UnsupportedJwtException exception){
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("Jwt not supported");
      }catch (IllegalArgumentException exception){
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("jwt empty");
      }

    }



}

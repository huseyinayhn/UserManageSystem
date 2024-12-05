package com.hsynayhn.security;

import com.hsynayhn.exception.BaseException;
import com.hsynayhn.exception.ErrorMessage;
import com.hsynayhn.exception.MessageType;
import com.hsynayhn.service.IAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if(header==null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

            try {
                String username = jwtService.getUsernameByToken(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (userDetails != null && jwtService.isTokenValid(token)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new
                                UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(userDetails);

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                throw new BaseException(new ErrorMessage(MessageType.PASSWORD_CANNOT_BE_EMPTY, null));
            }
        filterChain.doFilter(request, response);
    }
}

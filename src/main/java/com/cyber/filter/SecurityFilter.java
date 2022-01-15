package com.cyber.filter;

import com.cyber.entity.User;
import com.cyber.service.SecurityService;
import com.cyber.util.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Service
public class SecurityFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final SecurityService securityService;

    public SecurityFilter(JWTUtil jwtUtil, SecurityService securityService) {
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
    }

    //check if security is good, if token & user are valid, if user has correct authorization
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String token = null;
        String username = null;

        // this is for Open API
        if (authorizationHeader != null) {
            token = authorizationHeader.replace("Bearer","");
            username = jwtUtil.extractUsername(token);
        }

        //do authentication - major part of the security
        //before each API call, check 1. if token is valid & 2. if user is valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = securityService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails) && checkIfUserIsValid(username)) {
                UsernamePasswordAuthenticationToken currentUser =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                currentUser.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(currentUser);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean checkIfUserIsValid(String username) throws AccessDeniedException {
        User currentUser = securityService.loadUser(username);
        return currentUser != null && currentUser.isEnabled();
    }

}

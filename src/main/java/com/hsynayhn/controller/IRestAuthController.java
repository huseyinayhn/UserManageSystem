package com.hsynayhn.controller;

import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.dto.DtoUserChangePassword;
import com.hsynayhn.dto.DtoUserChangeUsername;
import com.hsynayhn.entity.User;
import com.hsynayhn.security.AuthRequest;
import com.hsynayhn.security.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface IRestAuthController {
    public DtoUser register(AuthRequest authRequest);
    public AuthResponse authenticate(AuthRequest authRequest);
    public AuthResponse login(AuthRequest authRequest);
    public User getUser( String param, HttpServletRequest request);
    public String logout(HttpServletRequest request);
    public ResponseEntity<String> changePassword(DtoUserChangePassword dto, String token) ;
}

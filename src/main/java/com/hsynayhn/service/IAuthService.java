package com.hsynayhn.service;

import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.dto.DtoUserChangePassword;
import com.hsynayhn.entity.User;
import com.hsynayhn.security.AuthRequest;
import com.hsynayhn.security.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface IAuthService {
    public DtoUser register(AuthRequest authRequest);
    public AuthResponse authenticate(AuthRequest authRequest);
    public AuthResponse login(AuthRequest authRequest);
    public User getUser(String param, String token);
    public boolean changePassword(String username, DtoUserChangePassword dto);
    public void addToBlacklist(String token);
    public boolean isTokenBlacklisted(String token);
    public DtoUser updateUser(String token, User user);
}

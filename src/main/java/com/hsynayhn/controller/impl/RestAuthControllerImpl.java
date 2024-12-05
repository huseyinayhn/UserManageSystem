package com.hsynayhn.controller.impl;

import com.hsynayhn.controller.IRestAuthController;
import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.dto.DtoUserChangePassword;
import com.hsynayhn.entity.User;
import com.hsynayhn.security.AuthRequest;
import com.hsynayhn.security.AuthResponse;
import com.hsynayhn.security.JwtService;
import com.hsynayhn.service.IAuthService;
import com.hsynayhn.service.IUserService;
import com.hsynayhn.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestAuthControllerImpl implements IRestAuthController {

    private final IAuthService authService;

    private final JwtService jwtService;

    private final IUserService userService;
    private final AuthServiceImpl authServiceImpl;

    public RestAuthControllerImpl(IAuthService authService, JwtService jwtService, IUserService userService, AuthServiceImpl authServiceImpl) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/register")
    @Override
    public DtoUser register(@RequestBody @Valid AuthRequest authRequest) {
        return authService.register(authRequest);
    }

    @PostMapping("/authenticate")
    @Override
    public AuthResponse authenticate(@RequestBody @Valid AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }


    @PostMapping("/login")
    @Override
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @GetMapping("/sign-out")
    @Override
    public String logout(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        authServiceImpl.addToBlacklist(token);

        SecurityContextHolder.clearContext();

        return "Logout successful";

    }


    @GetMapping("/get-user/{param}")
    public User getUser(@PathVariable String param,HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return authService.getUser(param, token);
    }

    @Override
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody DtoUserChangePassword dto, @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.getUsernameByToken(token);

        authService.changePassword(username, dto);

        return ResponseEntity.ok("Şifre başarıyla değiştirildi");
    }

    @PostMapping("/update-user")
    public DtoUser updateUser(@RequestHeader("Authorization") String token,@RequestBody User user) {
        return authService.updateUser(token, user);
    }

}

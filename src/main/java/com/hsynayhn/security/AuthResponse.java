package com.hsynayhn.security;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

}
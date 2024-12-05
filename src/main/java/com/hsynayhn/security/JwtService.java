package com.hsynayhn.security;

import com.hsynayhn.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String JWT_SECRET = "eTy+R0jug0wiM0TYxrIgGqKj7u83Rtgw7yJjQir8v3w=";

    public String generateToken(UserDetails userDetails) {
        // Kullanıcı nesnesini User türüne cast ediyoruz
        if (!(userDetails instanceof User user)) {
            throw new IllegalArgumentException("UserDetails nesnesi User türünde değil!");
        }

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // 2 saatlik geçerlilik
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T exportToken(String token , Function<Claims, T> claimsFunc) {
        Claims claims = getClaims(token);
        return claimsFunc.apply(claims);
    }

    public Claims getClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();

        return claims;
    }

    public String getUsernameByToken(String token) {
        return exportToken(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token) {
        Date expireDate = exportToken(token, Claims::getExpiration);
        return new Date().before(expireDate);
    }

    public Key getKey(){
        byte[] bytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(bytes);
    }

}

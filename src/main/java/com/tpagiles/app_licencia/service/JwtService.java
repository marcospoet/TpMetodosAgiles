package com.tpagiles.app_licencia.service;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JwtService {
    String generateToken(String subject, Map<String,Object> claims);
    boolean validateToken(String token);
    String getSubject(String token);
    Claims parseClaims(String token);      // ← nuevo
}
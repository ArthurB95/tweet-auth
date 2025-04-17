package com.example.springsecurity.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}

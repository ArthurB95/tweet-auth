package com.example.springsecurity.controller;

import com.example.springsecurity.controller.dto.LoginRequest;
import com.example.springsecurity.controller.dto.LoginResponse;
import com.example.springsecurity.entities.Role;
import com.example.springsecurity.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenController(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
         var user = userRepository.findByUsername(loginRequest.username());

         if(isEmpty(user) || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
             throw new BadCredentialsException("User or password is invalid!");
         }

         var now = Instant.now();
         var expiresIn = 300L;

         var scopes = user.get().getRoles()
                 .stream()
                 .map(Role::getName)
                 .collect(Collectors.joining(" "));

         var claims = JwtClaimsSet.builder()
                 .issuer("mybackend")
                 .subject(user.get().getUserId().toString())
                 .issuedAt(now)
                 .expiresAt(now.plusSeconds(expiresIn))
                 .claim("scope", scopes)
                 .build();

         var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

         return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }
}

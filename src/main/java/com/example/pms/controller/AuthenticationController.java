package com.example.pms.controller;

import com.example.pms.dto.AuthenticationResponse;
import com.example.pms.dto.LoginRequest;
import com.example.pms.dto.RegisterRequest;
import com.example.pms.security.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request){

        AuthenticationResponse response = authenticationService.register(request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login (@Valid @RequestBody LoginRequest request){

        AuthenticationResponse response = authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
}

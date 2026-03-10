package com.example.pms.security;

import com.example.pms.dto.AuthenticationResponse;
import com.example.pms.dto.LoginRequest;
import com.example.pms.dto.RegisterRequest;
import com.example.pms.enums.GlobalRole;
import com.example.pms.exception.EmailAlreadyExistsException;
import com.example.pms.exception.InvalidCredentialsException;
import com.example.pms.model.User;
import com.example.pms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final com.example.pms.security.JwtService jwtService;
    private final PasswordEncoder encoder;
    private final CustomUserDetailsService userDetailsService;

    public AuthenticationService(UserRepository userRepository, com.example.pms.security.JwtService jwtService, PasswordEncoder encoder, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
    }

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("User with email already exists.");
        }

        String encodedPassword = encoder.encode(request.getPassword());

        User user = new User(request.getName(), request.getEmail(), encodedPassword, GlobalRole.USER);

        userRepository.save(user);

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        return new AuthenticationResponse(user.getId(), token, user.getEmail(), user.getRole().name());

    }

    public AuthenticationResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        if(!encoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(request.getEmail()));

        return new AuthenticationResponse(user.getId(), token, user.getEmail(), user.getRole().name());
    }

}
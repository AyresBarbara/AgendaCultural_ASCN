package com.agenda.agendacultural.controller;

import com.agenda.agendacultural.dto.LoginRequestDTO;
import com.agenda.agendacultural.dto.LoginResponseDTO;
import com.agenda.agendacultural.dto.UserCreateDto;
import com.agenda.agendacultural.dto.UserResponseDTO;
import com.agenda.agendacultural.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data or email already exists")
    })
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserCreateDto request) {
        logger.info("POST /api/auth/register - Registering new user: {}", request.getEmail());
        UserResponseDTO response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        logger.info("POST /api/auth/login - Login attempt for: {}", request.getEmail());
        LoginResponseDTO response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
}

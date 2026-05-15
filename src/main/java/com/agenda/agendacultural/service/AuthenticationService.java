package com.agenda.agendacultural.service;

import com.agenda.agendacultural.dto.LoginRequestDTO;
import com.agenda.agendacultural.dto.LoginResponseDTO;
import com.agenda.agendacultural.dto.UserCreateDto;
import com.agenda.agendacultural.dto.UserResponseDTO;
import com.agenda.agendacultural.model.User;
import com.agenda.agendacultural.repository.UserRepository;
import com.agenda.agendacultural.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LogService logService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            LogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.logService = logService;
    }

    public UserResponseDTO register(UserCreateDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        validatePasswordPolicy(request.getPassword());

        User user = new User();
        user.setIdUser(UUID.randomUUID());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        logService.logCadastroUsuario(savedUser.getName(), savedUser.getEmail());

        return convertToResponseDto(savedUser);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail();  
        
        try{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getSenha())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        logService.logEventoAplicacao("LOGIN_SUCESSO", "Login realizado com sucesso", email);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("nome", user.getName());
        
        String token = jwtService.generateToken(extraClaims, userDetails);

        return new LoginResponseDTO(token, user.getEmail(), user.getName());
    }catch (Exception e) {
        logService.logErroAutenticacao(email, "Credenciais inválidas");
        throw new RuntimeException("Email ou senha inválidos");
    }
}

    private void validatePasswordPolicy(String password) {
        if (password.length() < 10) {
            throw new RuntimeException("A senha deve ter no mínimo 10 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("A senha deve conter pelo menos uma letra maiúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException("A senha deve conter pelo menos uma letra minúscula");
        }
        if (!password.matches(".*\\d.*")) {
            throw new RuntimeException("A senha deve conter pelo menos um número");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new RuntimeException("A senha deve conter pelo menos um caractere especial");
        }
    }

    private UserResponseDTO convertToResponseDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setIdUser(user.getIdUser());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }
}
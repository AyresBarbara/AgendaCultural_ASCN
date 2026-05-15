package com.agenda.agendacultural.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID idUser;
    private String name;
    private String email;
    private LocalDateTime registrationDate;
}

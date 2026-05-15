package com.agenda.agendacultural.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDTO {

    private UUID idComment;

    @NotBlank(message = "O comentário não pode estar em branco.")
    private String text;

    private LocalDateTime date;

    @NotNull(message = "O ID do usuário é obrigatório.")
    private UUID userId;

    @NotNull(message = "O ID do evento é obrigatório.")
    private UUID eventId;

    // Getters e Setters
    public UUID getIdComment() {
        return idComment;
    }

    public void setIdComment(UUID idComment) {
        this.idComment = idComment;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
}

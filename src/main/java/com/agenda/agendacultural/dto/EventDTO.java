package com.agenda.agendacultural.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;


public class EventDTO {

    private UUID idEvent;

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 200, message = "O título deve ter no máximo 200 caracteres.")
    private String title;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
    private String description;

    @Size(max = 200, message = "A localização deve ter no máximo 200 caracteres.")
    private String location;

    @NotNull(message = "A data e hora do evento são obrigatórias.")
    private LocalDateTime dateTime;

    @NotNull(message = "A categoria é obrigatória.")
    private UUID categoryId;

    @NotNull(message = "O criador do evento é obrigatório.")
    private UUID createdById;

	public UUID getIdEvent() {
		return idEvent;
	}

	public void setIdEvent(UUID idEvent) {
		this.idEvent = idEvent;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public UUID getCreatedById() {
		return createdById;
	}

	public void setCreatedById(UUID createdById) {
		this.createdById = createdById;
	}
    
    
}

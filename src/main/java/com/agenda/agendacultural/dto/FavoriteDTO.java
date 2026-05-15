package com.agenda.agendacultural.dto;

import java.util.UUID;
import java.time.LocalDateTime;

public class FavoriteDTO {

    private UUID idFavorite;
    private UUID userId;
    private UUID eventId;
    private LocalDateTime favoritedDate;

    // getters e setters
    public UUID getIdFavorite() {
        return idFavorite;
    }

    public void setIdFavorite(UUID idFavorite) {
        this.idFavorite = idFavorite;
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

    public LocalDateTime getFavoritedDate() {
        return favoritedDate;
    }

    public void setFavoritedDate(LocalDateTime favoritedDate) {
        this.favoritedDate = favoritedDate;
    }
}

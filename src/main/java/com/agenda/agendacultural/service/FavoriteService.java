package com.agenda.agendacultural.service;

import com.agenda.agendacultural.dto.FavoriteDTO;
import java.util.List;
import java.util.UUID;

public interface FavoriteService {
    FavoriteDTO addFavorite(String email, UUID eventId);
    void removeFavorite(UUID userId, UUID eventId);
    List<FavoriteDTO> getFavoritesByUser(UUID userId);
}
package com.agenda.agendacultural.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.agendacultural.model.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    // Buscar favoritos por usuário
    List<Favorite> findByUser_IdUser(UUID userId);

    // Verificar se um evento já está favoritado por um usuário
    boolean existsByUser_IdUserAndEvent_IdEvent(UUID userId, UUID eventId);

    // Buscar favorito específico
    Favorite findByUser_IdUserAndEvent_IdEvent(UUID userId, UUID eventId);
}

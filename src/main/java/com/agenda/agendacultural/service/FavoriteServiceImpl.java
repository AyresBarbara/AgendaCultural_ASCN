package com.agenda.agendacultural.service;

import com.agenda.agendacultural.dto.FavoriteDTO;
import com.agenda.agendacultural.model.Favorite;
import com.agenda.agendacultural.model.User;
import com.agenda.agendacultural.model.Event;
import com.agenda.agendacultural.repository.FavoriteRepository;
import com.agenda.agendacultural.repository.UserRepository;
import com.agenda.agendacultural.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               UserRepository userRepository,
                               EventRepository eventRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public FavoriteDTO addFavorite(String email, UUID eventId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Evento não encontrado: " + eventId));
        
        if (favoriteRepository.existsByUser_IdUserAndEvent_IdEvent(user.getIdUser(), event.getIdEvent())) {
            throw new RuntimeException("Evento já favoritado");
        }
        
        Favorite favorite = new Favorite();
        favorite.setIdFavorite(UUID.randomUUID());
        favorite.setUser(user);
        favorite.setEvent(event);
        favorite.setFavoritedDate(LocalDateTime.now());
        
        Favorite saved = favoriteRepository.save(favorite);
        
        return convertToDto(saved);
    }

    @Override
    public void removeFavorite(UUID userId, UUID eventId) {
        Favorite favorite = favoriteRepository.findByUser_IdUserAndEvent_IdEvent(userId, eventId);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
        }
    }

    @Override
    public List<FavoriteDTO> getFavoritesByUser(UUID userId) {
        List<Favorite> favorites = favoriteRepository.findByUser_IdUser(userId);
        return favorites.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private FavoriteDTO convertToDto(Favorite favorite) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setIdFavorite(favorite.getIdFavorite());
        dto.setUserId(favorite.getUser().getIdUser());
        dto.setEventId(favorite.getEvent().getIdEvent());
        dto.setFavoritedDate(favorite.getFavoritedDate());
        return dto;
    }
}
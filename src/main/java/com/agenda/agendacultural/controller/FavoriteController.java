package com.agenda.agendacultural.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agenda.agendacultural.dto.FavoriteDTO;
import com.agenda.agendacultural.service.FavoriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Endpoints for managing user favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    @Operation(summary = "Add a favorite", description = "Adds a new favorite for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Favorite added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<FavoriteDTO> addFavorite(@RequestBody Map<String, String> request, Authentication authentication) {
        logger.info("POST /api/favorites - Adding favorite");
        
        String eventId = request.get("eventId");
        String email = authentication.getName();
        
        logger.info("Evento ID: {}, Usuário email: {}", eventId, email);
        
        FavoriteDTO createdFavorite = favoriteService.addFavorite(email, UUID.fromString(eventId));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFavorite);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get favorites by user", description = "Retrieves all favorites for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorites retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUser(@PathVariable UUID userId) {
        logger.info("GET /api/favorites/user/{} - Getting favorites for user", userId);
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/user/{userId}/event/{eventId}")
    @Operation(summary = "Remove a favorite", description = "Removes a favorite event from a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Favorite removed successfully"),
            @ApiResponse(responseCode = "404", description = "Favorite not found")
    })
    public ResponseEntity<Void> removeFavorite(@PathVariable UUID userId, @PathVariable UUID eventId) {
        logger.info("DELETE /api/favorites/user/{}/event/{} - Removing favorite", userId, eventId);
        favoriteService.removeFavorite(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
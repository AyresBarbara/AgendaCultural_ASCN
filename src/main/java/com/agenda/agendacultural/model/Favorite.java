package com.agenda.agendacultural.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Favorite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @Column(name = "id_favorite", columnDefinition = "BINARY(16)")
    private UUID idFavorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "favorited_date")
    private LocalDateTime favoritedDate;

    @PrePersist
    public void prePersist() {
        if (favoritedDate == null) {
            favoritedDate = LocalDateTime.now();
        }
    }
}

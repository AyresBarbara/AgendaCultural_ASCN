package com.agenda.agendacultural.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @Column(name = "id_category", columnDefinition = "BINARY(16)")
    private UUID idCategory;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    public Category(UUID idCategory) {
        this.idCategory = idCategory;
    }
}

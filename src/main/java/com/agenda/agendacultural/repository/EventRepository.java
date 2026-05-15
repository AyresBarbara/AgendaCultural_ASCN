package com.agenda.agendacultural.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.agendacultural.model.Event;

public interface EventRepository extends JpaRepository<Event, UUID>{
    List<Event> findByTitleContainingIgnoreCase(String title);
    
    // Removido método problemático title(String title)
}

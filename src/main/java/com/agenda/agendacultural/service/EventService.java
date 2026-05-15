package com.agenda.agendacultural.service;

import java.util.List;
import java.util.UUID;

import com.agenda.agendacultural.dto.EventDTO;

public interface EventService {
	EventDTO createEvent(EventDTO eventDTO);
    List<EventDTO> searchEventsByName(String name);
    EventDTO updateEvent(UUID id, EventDTO eventDTO);
    void deleteEvent(UUID id);
    EventDTO getEventById(UUID id);
    List<EventDTO> getAllEvents();

}


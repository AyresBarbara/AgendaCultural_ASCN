package com.agenda.agendacultural.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.agenda.agendacultural.dto.EventDTO;
import com.agenda.agendacultural.repository.EventRepository;
import com.agenda.agendacultural.repository.CategoryRepository;
import com.agenda.agendacultural.repository.UserRepository;
import com.agenda.agendacultural.model.Event;
import com.agenda.agendacultural.model.Category;
import com.agenda.agendacultural.model.User;
import com.agenda.agendacultural.exception.ResourceNotFoundException;


@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    public EventServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        logger.info("Creating a new event with title: {}", eventDTO.getTitle());
        
        Category category = categoryRepository.findById(eventDTO.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + eventDTO.getCategoryId()));
            
        User createdBy = userRepository.findById(eventDTO.getCreatedById())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + eventDTO.getCreatedById()));
        
        Event event = convertToEntity(eventDTO);
        event.setIdEvent(UUID.randomUUID());
        event.setCategory(category);
        event.setCreatedBy(createdBy);
        
        Event savedEvent = eventRepository.save(event);
        logger.info("Event created with ID: {}", savedEvent.getIdEvent());
        return convertToDto(savedEvent);
    }

    @Override
    public List<EventDTO> searchEventsByName(String name) {
        logger.info("Searching events with title containing: {}", name);
        List<Event> events = eventRepository.findByTitleContainingIgnoreCase(name);
        logger.info("Found {} events", events.size());
        return events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO updateEvent(UUID id, EventDTO eventDTO) {
        logger.info("Updating event with ID: {}", id);
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Event not found with ID: {}", id);
                return new ResourceNotFoundException("Event not found with id " + id);
            });

        // Update fields from DTO
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setDateTime(eventDTO.getDateTime());
        event.setLocation(eventDTO.getLocation());
        
        // Update category if provided
        if (eventDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(eventDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + eventDTO.getCategoryId()));
            event.setCategory(category);
        }

        Event updatedEvent = eventRepository.save(event);
        logger.info("Event updated: {}", updatedEvent.getIdEvent());
        return convertToDto(updatedEvent);
    }

    @Override
    public void deleteEvent(UUID id) {
        logger.info("Deleting event with ID: {}", id);
        if (!eventRepository.existsById(id)) {
            logger.error("Event not found with ID: {}", id);
            throw new ResourceNotFoundException("Event not found with id " + id);
        }
        eventRepository.deleteById(id);
        logger.info("Event deleted: {}", id);
    }

    @Override
    public EventDTO getEventById(UUID id) {
        logger.info("Getting event by ID: {}", id);
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Event not found with ID: {}", id);
                return new ResourceNotFoundException("Event not found with id: " + id);
            });
        return convertToDto(event);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        logger.info("Fetching all events");
        List<Event> events = eventRepository.findAll();
        logger.info("Total events found: {}", events.size());
        return events.stream()
                     .map(this::convertToDto)
                     .collect(Collectors.toList());
    }

    // Helper method for DTO conversion
    private EventDTO convertToDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setIdEvent(event.getIdEvent());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDateTime(event.getDateTime());
        dto.setLocation(event.getLocation());
        
        if (event.getCategory() != null) {
            dto.setCategoryId(event.getCategory().getIdCategory());
        }
        
        if (event.getCreatedBy() != null) {
            dto.setCreatedById(event.getCreatedBy().getIdUser());
        }
        
        return dto;
    }

    // Helper method for Entity conversion
    private Event convertToEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setDateTime(eventDTO.getDateTime());
        event.setLocation(eventDTO.getLocation());
        return event;
    }
}

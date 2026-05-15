package com.agenda.agendacultural.service;

import com.agenda.agendacultural.dto.EventDTO;
import com.agenda.agendacultural.exception.ResourceNotFoundException;
import com.agenda.agendacultural.model.Event;
import com.agenda.agendacultural.model.Category;
import com.agenda.agendacultural.model.User;
import com.agenda.agendacultural.repository.EventRepository;
import com.agenda.agendacultural.repository.CategoryRepository;
import com.agenda.agendacultural.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event1;
    private Event event2;
    private EventDTO eventDTO1;
    private Category category;
    private User user;
    private UUID categoryId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        userId = UUID.randomUUID();

        category = new Category();
        category.setIdCategory(categoryId);
        category.setName("Música");
        category.setDescription("Eventos musicais");

        user = new User();
        user.setIdUser(userId);
        user.setName("Admin");
        user.setEmail("admin@example.com");

        event1 = new Event();
        event1.setIdEvent(id1);
        event1.setTitle("Concerto de Rock");
        event1.setDescription("Show da banda XPTO");
        event1.setDateTime(LocalDateTime.now().plusDays(10));
        event1.setLocation("Estádio Principal");
        event1.setCategory(category);
        event1.setCreatedBy(user);

        event2 = new Event();
        event2.setIdEvent(id2);
        event2.setTitle("Peça de Teatro");
        event2.setDescription("Drama clássico");
        event2.setDateTime(LocalDateTime.now().plusDays(5));
        event2.setLocation("Teatro Municipal");
        event2.setCategory(category);
        event2.setCreatedBy(user);

        eventDTO1 = new EventDTO();
        eventDTO1.setIdEvent(id1);
        eventDTO1.setTitle("Concerto de Rock");
        eventDTO1.setDescription("Show da banda XPTO");
        eventDTO1.setDateTime(event1.getDateTime());
        eventDTO1.setLocation("Estádio Principal");
        eventDTO1.setCategoryId(categoryId);
        eventDTO1.setCreatedById(userId);
    }

    @Test
    void getAllEvents_shouldReturnListOfEventDTOs() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

        List<EventDTO> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Concerto de Rock", result.get(0).getTitle());
        assertEquals("Peça de Teatro", result.get(1).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void getEventById_whenEventExists_shouldReturnEventDTO() {
        when(eventRepository.findById(event1.getIdEvent())).thenReturn(Optional.of(event1));

        EventDTO result = eventService.getEventById(event1.getIdEvent());

        assertNotNull(result);
        assertEquals(event1.getIdEvent(), result.getIdEvent());
        assertEquals("Concerto de Rock", result.getTitle());
        verify(eventRepository, times(1)).findById(event1.getIdEvent());
    }

    @Test
    void getEventById_whenEventDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(eventRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventById(nonExistentId);
        });
        verify(eventRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void createEvent_shouldReturnCreatedEventDTO() {
        // Mock para Category e User
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        EventDTO newEventDTO = new EventDTO();
        newEventDTO.setTitle(event1.getTitle());
        newEventDTO.setDescription(event1.getDescription());
        newEventDTO.setDateTime(event1.getDateTime());
        newEventDTO.setLocation(event1.getLocation());
        newEventDTO.setCategoryId(categoryId);
        newEventDTO.setCreatedById(userId);

        EventDTO result = eventService.createEvent(newEventDTO);

        assertNotNull(result);
        assertEquals(event1.getIdEvent(), result.getIdEvent());
        assertEquals(event1.getTitle(), result.getTitle());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(userRepository, times(1)).findById(userId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_whenEventExists_shouldReturnUpdatedEventDTO() {
        when(eventRepository.findById(event1.getIdEvent())).thenReturn(Optional.of(event1));
        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        EventDTO updatedInfo = new EventDTO();
        updatedInfo.setTitle("Concerto Pop");
        updatedInfo.setDescription("Show da banda YWZ");
        updatedInfo.setDateTime(LocalDateTime.now().plusDays(15));
        updatedInfo.setLocation("Arena Nova");

        EventDTO result = eventService.updateEvent(event1.getIdEvent(), updatedInfo);

        assertNotNull(result);
        assertEquals(event1.getIdEvent(), result.getIdEvent());
        assertEquals("Concerto Pop", result.getTitle());
        assertEquals("Arena Nova", result.getLocation());
        verify(eventRepository, times(1)).findById(event1.getIdEvent());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_whenEventDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(eventRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EventDTO updatedInfo = new EventDTO();
        updatedInfo.setTitle("Update Fail");

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.updateEvent(nonExistentId, updatedInfo);
        });
        verify(eventRepository, times(1)).findById(nonExistentId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void deleteEvent_whenEventExists_shouldCallDeleteById() {
        when(eventRepository.existsById(event1.getIdEvent())).thenReturn(true);
        doNothing().when(eventRepository).deleteById(event1.getIdEvent());

        eventService.deleteEvent(event1.getIdEvent());

        verify(eventRepository, times(1)).existsById(event1.getIdEvent());
        verify(eventRepository, times(1)).deleteById(event1.getIdEvent());
    }

    @Test
    void deleteEvent_whenEventDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(eventRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.deleteEvent(nonExistentId);
        });

        verify(eventRepository, times(1)).existsById(nonExistentId);
        verify(eventRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void searchEventsByName_shouldReturnMatchingEventDTOs() {
        String searchTerm = "Concerto";
        when(eventRepository.findByTitleContainingIgnoreCase(searchTerm)).thenReturn(Arrays.asList(event1));

        List<EventDTO> result = eventService.searchEventsByName(searchTerm);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event1.getIdEvent(), result.get(0).getIdEvent());
        assertTrue(result.get(0).getTitle().contains(searchTerm));
        verify(eventRepository, times(1)).findByTitleContainingIgnoreCase(searchTerm);
    }
}

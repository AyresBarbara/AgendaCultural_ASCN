package com.agenda.agendacultural.service;

import com.agenda.agendacultural.dto.UserCreateDto;
import com.agenda.agendacultural.dto.UserResponseDTO;
import com.agenda.agendacultural.exception.ResourceNotFoundException;
import com.agenda.agendacultural.model.User;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserCreateDto userCreateDto;
    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Test User");
        userCreateDto.setEmail("test@example.com");
        userCreateDto.setPassword("password123");

        user = new User();
        user.setIdUser(userId);
        user.setName(userCreateDto.getName());
        user.setEmail(userCreateDto.getEmail());
        user.setPassword(userCreateDto.getPassword());
        user.setRegistrationDate(LocalDateTime.now());
    }

    @Test
    void createUser_shouldReturnUserResponseDTO() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals(userId, result.getIdUser());
        assertEquals(userCreateDto.getName(), result.getName());
        assertEquals(userCreateDto.getEmail(), result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUserResponseDTO() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getIdUser());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(nonExistentId);
        });

        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void getAllUsers_shouldReturnListOfUserResponseDTOs() {
        User user2 = new User();
        user2.setIdUser(UUID.randomUUID());
        user2.setName("Another User");
        user2.setEmail("another@example.com");
        user2.setPassword("password456");
        user2.setRegistrationDate(LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        List<UserResponseDTO> results = userService.getAllUsers();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(user.getIdUser(), results.get(0).getIdUser());
        assertEquals(user2.getIdUser(), results.get(1).getIdUser());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_whenUserExists_shouldReturnUpdatedUserResponseDTO() {
        UserCreateDto updateDto = new UserCreateDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setPassword("newpassword");

        User updatedUser = new User();
        updatedUser.setIdUser(userId);
        updatedUser.setName(updateDto.getName());
        updatedUser.setEmail(updateDto.getEmail());
        updatedUser.setPassword(updateDto.getPassword());
        updatedUser.setRegistrationDate(user.getRegistrationDate());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDTO result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals(userId, result.getIdUser());
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        UserCreateDto updateDto = new UserCreateDto();
        updateDto.setName("Updated Name");

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(nonExistentId, updateDto);
        });

        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_whenUserExists_shouldCallDeleteById() {
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(nonExistentId);
        });

        verify(userRepository, times(1)).existsById(nonExistentId);
        verify(userRepository, never()).deleteById(any(UUID.class));
    }
}

package com.example.jibexample.service;

import com.example.jibexample.dao.UserRepository;
import com.example.jibexample.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("测试用户");
    }
    
    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User created = userService.createUser(testUser);
        
        assertNotNull(created);
        assertEquals("testuser", created.getUsername());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testCreateUser_DuplicateUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(testUser);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testCreateUser_DuplicateEmail() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(testUser);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        
        List<User> result = userService.getAllUsers();
        
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
    
    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        Optional<User> result = userService.getUserById(1L);
        
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }
    
    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        Optional<User> result = userService.getUserById(1L);
        
        assertFalse(result.isPresent());
    }
    
    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Optional<User> result = userService.getUserByUsername("testuser");
        
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }
    
    @Test
    void testUpdateUser_Success() {
        User updatedDetails = new User();
        updatedDetails.setUsername("updateduser");
        updatedDetails.setEmail("updated@example.com");
        updatedDetails.setName("更新用户");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User result = userService.updateUser(1L, updatedDetails);
        
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testUpdateUser_NotFound() {
        User updatedDetails = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(1L, updatedDetails);
        });
    }
    
    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        
        userService.deleteUser(1L);
        
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(1L);
        });
        
        verify(userRepository, never()).deleteById(any());
    }
}

package com.myplan.server.service;

import com.myplan.server.dto.auth.RequestUserDTO;
import com.myplan.server.exception.AlreadyExistsException;
import com.myplan.server.exception.UserNotFoundException;
import com.myplan.server.model.Member;
import com.myplan.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Member member;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* registerUser: 회원가입 */
    @Test
    void registerUser_ShouldRegisterUser_WhenUserDoesNotExist() {
        // Given
        RequestUserDTO userDTO = new RequestUserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("password");
        userDTO.setEmail("newuser@example.com");

        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        // When
        Member registeredUser = userService.registerUser(userDTO);

        // Then
        assertNotNull(registeredUser);
        assertEquals("newuser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals("newuser@example.com", registeredUser.getEmail());
        assertEquals("ROLE_USER", registeredUser.getRole());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(bCryptPasswordEncoder).encode("password");
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Given
        RequestUserDTO userDTO = new RequestUserDTO();
        userDTO.setUsername("existinguser");
        userDTO.setPassword("password");
        userDTO.setEmail("user@example.com");

        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        // When & Then
        assertThrows(AlreadyExistsException.class, () -> userService.registerUser(userDTO));

        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        RequestUserDTO userDTO = new RequestUserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("password");
        userDTO.setEmail("existing@example.com");

        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(AlreadyExistsException.class, () -> userService.registerUser(userDTO));

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("existing@example.com");
        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    // 비밀번호 수정
    @Test
    void updateUser_ShouldThrowException_WhenIdOrPasswordIsNull() {
        // Given
        Long id = null;
        String password = "pw";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(id, password));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserIsNotFound() {
        // Given
        Long id = 1L;
        String password = "new";

        // When
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, password));

        verify(userRepository).findById(id);
    }

    @Test
    void updateUser() {

        //Given
        Long id = 1L;
        String newPassword = "new";
        Member user = Member.builder()
                .id(id)
                .username("test12")
                .password("old")
                .email("test12@test.com")
                .role("ROLE_USER")
                .build();

        user.setPassword(newPassword);

        // When
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Member.class))).thenReturn(user);

        boolean isUpdated = userService.updateUser(id, newPassword);

        // Then
        assertTrue(isUpdated);
    }


    /* 회원탈퇴 */

}

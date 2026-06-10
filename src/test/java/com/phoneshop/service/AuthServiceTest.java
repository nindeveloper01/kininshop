package com.phoneshop.service;

import com.phoneshop.dto.*;
import com.phoneshop.entity.User;
import com.phoneshop.exception.DuplicateResourceException;
import com.phoneshop.repository.UserRepository;
import com.phoneshop.security.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register: successfully creates user and returns token")
    void register_newEmail_createsUserAndReturnsToken() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-pw");

        User savedUser = User.builder().id(1L).email("new@test.com")
                .passwordHash("hashed-pw").role(User.Role.CUSTOMER).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("new@test.com");
        when(userDetailsService.loadUserByUsername("new@test.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("generated-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response.getToken()).isEqualTo("generated-token");
        assertThat(response.getEmail()).isEqualTo("new@test.com");
        assertThat(response.getRole()).isEqualTo(User.Role.CUSTOMER);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("register: throws DuplicateResourceException for existing email")
    void register_existingEmail_throwsDuplicateException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("existing@test.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login: valid credentials return token")
    void login_validCredentials_returnsToken() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        User user = User.builder().id(1L).email("user@test.com")
                .passwordHash("hashed").role(User.Role.CUSTOMER).build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("login-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response.getToken()).isEqualTo("login-token");
        assertThat(response.getRole()).isEqualTo(User.Role.CUSTOMER);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login: bad credentials propagates BadCredentialsException")
    void login_badCredentials_throwsException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}

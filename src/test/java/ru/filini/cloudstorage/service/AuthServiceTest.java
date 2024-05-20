package ru.filini.cloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import ru.filini.cloudstorage.DTO.requests.AuthRequest;
import ru.filini.cloudstorage.DTO.responses.AuthResponse;
import ru.filini.cloudstorage.jwt.JwtTokenUtil;
import ru.filini.cloudstorage.repository.AuthRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private static final String USERNAME_1 = "testUser";
    private static final String PASSWORD_1 = "testPass";
    private static final String TOKEN_1 = "jwtToken";
    private static final String BEARER_TOKEN = "Bearer jwtToken";
    private static final String BEARER_TOKEN_SUBSTRING_7 = "jwtToken";
    private static final AuthRequest AUTHENTICATION_RQ = new AuthRequest(USERNAME_1, PASSWORD_1);
    private static final AuthResponse AUTHENTICATION_RS = new AuthResponse(TOKEN_1);
    private static final UsernamePasswordAuthenticationToken USERNAME_PASSWORD_AUTHENTICATION_TOKEN = new UsernamePasswordAuthenticationToken(USERNAME_1, PASSWORD_1);

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserDetails USER_1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login() {
        when(userService.loadUserByUsername(USERNAME_1)).thenReturn(USER_1);
        when(jwtTokenUtil.generateToken(USER_1)).thenReturn(TOKEN_1);

        AuthResponse response = authService.login(AUTHENTICATION_RQ);

        assertEquals(AUTHENTICATION_RS, response);
        verify(authenticationManager, times(1)).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
        verify(authRepository, times(1)).putTokenAndUsername(TOKEN_1, USERNAME_1);
    }

    @Test
    void logout() {
        when(authRepository.getUsernameByToken(BEARER_TOKEN_SUBSTRING_7)).thenReturn(USERNAME_1);

        authService.logout(BEARER_TOKEN);

        verify(authRepository, times(1)).getUsernameByToken(BEARER_TOKEN_SUBSTRING_7);
        verify(authRepository, times(1)).removeTokenAndUsernameByToken(BEARER_TOKEN_SUBSTRING_7);
    }
}
package ru.filini.cloudstorage.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthRepositoryTest {

    private AuthRepository authRepository;

    private static final String TOKEN_1 = "token1";
    private static final String USERNAME_1 = "user1";
    private static final String TOKEN_2 = "token2";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    void setUp() {
        authRepository = new AuthRepository();
        authRepository.putTokenAndUsername(TOKEN_1, USERNAME_1);
    }

    @Test
    void putTokenAndUsername() {
        String beforePut = authRepository.getUsernameByToken(TOKEN_2);
        assertNull(beforePut);

        authRepository.putTokenAndUsername(TOKEN_2, USERNAME_2);

        String afterPut = authRepository.getUsernameByToken(TOKEN_2);
        assertEquals(USERNAME_2, afterPut);
    }

    @Test
    void removeTokenAndUsernameByToken() {
        String beforeRemove = authRepository.getUsernameByToken(TOKEN_1);
        assertEquals(USERNAME_1, beforeRemove);

        authRepository.removeTokenAndUsernameByToken(TOKEN_1);

        String afterRemove = authRepository.getUsernameByToken(TOKEN_1);
        assertNull(afterRemove);
    }

    @Test
    void getUsernameByToken() {
        assertEquals(USERNAME_1, authRepository.getUsernameByToken(TOKEN_1));
    }
}
package ru.filini.cloudstorage.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.filini.cloudstorage.DTO.requests.AuthRequest;
import ru.filini.cloudstorage.DTO.responses.AuthResponse;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    public AuthResponse login(AuthRequest authRequest) {


        return new AuthResponse();
    }

    public void logout(String authToken) {

    }

}

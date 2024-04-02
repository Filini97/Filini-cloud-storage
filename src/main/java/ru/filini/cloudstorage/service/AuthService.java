package ru.filini.cloudstorage.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.filini.cloudstorage.DTO.requests.AuthRequest;
import ru.filini.cloudstorage.DTO.responses.AuthResponse;
import ru.filini.cloudstorage.jwt.JwtTokenUtil;
import ru.filini.cloudstorage.repository.AuthRepository;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public AuthResponse login(AuthRequest authRequest) {
        if (authRequest == null || authRequest.getLogin() == null || authRequest.getPassword() == null) {
            throw new IllegalArgumentException("Invalid authentication request");
        }

        final String username = authRequest.getLogin();
        final String password = authRequest.getPassword();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final UserDetails userDetails = userService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        authRepository.putTokenAndUsername(token, username);
        log.info("User {} authentication. JWT: {}", username, token);
        return new AuthResponse(token);
    }

    public void logout(String authToken) {

    }

}

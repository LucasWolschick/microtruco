package microtruco.lobbies.services;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Long getUserIdFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        var token = authorizationHeader.substring(7);
        Long userId = Long.parseLong(jwtService.getUserId(token));
        return userId;
    }
}

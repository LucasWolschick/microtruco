package microtruco.lobbies.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final RabbitTemplate rabbitTemplate;

    public AuthService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Long getUserIdFromToken(String token) {
        Long userId = (Long) rabbitTemplate.convertSendAndReceive("authExchange", "userIdFromToken", token);
        if (userId == null) {
            throw new RuntimeException("Invalid token");
        }
        return userId;
    }

    public String getTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        return authorizationHeader.substring(7);
    }
}

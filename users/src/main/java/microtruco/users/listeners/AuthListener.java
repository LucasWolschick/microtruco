package microtruco.users.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import microtruco.users.repositories.UserRepository;
import microtruco.users.services.JwtService;

@Component
public class AuthListener {
    private final UserRepository repository;
    private final JwtService jwtService;

    public AuthListener(UserRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @RabbitListener(queues = "authQueue")
    public Long handleAuthMessage(String token) {
        try {
            var userId = Long.parseLong(jwtService.getUserId(token));
            var user = repository.findById(userId).orElseThrow();
            return user.getId();
        } catch (Exception e) {
            return null;
        }
    }
}

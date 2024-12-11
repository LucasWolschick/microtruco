package microtruco.lobbies.services;

import java.util.HashMap;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import microtruco.lobbies.entities.Lobby;

@Service
public class GameService {
    private final RabbitTemplate rabbitTemplate;

    public GameService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Long startGame(Lobby lobby) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("lobbyId", lobby.getId());
        message.put("players", lobby.getUsers());
        message.put("type", "startGame");

        var result = rabbitTemplate.convertSendAndReceive("gameExchange", "startGame", message);
        if (result == null) {
            throw new RuntimeException("Failed to start game");
        }
        return ((Number) result).longValue();
    }

    public void stopGame(Lobby lobby) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("lobbyId", lobby.getId());
        message.put("gameId", lobby.getGame().get());
        message.put("type", "stopGame");
        rabbitTemplate.convertAndSend("gameExchange", "stopGame", message);
    }
}

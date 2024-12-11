package microtruco.games.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import microtruco.games.controllers.MatchController;

@Component
public class GameListener {
    private MatchController matchController;

    public GameListener(MatchController matchController) {
        this.matchController = matchController;
    }

    @RabbitListener(queues = "gameQueue")
    public Object handleGameMessage(HashMap<String, Object> message) {
        String type = (String) message.get("type");

        switch (type) {
            case "startGame":
                return handleStartGameMessage(message);
            case "stopGame":
                handleStopGameMessage(message);
                return null;
            default:
                throw new IllegalArgumentException("Unknown message type: " + type);
        }
    }

    public Long handleStartGameMessage(HashMap<String, Object> message) {
        var lobbyId = (String) message.get("lobbyId");
        @SuppressWarnings("unchecked")
        var playerObjects = (List<Object>) message.get("players");
        List<Long> players = playerObjects.stream()
                .map(playerId -> ((Number) playerId).longValue())
                .collect(Collectors.toList());
        var match = matchController.createMatch(lobbyId, players);
        return match.getId();
    }

    public void handleStopGameMessage(HashMap<String, Object> message) {
        var lobbyId = (String) message.get("lobbyId");
        var matchId = (Long) message.get("gameId");
        matchController.stopMatch(lobbyId, matchId);
    }
}

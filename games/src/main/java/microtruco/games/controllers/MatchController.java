package microtruco.games.controllers;

import org.springframework.web.bind.annotation.RestController;

import microtruco.games.entities.Game;
import microtruco.games.entities.Match;
import microtruco.games.entities.Trick.PlayerActions;
import microtruco.games.repositories.MatchRepository;
import microtruco.games.services.AuthService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/games")
public class MatchController {
    @Autowired
    private AuthService authService;

    @Autowired
    private MatchRepository repository;

    public Match createMatch(String lobbyId, List<Long> players) {
        if (players.size() != 2 && players.size() != 4) {
            throw new IllegalArgumentException("A match must have exactly 2 or 4 players.");
        }
        var match = new Match();
        match.setLobbyId(lobbyId);
        match.setPlayers(players);
        match.setGame(new Game(players));
        return repository.save(match);
    }

    @GetMapping("/{id}")
    public Match getMatch(@PathVariable("id") Long id) {
        return repository.findById(id).orElseThrow();
    }

    @GetMapping("/{id}/actions")
    public PlayerActions getActions(@PathVariable("id") Long id) {
        var match = repository.findById(id).orElseThrow();
        return match.getGame().getActions();
    }

    @PostMapping("/{id}/actions")
    public Match takeAction(@PathVariable("id") Long id, @RequestParam("n") int index,
            @RequestHeader("Authorization") String authorization) {
        var token = authService.getTokenFromAuthorizationHeader(authorization);
        var user = authService.getUserIdFromToken(token);

        var match = repository.findById(id).orElseThrow();
        var game = match.getGame();
        var playerIndex = match.getPlayers().indexOf(user);
        if (playerIndex == -1) {
            throw new IllegalArgumentException("User is not in this match.");
        }

        var actions = game.getActions();
        if (actions.player() != playerIndex) {
            throw new IllegalArgumentException("It's not your turn.");
        }
        if (index < 0 || index >= actions.actions().size()) {
            throw new IllegalArgumentException("Invalid action.");
        }

        game.applyAction(playerIndex, actions.actions().get(index));
        return repository.save(match);
    }

    public void stopMatch(String lobbyId, Long matchId) {
        var match = repository.findById(matchId).orElseThrow();
        if (!match.getLobbyId().equals(lobbyId)) {
            throw new IllegalArgumentException("Match does not belong to this lobby.");
        }
        repository.delete(match);
    }
}

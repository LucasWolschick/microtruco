package microtruco.games.controllers;

import org.springframework.web.bind.annotation.RestController;

import microtruco.games.entities.Game;
import microtruco.games.entities.Match;
import microtruco.games.entities.Trick.PlayerActions;
import microtruco.games.repositories.MatchRepository;
import microtruco.games.services.AuthService;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/games")
public class MatchController {
    private AuthService authService;
    private MatchRepository repository;

    public MatchController(AuthService authService, MatchRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

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

    public void stopMatch(String lobbyId, Long matchId) {
        var match = repository.findById(matchId).orElseThrow();
        if (!match.getLobbyId().equals(lobbyId)) {
            throw new IllegalArgumentException("Match does not belong to this lobby.");
        }
        repository.delete(match);
    }

    @GetMapping("/{id}")
    public Match getMatch(@PathVariable("id") Long id, @RequestHeader("Authorization") String authz) {
        var user = authService.getUserIdFromAuthorizationHeader(authz);
        var match = repository.findById(id).orElseThrow();
        if (!match.getPlayers().contains(user)) {
            throw new IllegalArgumentException("User is not in this match.");
        }
        // hide hands from other players
        var game = match.getGame();
        var playerIndex = match.getPlayers().indexOf(user);
        game.hideHandsForPlayer(playerIndex);
        return match;
    }

    @GetMapping("/{id}/actions")
    public PlayerActions getActions(@PathVariable("id") Long id, @RequestHeader("Authorization") String authz) {
        var user = authService.getUserIdFromAuthorizationHeader(authz);
        var match = repository.findById(id).orElseThrow();
        if (!match.getPlayers().contains(user)) {
            throw new IllegalArgumentException("User is not in this match.");
        }

        var actions = match.getGame().getActions();
        var playerIndex = match.getPlayers().indexOf(id);
        if (actions.player() != playerIndex) {
            // only show actions if it's the player's turn
            // only show actions to the right player
            return new PlayerActions(actions.player(), List.of());
        }
        return actions;
    }

    @PostMapping("/{id}/actions")
    public Match takeAction(@PathVariable("id") Long id, @RequestParam("n") int index,
            @RequestHeader("Authorization") String authz) {
        var user = authService.getUserIdFromAuthorizationHeader(authz);
        var match = repository.findById(id).orElseThrow();
        if (!match.getPlayers().contains(user)) {
            throw new IllegalArgumentException("User is not in this match.");
        }
        var game = match.getGame();
        var playerIndex = match.getPlayers().indexOf(user);
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
}

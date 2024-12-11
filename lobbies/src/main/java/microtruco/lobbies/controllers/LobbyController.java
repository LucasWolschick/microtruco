package microtruco.lobbies.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import microtruco.lobbies.entities.Lobby;
import microtruco.lobbies.repositories.LobbyRepository;
import microtruco.lobbies.services.AuthService;
import microtruco.lobbies.services.GameService;
import microtruco.lobbies.services.UniqueIdGenerator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/lobbies")
public class LobbyController {
    @Autowired
    private LobbyRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UniqueIdGenerator idGenerator;

    @Autowired
    private GameService gameService;

    @PostMapping("/create")
    public Lobby createLobby() {
        var id = idGenerator.generate();
        var lobby = new Lobby(id);
        return repository.save(lobby);
    }

    @GetMapping("/{id}")
    public Lobby getLobby(@PathVariable("id") String id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping("/{id}/join")
    public Lobby joinLobby(@PathVariable("id") String id, @RequestHeader("Authorization") String authz) {
        var token = authService.getTokenFromAuthorizationHeader(authz);
        var user = authService.getUserIdFromToken(token);
        var lobby = repository.findById(id).orElseThrow();
        lobby.addUser(user);
        return repository.save(lobby);
    }

    @PostMapping("/{id}/leave")
    public Lobby postMethodName(@PathVariable("id") String id, @RequestHeader("Authorization") String authz) {
        var token = authService.getTokenFromAuthorizationHeader(authz);
        var user = authService.getUserIdFromToken(token);
        var lobby = repository.findById(id).orElseThrow();
        lobby.removeUser(user);
        return repository.save(lobby);
    }

    @PostMapping("/{id}/start")
    public Lobby startGame(@PathVariable("id") String id, @RequestHeader("Authorization") String authz) {
        var token = authService.getTokenFromAuthorizationHeader(authz);
        var user = authService.getUserIdFromToken(token);
        var lobby = repository.findById(id).orElseThrow();
        if (!lobby.isOwner(user)) {
            throw new RuntimeException("Only the owner can start the game");
        }
        if (lobby.getGame().isPresent()) {
            throw new RuntimeException("Game already started");
        }
        if (lobby.getUsers().size() != 2 && lobby.getUsers().size() != 4) {
            throw new RuntimeException("Not enough players");
        }
        var gameId = gameService.startGame(lobby);
        lobby.setGame(gameId);
        return repository.save(lobby);
    }

    @PostMapping("/{id}/stop")
    public Lobby stopGame(@PathVariable("id") String id, @RequestHeader("Authorization") String authz) {
        var token = authService.getTokenFromAuthorizationHeader(authz);
        var user = authService.getUserIdFromToken(token);
        var lobby = repository.findById(id).orElseThrow();
        if (!lobby.isOwner(user)) {
            throw new RuntimeException("Only the owner can stop the game");
        }
        if (!lobby.getGame().isPresent()) {
            throw new RuntimeException("No game to stop");
        }
        gameService.stopGame(lobby);
        lobby.setGame(null);
        return repository.save(lobby);
    }

}
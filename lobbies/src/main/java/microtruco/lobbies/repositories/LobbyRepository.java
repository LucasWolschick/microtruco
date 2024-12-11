package microtruco.lobbies.repositories;

import org.springframework.data.repository.CrudRepository;

import microtruco.lobbies.entities.Lobby;

public interface LobbyRepository extends CrudRepository<Lobby, String> {
}

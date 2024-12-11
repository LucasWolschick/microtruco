package microtruco.lobbies.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Lobby {
    @Id
    private String id;
    private List<Long> users = new ArrayList<>();
    private Long game = null;

    public Lobby() {
        this.id = "";
    }

    public Lobby(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Long> getUsers() {
        return List.copyOf(users);
    }

    public void addUser(Long user) {
        if (!users.contains(user) && users.size() < 4)
            users.add(user);
    }

    public void removeUser(Long user) {
        users.remove(user);
    }

    public boolean isOwner(long user) {
        return users.get(0) == user;
    }

    public Optional<Long> getGame() {
        return Optional.ofNullable(game);
    }

    public void setGame(Long game) {
        this.game = game;
    }
}

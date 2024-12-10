package microtruco.lobbies;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Lobby {
    @Id
    private String id;
    private Set<Long> users = new HashSet<>();

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
        users.add(user);
    }

    public void removeUser(Long user) {
        users.remove(user);
    }
}

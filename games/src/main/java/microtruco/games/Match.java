package microtruco.games;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Match {
    @Id
    private Long id;

    // constructed from lobby
    private String lobbyId;
    private List<Long> players;

    private Game game;
}

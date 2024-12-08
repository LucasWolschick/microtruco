package microtruco.players;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private PlayerRepository repository;

    public PlayerController(@Autowired PlayerRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/")
    public Long createPlayer(@RequestBody String name) {
        var player = new Player();
        player.setUsername(name);
        player = repository.save(player);
        return player.getId();
    }
}
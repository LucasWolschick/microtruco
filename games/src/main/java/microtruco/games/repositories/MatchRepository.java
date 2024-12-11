package microtruco.games.repositories;

import org.springframework.data.repository.CrudRepository;

import microtruco.games.entities.Match;

public interface MatchRepository extends CrudRepository<Match, Long> {
}

package microtruco.users.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import microtruco.users.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}

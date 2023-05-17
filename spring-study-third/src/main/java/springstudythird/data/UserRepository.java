package springstudythird.data;

import org.springframework.data.repository.CrudRepository;
import springstudythird.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}
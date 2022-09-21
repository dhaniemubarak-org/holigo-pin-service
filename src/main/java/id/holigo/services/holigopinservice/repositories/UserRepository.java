package id.holigo.services.holigopinservice.repositories;

import id.holigo.services.holigopinservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

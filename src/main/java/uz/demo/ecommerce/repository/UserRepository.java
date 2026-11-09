package uz.demo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.demo.ecommerce.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

}

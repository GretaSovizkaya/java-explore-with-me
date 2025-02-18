package main.users.repository;

import main.users.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(List<Long> ids, Pageable pageable);
}
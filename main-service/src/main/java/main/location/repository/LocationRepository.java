package main.location.repository;

import main.location.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface LocationRepository extends JpaRepository<Location, Long> {
}
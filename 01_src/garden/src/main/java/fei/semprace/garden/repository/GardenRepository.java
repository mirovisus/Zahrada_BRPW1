package fei.semprace.garden.repository;

import fei.semprace.garden.model.Garden;
import fei.semprace.garden.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GardenRepository extends JpaRepository<Garden, Long> {
    List<Garden> findByUser(User user);
}

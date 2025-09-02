package fei.semprace.garden.repository;

import fei.semprace.garden.model.Garden;
import fei.semprace.garden.model.Request;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByGarden(Garden garden);

    @EntityGraph(attributePaths = {"garden", "garden.address"})
    List<Request> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"garden", "garden.address"})
    List<Request> findByGarden_Address_CityIgnoreCaseOrderByCreatedAtDesc(String city);

    @EntityGraph(attributePaths = {"garden", "garden.address"})
    Optional<Request> findById(Long id);
}

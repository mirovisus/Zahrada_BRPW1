package fei.semprace.garden.repository;

import fei.semprace.garden.model.User;
import fei.semprace.garden.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByWorkerOrderByCreatedAtDesc(User worker);
}

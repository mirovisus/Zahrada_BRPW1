package fei.semprace.garden.service;

import fei.semprace.garden.dto.WorkDTO;
import fei.semprace.garden.model.Request;
import fei.semprace.garden.model.User;
import fei.semprace.garden.model.Work;
import fei.semprace.garden.model.WorkStatus;
import fei.semprace.garden.repository.WorkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkService {
    private final WorkRepository workRepository;

    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public List<WorkDTO> getWorksByWorker(User worker) {
        return workRepository.findByWorkerOrderByCreatedAtDesc(worker)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private WorkDTO toDTO(Work work) {
        WorkDTO dto = new WorkDTO();
        dto.setId(work.getId());
        dto.setStatus(work.getStatus() != null ? work.getStatus().name() : null);
        dto.setCreatedAt(work.getCreatedAt());
        dto.setNote(work.getNote());
        dto.setRequestId(work.getRequest() != null ? work.getRequest().getId() : null);
        return dto;
    }

    public Work createFromRequest(Request r, User worker, WorkStatus status) {
        Work w = new Work();
        w.setWorker(worker);
        w.setRequest(r);
        w.setStatus(status != null ? status : WorkStatus.VYTVOREN);
        w.setCreatedAt(LocalDateTime.now());
        return workRepository.save(w);
    }
}

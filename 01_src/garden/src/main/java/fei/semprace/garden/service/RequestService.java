package fei.semprace.garden.service;

import fei.semprace.garden.dto.GardenBriefDTO;
import fei.semprace.garden.dto.RequestDTO;
import fei.semprace.garden.model.*;
import fei.semprace.garden.repository.GardenRepository;
import fei.semprace.garden.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestService {
    private final RequestRepository requestRepository;
    private final GardenRepository gardenRepository;
    private final WorkService workService;

    @Autowired
    public RequestService(RequestRepository requestRepository, GardenRepository gardenRepository, WorkService workService) {
        this.requestRepository = requestRepository;
        this.gardenRepository = gardenRepository;
        this.workService = workService;
    }

    public RequestDTO createRequest(Long gardenId, RequestDTO dto) {
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new RuntimeException("Garden not found: " + gardenId));

        Request request = new Request();
        request.setDescription(dto.getDescription());
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.VYTVOREN);
        request.setGarden(garden);

        Request saved = requestRepository.save(request);
        return toDTO(saved);
    }

    private RequestDTO toDTO(Request request) {
        RequestDTO dto = new RequestDTO();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<RequestDTO> getRequestsByGarden(Long gardenId) {
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new RuntimeException("Garden not found: " + gardenId));
        return requestRepository.findByGarden(garden).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long requestId) {
        requestRepository.deleteById(requestId);
    }

    private RequestDTO toDTOFeed(Request r) {
        RequestDTO dto = new RequestDTO();
        dto.setId(r.getId());
        dto.setDescription(r.getDescription());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setStatus(r.getStatus() != null ? r.getStatus().name() : null);

        if (r.getGarden() != null) {
            GardenBriefDTO g = new GardenBriefDTO();
            g.setId(r.getGarden().getId());
            if (r.getGarden().getAddress() != null) {
                g.setCity(r.getGarden().getAddress().getCity());
            }
            g.setPreviewImageId(r.getGarden().getPreviewImageId());
            g.setImagePath(buildGardenImagePath(r.getGarden()));
            dto.setGarden(g);
        }
        return dto;
    }

    private String buildGardenImagePath(Garden garden) {
        if (garden.getPreviewImageId() != null) {
            return "/api/images/" + garden.getPreviewImageId();
        }
        return "/images/garden-placeholder.jpg";
    }


    @Transactional(readOnly = true)
    public List<RequestDTO> getRequestsFeed(String city) {
        List<Request> list = (city == null || city.isBlank())
                ? requestRepository.findAllByOrderByCreatedAtDesc()
                : requestRepository.findByGarden_Address_CityIgnoreCaseOrderByCreatedAtDesc(city.trim());

        return list.stream().map(this::toDTOFeed).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RequestDTO getRequestDetail(Long id) {
        Request r = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));

        RequestDTO dto = new RequestDTO();
        dto.setId(r.getId());
        dto.setDescription(r.getDescription());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setStatus(r.getStatus() != null ? r.getStatus().name() : null);

        if (r.getGarden() != null) {
            GardenBriefDTO g = new GardenBriefDTO();
            g.setId(r.getGarden().getId());
            g.setName(r.getGarden().getName());
            if (r.getGarden().getAddress() != null) {
                g.setCity(r.getGarden().getAddress().getCity());
            }
            g.setPreviewImageId(r.getGarden().getPreviewImageId());
            g.setImagePath(buildGardenImageUrl(r.getGarden())); // см. helper ниже
            dto.setGarden(g);
        }
        return dto;
    }

    public void applyForRequest(Long requestId, fei.semprace.garden.model.User worker) {
        Request r = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (worker.getRole() != Role.WORKER) {
            throw new RuntimeException("Pouze pracovník se může přihlásit k práci.");
        }

        // CEKA_PLATBU
        r.setStatus(RequestStatus.CEKA_PLATBU);

        // VYTVOREN
        Work work = workService.createFromRequest(r, worker, WorkStatus.VYTVOREN);

        requestRepository.save(r);
    }

    private String buildGardenImageUrl(Garden garden) {
        if (garden.getPreviewImageId() != null) {
            return "/api/images/" + garden.getPreviewImageId();
        }
        return "/images/garden-placeholder.jpg";
    }

}

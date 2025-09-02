package fei.semprace.garden.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class RequestDTO {
    private Long id;

    @NotBlank(message = "Description must not be empty")
    @Size(max=2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private LocalDateTime createdAt;
    private String status;

    private GardenBriefDTO garden;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GardenBriefDTO getGarden() {
        return garden;
    }

    public void setGarden(GardenBriefDTO garden) {
        this.garden = garden;
    }
}

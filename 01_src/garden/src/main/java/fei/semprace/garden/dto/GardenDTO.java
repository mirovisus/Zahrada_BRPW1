package fei.semprace.garden.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class GardenDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be positive")
    private Double areaGarden;

    private String imageFileName;
    private String imagePath;
    private Long previewImageId;

    private AddressDTO address;

    public GardenDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAreaGarden() {
        return areaGarden;
    }

    public void setAreaGarden(Double areaGarden) {
        this.areaGarden = areaGarden;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    private List<GardenImgDTO> images;

    public List<GardenImgDTO> getImages() {
        return images;
    }

    public void setImages(List<GardenImgDTO> images) {
        this.images = images;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getPreviewImageId() {
        return previewImageId;
    }
    public void setPreviewImageId(Long previewImageId) {
        this.previewImageId = previewImageId;
    }
}

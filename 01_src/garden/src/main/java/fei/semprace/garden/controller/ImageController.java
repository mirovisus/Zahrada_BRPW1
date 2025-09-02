package fei.semprace.garden.controller;

import fei.semprace.garden.model.GardenImage;
import fei.semprace.garden.repository.GardenImageRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {
    private final GardenImageRepository imageRepository;

    public ImageController(GardenImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/api/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        GardenImage image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(image.getFileType()))
                .body(image.getBytes());
    }
}

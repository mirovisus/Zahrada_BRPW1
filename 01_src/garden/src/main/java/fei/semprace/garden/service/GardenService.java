package fei.semprace.garden.service;

import fei.semprace.garden.dto.AddressDTO;
import fei.semprace.garden.dto.GardenDTO;
import fei.semprace.garden.model.User;
import fei.semprace.garden.model.Address;
import fei.semprace.garden.model.Garden;
import fei.semprace.garden.model.GardenImage;
import fei.semprace.garden.repository.GardenRepository;
import fei.semprace.garden.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GardenService {
    public final GardenRepository gardenRepository;
    private final UserRepository userRepository;

    @Autowired
    public GardenService(GardenRepository gardenRepository, UserRepository userRepository) {
        this.gardenRepository = gardenRepository;
        this.userRepository = userRepository;
    }

    public String getGardenInfo() {
        return "Total gardens in DB: " + gardenRepository.count();
    }

    private List<Garden> gardens = new ArrayList<>();

    public List<GardenDTO> getAllGardens() {
        return gardenRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GardenDTO saveGarden(GardenDTO gardenDTO, MultipartFile file, String username) throws IOException {
        Garden garden = convertToEntity(gardenDTO);

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found: " + username));
        garden.setUser(user);
        user.getGardens().add(garden);

        Garden savedGarden = gardenRepository.save(garden);

        userRepository.save(user);

        if (file != null && file.getSize() != 0) {
            GardenImage gardenImage = toImageEntity(file);
            gardenImage.setPreviewImage(true);
            gardenImage.setGarden(savedGarden);

            savedGarden.getImages().add(gardenImage);

            savedGarden = gardenRepository.save(savedGarden);

            Optional<GardenImage> previewImageOpt = savedGarden.getImages().stream()
                    .filter(GardenImage::isPreviewImage)
                    .findFirst();

            if (previewImageOpt.isPresent()) {
                savedGarden.setPreviewImageId(previewImageOpt.get().getId());
                gardenRepository.save(savedGarden);
            }
        }

        return convertToDTO(savedGarden);
    }

    private GardenImage toImageEntity(MultipartFile file) throws IOException {
        GardenImage image = new GardenImage();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void updateGarden(Long id, GardenDTO gardenDTO, MultipartFile file) throws IOException{
        Garden existingGarden = gardenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garden not found with id: " + id));

        existingGarden.setName(gardenDTO.getName());
        existingGarden.setAreaGarden(gardenDTO.getAreaGarden());

        if (gardenDTO.getAddress() != null) {
            Address existingAddress = existingGarden.getAddress();
            if (existingAddress == null) {
                existingAddress = new Address();
            }
            existingAddress.setCity(gardenDTO.getAddress().getCity());
            existingAddress.setStreet(gardenDTO.getAddress().getStreet());
            existingAddress.setHouseNumber(gardenDTO.getAddress().getHouseNumber());
            existingAddress.setZipCode(gardenDTO.getAddress().getZipCode());
            existingGarden.setAddress(existingAddress);
        }

        //IMAGE HANDLING
        if (file != null && file.getSize() > 0) {
            existingGarden.getImages().removeIf(GardenImage::isPreviewImage);

            GardenImage gardenImage = toImageEntity(file);
            gardenImage.setPreviewImage(true);
            gardenImage.setGarden(existingGarden);
            existingGarden.getImages().add(gardenImage);
        }

        gardenRepository.save(existingGarden);
    }

    public void deleteGarden(Long id) {
        gardenRepository.deleteById(id);
    }

    public GardenDTO getGardenById(Long id) {
        return gardenRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Garden not found with id: " + id));
    }

    private GardenDTO convertToDTO(Garden garden) {
        GardenDTO gardenDTO = new GardenDTO();
        gardenDTO.setId(garden.getId());
        gardenDTO.setName(garden.getName());
        gardenDTO.setAreaGarden(garden.getAreaGarden());

        garden.getImages().stream()
                .filter(GardenImage::isPreviewImage)
                .findFirst()
                .ifPresent(previewImage -> gardenDTO.setPreviewImageId(previewImage.getId()));

        if (garden.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setCity(garden.getAddress().getCity());
            addressDTO.setStreet(garden.getAddress().getStreet());
            addressDTO.setHouseNumber(garden.getAddress().getHouseNumber());
            addressDTO.setZipCode(garden.getAddress().getZipCode());
            gardenDTO.setAddress(addressDTO);
        }

        return gardenDTO;
    }

    private Garden convertToEntity(GardenDTO gardenDTO) {
        Garden garden = new Garden();
        garden.setId(gardenDTO.getId());
        garden.setName(gardenDTO.getName());
        garden.setAreaGarden(gardenDTO.getAreaGarden());

        if (gardenDTO.getAddress() != null) {
            Address address = new Address();
            address.setCity(gardenDTO.getAddress().getCity());
            address.setStreet(gardenDTO.getAddress().getStreet());
            address.setHouseNumber(gardenDTO.getAddress().getHouseNumber());
            address.setZipCode(gardenDTO.getAddress().getZipCode());
            garden.setAddress(address);
        }
        if (garden.getCreationDate() == null) {
            garden.setCreationDate(LocalDate.now());
        }

        return garden;
    }

    public List<GardenDTO> getGardensByUser(User user) {
        return gardenRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}

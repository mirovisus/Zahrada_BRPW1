package fei.semprace.garden.controller;

import fei.semprace.garden.dto.AddressDTO;
import fei.semprace.garden.dto.GardenDTO;
import fei.semprace.garden.dto.RequestDTO;
import fei.semprace.garden.service.GardenService;
import fei.semprace.garden.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
public class GardenController {

    private final GardenService gardenService;
    private final RequestService requestService;

    public GardenController(GardenService gardenService, RequestService requestService) {
        this.gardenService = gardenService;
        this.requestService = requestService;
    }

    @GetMapping("/garden/add")
    public String showAddForm(Model model) {
        GardenDTO gardenDTO = new GardenDTO();
        gardenDTO.setAddress(new AddressDTO());
        model.addAttribute("gardenDTO", gardenDTO);
        return "newgarden";
    }

    @PostMapping("/garden/add")
    public String saveGarden(
            @Valid @ModelAttribute("gardenDTO") GardenDTO gardenDTO,
            BindingResult bindingResult,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Principal principal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "newgarden";
        }

        try {
            gardenService.saveGarden(gardenDTO, file, principal.getName());
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving garden: " + e.getMessage());
            return "newgarden";
        }
    }

    @GetMapping("/garden/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        GardenDTO gardenDTO = gardenService.getGardenById(id);
        model.addAttribute("gardenDTO", gardenDTO);

        try {
            java.util.List<RequestDTO> requests = requestService.getRequestsByGarden(id);
            model.addAttribute("requests", requests);
        } catch (Exception e) {
            model.addAttribute("requests", java.util.Collections.emptyList());
        }
        model.addAttribute("gardenId", id);
        model.addAttribute("requestDTO", new RequestDTO());

        return "editgarden";
    }

    @PostMapping("/garden/edit/{id}")
    public String updateGarden(
            @PathVariable Long id,
            @ModelAttribute("gardenDTO") GardenDTO gardenDTO,
            BindingResult bindingResult,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model
    ){
        if (bindingResult.hasErrors()) {
            return "editgarden";
        }

        try {
            gardenService.updateGarden(id, gardenDTO, file);
            return "redirect:/home";

        } catch (Exception e) {
            model.addAttribute("error", "Error updating garden: " + e.getMessage());
            return "editgarden";
        }
    }

    @PostMapping("/garden/delete/{id}")
    public String deleteGarden(@PathVariable Long id) {
        System.out.println("Deleting garden with id: " + id);
        gardenService.deleteGarden(id);
        return "redirect:/home";
    }
}

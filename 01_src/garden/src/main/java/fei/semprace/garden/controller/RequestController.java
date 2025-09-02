package fei.semprace.garden.controller;

import fei.semprace.garden.model.User;
import fei.semprace.garden.service.GardenService;
import fei.semprace.garden.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import fei.semprace.garden.dto.RequestDTO;
import fei.semprace.garden.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class RequestController {

    private final RequestService requestService;
    private final GardenService gardenService;
    private final UserService userService;

    @Autowired
    public RequestController(RequestService requestService, GardenService gardenService, UserService userService) {
        this.requestService = requestService;
        this.gardenService = gardenService;
        this.userService = userService;
    }

    @GetMapping("/request/add/{gardenId}")
    public String showRequestForm(@PathVariable Long gardenId, Model model) {
        model.addAttribute("requestDTO", new RequestDTO());
        model.addAttribute("gardenId", gardenId);
        return "newrequest";
    }

    @PostMapping("/request/add/{gardenId}")
    public String createRequest(@PathVariable Long gardenId,
                                @Valid @ModelAttribute("requestDTO") RequestDTO requestDTO,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("gardenDTO", gardenService.getGardenById(gardenId));
            model.addAttribute("requests", requestService.getRequestsByGarden(gardenId));
            model.addAttribute("gardenId", gardenId);
            return "editgarden";
        }

        try {
            requestService.createRequest(gardenId, requestDTO);
            return "redirect:/garden/edit/" + gardenId;
        } catch (Exception e) {
            model.addAttribute("error", "Error creating request: " + e.getMessage());
            model.addAttribute("gardenDTO", gardenService.getGardenById(gardenId));
            model.addAttribute("requests", requestService.getRequestsByGarden(gardenId));
            model.addAttribute("gardenId", gardenId);
            return "editgarden";
        }
    }

    @PostMapping("/request/delete/{requestId}")
    public String deleteRequest(@PathVariable Long requestId,
                                @RequestParam Long gardenId) {
        requestService.delete(requestId);
        return "redirect:/garden/edit/" + gardenId;
    }

    @GetMapping("/request/{id}")
    public String requestPage(@PathVariable Long id, Model model) {
        RequestDTO dto = requestService.getRequestDetail(id);
        model.addAttribute("request", dto);
        return "requestpage";
    }

    @PostMapping("/request/apply/{id}")
    public String apply(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        User current = userService.findByUsername(principal.getName());
        requestService.applyForRequest(id, current);
        return "redirect:/home";
    }

}

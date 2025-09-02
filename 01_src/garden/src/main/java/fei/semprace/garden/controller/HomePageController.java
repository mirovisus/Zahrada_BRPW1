package fei.semprace.garden.controller;

import fei.semprace.garden.dto.GardenDTO;
import fei.semprace.garden.dto.RequestDTO;
import fei.semprace.garden.dto.WorkDTO;
import fei.semprace.garden.model.User;
import fei.semprace.garden.model.Role;
import fei.semprace.garden.service.GardenService;
import fei.semprace.garden.service.RequestService;
import fei.semprace.garden.service.UserService;
import fei.semprace.garden.service.WorkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class HomePageController {

    private final UserService userService;
    private final GardenService gardenService;
    private final WorkService workService;
    private final RequestService requestService;

    public HomePageController(UserService userService, GardenService gardenService, WorkService workService, RequestService requestService) {
        this.userService = userService;
        this.gardenService = gardenService;
        this.workService = workService;
        this.requestService = requestService;
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(required = false) String city,
                           Model model, Principal principal) {
        String username = (principal != null) ? principal.getName() : "Guest";

        if ("Guest".equals(username)) {
            model.addAttribute("username", "Guest");
            model.addAttribute("gardens", List.of());
            model.addAttribute("email", "");
            model.addAttribute("role", "Neregistrovaný");
            return "homepage";
        }

        User user = userService.findByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("role", user.getRole());

        if (user.getRole() == Role.WORKER) {
            // STRANKA PRO PRACOVNIKA
            List<WorkDTO> works = workService.getWorksByWorker(user);
            List<RequestDTO> requests = requestService.getRequestsFeed(city);

            model.addAttribute("works", works);
            model.addAttribute("requests", requests);
            model.addAttribute("cityFilter", city == null ? "" : city);

            return "workerpage"; // VRATIT STRANKU PRO PRACOVNIKA
        } else {
            // VLASTNIK ZAHRADY
            List<GardenDTO> gardens = gardenService.getGardensByUser(user);
            for (GardenDTO garden : gardens) {
                if (garden.getImageFileName() != null && !garden.getImageFileName().isEmpty()) {
                    garden.setImagePath("/images/uploads/" + garden.getImageFileName());
                }
            }
            model.addAttribute("gardens", gardens);
            return "homepage";
        }
    }

    @PostMapping("/home/update")
    public String updateUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            Principal principal,
            Model model
    ) {
        String currentUsername = principal.getName();
        try {
            userService.updateUser(currentUsername, username, email, password);
            return "redirect:/home?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "homepage";
        }
    }

    @PostMapping("/home/delete")
    public String deleteUser(Principal principal) {
        String username = principal.getName();

        userService.deleteUserByUsername(username);
        return "redirect:/login?accountDeleted";
    }

}

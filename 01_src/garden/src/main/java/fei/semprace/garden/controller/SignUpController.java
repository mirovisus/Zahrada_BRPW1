package fei.semprace.garden.controller;

import fei.semprace.garden.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignUpController {

    private final SignUpService signUpService;

    @Autowired
    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @GetMapping("/signup")
    public String showSignUpForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam(required = false) String role,
            Model model){

        try {
            signUpService.registerNewUser(username, password, email, role);
            return "redirect:/login?registered";
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }
}
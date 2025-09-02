package fei.semprace.garden.service;

import fei.semprace.garden.model.Role;
import fei.semprace.garden.model.User;
import fei.semprace.garden.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignUpService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerNewUser(String username, String password, String email, String roleStr) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username is already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);

        newUser.setRole(mapRole(roleStr));

        userRepository.save(newUser);

    }

    public void registerNewUser(String username, String password, String email) {
        registerNewUser(username, password, email, null);
    }

    private Role mapRole(String roleStr) {
        if (roleStr == null) return Role.OWNER;
        String r = roleStr.trim().toUpperCase();
        switch (r) {
            case "WORKER": return Role.WORKER;
            case "OWNER":  return Role.OWNER;
            default:       return Role.OWNER;
        }
    }
}
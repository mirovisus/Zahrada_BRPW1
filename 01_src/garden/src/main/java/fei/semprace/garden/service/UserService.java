package fei.semprace.garden.service;

import fei.semprace.garden.dto.UserDTO;
import fei.semprace.garden.model.Role;
import fei.semprace.garden.model.User;
import fei.semprace.garden.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User registerNewUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(mapRole(userDTO.getRole()));

        return userRepository.save(user);
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void updateUser(String currentUsername, String username, String email, String password) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUsername));

        if (!currentUsername.equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        user.setUsername(username);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);
    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        userRepository.delete(user);
    }

    private Role mapRole(String roleStr) {
        if (roleStr == null) return Role.OWNER;
        String r = roleStr.trim().toUpperCase();
        return switch (r) {
            case "WORKER" -> Role.WORKER;
            default -> Role.OWNER;
        };
    }
}

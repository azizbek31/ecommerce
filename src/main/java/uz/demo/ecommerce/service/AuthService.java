package uz.demo.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.demo.ecommerce.dto.AuthResponse;
import uz.demo.ecommerce.dto.LoginRequest;
import uz.demo.ecommerce.dto.RegisterRequest;
import uz.demo.ecommerce.dto.UserResponse;
import uz.demo.ecommerce.entity.User;
import uz.demo.ecommerce.enums.Role;
import uz.demo.ecommerce.exception.DuplicateResourceException;
import uz.demo.ecommerce.exception.NotFoundException;
import uz.demo.ecommerce.exception.UnauthorizedException;
import uz.demo.ecommerce.repository.UserRepository;
import uz.demo.ecommerce.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("The email already exists!: " + email);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(Role.USER);
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getFullName(), saved.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new UnauthorizedException("Invalid email or password!"));
        if (!passwordEncoder.matches(request.password(), user.getPassword()) || !user.isEnabled()) {
            throw new UnauthorizedException("Invalid email or password!");
        }
        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
        return new AuthResponse(jwtService.generateToken(user), "Bearer", userResponse);
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("Invalid token!");
        }
        User user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow(
                () -> new UnauthorizedException("Invalid token!"));
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
}

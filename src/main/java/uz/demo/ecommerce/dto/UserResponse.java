package uz.demo.ecommerce.dto;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        String role
) {}

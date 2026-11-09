package uz.demo.ecommerce.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UserResponse user
){}

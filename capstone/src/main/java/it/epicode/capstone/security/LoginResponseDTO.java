package it.epicode.capstone.security;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponseDTO {
    RegisteredUserDTO user;
    String token;

    @Builder(setterPrefix = "with")
    public LoginResponseDTO(RegisteredUserDTO user, String token) {
        this.user = user;
        this.token = token;
    }
}


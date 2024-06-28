package it.epicode.capstone.modules.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Lo username non può contenere solo spazi vuoti")
    @Size(max = 20, message ="Il tuo username è troppo lungo max 20 caratteri")
    private String username;

    @NotBlank(message = "La password non può contenere solo spazi vuoti")
    @Size(max = 25, message ="La password è troppo lunga max 25 caratteri")
    private String password;
}

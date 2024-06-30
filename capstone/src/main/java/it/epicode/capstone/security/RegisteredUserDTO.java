package it.epicode.capstone.security;

import it.epicode.capstone.security.roles.Roles;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class RegisteredUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String avatar;
    private LocalDateTime createdAt;
    private List<Roles> roles;

    @Builder(setterPrefix = "with")
    public RegisteredUserDTO(Long id, String firstName, String lastName, String username, String email, String avatar, LocalDateTime createdAt , List<Roles> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.roles = roles;
    }
}
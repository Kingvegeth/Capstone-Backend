package it.epicode.capstone.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
}

package it.epicode.capstone.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class RegisterUserDTO {
   private String firstName;
   private String lastName;
   private String username;
   private String email;
   private String password;
   private String avatar;
}
package it.epicode.capstone.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String avatar;
    private LocalDateTime createdAt;

}
package it.epicode.capstone.review;

import it.epicode.capstone.user.User;
import it.epicode.capstone.user.UserResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewResponseForMovie {
    private Long id;
    private String title;
    private String body;
    private UserResponse user;
}

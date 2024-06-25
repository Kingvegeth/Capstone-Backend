package it.epicode.capstone.review;

import it.epicode.capstone.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewRequest {

    private String title;
    private String body;
    private Long userId;
    private Long movieId;

}

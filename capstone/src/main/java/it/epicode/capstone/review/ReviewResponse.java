package it.epicode.capstone.review;

import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewResponse {

    private Long id;
    private String title;
    private String body;
    private User user;
    private Movie movie;

}

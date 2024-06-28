package it.epicode.capstone.review;

import it.epicode.capstone.comment.CommentResponse;
import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.security.RegisteredUserDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReviewResponse {

    private Long id;
    private String title;
    private String body;
    private RegisteredUserDTO user;

    private Movie movie;
    private List<CommentResponse> comments;
}

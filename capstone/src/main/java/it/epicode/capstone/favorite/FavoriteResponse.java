package it.epicode.capstone.favorite;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoriteResponse {
    private Long movieId;
    private String movieTitle;

    public FavoriteResponse(Long movieId, String movieTitle) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }
}

package it.epicode.capstone.favorite;

import it.epicode.capstone.movie.MovieResponse;
import it.epicode.capstone.movie.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private MovieService movieService;

    @PostMapping("/{movieId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long movieId) {
        favoriteService.addFavorite(movieId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long movieId) {
        favoriteService.removeFavorite(movieId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites() {
        List<FavoriteResponse> favorites = favoriteService.getFavorites();
        return new ResponseEntity<>(favorites, HttpStatus.OK);
    }
}

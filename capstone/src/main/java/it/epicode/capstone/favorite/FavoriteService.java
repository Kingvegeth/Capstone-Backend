package it.epicode.capstone.favorite;

import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.movie.MovieRepository;
import it.epicode.capstone.user.User;
import it.epicode.capstone.user.UserRepository;
import it.epicode.capstone.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserService userService;

    public void addFavorite(Long movieId) {
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        if (favoriteRepository.findByUserAndMovie(user, movie).isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setMovie(movie);
            favoriteRepository.save(favorite);
        }
    }

    public void removeFavorite(Long movieId) {
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        favoriteRepository.findByUserAndMovie(user, movie).ifPresent(favoriteRepository::delete);
    }

    public List<FavoriteResponse> getFavorites() {
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return favoriteRepository.findAllByUser(user).stream()
                .map(favorite -> new FavoriteResponse(favorite.getMovie().getId(), favorite.getMovie().getTitle()))
                .collect(Collectors.toList());
    }
}
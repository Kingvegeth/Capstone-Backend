package it.epicode.capstone.favorite;

import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndMovie(User user, Movie movie);
    List<Favorite> findAllByUser(User user);
}

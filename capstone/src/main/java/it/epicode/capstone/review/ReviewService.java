package it.epicode.capstone.review;

import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.movie.MovieRepository;
import it.epicode.capstone.security.SecurityUserDetails;
import it.epicode.capstone.user.User;
import it.epicode.capstone.user.UserRepository;
import it.epicode.capstone.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserService userService;

    public Optional<ReviewResponse> findById(Long id) {
        return reviewRepository.findById(id).map(this::convertToResponse);
    }



    public ReviewResponse save(ReviewRequest request) {
        Review review = new Review();
        BeanUtils.copyProperties(request, review);

        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        review.setUser(user);

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        review.setMovie(movie);

        reviewRepository.save(review);
        return convertToResponse(review);
    }

    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);
        response.setUser(review.getUser());
        response.setMovie(review.getMovie());
        return response;
    }
}

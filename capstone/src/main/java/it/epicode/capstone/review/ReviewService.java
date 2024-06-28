package it.epicode.capstone.review;

import it.epicode.capstone.comment.CommentService;
import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.movie.MovieRepository;
import it.epicode.capstone.security.RegisteredUserDTO;
import it.epicode.capstone.security.SecurityUserDetails;
import it.epicode.capstone.user.User;
import it.epicode.capstone.user.UserRepository;
import it.epicode.capstone.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private CommentService commentService;

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

    public ReviewResponse updateReview(ReviewRequest request) {
        Review review = reviewRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        Long currentUserId = userService.getCurrentUserId();
        if (!review.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to update this review");
        }

        review.setTitle(request.getTitle());
        review.setBody(request.getBody());
        review.setRating(request.getRating());

        reviewRepository.save(review);
        return convertToResponse(review);
    }

    public List<ReviewResponse> findAllByMovieId(Long movieId) {
        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(review -> review.getMovie().getId().equals(movieId))
                .collect(Collectors.toList());
        return reviews.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<ReviewResponse> findAllReviewsByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Review convertToEntity(ReviewResponse response) {
        Review review = new Review();
        BeanUtils.copyProperties(response, review);
        return review;
    }

    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);
        response.setUser(new RegisteredUserDTO(
                review.getUser().getId(),
                review.getUser().getFirstName(),
                review.getUser().getLastName(),
                review.getUser().getUsername(),
                review.getUser().getEmail(),
                review.getUser().getRoles()
        ));
        response.setComments(commentService.findAllByReviewId(review.getId()));
        response.setRating(review.getRating());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }

    public ReviewResponseForMovie convertToResponseForMovie(Review review) {
        ReviewResponseForMovie response = new ReviewResponseForMovie();
        BeanUtils.copyProperties(review, response);
        response.setUser(review.getUser());
        return response;
    }
}

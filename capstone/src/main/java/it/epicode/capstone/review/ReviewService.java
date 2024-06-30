package it.epicode.capstone.review;

import it.epicode.capstone.comment.Comment;
import it.epicode.capstone.comment.CommentRepository;
import it.epicode.capstone.comment.CommentService;
import it.epicode.capstone.exceptions.NotFoundException;
import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.movie.MovieRepository;
import it.epicode.capstone.security.RegisteredUserDTO;
import it.epicode.capstone.security.SecurityUserDetails;
import it.epicode.capstone.user.User;
import it.epicode.capstone.user.UserRepository;
import it.epicode.capstone.user.UserResponse;
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
    private CommentRepository commentRepository;

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

        // Update fields only if they are provided in the request
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getBody() != null) {
            review.setBody(request.getBody());
        }
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

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

        if (review.getUser() != null) {
            response.setUser(convertToUserResponse(review.getUser())); // Convert User to UserResponse
        } else {
            response.setUserStatus("Utente eliminato");
        }

        response.setComments(commentService.findAllByReviewId(review.getId()));
        response.setRating(review.getRating());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }


    private UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setAvatar(user.getAvatar());
        userResponse.setCreatedAt(user.getCreatedAt());
        return userResponse;
    }

    public ReviewResponseForMovie convertToResponseForMovie(Review review) {
        ReviewResponseForMovie response = new ReviewResponseForMovie();
        BeanUtils.copyProperties(review, response);

        if (review.getUser() != null) {
            response.setUser(convertToUserResponse(review.getUser())); // Convert User to UserResponse
        } else {
            response.setUserStatus("Utente eliminato");
        }

        return response;
    }

    public void deleteReview(Long reviewId) {
        Long currentUserId = userService.getCurrentUserId();

        // Trova la recensione
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(reviewId, "Review not found"));

        // Verifica se l'utente corrente è l'autore della recensione
        if (!review.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to delete this review");
        }

        // Trova tutti i commenti della recensione
        List<Comment> comments = commentRepository.findAllByReviewId(reviewId);

        // Cancella tutti i commenti della recensione
        for (Comment comment : comments) {
            deleteCommentRecursive(comment);
        }

        // Cancella la recensione
        reviewRepository.deleteById(reviewId);
    }

    private void deleteCommentRecursive(Comment comment) {
        // Trova tutti i commenti figli del commento
        List<Comment> replies = commentRepository.findAllByParentCommentId(comment.getId());

        // Cancella tutti i commenti figli
        for (Comment reply : replies) {
            deleteCommentRecursive(reply); // Ricorsione per cancellare i commenti figli dei figli
        }

        // Cancella il commento
        commentRepository.deleteById(comment.getId());
    }

}

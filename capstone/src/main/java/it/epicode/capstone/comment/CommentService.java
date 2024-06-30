package it.epicode.capstone.comment;

import it.epicode.capstone.exceptions.NotFoundException;
import it.epicode.capstone.review.ReviewRepository;
import it.epicode.capstone.security.RegisteredUserDTO;
import it.epicode.capstone.user.User;
import it.epicode.capstone.user.UserRepository;
import it.epicode.capstone.user.UserResponse;
import it.epicode.capstone.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    public CommentResponse save(CommentRequest request) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(request, comment);

        var user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        comment.setUser(user);

        if (request.getReviewId() != null) {
            var review = reviewRepository.findById(request.getReviewId())
                    .orElseThrow(() -> new IllegalArgumentException("Review not found"));
            comment.setReview(review);
            comment.setReplyToComment(false);
        }

        if (request.getParentCommentId() != null) {
            var parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
            comment.setReplyToComment(true);
        }

        commentRepository.save(comment);
        return convertToResponse(comment);
    }

    public CommentResponse updateComment(CommentRequest request) {
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Long currentUserId = userService.getCurrentUserId();

        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to update this comment");
        }

        comment.setBody(request.getBody());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);
        return convertToResponse(comment);
    }


    public Optional<CommentResponse> findById(Long id) {
        return commentRepository.findById(id).map(this::convertToResponse);
    }

    public List<CommentResponse> findAllCommentsByUserId(Long userId) {
        return commentRepository.findAllByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        BeanUtils.copyProperties(comment, response);

        if (comment.getUser() != null) {
            response.setUser(convertToUserResponse(comment.getUser())); // Convert User to UserResponse
        } else {
            response.setUserStatus("Utente eliminato");
        }

        response.setReplies(comment.getReplies().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));

        response.setReviewId(comment.getReview() != null ? comment.getReview().getId() : null);
        response.setParentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        response.setReplyToComment(comment.isReplyToComment());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
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

    public List<CommentResponse> findAllByReviewId(Long reviewId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getReview() != null && comment.getReview().getId().equals(reviewId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> findAllByParentCommentId(Long parentCommentId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getParentComment() != null && comment.getParentComment().getId().equals(parentCommentId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        Long currentUserId = userService.getCurrentUserId();

        // Trova il commento
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(commentId, "Comment not found"));

        // Verifica se l'utente corrente è l'autore del commento
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to delete this comment");
        }

        // Cancella il commento e i suoi figli
        deleteCommentRecursive(comment);
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

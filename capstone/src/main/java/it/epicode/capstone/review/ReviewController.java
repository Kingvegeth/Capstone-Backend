package it.epicode.capstone.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        Optional<ReviewResponse> review = reviewService.findById(id);
        return review.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMovieId(@PathVariable Long movieId) {
        List<ReviewResponse> reviews = reviewService.findAllByMovieId(movieId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.findAllReviewsByUserId(userId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request, BindingResult validator) {
        if (validator.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        ReviewResponse response = reviewService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponse> patchReview(@PathVariable Long id, @RequestBody ReviewRequest request) {
        request.setId(id);
        ReviewResponse updatedReview = reviewService.updateReview(request);
        return ResponseEntity.ok(updatedReview);
    }
}

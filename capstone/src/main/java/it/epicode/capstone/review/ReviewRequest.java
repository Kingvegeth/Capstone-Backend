package it.epicode.capstone.review;

import it.epicode.capstone.user.User;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
public class ReviewRequest {

    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String body;
    private Long userId;
    @Min(1)
    @Max(10)
    private Integer rating;
    private Long movieId;

}

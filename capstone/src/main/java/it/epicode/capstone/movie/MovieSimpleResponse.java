package it.epicode.capstone.movie;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MovieSimpleResponse {
    private Long id;
    private String title;
    private int year;
    private int duration;
    private String description;
    private String posterImg;
    private double averageRating;
}

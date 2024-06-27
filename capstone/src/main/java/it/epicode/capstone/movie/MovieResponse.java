package it.epicode.capstone.movie;

import it.epicode.capstone.company.CompanyResponse;
import it.epicode.capstone.people.Person;
import it.epicode.capstone.company.Company;
import it.epicode.capstone.people.PersonResponse;
import it.epicode.capstone.review.Review;
import it.epicode.capstone.review.ReviewResponse;
import it.epicode.capstone.review.ReviewResponseForMovie;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class MovieResponse {

    private Long id;

    @NonNull
    @Size(min = 1, max = 255)
    private String title;

    private int year;
    private int duration;
    private String description;
    private Genre genre;
    private String posterImg;


    private List<ReviewResponseForMovie> reviews;
    private List<PersonResponse> cast;
    private List<PersonResponse> directors;
    private List<PersonResponse> screenwriters;
    private List<CompanyResponse> producers;
    private CompanyResponse distributor;
}

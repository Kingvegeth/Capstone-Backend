package it.epicode.capstone.movie;

import it.epicode.capstone.people.Person;
import it.epicode.capstone.company.Company;
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

    private List<Person> cast;
    private List<Person> directors;
    private List<Person> screenwriters;
    private List<Company> producers;
    private Company distributor;
}

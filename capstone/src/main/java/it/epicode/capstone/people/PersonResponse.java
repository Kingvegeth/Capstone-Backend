package it.epicode.capstone.people;

import it.epicode.capstone.movie.MovieResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class PersonResponse {

    private Long id;

    @NonNull
    @Size(min = 1, max = 255)
    private String firstName;

    @NonNull
    @Size(min = 1, max = 255)
    private String lastName;

    private LocalDate dateOfBirth;

}

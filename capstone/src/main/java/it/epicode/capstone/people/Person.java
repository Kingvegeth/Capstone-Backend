package it.epicode.capstone.people;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.capstone.BaseEntity;
import it.epicode.capstone.movie.Movie;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.Size;

@Entity
@Table(name = "people")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Person extends BaseEntity {

    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    private LocalDate dateOfBirth;

    @JsonIgnore
    @ManyToMany(mappedBy = "cast")
    private List<Movie> movies;


}

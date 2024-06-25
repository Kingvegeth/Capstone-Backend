package it.epicode.capstone.people;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.time.LocalDate;

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

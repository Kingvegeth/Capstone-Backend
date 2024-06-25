package it.epicode.capstone.company;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CompanyRequest {

    @NonNull
    @Size(min = 1, max = 255)
    private String name;
}

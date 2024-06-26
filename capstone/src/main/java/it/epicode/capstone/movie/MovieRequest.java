package it.epicode.capstone.movie;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class MovieRequest {

    @NonNull
    @Size(min = 1, max = 255)
    private String title;

    private int year;
    private int duration;
    private String description;
    private Genre genre;
    private String posterImg;

    private List<Long> castIds;
    private List<Long> directorIds;
    private List<Long> screenwriterIds;
    private List<Long> producerIds;
    private Long distributorId;

}

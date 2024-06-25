package it.epicode.capstone.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.capstone.BaseEntity;
import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "reviews")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Review extends BaseEntity {

    private String title;

    private String body;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private Movie movie;
}

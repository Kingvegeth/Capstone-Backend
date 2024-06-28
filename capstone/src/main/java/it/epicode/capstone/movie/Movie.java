package it.epicode.capstone.movie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.capstone.BaseEntity;
import it.epicode.capstone.favorite.Favorite;
import it.epicode.capstone.people.Person;
import it.epicode.capstone.company.Company;
import it.epicode.capstone.review.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "movies")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Movie extends BaseEntity {

    private String title;
    private int year;
    private int duration;
    private String description;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private String posterImg;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "movie_cast",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> cast;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "movie_directors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> directors;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "movie_screenwriters",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> screenwriters;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "movie_producers",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private List<Company> producers;


    @ManyToOne
    @JoinColumn(name = "distributor_id")
    private Company distributor;


    @JsonIgnore
    @OneToMany(mappedBy = "movie")
    List<Review> reviews;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Favorite> favorites;
}

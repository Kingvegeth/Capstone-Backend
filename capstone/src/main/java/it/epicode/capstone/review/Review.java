package it.epicode.capstone.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.capstone.BaseEntity;
import it.epicode.capstone.movie.Movie;
import it.epicode.capstone.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Min(1)
    @Max(10)
    private int rating;

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void removeUser() {
        this.user = null;
    }

    @Transient
    public String getUserStatus() {
        return (user == null) ? "Utente eliminato" : user.getUsername();
    }

    @Transient
    public Long getUserId() {
        return (user == null) ? null : user.getId();
    }
}

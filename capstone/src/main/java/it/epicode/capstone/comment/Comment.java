package it.epicode.capstone.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.capstone.BaseEntity;
import it.epicode.capstone.review.Review;
import it.epicode.capstone.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Comment extends BaseEntity {

    private String body;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private Review review;

    @ManyToOne
    @JsonIgnore
    private Comment parentComment;

    @JsonIgnore
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    @Builder.Default
    private boolean isReplyToComment = false;

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
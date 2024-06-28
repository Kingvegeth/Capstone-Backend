package it.epicode.capstone.comment;


import it.epicode.capstone.security.RegisteredUserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    @NonNull
    private String body;
    private boolean isReplyToComment;
    private RegisteredUserDTO user;
    private List<CommentResponse> replies;
    private Long reviewId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
package it.epicode.capstone.comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class CommentRequest {

    private Long id;

    @NonNull
    private String body;
    private Long reviewId;
    private Long parentCommentId;
    private boolean isReplyToComment;
}
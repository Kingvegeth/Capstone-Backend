package it.epicode.capstone.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findByUserId(@Param("userId") Long userId);

}
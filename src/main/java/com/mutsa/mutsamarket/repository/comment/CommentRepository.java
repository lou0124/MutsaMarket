package com.mutsa.mutsamarket.repository.comment;

import com.mutsa.mutsamarket.entity.Comment;
import com.mutsa.mutsamarket.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    @Query("select c from Comment c join fetch c.user where c.item = :item")
    Page<Comment> findByItemWithUser(@Param("item") Item item, Pageable pageable);
}

package com.example.aiatest.repository;

import com.example.aiatest.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "select p.* from POST as p " +
            "right join POST_TAGS as pt on pt.post_id = p.id " +
            "group by p.id " +
            "having sum( " +
            "    case " +
            "        when pt.tag in :tags then 1 else 0 end " +
            ") = :listSize",
            countQuery = "select count(p.*) from POST as p " +
                    "right join POST_TAGS as pt on pt.post_id = p.id " +
                    "group by p.id " +
                    "having sum( " +
                    "    case " +
                    "        when pt.tag in :tags then 1 else 0 end " +
                    ") = :listSize",
            nativeQuery = true
    )
    Page<Post> findAllByAllTags(@Param("tags") List<String> tags, @Param("listSize") long listSize, Pageable pageable);

    @Query(value = "select p.* from POST as p " +
            "join POST_TAGS as pt on pt.post_id = p.id " +
            "where pt.tag in :tags " +
            "group by p.id",
            countQuery = "select count(p.*) from POST as p " +
                    "join POST_TAGS as pt on pt.post_id = p.id " +
                    "where pt.tag in :tags " +
                    "group by p.id",
            nativeQuery = true
    )
    Page<Post> findAllByAnyTags(@Param("tags") List<String> tags, Pageable pageable);

    Optional<Post> findByLink(String link);
}

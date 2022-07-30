package com.example.aiatest.repository;

import com.example.aiatest.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByTagsIn(List<String> tags, Pageable pageable);
}

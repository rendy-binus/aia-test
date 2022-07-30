package com.example.aiatest.repository;

import com.example.aiatest.model.entity.PostRequest;
import com.example.aiatest.webclient.model.TagMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface PostRequestRepository extends JpaRepository<PostRequest, Long> {
    Optional<PostRequest> findFirstByTagsAndTagMode(String tags, TagMode tagMode);

    Optional<PostRequest> findFirstByUpdatedDateBeforeOrderByUpdatedDateAsc(OffsetDateTime updatedDate);
}

package com.samiul.Y.controller;

import com.samiul.Y.dto.PostResponse;
import com.samiul.Y.repository.PostRepository;
import com.samiul.Y.service.PostService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/liked/{id}")
    public ResponseEntity<?> getLikedPosts(@PathVariable("id") ObjectId userId) {
        return ResponseEntity.ok(postService.getLikedPosts(userId));
    }
}

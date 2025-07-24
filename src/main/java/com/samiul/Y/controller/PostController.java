package com.samiul.Y.controller;

import com.samiul.Y.dto.CreatePostRequest;
import com.samiul.Y.dto.PostResponse;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.PostRepository;
import com.samiul.Y.service.PostService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/likes/{id}")
    public ResponseEntity<?> getLikedPosts(@PathVariable("id") ObjectId userId) {
        return ResponseEntity.ok(postService.getLikedPosts(userId));
    }

    @GetMapping("/following")
    public ResponseEntity<?> getFollowingPosts(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.getFollowingPosts(currentUser));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserPosts(@PathVariable String username) {
        return ResponseEntity.ok(postService.getUserPosts(username));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request, @AuthenticationPrincipal User currentUser) throws IOException {
        PostResponse response = postService.createPost(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likeUnlikePost(@PathVariable("id") ObjectId postId, @AuthenticationPrincipal User currentUser) {
        List<String> updatedLikes = postService.likeUnlikePost(postId, currentUser.getId());

        return ResponseEntity.ok(updatedLikes);
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<?> commentPost(@PathVariable("id") ObjectId postId, @AuthenticationPrincipal User currentUser, @RequestBody Map<String, String> body) {
        List<PostResponse.CommentResponse> updatedComments = postService.commentPost(postId, currentUser.getId(), body.get("text"));

        return ResponseEntity.ok(updatedComments);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") ObjectId postId, @AuthenticationPrincipal User currentUser) {
        postService.deletePost(postId, currentUser.getId());

        return ResponseEntity.ok(Map.of("message", "Post deleted successfully."));
    }
}

package com.samiul.Y.service;

import com.samiul.Y.dto.PostResponse;
import com.samiul.Y.model.Post;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.PostRepository;
import com.samiul.Y.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    User postUser = userRepository.findById(post.getUser())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Post user not found."));

                    List<PostResponse.CommentResponse> commentResponses = post.getComments().stream()
                            .map(comment -> {
                                User commentUser = userRepository.findById(comment.getUser())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Comment user not found."));
                                return new PostResponse.CommentResponse(comment, commentUser);
                            })
                            .toList();

                    return new PostResponse(post, new PostResponse.SimpleUser(postUser), commentResponses);
                })
                .toList();
    }

    public List<PostResponse> getLikedPosts(ObjectId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));

        return postRepository.findByIdIn(user.getLikedPosts()).stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    User postUser = userRepository.findById(post.getUser())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Post user not found."));

                    List<PostResponse.CommentResponse> commentResponses = post.getComments().stream()
                            .map(comment -> {
                                User commentUser = userRepository.findById(comment.getUser())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Comment user not found."));
                                return new PostResponse.CommentResponse(comment, commentUser);
                            })
                            .toList();

                    return new PostResponse(post, new PostResponse.SimpleUser(postUser), commentResponses);
                })
                .toList();
    }
}

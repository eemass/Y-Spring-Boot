package com.samiul.Y.service;

import com.samiul.Y.dto.PostResponse;
import com.samiul.Y.model.Post;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.PostRepository;
import com.samiul.Y.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    PostResponse response = new PostResponse();
                    response.setId(post.getId().toHexString());

                    User user = userRepository.findById(post.getUser()).orElse(null);
                    PostResponse.SimpleUser userDto = new PostResponse.SimpleUser();

                    if (user != null) {
                        userDto.setId(user.getId().toHexString());
                        userDto.setFullName(user.getFullName());
                        userDto.setUsername(user.getUsername());
                        userDto.setProfileImg(user.getProfileImg());
                    }

                    response.setUser(userDto);

                    response.setText(post.getText());
                    response.setImage(post.getImage());
                    response.setCreatedAt(post.getCreatedAt());
                    response.setUpdatedAt(post.getUpdatedAt());

                    response.setLikes(post.getLikes().stream()
                            .map(ObjectId::toHexString)
                            .collect(Collectors.toList()));

                    List<PostResponse.CommentResponse> comments = post.getComments().stream().map(c -> {
                        PostResponse.CommentResponse commentResponse = new PostResponse.CommentResponse();
                        commentResponse.setText(c.getText());
                        User commentUser = userRepository.findById(c.getUser()).orElse(null);
                        PostResponse.SimpleUser commentUserDto = new PostResponse.SimpleUser();
                        if (commentUser != null) {
                            commentUserDto.setId(commentUser.getId().toHexString());
                            commentUserDto.setUsername(commentUser.getUsername());
                            commentUserDto.setFullName(commentUser.getFullName());
                            commentUserDto.setProfileImg(commentUser.getProfileImg());
                        }
                        commentResponse.setUser(commentUserDto);

                        return commentResponse;
                    }).collect(Collectors.toList());

                    response.setComments(comments);

                    return response;

                })
                .collect(Collectors.toList());
    }
}

package com.samiul.Y.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.samiul.Y.dto.CreatePostRequest;
import com.samiul.Y.dto.PostResponse;
import com.samiul.Y.model.Notification;
import com.samiul.Y.model.NotificationType;
import com.samiul.Y.model.Post;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.NotificationRepository;
import com.samiul.Y.repository.PostRepository;
import com.samiul.Y.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final Cloudinary cloudinary;


    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    User postUser = userRepository.findById(post.getUser())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Post owner not found."));

                    List<PostResponse.CommentResponse> commentResponses = post.getComments().stream()
                            .map(comment -> {
                                User commentUser = userRepository.findById(comment.getUser())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Comment author not found."));
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
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Post owner not found."));

                    List<PostResponse.CommentResponse> commentResponses = post.getComments().stream()
                            .map(comment -> {
                                User commentUser = userRepository.findById(comment.getUser())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Comment author not found."));
                                return new PostResponse.CommentResponse(comment, commentUser);
                            })
                            .toList();

                    return new PostResponse(post, new PostResponse.SimpleUser(postUser), commentResponses);
                })
                .toList();
    }

    public List<PostResponse> getFollowingPosts(User user) {
        List<ObjectId> followingIds = user.getFollowing();

        List<Post> posts = postRepository.findByUserIn(followingIds);

        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map( post -> {
                    User postUser = userRepository.findById(post.getUser()).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Post owner not found"));
                    List<PostResponse.CommentResponse> commentResponses = post.getComments().stream().map(c -> {
                        User commentUser = userRepository.findById(c.getUser()).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Comment author not found"));
                        return new PostResponse.CommentResponse(c, commentUser);
                    }).toList();

                    return new PostResponse(post, new PostResponse.SimpleUser(postUser), commentResponses);
                }).toList();
    }

    public List<PostResponse> getUserPosts(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));

        List<Post> posts = postRepository.findByUser(user.getId());

        return posts.stream().sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    User postOwner = userRepository.findById(post.getUser()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post owner not found."));
                    List<PostResponse.CommentResponse> commentResponses = post.getComments().stream().map(comment -> {
                        User commentUser = userRepository.findById(comment.getUser()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment author not found."));
                        return new PostResponse.CommentResponse(comment, commentUser);
                    }).toList();

                    return new PostResponse(post, new PostResponse.SimpleUser(postOwner), commentResponses);
        }).toList();
    }

    public PostResponse createPost(CreatePostRequest request, User currentUser) throws IOException {
        if ((request.getText() == null || request.getText().isBlank()) &&
                (request.getImage() == null || request.getImage().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post must include text or image.");
        }

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isBlank()) {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(request.getImage(), Map.of());
            imageUrl = (String) uploadResult.get("secure_url");
        }

        Post post = Post.builder()
                .user(currentUser.getId())
                .text(request.getText())
                .image(imageUrl)
                .build();

        Post savedPost = postRepository.save(post);

        List<PostResponse.CommentResponse> emptyComments = new ArrayList<>();
        return new PostResponse(savedPost, new PostResponse.SimpleUser(currentUser), emptyComments);
    }

    public List<String> likeUnlikePost(ObjectId postId, ObjectId userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isLiked = post.getLikes().contains(userId);

        if (isLiked) {
            post.getLikes().remove(userId);
            user.getLikedPosts().remove(postId);
            userRepository.save(user);
            postRepository.save(post);

        } else {
            post.getLikes().add(userId);
            user.getLikedPosts().add(postId);
            userRepository.save(user);
            postRepository.save(post);

            Notification notification = Notification.builder()
                    .to(post.getUser())
                    .from(userId)
                    .type(NotificationType.like)
                    .build();

            notificationRepository.save(notification);

        }
        return post.getLikes().stream().map(ObjectId::toHexString).toList();
    }

    public List<PostResponse.CommentResponse> commentPost(ObjectId postId, ObjectId userId, String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment must include text.");
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        Post.Comment newComment = Post.Comment.builder()
                .text(text)
                .user(userId)
                .build();

        post.getComments().add(newComment);
        postRepository.save(post);

        notificationRepository.save( Notification.builder()
                        .from(userId)
                        .to(post.getUser())
                        .type(NotificationType.comment)
                .build());

        return post.getComments().stream().map(comment -> {
            User commentUser = userRepository.findById(comment.getUser()).orElse(null);
            return new PostResponse.CommentResponse(comment, commentUser);
        }).toList();
    }

    public void deletePost(ObjectId postId, ObjectId userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        if (!post.getUser().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to delete this post.");
        }

        if (post.getImage() != null && !post.getImage().isBlank()) {
            String publicId = post.getImage().substring(post.getImage().lastIndexOf("/") + 1).split("\\.")[0];
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete image from Cloudinary.");
            }
        }

        postRepository.deleteById(postId);
    }
}

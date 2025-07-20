package com.samiul.Y.repository;

import com.samiul.Y.model.Post;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, ObjectId> {
    List<Post> findByIdIn(List<ObjectId> ids);
}

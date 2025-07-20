package com.samiul.Y.repository;

import com.samiul.Y.model.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
    List<Notification> findByTo(ObjectId to);
    void deleteByTo(ObjectId to);
}

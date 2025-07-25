# Y – Spring Boot Backend

This repository contains the Spring Boot backend for **Y**, a full-stack social media platform. The backend supports secure user authentication, post and comment management, follow/unfollow functionality, and real-time notification logic. The backend was rewritten from an earlier Node.js implementation to adopt a more scalable and maintainable Java-based architecture.

**Frontend Repository:** [Y-Frontend](https://github.com/eemass/Y-Frontend)  
**Live Demo:** [https://y-frontend.onrender.com](https://y-frontend.onrender.com)

## Features

- JWT-based authentication using Spring Security with HTTP-only cookies
- User registration, login, logout, and profile updates
- Post creation, deletion, and retrieval
- Like and comment functionality
- Follow and unfollow other users
- Notification system for likes, comments, and follows
- Cloudinary integration for media uploads
- MongoDB data persistence with audit logging
- Layered architecture using DTOs for clean separation of concerns

## Technologies Used

- Java 21
- Spring Boot
- Spring Security
- MongoDB
- Cloudinary
- Maven

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven
- MongoDB instance (local or remote)
- Cloudinary account (for image uploads)

### Environment Configuration

Add the following to your environment variables or `application.properties` file:

```properties
MONGODB_URI=your_mongodb_connection_string
JWT_SECRET=your_jwt_secret
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

### Running the Application

To start the backend server locally:

```bash
mvn spring-boot:run
```

By default, the application will be accessible at:  
`http://localhost:8080`

### Project Structure

```
com.samiul.Y
├── controller       # REST controllers
├── service          # Business logic layer
├── repository       # MongoDB repositories
├── model            # Data models
├── dto              # Data Transfer Objects
├── security         # JWT authentication and filters
├── config           # Configuration classes
```

## API Overview

All endpoints are prefixed with `/api`.

### Authentication
- `POST /api/auth/signup` — Register a new user
- `POST /api/auth/login` — User login
- `POST /api/auth/logout` — User logout

### Users
- `GET /api/users/{id}` — Get user by ID
- `PUT /api/users/update` — Update user profile
- `PUT /api/users/follow/{id}` — Follow or unfollow a user

### Posts
- `POST /api/posts/` — Create a new post
- `GET /api/posts/` — Retrieve all posts
- `DELETE /api/posts/{id}` — Delete a post

### Notifications
- `GET /api/notifications/` — Retrieve notifications for the current user
- `DELETE /api/notifications/` — Clear all notifications

## Deployment

This backend can be deployed on platforms such as Render or Heroku. Ensure that all necessary environment variables are securely configured in the deployment environment.

## License

This project is licensed under the MIT License.

## Author

**Samiul Islam**  
[Portfolio](https://islamsamiul.netlify.app) | [GitHub](https://github.com/eemass) | [LinkedIn](https://www.linkedin.com/in/eemass)

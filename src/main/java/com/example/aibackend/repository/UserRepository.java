package com.example.aibackend.repository;

import com.example.aibackend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    // We'll use this to check if a user already exists
    Optional<User> findByEmail(String email);

    // Find a user by their unique Google ID
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByGithubId(String githubId);
}
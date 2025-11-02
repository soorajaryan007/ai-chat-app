package com.example.aibackend.service;

import com.example.aibackend.model.User;
import com.example.aibackend.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Get the user from GitHub
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 2. Get GitHub-specific attributes
        String githubId = oauth2User.getAttribute("id").toString();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String pictureUrl = oauth2User.getAttribute("avatar_url");

        // If email is null (GitHub allows private emails), try to find by ID
        Optional<User> userOptional = (email != null) 
            ? userRepository.findByEmail(email) 
            : userRepository.findByGithubId(githubId);

        User user;
        if (userOptional.isPresent()) {
            // --- User exists ---
            user = userOptional.get();
            // Update their info
            if (user.getGithubId() == null) user.setGithubId(githubId);
            if (email != null) user.setEmail(email);
            user.setName(name);
            user.setPictureUrl(pictureUrl);
        } else {
            // --- New user ---
            user = new User();
            user.setGithubId(githubId);
            user.setEmail(email); // Can be null
            user.setName(name);
            user.setPictureUrl(pictureUrl);
        }

        // 3. Save the user to our database
        userRepository.save(user);

        return oauth2User;
    }
}
package com.example.aibackend.service;

import com.example.aibackend.model.User;
import com.example.aibackend.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Get the user from Google
        OidcUser oidcUser = super.loadUser(userRequest);

        // 2. Check if the user already exists in our database
        // We check by Google ID (subject) as email *can* change
        Optional<User> userOptional = userRepository.findByGoogleId(oidcUser.getSubject());

        User user;
        if (userOptional.isPresent()) {
            // --- User exists ---
            user = userOptional.get();
            // Update their name, email, or picture if it has changed
            user.setEmail(oidcUser.getEmail());
            user.setName(oidcUser.getFullName());
            user.setPictureUrl(oidcUser.getPicture());
        } else {
            // --- New user ---
            user = new User();
            user.setGoogleId(oidcUser.getSubject());
            user.setEmail(oidcUser.getEmail());
            user.setName(oidcUser.getFullName());
            user.setPictureUrl(oidcUser.getPicture());
        }

        // 3. Save the user to our database
        userRepository.save(user);

        return oidcUser;
    }
}
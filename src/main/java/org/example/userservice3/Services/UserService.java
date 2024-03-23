package org.example.userservice3.Services;

import org.example.userservice3.Models.User;
import org.example.userservice3.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isEmpty()) {
            return userOptional.get();
        }

        return null;
    }
}

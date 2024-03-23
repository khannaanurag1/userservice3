package org.example.userservice3.Security.Services;


import org.example.userservice3.Security.Models.CustomUserDetails;
import org.example.userservice3.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.example.userservice3.Models.User;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    //username=anu , password=anu on spring security
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
           throw new UsernameNotFoundException("User doesn't exist");
        }

        User user = userOptional.get();
        return new CustomUserDetails(user);
    }
}

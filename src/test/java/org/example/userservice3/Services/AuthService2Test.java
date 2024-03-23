package org.example.userservice3.Services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthService2Test {
    @Autowired
    AuthService2 authService2;

    @Test
    void signUp() {
        authService2.signUp("anurag123","anurag123");
    }

//    @Test
//    void login() {
//    }
//
//    @Test
//    void validateToken() {
//    }
}
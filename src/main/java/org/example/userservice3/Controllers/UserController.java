package org.example.userservice3.Controllers;

import org.example.userservice3.Dtos.UserDto;
import org.example.userservice3.Models.User;
import org.example.userservice3.Security.Models.CustomUserDetails;
import org.example.userservice3.Security.Services.CustomUserDetailsService;
import org.example.userservice3.Services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getUserDetails(@PathVariable("id") Long id) {
       User user = userService.getUser(id);
       return getUserDto(user);
    }

    private UserDto getUserDto(User user) {
        UserDto userDto =  new UserDto();
        userDto.setEmail(user.getEmail());
        //userDto.setRoles(user.getRoles());
        return userDto;
    }
}

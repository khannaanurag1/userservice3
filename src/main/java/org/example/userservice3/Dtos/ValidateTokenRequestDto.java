package org.example.userservice3.Dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidateTokenRequestDto {
    private Long id;
    private String token;
}

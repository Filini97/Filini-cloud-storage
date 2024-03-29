package ru.filini.cloudstorage.DTO.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class AuthRequest {
    @NotBlank(message = "login must not be null")
    private String login;

    @NotBlank(message = "password must not be null")
    private String password;
}

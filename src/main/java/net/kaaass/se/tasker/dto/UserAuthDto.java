package net.kaaass.se.tasker.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserAuthDto {

    private String id;

    private String username;

    private String password;

    private List<String> roles;
}
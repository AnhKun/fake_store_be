package com.example.fakestore.dto.response;

import com.example.fakestore.entity.Role;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long userId;
    String email;
    String firstName;
    String lastName;
    String avatar;
}

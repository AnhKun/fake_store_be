package com.example.fakestore.dto.response;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Long categoryId;
    String name;
    String image;
}

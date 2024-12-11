package com.example.fakestore.dto.response;

import com.example.fakestore.entity.Category;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long productId;
    String title;
    double price;
    String description;
    CategoryResponse category;
    String image;

}

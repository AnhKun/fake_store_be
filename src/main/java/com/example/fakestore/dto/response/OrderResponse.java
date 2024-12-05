package com.example.fakestore.dto.response;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    LocalDateTime orderDate;
    UserResponse user;
    double totalAmount;
    String deliveryAddress;

}

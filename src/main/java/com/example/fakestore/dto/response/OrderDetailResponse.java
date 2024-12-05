package com.example.fakestore.dto.response;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    Long orderDetailId;
    OrderResponse order;
    ProductResponse product;
    int quantity;
    double subtotal;
}

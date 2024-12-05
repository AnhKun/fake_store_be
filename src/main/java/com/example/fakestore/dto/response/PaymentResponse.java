package com.example.fakestore.dto.response;

import com.example.fakestore.entity.PaymentMethod;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    Long paymentId;
    OrderResponse order;
    LocalDateTime paymentDate;
    PaymentMethod paymentMethod;
    Boolean status;
}

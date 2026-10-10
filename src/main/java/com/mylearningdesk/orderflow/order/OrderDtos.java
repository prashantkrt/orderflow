package com.mylearningdesk.orderflow.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {
    private OrderDtos() {}
    public record CreateOrderRequest(@NotBlank @Email String customerEmail,
                                     @NotEmpty List<@Valid CreateOrderItemRequest> items) {}
    public record CreateOrderItemRequest(@NotBlank @Size(max = 80) String productCode,
                                         @Min(1) @Max(10000) int quantity,
                                         @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal unitPrice) {}
    public record UpdateStatusRequest(@NotNull OrderStatus status) {}
    public record OrderItemResponse(String productCode, int quantity, BigDecimal unitPrice) {}
    public record OrderResponse(String id, String customerEmail, OrderStatus status,
                                BigDecimal totalAmount, Instant createdAt, List<OrderItemResponse> items) {}
}

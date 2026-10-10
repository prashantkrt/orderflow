package com.mylearningdesk.orderflow.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.mylearningdesk.orderflow.order.OrderDtos.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) { return service.create(request); }
    @GetMapping public List<OrderResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public OrderResponse findById(@PathVariable String id) { return service.findById(id); }
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
        return service.updateStatus(id, request);
    }
}

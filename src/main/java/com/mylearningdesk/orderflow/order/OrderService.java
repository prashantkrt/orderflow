package com.mylearningdesk.orderflow.order;

import com.mylearningdesk.orderflow.events.OrderEventPublisher;
import com.mylearningdesk.orderflow.events.avro.OrderCreated;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

import static com.mylearningdesk.orderflow.order.OrderDtos.*;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository repository, OrderEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(cacheNames = "allOrders", allEntries = true)
    public OrderResponse create(CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
            .map(item -> new OrderItem(item.productCode(), item.quantity(), item.unitPrice()))
            .toList();
        OrderDocument saved = repository.save(new OrderDocument(request.customerEmail(), items));
        OrderResponse response = toResponse(saved);

        // Starter implementation: production-grade delivery should use a transactional outbox
        // so the MongoDB write and Kafka event cannot silently diverge.
        OrderCreated event = OrderCreated.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setOrderId(saved.getId())
            .setCustomerEmail(saved.getCustomerEmail())
            .setTotalAmount(saved.getTotalAmount().toPlainString())
            .setCurrency("USD")
            .setOccurredAtEpochMillis(saved.getCreatedAt().toEpochMilli())
            .build();
        eventPublisher.publish(event);
        return response;
    }

    @Cacheable("allOrders")
    public List<OrderResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Cacheable(cacheNames = "orderById", key = "#id")
    public OrderResponse findById(String id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id)));
    }

    @CacheEvict(cacheNames = {"orderById", "allOrders"}, allEntries = true)
    public OrderResponse updateStatus(String id, UpdateStatusRequest request) {
        OrderDocument order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("A delivered or cancelled order cannot be updated.");
        }
        order.setStatus(request.status());
        return toResponse(repository.save(order));
    }

    private OrderResponse toResponse(OrderDocument order) {
        var items = order.getItems().stream()
            .map(item -> new OrderItemResponse(item.getProductCode(), item.getQuantity(), item.getUnitPrice()))
            .toList();
        return new OrderResponse(order.getId(), order.getCustomerEmail(), order.getStatus(),
            order.getTotalAmount(), order.getCreatedAt(), items);
    }
}

package com.mylearningdesk.orderflow.events;

import com.mylearningdesk.orderflow.events.avro.OrderCreated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {
    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final String topic;

    public OrderEventPublisher(
            KafkaTemplate<String, OrderCreated> kafkaTemplate,
            @Value("${orderflow.kafka.topics.order-created:order.created.v1}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(OrderCreated event) {
        kafkaTemplate.send(topic, event.getOrderId().toString(), event);
    }
}

package com.order_service.micro.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Retryable(retryFor = SQLException.class, maxAttemptsExpression = "${retry.maxRetryAttempts}",
            backoff = @Backoff(delayExpression = "${retry.waitDuration}"))
    @Transactional
    @Async
    @Override
    public Order createOrder(Order order) {
        Order saved = orderRepository.save(order);
        try {
            rabbitTemplate.convertAndSend("order-exchange", "order.created", order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish order", e);
        }
        return saved;
    }

    @Override
    public Order updateOrder(Long id, Order updatedOrder) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setProduct(updatedOrder.getProduct());
        order.setQuantity(updatedOrder.getQuantity());
        order.setPrice(updatedOrder.getPrice());
        order.setStatus(updatedOrder.getStatus());
        rabbitTemplate.convertAndSend("order-exchange", "order.updated", order);
        return orderRepository.save(order);

    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
        rabbitTemplate.convertAndSend("order-exchange", "order.deleted", id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}

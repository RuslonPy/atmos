package com.order_service.micro.order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order updateOrder(Long id, Order updatedOrder);
    void deleteOrder(Long id);
    List<Order> getAllOrders();
    Order getOrderById(Long id);
}

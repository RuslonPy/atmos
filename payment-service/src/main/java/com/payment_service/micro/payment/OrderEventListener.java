package com.payment_service.micro.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private PaymentService paymentService;

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(Order order) {
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice() * order.getQuantity());
        payment.setStatus("PENDING");
        paymentService.createPayment(payment);
    }

    @RabbitListener(queues = "order.updated.queue")
    public void handleOrderUpdated(Order order) {
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice() * order.getQuantity());
        payment.setStatus("PENDING");
        paymentService.createPayment(payment);
    }

    @RabbitListener(queues = "order.deleted.queue")
    public void handleOrderDeleted(List<Long> orderId) {
        List<Payment> payments = paymentService.findAllByOrderId(orderId);
        paymentService.deleteAll(payments);
    }
}


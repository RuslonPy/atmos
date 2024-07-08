package com.payment_service.micro.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Payment confirmPayment(Long Id);
    List<Payment> getAllPayments();
    Payment getPaymentById(Long Id);
    List<Payment> findAllByOrderId(List<Long> orderId);
    void deleteAll(List<Payment> payments);
}

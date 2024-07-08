package com.payment_service.micro.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    @Retryable(retryFor = SQLException.class, maxAttemptsExpression = "${retry.maxRetryAttempts}",
            backoff = @Backoff(delayExpression = "${retry.waitDuration}"))
    @Transactional
    @Async
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Retryable(retryFor = SQLException.class, maxAttemptsExpression = "${retry.maxRetryAttempts}",
            backoff = @Backoff(delayExpression = "${retry.waitDuration}"))
    @Transactional
    @Async
    public Payment confirmPayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus("CONFIRMED");
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public List<Payment> findAllByOrderId(List<Long> orderId) {
        return paymentRepository.findAllById(orderId);
    }

    public void deleteAll(List<Payment> payments) {
        paymentRepository.deleteAll(payments);
    }
}

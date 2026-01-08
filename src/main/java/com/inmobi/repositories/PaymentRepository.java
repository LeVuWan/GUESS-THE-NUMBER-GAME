package com.inmobi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inmobi.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByVnpTxnRef(String vnpTxnRef);
}

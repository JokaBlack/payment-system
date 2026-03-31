package com.joka.optima.repository;

import com.joka.optima.entity.PaymentSystem;
import com.joka.optima.enums.PaymentSystemCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentSystemRepository extends JpaRepository<PaymentSystem, Long> {

    Optional<PaymentSystem> findByCode (PaymentSystemCode code);
}

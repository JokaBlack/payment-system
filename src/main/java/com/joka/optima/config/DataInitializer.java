package com.joka.optima.config;

import com.joka.optima.entity.Client;
import com.joka.optima.entity.PaymentSystem;
import com.joka.optima.enums.PaymentSystemCode;
import com.joka.optima.repository.ClientRepository;
import com.joka.optima.repository.PaymentSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Инициализирует справочник платежных систем при запуске приложения.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PaymentSystemRepository paymentSystemRepository;
    private final ClientRepository clientRepository;

    @Override
    public void run(String... args) {
        createPaymentSystemIfNotExists(PaymentSystemCode.VISA, "Visa");
        createPaymentSystemIfNotExists(PaymentSystemCode.MASTERCARD, "MasterCard");
        createPaymentSystemIfNotExists(PaymentSystemCode.ELCART, "Elcart");
        createTestClientIfNotExists();
    }

    /**
     * Создает запись платежной системы, если она отсутствует в базе.
     */
    private void createPaymentSystemIfNotExists(PaymentSystemCode code, String name) {
        if (paymentSystemRepository.findByCode(code).isEmpty()) {
            PaymentSystem paymentSystem = new PaymentSystem();
            paymentSystem.setCode(code);
            paymentSystem.setName(name);
            paymentSystemRepository.save(paymentSystem);
        }
    }
    /**
     * Создает одного тестового клиента, если в базе еще нет клиентов.
     */
    private void createTestClientIfNotExists() {
        if (clientRepository.count() == 0) {
            Client client = new Client();
            client.setName("Test");
            client.setLastName("Client");
            clientRepository.save(client);
        }
    }
}

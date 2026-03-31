package com.joka.optima.strategy;

import com.joka.optima.enums.PaymentSystemCode;
import com.joka.optima.exception.UnsupportedPaymentSystemException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Компонент для выбора процессинговой стратегии по коду платежной системы.
 */
@Component
public class CardProcessingCenterStrategyResolver {

    private final Map<PaymentSystemCode, CardProcessingCenterStrategy> strategyMap;

    public CardProcessingCenterStrategyResolver(List<CardProcessingCenterStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        CardProcessingCenterStrategy::supportedSystem,
                        Function.identity()
                ));
    }

    /**
     * Возвращает стратегию для указанной платежной системы.
     */
    public CardProcessingCenterStrategy resolve(PaymentSystemCode paymentSystemCode) {
        CardProcessingCenterStrategy strategy = strategyMap.get(paymentSystemCode);

        if (strategy == null) {
            throw new UnsupportedPaymentSystemException(
                    "No processing strategy found for payment system: " + paymentSystemCode
            );
        }

        return strategy;
    }

}

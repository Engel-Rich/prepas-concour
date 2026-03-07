package com.mutrix.prepa.infrastructure.payment;

import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.infrastructure.payment.strategies.CAMPAYStrategy;
import com.mutrix.prepa.infrastructure.payment.strategies.MundiPayStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFactory {
    private  final MundiPayStrategy mundiPayStrategy;
    private final CAMPAYStrategy campayStrategy;

    public TransactionPaymentService create(String name){
        return switch (name) {
            case "CAMPAY" -> campayStrategy;
            case "MUNDY_PAY" -> mundiPayStrategy;
            default -> throw new RuntimeException("Payment provider does'nt exist ");
        };
    }
}

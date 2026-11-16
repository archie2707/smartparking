package com.smartparking.strategy;

import com.smartparking.model.Ticket;
import java.time.LocalDateTime;

public interface PricingStrategy {
    double calculateFee(Ticket ticket, LocalDateTime exitTime);
}
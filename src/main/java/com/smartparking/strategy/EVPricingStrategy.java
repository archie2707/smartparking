package com.smartparking.strategy;

import com.smartparking.model.EVCar;
import com.smartparking.model.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

public class EVPricingStrategy implements PricingStrategy {

    @Override
    public double calculateFee(Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        long billableHours = Math.max(1, (long) Math.ceil(minutes / 60.0));

        double parkingFee = billableHours * 40.0;
        double chargingFee = 0.0;

        if (ticket.getVehicle() instanceof EVCar evCar) {
            chargingFee = evCar.getBatteryCapacityKWh() * 4.0;
        }
        return parkingFee + chargingFee;
    }
}
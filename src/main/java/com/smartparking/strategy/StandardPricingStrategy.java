package com.smartparking.strategy;

import com.smartparking.model.Ticket;
import com.smartparking.model.VehicleType;
import java.time.Duration;
import java.time.LocalDateTime;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculateFee(Ticket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        long billableHours = Math.max(1, (long) Math.ceil(minutes / 60.0));

        double hourlyRate = (ticket.getVehicle().getVehicleType() == VehicleType.BIKE) ? 20.0 : 40.0;
        return billableHours * hourlyRate;
    }
}
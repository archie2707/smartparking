package com.smartparking.model;

public class Bike extends Vehicle {
    public Bike(String licensePlate, String ownerName) {
        super(licensePlate, ownerName, VehicleType.BIKE);
    }

    @Override
    public SlotType getRequiredSlotType() {
        return SlotType.COMPACT;
    }
}
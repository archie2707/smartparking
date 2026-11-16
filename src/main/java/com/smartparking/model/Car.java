package com.smartparking.model;

public class Car extends Vehicle {
    public Car(String licensePlate, String ownerName) {
        super(licensePlate, ownerName, VehicleType.CAR);
    }

    @Override
    public SlotType getRequiredSlotType() {
        return SlotType.STANDARD;
    }
}
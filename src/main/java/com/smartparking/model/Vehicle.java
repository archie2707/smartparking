package com.smartparking.model;

public abstract class Vehicle {
    private final String licensePlate;
    private final String ownerName;
    private final VehicleType vehicleType;

    public Vehicle(String licensePlate, String ownerName, VehicleType vehicleType) {
        this.licensePlate = licensePlate;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
    }

    // Abstract method: every subclass decides what slot type it needs (Polymorphism)
    public abstract SlotType getRequiredSlotType();

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {
        return vehicleType + " [" + licensePlate + " - " + ownerName + "]";
    }
}
package com.smartparking.model;

public class EVCar extends Vehicle {
    private final double batteryCapacityKWh;

    public EVCar(String licensePlate, String ownerName, double batteryCapacityKWh) {
        super(licensePlate, ownerName, VehicleType.EV_CAR);
        this.batteryCapacityKWh = batteryCapacityKWh;
    }

    public double getBatteryCapacityKWh() {
        return batteryCapacityKWh;
    }

    @Override
    public SlotType getRequiredSlotType() {
        return SlotType.EV_CHARGING;
    }
}
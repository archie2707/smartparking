package com.smartparking.model;

public class ParkingSlot implements Comparable<ParkingSlot> {
    private final String slotId;
    private final int floor;
    private final int distanceFromGate; // in meters
    private final SlotType slotType;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSlot(String slotId, int floor, int distanceFromGate, SlotType slotType) {
        this.slotId = slotId;
        this.floor = floor;
        this.distanceFromGate = distanceFromGate;
        this.slotType = slotType;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    public void assignVehicle(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }

    public void vacate() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }

    public String getSlotId() { return slotId; }
    public int getFloor() { return floor; }
    public int getDistanceFromGate() { return distanceFromGate; }
    public SlotType getSlotType() { return slotType; }
    public boolean isOccupied() { return isOccupied; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    // Sort slots by shortest distance from entry gate
    @Override
    public int compareTo(ParkingSlot other) {
        int distanceCompare = Integer.compare(this.distanceFromGate, other.distanceFromGate);
        if (distanceCompare != 0) {
            return distanceCompare;
        }
        return this.slotId.compareTo(other.slotId);
    }

    @Override
    public String toString() {
        String status = isOccupied ? "OCCUPIED (" + parkedVehicle.getLicensePlate() + ")" : "FREE";
        return String.format("[%s | Floor %d | %dm | %s | %s]",
                slotId, floor, distanceFromGate, slotType, status);
    }
}
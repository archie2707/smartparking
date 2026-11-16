package com.smartparking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSlot slot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double totalFee;
    private boolean isPaid;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSlot slot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public void completeTicket(LocalDateTime exitTime, double totalFee) {
        this.exitTime = exitTime;
        this.totalFee = totalFee;
        this.isPaid = true;
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSlot getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getTotalFee() { return totalFee; }
    public boolean isPaid() { return isPaid; }

    public String getFormattedEntryTime() {
        return entryTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
}
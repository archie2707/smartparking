package com.smartparking.service;

import com.smartparking.exception.InvalidTicketException;
import com.smartparking.exception.ParkingFullException;
import com.smartparking.model.*;
import com.smartparking.strategy.EVPricingStrategy;
import com.smartparking.strategy.PricingStrategy;
import com.smartparking.strategy.StandardPricingStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ParkingLotService {
    private final List<ParkingSlot> allSlots = new ArrayList<>();
    private final Map<SlotType, PriorityQueue<ParkingSlot>> availableSlots = new EnumMap<>(SlotType.class);
    private final Map<String, Ticket> activeTickets = new HashMap<>();
    private final Map<VehicleType, PricingStrategy> pricingStrategies = new EnumMap<>(VehicleType.class);
    private final ReentrantLock lock = new ReentrantLock();
    private int ticketCounter = 100;
    private double totalRevenue = 0.0;

    public ParkingLotService() {
        for (SlotType type : SlotType.values()) {
            availableSlots.put(type, new PriorityQueue<>());
        }

        PricingStrategy standardPricing = new StandardPricingStrategy();
        pricingStrategies.put(VehicleType.BIKE, standardPricing);
        pricingStrategies.put(VehicleType.CAR, standardPricing);
        pricingStrategies.put(VehicleType.EV_CAR, new EVPricingStrategy());

        initializeSlots();
    }

    private void initializeSlots() {
        // Floor 1 (Closer to entry gate)
        addSlot(new ParkingSlot("F1-B1", 1, 5, SlotType.COMPACT));
        addSlot(new ParkingSlot("F1-B2", 1, 10, SlotType.COMPACT));
        addSlot(new ParkingSlot("F1-C1", 1, 8, SlotType.STANDARD));
        addSlot(new ParkingSlot("F1-C2", 1, 15, SlotType.STANDARD));
        addSlot(new ParkingSlot("F1-EV1", 1, 12, SlotType.EV_CHARGING));
        addSlot(new ParkingSlot("F1-EV2", 1, 18, SlotType.EV_CHARGING));

        // Floor 2 (Further from entry gate)
        addSlot(new ParkingSlot("F2-B1", 2, 25, SlotType.COMPACT));
        addSlot(new ParkingSlot("F2-C1", 2, 22, SlotType.STANDARD));
        addSlot(new ParkingSlot("F2-C2", 2, 30, SlotType.STANDARD));
        addSlot(new ParkingSlot("F2-EV1", 2, 28, SlotType.EV_CHARGING));
    }

    public void addSlot(ParkingSlot slot) {
        allSlots.add(slot);
        availableSlots.get(slot.getSlotType()).offer(slot);
    }

    public Ticket parkVehicle(Vehicle vehicle) throws ParkingFullException {
        lock.lock();
        try {
            SlotType requiredType = vehicle.getRequiredSlotType();
            PriorityQueue<ParkingSlot> slotQueue = availableSlots.get(requiredType);

            if (slotQueue == null || slotQueue.isEmpty()) {
                throw new ParkingFullException("Parking Full! No available " + requiredType + " slots.");
            }

            ParkingSlot nearestSlot = slotQueue.poll();
            nearestSlot.assignVehicle(vehicle);

            String ticketId = "TKT-" + (++ticketCounter);
            Ticket ticket = new Ticket(ticketId, vehicle, nearestSlot);
            activeTickets.put(ticketId, ticket);

            return ticket;
        } finally {
            lock.unlock();
        }
    }

    public Ticket exitVehicle(String ticketId, int simulatedHours) throws InvalidTicketException {
        lock.lock();
        try {
            Ticket ticket = activeTickets.remove(ticketId.toUpperCase());
            if (ticket == null) {
                throw new InvalidTicketException("Invalid or already closed Ticket ID: " + ticketId);
            }

            LocalDateTime exitTime = (simulatedHours > 0)
                    ? ticket.getEntryTime().plusHours(simulatedHours)
                    : LocalDateTime.now();

            PricingStrategy strategy = pricingStrategies.get(ticket.getVehicle().getVehicleType());
            double fee = strategy.calculateFee(ticket, exitTime);

            ticket.completeTicket(exitTime, fee);
            totalRevenue += fee;

            ParkingSlot slot = ticket.getSlot();
            slot.vacate();
            availableSlots.get(slot.getSlotType()).offer(slot);

            return ticket;
        } finally {
            lock.unlock();
        }
    }

    public List<ParkingSlot> getAllSlots() {
        lock.lock();
        try {
            return new ArrayList<>(allSlots);
        } finally {
            lock.unlock();
        }
    }

    public List<Ticket> getActiveTickets() {
        lock.lock();
        try {
            return new ArrayList<>(activeTickets.values());
        } finally {
            lock.unlock();
        }
    }

    public double getTotalRevenue() {
        lock.lock();
        try {
            return totalRevenue;
        } finally {
            lock.unlock();
        }
    }
}

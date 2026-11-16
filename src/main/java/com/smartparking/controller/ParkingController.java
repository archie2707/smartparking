package com.smartparking.controller;

import com.smartparking.exception.InvalidTicketException;
import com.smartparking.exception.ParkingFullException;
import com.smartparking.model.*;
import com.smartparking.service.ParkingLotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingLotService parkingLotService;

    public ParkingController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    public record ParkRequest(VehicleType vehicleType, String licensePlate, String ownerName, Double batteryCapacityKWh) {}
    public record ExitRequest(String ticketId, Integer simulatedHours) {}

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        List<ParkingSlot> slots = parkingLotService.getAllSlots();
        long freeCount = slots.stream().filter(s -> !s.isOccupied()).count();
        long evFreeCount = slots.stream()
                .filter(s -> s.getSlotType() == SlotType.EV_CHARGING && !s.isOccupied())
                .count();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalSlots", slots.size());
        response.put("availableSlots", freeCount);
        response.put("occupiedSlots", slots.size() - freeCount);
        response.put("evAvailable", evFreeCount);
        response.put("totalRevenue", parkingLotService.getTotalRevenue());
        response.put("slots", slots);
        response.put("activeTickets", parkingLotService.getActiveTickets());
        return response;
    }

    @PostMapping("/park")
    public ResponseEntity<?> parkVehicle(@RequestBody ParkRequest request) {
        if (request.licensePlate() == null || request.licensePlate().isBlank() ||
                request.ownerName() == null || request.ownerName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "License plate and owner name are required."));
        }

        String plate = request.licensePlate().trim().toUpperCase();
        String owner = request.ownerName().trim();

        Vehicle vehicle = switch (request.vehicleType()) {
            case BIKE -> new Bike(plate, owner);
            case CAR -> new Car(plate, owner);
            case EV_CAR -> new EVCar(plate, owner, request.batteryCapacityKWh() != null ? request.batteryCapacityKWh() : 40.0);
        };

        try {
            Ticket ticket = parkingLotService.parkVehicle(vehicle);
            return ResponseEntity.ok(ticket);
        } catch (ParkingFullException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/exit")
    public ResponseEntity<?> exitVehicle(@RequestBody ExitRequest request) {
        try {
            int hours = request.simulatedHours() != null ? request.simulatedHours() : 0;
            Ticket receipt = parkingLotService.exitVehicle(request.ticketId().trim(), hours);
            return ResponseEntity.ok(receipt);
        } catch (InvalidTicketException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/concurrency-demo")
    public Map<String, Object> runConcurrencyDemo() {
        List<String> logs = Collections.synchronizedList(new ArrayList<>());

        Thread gateA = new Thread(() -> {
            try {
                Ticket t = parkingLotService.parkVehicle(new Car("MH-12-GATE-A", "Rohan (Gate A)"));
                logs.add("Gate A -> Safely assigned " + t.getSlot().getSlotId() + " (" + t.getTicketId() + ")");
            } catch (ParkingFullException e) {
                logs.add("Gate A -> " + e.getMessage());
            }
        });

        Thread gateB = new Thread(() -> {
            try {
                Ticket t = parkingLotService.parkVehicle(new Car("DL-04-GATE-B", "Ananya (Gate B)"));
                logs.add("Gate B -> Safely assigned " + t.getSlot().getSlotId() + " (" + t.getTicketId() + ")");
            } catch (ParkingFullException e) {
                logs.add("Gate B -> " + e.getMessage());
            }
        });

        gateA.start();
        gateB.start();
        try {
            gateA.join();
            gateB.join();
        } catch (InterruptedException ignored) {}

        return Map.of("logs", logs);
    }
}
package com.parking.service;

import com.parking.enums.SpotType;
import com.parking.enums.VehicleType;
import com.parking.model.ParkingSpot;
import com.parking.model.ParkingTicket;
import com.parking.model.Vehicle;
import com.parking.strategy.FeeCalculationStrategy;
import com.parking.strategy.HourlyFeeStrategy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingLotService {
    private final List<ParkingSpot> spots;
    private final Map<String, ParkingTicket> activeTickets;
    private final FeeCalculationStrategy feeStrategy;
    private final AtomicInteger ticketCounter;

    public ParkingLotService(int floors, int spotsPerFloor) {
        this.spots = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
        this.feeStrategy = new HourlyFeeStrategy();
        this.ticketCounter = new AtomicInteger(1);
        initializeSpots(floors, spotsPerFloor);
    }

    private void initializeSpots(int floors, int spotsPerFloor) {
        for (int floor = 1; floor <= floors; floor++) {
            for (int i = 0; i < spotsPerFloor; i++) {
                SpotType type = i % 3 == 0 ? SpotType.MOTORCYCLE : 
                               i % 3 == 1 ? SpotType.COMPACT : SpotType.LARGE;
                spots.add(new ParkingSpot("F" + floor + "-S" + i, type, floor));
            }
        }
    }

    public synchronized ParkingTicket parkVehicle(Vehicle vehicle) {
        SpotType requiredSpotType = mapVehicleToSpotType(vehicle.getType());
        ParkingSpot spot = findAvailableSpot(requiredSpotType);
        
        if (spot == null) {
            throw new RuntimeException("No available spot for vehicle type: " + vehicle.getType());
        }

        spot.occupy();
        String ticketId = "T" + ticketCounter.getAndIncrement();
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, spot);
        activeTickets.put(ticketId, ticket);
        
        return ticket;
    }

    public synchronized double exitVehicle(String ticketId) {
        ParkingTicket ticket = activeTickets.get(ticketId);
        if (ticket == null) {
            throw new RuntimeException("Invalid ticket ID: " + ticketId);
        }

        ticket.setExitTime(LocalDateTime.now());
        double fee = feeStrategy.calculateFee(ticket);
        ticket.setFee(fee);
        
        ticket.getSpot().release();
        activeTickets.remove(ticketId);
        
        return fee;
    }

    private ParkingSpot findAvailableSpot(SpotType requiredType) {
        return spots.stream()
                .filter(spot -> spot.getType() == requiredType && spot.isAvailable())
                .findFirst()
                .orElse(null);
    }

    private SpotType mapVehicleToSpotType(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> SpotType.MOTORCYCLE;
            case CAR -> SpotType.COMPACT;
            case BUS -> SpotType.LARGE;
        };
    }

    public Map<SpotType, Long> getAvailability() {
        Map<SpotType, Long> availability = new HashMap<>();
        for (SpotType type : SpotType.values()) {
            long count = spots.stream()
                    .filter(spot -> spot.getType() == type && spot.isAvailable())
                    .count();
            availability.put(type, count);
        }
        return availability;
    }
}

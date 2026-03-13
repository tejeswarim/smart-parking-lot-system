package com.parking.strategy;

import com.parking.model.ParkingTicket;

public interface FeeCalculationStrategy {
    double calculateFee(ParkingTicket ticket);
}

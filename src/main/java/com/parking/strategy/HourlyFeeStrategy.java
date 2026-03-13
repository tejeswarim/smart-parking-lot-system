package com.parking.strategy;

import com.parking.enums.VehicleType;
import com.parking.model.ParkingTicket;
import java.time.Duration;

public class HourlyFeeStrategy implements FeeCalculationStrategy {
    private static final double MOTORCYCLE_RATE = 5.0;
    private static final double CAR_RATE = 10.0;
    private static final double BUS_RATE = 20.0;

    @Override
    public double calculateFee(ParkingTicket ticket) {
        long hours = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toHours();
        if (hours == 0) hours = 1;

        VehicleType type = ticket.getVehicle().getType();
        double rate = switch (type) {
            case MOTORCYCLE -> MOTORCYCLE_RATE;
            case CAR -> CAR_RATE;
            case BUS -> BUS_RATE;
        };

        return hours * rate;
    }
}

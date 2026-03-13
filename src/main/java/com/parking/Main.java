package com.parking;

import com.parking.enums.VehicleType;
import com.parking.model.ParkingTicket;
import com.parking.model.Vehicle;
import com.parking.service.ParkingLotService;

public class Main {
    public static void main(String[] args) {
        ParkingLotService parkingLot = new ParkingLotService(3, 30);

        Vehicle car = new Vehicle("ABC123", VehicleType.CAR);
        Vehicle motorcycle = new Vehicle("XYZ789", VehicleType.MOTORCYCLE);

        ParkingTicket ticket1 = parkingLot.parkVehicle(car);
        System.out.println("Car parked at: " + ticket1.getSpot().getSpotId());

        ParkingTicket ticket2 = parkingLot.parkVehicle(motorcycle);
        System.out.println("Motorcycle parked at: " + ticket2.getSpot().getSpotId());

        System.out.println("\nAvailability: " + parkingLot.getAvailability());

        double fee1 = parkingLot.exitVehicle(ticket1.getTicketId());
        System.out.println("\nCar exit fee: $" + fee1);

        System.out.println("Updated availability: " + parkingLot.getAvailability());
    }
}

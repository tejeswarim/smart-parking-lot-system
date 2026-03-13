package com.parking.model;

import com.parking.enums.SpotStatus;
import com.parking.enums.SpotType;

public class ParkingSpot {
    private final String spotId;
    private final SpotType type;
    private final int floor;
    private SpotStatus status;

    public ParkingSpot(String spotId, SpotType type, int floor) {
        this.spotId = spotId;
        this.type = type;
        this.floor = floor;
        this.status = SpotStatus.AVAILABLE;
    }

    public synchronized boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    public synchronized void occupy() {
        this.status = SpotStatus.OCCUPIED;
    }

    public synchronized void release() {
        this.status = SpotStatus.AVAILABLE;
    }

    public String getSpotId() {
        return spotId;
    }

    public SpotType getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }
}

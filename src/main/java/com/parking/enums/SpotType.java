package com.parking.enums;

public enum SpotType {
    MOTORCYCLE(1),
    COMPACT(2),
    LARGE(4);

    private final int size;

    SpotType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}

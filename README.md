# Smart Parking Lot System - Low-Level Design

## Architecture Overview

### 1. Data Model

**Core Entities:**
- **Vehicle**: Represents a vehicle with license plate and type
- **ParkingSpot**: Represents a parking spot with ID, type, floor, and status
- **ParkingTicket**: Tracks parking session with entry/exit times and fee
- **ParkingLotService**: Main service managing parking operations

**Enums:**
- **VehicleType**: MOTORCYCLE, CAR, BUS
- **SpotType**: MOTORCYCLE, COMPACT, LARGE
- **SpotStatus**: AVAILABLE, OCCUPIED

### 2. Spot Allocation Algorithm

**Strategy:**
- Maps vehicle types to appropriate spot types (MOTORCYCLE → MOTORCYCLE, CAR → COMPACT, BUS → LARGE)
- Uses first-available algorithm: scans spots sequentially and assigns the first available matching spot
- Thread-safe allocation using synchronized methods

### 3. Fee Calculation Logic

**Strategy Pattern Implementation:**
- Interface: `FeeCalculationStrategy`
- Implementation: `HourlyFeeStrategy`
- Rates: Motorcycle ($5/hr), Car ($10/hr), Bus ($20/hr)
- Minimum charge: 1 hour

### 4. Concurrency Handling

**Thread-Safety Mechanisms:**
- `synchronized` methods in ParkingLotService for parkVehicle() and exitVehicle()
- `synchronized` methods in ParkingSpot for status changes
- `ConcurrentHashMap` for active tickets storage
- `AtomicInteger` for thread-safe ticket ID generation

### 5. Real-Time Availability

- `getAvailability()` method returns real-time count of available spots by type
- Updates automatically as vehicles enter/exit

## Database Schema (Conceptual)

```sql
-- Parking Spots Table
CREATE TABLE parking_spots (
    spot_id VARCHAR(20) PRIMARY KEY,
    spot_type VARCHAR(20) NOT NULL,
    floor INT NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Vehicles Table
CREATE TABLE vehicles (
    license_plate VARCHAR(20) PRIMARY KEY,
    vehicle_type VARCHAR(20) NOT NULL
);

-- Parking Tickets Table
CREATE TABLE parking_tickets (
    ticket_id VARCHAR(20) PRIMARY KEY,
    license_plate VARCHAR(20) REFERENCES vehicles(license_plate),
    spot_id VARCHAR(20) REFERENCES parking_spots(spot_id),
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    fee DECIMAL(10,2)
);
```

## Usage Example

```java
ParkingLotService parkingLot = new ParkingLotService(3, 30); // 3 floors, 30 spots each

Vehicle car = new Vehicle("ABC123", VehicleType.CAR);
ParkingTicket ticket = parkingLot.parkVehicle(car);

// Check availability
Map<SpotType, Long> availability = parkingLot.getAvailability();

// Exit and calculate fee
double fee = parkingLot.exitVehicle(ticket.getTicketId());
```

## Key Design Decisions

1. **Immutability**: Vehicle and core ticket fields are immutable for thread safety
2. **Strategy Pattern**: Allows flexible fee calculation strategies
3. **Synchronized Access**: Prevents race conditions during concurrent operations
4. **Simple Allocation**: First-available algorithm balances simplicity and efficiency
5. **In-Memory Storage**: Uses collections for fast access (can be replaced with database)

import java.util.*;

// ===================== MAIN CLASS =====================
public class UC9 {

    public static void main(String[] args) {

        HotelInventory inventory = new HotelInventory();
        BookingService service = new BookingService(inventory);

        // ✅ Valid booking
        service.processBooking(new BookingRequest("Amit", "SINGLE", 2));

        // ❌ Invalid room type
        service.processBooking(new BookingRequest("Riya", "SUITE", 1));

        // ❌ Invalid nights
        service.processBooking(new BookingRequest("John", "DOUBLE", 0));

        // ❌ Empty name
        service.processBooking(new BookingRequest("", "SINGLE", 1));

        // ❌ Exhaust inventory
        service.processBooking(new BookingRequest("Alex", "DELUXE", 1));
        service.processBooking(new BookingRequest("Sam", "DELUXE", 1));

        // Print final inventory
        inventory.printInventory();
    }
}

// ===================== ENUM =====================
enum RoomType {
    SINGLE, DOUBLE, DELUXE;

    public static RoomType fromString(String value) throws InvalidRoomTypeException {
        try {
            return RoomType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new InvalidRoomTypeException("Invalid room type: " + value);
        }
    }
}

// ===================== REQUEST MODEL =====================
class BookingRequest {
    String guestName;
    String roomType;
    int nights;

    public BookingRequest(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }
}

// ===================== VALIDATOR =====================
class BookingValidator {

    public static void validate(BookingRequest request, HotelInventory inventory)
            throws BookingException {

        if (request.guestName == null || request.guestName.trim().isEmpty()) {
            throw new InvalidBookingInputException("Guest name cannot be empty.");
        }

        if (request.nights <= 0) {
            throw new InvalidBookingInputException("Nights must be greater than zero.");
        }

        // Validate room type
        RoomType type = RoomType.fromString(request.roomType);

        // Check availability before booking
        if (!inventory.hasAvailability(type)) {
            throw new InsufficientInventoryException(
                    "No rooms available for: " + type);
        }
    }
}

// ===================== INVENTORY =====================
class HotelInventory {

    private Map<RoomType, Integer> rooms = new HashMap<>();

    public HotelInventory() {
        rooms.put(RoomType.SINGLE, 2);
        rooms.put(RoomType.DOUBLE, 2);
        rooms.put(RoomType.DELUXE, 1);
    }

    public boolean hasAvailability(RoomType type) {
        return rooms.getOrDefault(type, 0) > 0;
    }

    public void allocate(RoomType type) throws InsufficientInventoryException {
        int available = rooms.getOrDefault(type, 0);

        if (available <= 0) {
            throw new InsufficientInventoryException(
                    "Cannot allocate. No rooms left for: " + type);
        }

        rooms.put(type, available - 1);
    }

    public void printInventory() {
        System.out.println("\nFinal Inventory Status:");
        for (Map.Entry<RoomType, Integer> entry : rooms.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// ===================== SERVICE =====================
class BookingService {

    private HotelInventory inventory;

    public BookingService(HotelInventory inventory) {
        this.inventory = inventory;
    }

    public void processBooking(BookingRequest request) {

        try {
            // Step 1: Validate (Fail-Fast)
            BookingValidator.validate(request, inventory);

            // Step 2: Convert room type
            RoomType type = RoomType.fromString(request.roomType);

            // Step 3: Allocate room
            inventory.allocate(type);

            System.out.println("✅ Booking successful for " + request.guestName);

        } catch (BookingException e) {
            // Graceful failure
            System.out.println("❌ Booking failed: " + e.getMessage());
        }
    }
}

// ===================== CUSTOM EXCEPTIONS =====================
class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

class InvalidRoomTypeException extends BookingException {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class InvalidBookingInputException extends BookingException {
    public InvalidBookingInputException(String message) {
        super(message);
    }
}

class InsufficientInventoryException extends BookingException {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
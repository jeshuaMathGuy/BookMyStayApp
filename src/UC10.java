import java.util.*;

// ===================== MAIN =====================
public class UC10 {

    public static void main(String[] args) {

        HotelInventory inventory = new HotelInventory();
        BookingService service = new BookingService(inventory);

        // Create bookings
        String b1 = service.processBooking(new BookingRequest("Amit", "SINGLE", 2));
        String b2 = service.processBooking(new BookingRequest("Riya", "DOUBLE", 1));

        // Invalid booking
        service.processBooking(new BookingRequest("John", "SUITE", 1));

        // Cancel booking
        service.cancelBooking(b1);

        // Try cancelling again (should fail)
        service.cancelBooking(b1);

        // Cancel second booking
        service.cancelBooking(b2);

        // Final inventory
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

// ===================== MODEL =====================
class Booking {
    String bookingId;
    String guestName;
    RoomType roomType;
    boolean isCancelled;

    public Booking(String bookingId, String guestName, RoomType roomType) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.isCancelled = false;
    }
}

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
            throw new InvalidBookingInputException("Nights must be > 0.");
        }

        RoomType type = RoomType.fromString(request.roomType);

        if (!inventory.hasAvailability(type)) {
            throw new InsufficientInventoryException("No rooms available for: " + type);
        }
    }
}

// ===================== INVENTORY =====================
class HotelInventory {

    private Map<RoomType, Integer> rooms = new HashMap<>();

    // Stack for rollback (room IDs simulation)
    private Stack<String> rollbackStack = new Stack<>();

    public HotelInventory() {
        rooms.put(RoomType.SINGLE, 2);
        rooms.put(RoomType.DOUBLE, 2);
        rooms.put(RoomType.DELUXE, 1);
    }

    public boolean hasAvailability(RoomType type) {
        return rooms.getOrDefault(type, 0) > 0;
    }

    public String allocate(RoomType type) throws InsufficientInventoryException {
        int available = rooms.getOrDefault(type, 0);

        if (available <= 0) {
            throw new InsufficientInventoryException("No rooms left for: " + type);
        }

        rooms.put(type, available - 1);

        // Generate room ID and push to stack
        String roomId = type + "_ROOM_" + UUID.randomUUID().toString().substring(0, 5);
        rollbackStack.push(roomId);

        return roomId;
    }

    public void release(RoomType type) throws BookingException {
        if (rollbackStack.isEmpty()) {
            throw new BookingException("Rollback stack empty. Cannot release room.");
        }

        // LIFO rollback
        String releasedRoom = rollbackStack.pop();

        rooms.put(type, rooms.getOrDefault(type, 0) + 1);

        System.out.println("↩️ Rolled back Room: " + releasedRoom);
    }

    public void printInventory() {
        System.out.println("\nFinal Inventory:");
        for (Map.Entry<RoomType, Integer> entry : rooms.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// ===================== SERVICE =====================
class BookingService {

    private HotelInventory inventory;
    private Map<String, Booking> bookings = new HashMap<>();

    public BookingService(HotelInventory inventory) {
        this.inventory = inventory;
    }

    public String processBooking(BookingRequest request) {

        try {
            // Validate
            BookingValidator.validate(request, inventory);

            RoomType type = RoomType.fromString(request.roomType);

            // Allocate room
            String roomId = inventory.allocate(type);

            // Create booking
            String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 5);
            Booking booking = new Booking(bookingId, request.guestName, type);

            bookings.put(bookingId, booking);

            System.out.println("✅ Booking Confirmed: " + bookingId + " | Room: " + roomId);

            return bookingId;

        } catch (BookingException e) {
            System.out.println("❌ Booking failed: " + e.getMessage());
            return null;
        }
    }

    public void cancelBooking(String bookingId) {

        try {
            if (bookingId == null || !bookings.containsKey(bookingId)) {
                throw new BookingNotFoundException("Booking does not exist.");
            }

            Booking booking = bookings.get(bookingId);

            if (booking.isCancelled) {
                throw new BookingException("Booking already cancelled.");
            }

            // Controlled rollback
            inventory.release(booking.roomType);

            booking.isCancelled = true;

            System.out.println("🗑️ Booking Cancelled: " + bookingId);

        } catch (BookingException e) {
            System.out.println("❌ Cancellation failed: " + e.getMessage());
        }
    }
}

// ===================== EXCEPTIONS =====================
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

class BookingNotFoundException extends BookingException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}
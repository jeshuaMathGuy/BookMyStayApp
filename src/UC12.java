import java.io.*;
import java.util.*;

// ===================== MAIN =====================
public class UC12 {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();

        // 🔁 Load previous state (Recovery)
        SystemState state = persistence.loadState();

        BookingService service = new BookingService(state);

        // Perform operations
        String b1 = service.book("Amit", "SINGLE");
        String b2 = service.book("Riya", "DOUBLE");

        service.cancel(b1);

        service.printState();

        // 💾 Save state before shutdown
        persistence.saveState(state);

        System.out.println("\n💾 System state saved. Restart app to see recovery.");
    }
}

// ===================== SYSTEM STATE =====================
class SystemState implements Serializable {
    Map<RoomType, Integer> inventory = new HashMap<>();
    Map<String, Booking> bookings = new HashMap<>();

    public SystemState() {
        inventory.put(RoomType.SINGLE, 2);
        inventory.put(RoomType.DOUBLE, 2);
        inventory.put(RoomType.DELUXE, 1);
    }
}

// ===================== ENUM =====================
enum RoomType {
    SINGLE, DOUBLE, DELUXE;

    public static RoomType fromString(String value) throws Exception {
        return RoomType.valueOf(value.toUpperCase());
    }
}

// ===================== BOOKING =====================
class Booking implements Serializable {
    String bookingId;
    String guestName;
    RoomType roomType;
    boolean cancelled;

    public Booking(String bookingId, String guestName, RoomType roomType) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.cancelled = false;
    }
}

// ===================== SERVICE =====================
class BookingService {

    private SystemState state;

    public BookingService(SystemState state) {
        this.state = state;
    }

    public String book(String guest, String roomTypeStr) {

        try {
            RoomType type = RoomType.fromString(roomTypeStr);

            int available = state.inventory.getOrDefault(type, 0);

            if (available <= 0) {
                System.out.println("❌ No rooms available for " + guest);
                return null;
            }

            // Allocate
            state.inventory.put(type, available - 1);

            String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 5);
            Booking booking = new Booking(bookingId, guest, type);

            state.bookings.put(bookingId, booking);

            System.out.println("✅ Booking confirmed: " + bookingId);
            return bookingId;

        } catch (Exception e) {
            System.out.println("❌ Booking failed: " + e.getMessage());
            return null;
        }
    }

    public void cancel(String bookingId) {

        if (bookingId == null || !state.bookings.containsKey(bookingId)) {
            System.out.println("❌ Invalid booking ID");
            return;
        }

        Booking booking = state.bookings.get(bookingId);

        if (booking.cancelled) {
            System.out.println("❌ Already cancelled");
            return;
        }

        // Restore inventory
        RoomType type = booking.roomType;
        state.inventory.put(type, state.inventory.getOrDefault(type, 0) + 1);

        booking.cancelled = true;

        System.out.println("↩️ Booking cancelled: " + bookingId);
    }

    public void printState() {
        System.out.println("\n📊 Current Inventory: " + state.inventory);
        System.out.println("📘 Bookings: " + state.bookings.keySet());
    }
}

// ===================== PERSISTENCE =====================
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // 💾 Save state to file
    public void saveState(SystemState state) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(state);
            System.out.println("✅ State saved to file.");

        } catch (IOException e) {
            System.out.println("❌ Error saving state: " + e.getMessage());
        }
    }

    // 🔁 Load state from file
    public SystemState loadState() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("⚠️ No saved state found. Starting fresh.");
            return new SystemState();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            SystemState state = (SystemState) in.readObject();
            System.out.println("✅ State restored from file.");
            return state;

        } catch (Exception e) {
            System.out.println("⚠️ Corrupted file. Starting fresh.");
            return new SystemState();
        }
    }
}
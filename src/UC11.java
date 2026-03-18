import java.util.*;
import java.util.concurrent.*;

// ===================== MAIN =====================
public class UC11 {

    public static void main(String[] args) {

        HotelInventory inventory = new HotelInventory();

        // Shared booking queue
        BlockingQueue<BookingRequest> queue = new LinkedBlockingQueue<>();

        // Add multiple booking requests (Simulating concurrent users)
        queue.add(new BookingRequest("Amit", "SINGLE"));
        queue.add(new BookingRequest("Riya", "SINGLE"));
        queue.add(new BookingRequest("John", "SINGLE"));
        queue.add(new BookingRequest("Neha", "DOUBLE"));
        queue.add(new BookingRequest("Sam", "DOUBLE"));

        // Create worker threads
        int THREAD_COUNT = 3;

        for (int i = 1; i <= THREAD_COUNT; i++) {
            new Thread(new BookingProcessor(queue, inventory), "Worker-" + i).start();
        }
    }
}

// ===================== ENUM =====================
enum RoomType {
    SINGLE, DOUBLE, DELUXE;

    public static RoomType fromString(String value) throws Exception {
        return RoomType.valueOf(value.toUpperCase());
    }
}

// ===================== MODEL =====================
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
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

    // 🔒 Critical Section (Thread-safe)
    public synchronized boolean allocate(RoomType type, String guestName) {

        int available = rooms.getOrDefault(type, 0);

        if (available <= 0) {
            System.out.println("❌ No room available for " + guestName + " (" + type + ")");
            return false;
        }

        // Simulate delay (to expose race condition if not synchronized)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        rooms.put(type, available - 1);

        System.out.println("✅ Room allocated to " + guestName + " (" + type + ")");
        return true;
    }

    public synchronized void printInventory() {
        System.out.println("\nFinal Inventory: " + rooms);
    }
}

// ===================== PROCESSOR (THREAD) =====================
class BookingProcessor implements Runnable {

    private BlockingQueue<BookingRequest> queue;
    private HotelInventory inventory;

    public BookingProcessor(BlockingQueue<BookingRequest> queue, HotelInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {
            try {
                // Thread-safe retrieval from queue
                BookingRequest request = queue.poll(2, TimeUnit.SECONDS);

                if (request == null) {
                    // No more requests
                    inventory.printInventory();
                    break;
                }

                process(request);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void process(BookingRequest request) {

        try {
            RoomType type = RoomType.fromString(request.roomType);

            // 🔒 Critical section inside inventory
            inventory.allocate(type, request.guestName);

        } catch (Exception e) {
            System.out.println("❌ Invalid request for " + request.guestName);
        }
    }
}
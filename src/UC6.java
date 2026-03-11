import java.util.*;

/**
 * BookMyStayApp - UC6: Reservation Confirmation & Room Allocation
 *
 * Demonstrates processing queued booking requests and assigning unique room IDs.
 * Inventory counts are updated immediately to prevent double-booking.
 *
 * Author: Your Name
 * Version: 1.0
 */
public class UC6 {

    public static void main(String[] args) {
        HotelApplication app = new HotelApplication();
        app.start();
    }
}

class HotelApplication {

    public void start() {
        System.out.println("===== Book My Stay - Reservation Confirmation & Room Allocation =====");

        // Initialize inventory with room availability
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 2);

        // Initialize booking queue (simulating requests from UC5)
        BookingQueue bookingQueue = new BookingQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Diana", "Single Room"));

        // Process bookings
        while (!bookingQueue.isEmpty()) {
            Reservation res = bookingQueue.pollRequest();
            String roomId = inventory.allocateRoom(res.getRoomType());
            if (roomId != null) {
                System.out.println("Reservation Confirmed: " + res.getGuestName() +
                        " -> " + res.getRoomType() + " (Room ID: " + roomId + ")");
            } else {
                System.out.println("Reservation Failed (No availability): " + res.getGuestName() +
                        " -> " + res.getRoomType());
            }
        }

        System.out.println("\nFinal Inventory State:");
        inventory.displayInventory();

        System.out.println("\nApplication finished.");
    }
}

/**
 * Reservation represents a guest's booking intent
 */
class Reservation {
    private final String guestName;
    private final String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * BookingQueue manages incoming reservation requests (FIFO)
 */
class BookingQueue {
    private final Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        queue.add(reservation);
    }

    public Reservation pollRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

/**
 * InventoryService manages room availability and unique allocation
 */
class InventoryService {
    private final Map<String, Integer> availability = new HashMap<>();
    private final Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private final Random random = new Random();

    // Add room type with initial availability
    public void addRoomType(String roomType, int count) {
        availability.put(roomType, count);
        allocatedRooms.put(roomType, new HashSet<>());
    }

    // Allocate a unique room ID if available
    public String allocateRoom(String roomType) {
        int available = availability.getOrDefault(roomType, 0);
        if (available <= 0) return null; // no rooms left

        // Generate a unique room ID
        String roomId;
        Set<String> assigned = allocatedRooms.get(roomType);
        do {
            roomId = roomType.substring(0, 1).toUpperCase() + (random.nextInt(100) + 1);
        } while (assigned.contains(roomId));

        // Record allocation
        assigned.add(roomId);
        availability.put(roomType, available - 1);
        return roomId;
    }

    // Display current inventory
    public void displayInventory() {
        for (String roomType : availability.keySet()) {
            System.out.println(roomType + ": " + availability.get(roomType) + " available, Allocated IDs: " + allocatedRooms.get(roomType));
        }
    }
}
import java.util.*;

/**
 * BookMyStayApp - UC8: Booking History & Reporting
 *
 * Demonstrates storing confirmed bookings in a historical record and generating
 * reports for admin purposes, while preserving insertion order.
 */
public class UC8 {

    public static void main(String[] args) {
        HotelApplication app = new HotelApplication();
        app.start();
    }
}

class HotelApplication {

    public void start() {
        System.out.println("===== Book My Stay - Booking History & Reporting =====");

        // Initialize inventory and booking queue
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);

        BookingQueue bookingQueue = new BookingQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Single Room"));

        // Initialize booking history
        BookingHistory history = new BookingHistory();

        // Process bookings and store in history
        while (!bookingQueue.isEmpty()) {
            Reservation res = bookingQueue.pollRequest();
            String roomId = inventory.allocateRoom(res.getRoomType());
            if (roomId != null) {
                res.setRoomId(roomId);
                history.addReservation(res);
                System.out.println("Reservation Confirmed: " + res.getGuestName() +
                        " -> " + res.getRoomType() + " (Room ID: " + roomId + ")");
            }
        }

        // Admin generates booking report
        BookingReportService reportService = new BookingReportService(history);
        System.out.println("\nBooking Report:");
        reportService.generateReport();

        System.out.println("Application finished.");
    }
}

// ---------------- Core Booking Classes ----------------

class Reservation {
    private final String guestName;
    private final String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}

class BookingQueue {
    private final Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation reservation) { queue.add(reservation); }
    public Reservation pollRequest() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}

class InventoryService {
    private final Map<String, Integer> availability = new HashMap<>();
    private final Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private final Random random = new Random();

    public void addRoomType(String roomType, int count) {
        availability.put(roomType, count);
        allocatedRooms.put(roomType, new HashSet<>());
    }

    public String allocateRoom(String roomType) {
        int available = availability.getOrDefault(roomType, 0);
        if (available <= 0) return null;

        String roomId;
        Set<String> assigned = allocatedRooms.get(roomType);
        do {
            roomId = roomType.substring(0, 1).toUpperCase() + (random.nextInt(100) + 1);
        } while (assigned.contains(roomId));

        assigned.add(roomId);
        availability.put(roomType, available - 1);
        return roomId;
    }
}

// ---------------- Booking History & Reporting ----------------

class BookingHistory {
    private final List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(reservations);
    }
}

class BookingReportService {
    private final BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void generateReport() {
        List<Reservation> reservations = history.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        for (Reservation res : reservations) {
            System.out.println(res.getGuestName() + " booked " + res.getRoomType() +
                    " (Room ID: " + res.getRoomId() + ")");
        }
    }
}
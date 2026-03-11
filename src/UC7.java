import java.util.*;

/**
 * BookMyStayApp - UC7: Add-On Service Selection
 *
 * Demonstrates how guests can select optional services (e.g., breakfast, spa, airport pickup)
 * for confirmed reservations. Core booking and inventory remain unchanged.
 */
public class UC7 {

    public static void main(String[] args) {
        HotelApplication app = new HotelApplication();
        app.start();
    }
}

class HotelApplication {

    public void start() {
        System.out.println("===== Book My Stay - Add-On Service Selection =====");

        // Initialize inventory and booking queue
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);

        BookingQueue bookingQueue = new BookingQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));

        // Process bookings
        List<Reservation> confirmed = new ArrayList<>();
        while (!bookingQueue.isEmpty()) {
            Reservation res = bookingQueue.pollRequest();
            String roomId = inventory.allocateRoom(res.getRoomType());
            if (roomId != null) {
                res.setRoomId(roomId);
                confirmed.add(res);
                System.out.println("Reservation Confirmed: " + res.getGuestName() +
                        " -> " + res.getRoomType() + " (Room ID: " + roomId + ")");
            }
        }

        // Initialize add-on service manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Guest selects add-on services
        serviceManager.addService(confirmed.get(0).getRoomId(), new Service("Breakfast", 15.0));
        serviceManager.addService(confirmed.get(0).getRoomId(), new Service("Airport Pickup", 30.0));
        serviceManager.addService(confirmed.get(1).getRoomId(), new Service("Spa Session", 50.0));

        // Display add-on selections
        System.out.println("\nAdd-On Services Summary:");
        for (Reservation res : confirmed) {
            List<Service> services = serviceManager.getServices(res.getRoomId());
            double total = services.stream().mapToDouble(Service::getCost).sum();
            System.out.println(res.getGuestName() + " (" + res.getRoomType() + "):");
            for (Service s : services) {
                System.out.println("  - " + s.getName() + " ($" + s.getCost() + ")");
            }
            System.out.println("  Total Add-On Cost: $" + total + "\n");
        }

        System.out.println("Application finished.");
    }
}

// ---------------- Core Booking Classes ----------------

class Reservation {
    private final String guestName;
    private final String roomType;
    private String roomId; // Assigned after allocation

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

// ---------------- Add-On Service Classes ----------------

class Service {
    private final String name;
    private final double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() { return name; }
    public double getCost() { return cost; }
}

class AddOnServiceManager {
    private final Map<String, List<Service>> reservationServices = new HashMap<>();

    public void addService(String roomId, Service service) {
        reservationServices.computeIfAbsent(roomId, k -> new ArrayList<>()).add(service);
    }

    public List<Service> getServices(String roomId) {
        return reservationServices.getOrDefault(roomId, new ArrayList<>());
    }
}
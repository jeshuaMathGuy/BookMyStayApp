/**
 * BookMyStayApp - UC4: Room Search & Availability Check
 *
 * Demonstrates read-only search of available rooms.
 * Only rooms with availability > 0 are displayed.
 *
 * Author: Your Name
 * Version: 1.0
 */
import java.util.HashMap;
import java.util.Map;

public class UC4 {

    public static void main(String[] args) {
        HotelApplication app = new HotelApplication();
        app.start();
    }
}

class HotelApplication {

    public void start() {
        System.out.println("===== Book My Stay - Room Search =====");

        // Create room objects
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Initialize centralized inventory (same as UC3)
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom(singleRoom, 5);
        inventory.addRoom(doubleRoom, 0); // simulate zero availability
        inventory.addRoom(suiteRoom, 2);

        // Perform a search
        RoomSearchService searchService = new RoomSearchService(inventory);

        System.out.println("\nAvailable Rooms:");
        for (Room room : new Room[]{singleRoom, doubleRoom, suiteRoom}) {
            if (searchService.isAvailable(room)) {
                room.displayRoomDetails();
                System.out.println("Available: " + inventory.getAvailability(room) + "\n");
            }
        }

        System.out.println("Application finished.");
    }
}

/**
 * Search service provides read-only access to inventory
 */
class RoomSearchService {
    private final RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public boolean isAvailable(Room room) {
        return inventory.getAvailability(room) > 0;
    }
}

/**
 * Centralized room inventory management (UC3)
 */
class RoomInventory {
    private final Map<String, Integer> availabilityMap = new HashMap<>();

    public void addRoom(Room room, int count) {
        availabilityMap.put(room.getRoomType(), count);
    }

    public int getAvailability(Room room) {
        return availabilityMap.getOrDefault(room.getRoomType(), 0);
    }

    public void updateAvailability(Room room, int newCount) {
        availabilityMap.put(room.getRoomType(), newCount);
    }
}

/**
 * Abstract Room class
 */
abstract class Room {

    private final String roomType;
    private final int beds;
    private final double price;

    public Room(String roomType, int beds, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Beds: " + beds);
        System.out.println("Price: $" + price);
    }
}

/**
 * Concrete room types
 */
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 100.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 180.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 350.0);
    }
}
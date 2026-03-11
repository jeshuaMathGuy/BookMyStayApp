/**
 * BookMyStayApp - UC3: Centralized Room Inventory Management
 *
 * Demonstrates centralized room availability using HashMap.
 * Displays room details and current availability.
 *
 * Author: Your Name
 * Version: 1.0
 */
import java.util.HashMap;
import java.util.Map;

public class UC3 {

    public static void main(String[] args) {
        HotelApplication app = new HotelApplication();
        app.start();
    }
}

/**
 * HotelApplication class handles the application flow.
 */
class HotelApplication {

    public void start() {
        System.out.println("===== Book My Stay - Room Availability =====");

        // Create room objects
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom(singleRoom, 5);
        inventory.addRoom(doubleRoom, 3);
        inventory.addRoom(suiteRoom, 2);

        // Display room info and availability from centralized inventory
        for (Room room : new Room[]{singleRoom, doubleRoom, suiteRoom}) {
            System.out.println("\n--- " + room.getRoomType() + " ---");
            room.displayRoomDetails();
            System.out.println("Available: " + inventory.getAvailability(room));
        }

        System.out.println("\nApplication finished.");
    }
}

/**
 * Centralized room inventory management
 */
class RoomInventory {
    private final Map<String, Integer> availabilityMap = new HashMap<>();

    // Register a room type and its availability
    public void addRoom(Room room, int count) {
        availabilityMap.put(room.getRoomType(), count);
    }

    // Retrieve availability for a given room
    public int getAvailability(Room room) {
        return availabilityMap.getOrDefault(room.getRoomType(), 0);
    }

    // Update availability (for future use cases like booking)
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
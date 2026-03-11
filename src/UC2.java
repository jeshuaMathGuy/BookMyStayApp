
public class UC2{

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

        // Static availability for each room type
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // Display room info
        System.out.println("\n--- Single Room ---");
        singleRoom.displayRoomDetails();
        System.out.println("Available: " + singleAvailable);

        System.out.println("\n--- Double Room ---");
        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleAvailable);

        System.out.println("\n--- Suite Room ---");
        suiteRoom.displayRoomDetails();
        System.out.println("Available: " + suiteAvailable);

        System.out.println("\nApplication finished.");
    }
}

/**
 * Abstract Room class - cannot be instantiated directly.
 * Holds common properties for all room types.
 */
abstract class Room {

    private String roomType;
    private int beds;
    private double price;

    public Room(String roomType, int beds, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.price = price;
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
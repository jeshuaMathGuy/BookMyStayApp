import java.util.LinkedList;
import java.util.Queue;

/**
 * BookMyStayApp - UC5: Booking Request Queue (FIFO)
 *
 * Demonstrates intake of booking requests using a queue.
 * Rooms are not allocated yet; requests preserve arrival order.
 *
 * Author: Your Name
 * Version: 1.0
 */
public class UC5{

    public static void main(String[] args) {
        HotelApplication app = new HotelApplication();
        app.start();
    }
}

class HotelApplication {

    public void start() {
        System.out.println("===== Book My Stay - Booking Request Queue =====");

        // Initialize queue for reservations
        BookingQueue bookingQueue = new BookingQueue();

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room"));

        // Display queued requests in FIFO order
        System.out.println("\nQueued Booking Requests:");
        while (!bookingQueue.isEmpty()) {
            Reservation res = bookingQueue.pollRequest();
            System.out.println(res.getGuestName() + " requested: " + res.getRoomType());
        }

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
 * BookingQueue manages incoming reservation requests
 */
class BookingQueue {
    private final Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        queue.add(reservation);
        System.out.println("Request added: " + reservation.getGuestName() + " -> " + reservation.getRoomType());
    }

    public Reservation pollRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
import java.util.*;

class Reservation {

    private String guestName;
    private String roomType;

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

class RoomInventory {

    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();

        roomAvailability.put("Single", 2);
        roomAvailability.put("Double", 2);
        roomAvailability.put("Suite", 1);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void decreaseRoom(String roomType) {
        int count = roomAvailability.get(roomType);
        roomAvailability.put(roomType, count - 1);
    }
}

class RoomAllocationService {

    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;

    public RoomAllocationService() {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {

        String roomType = reservation.getRoomType();

        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.get(roomType) > 0) {

            String roomId = generateRoomId(roomType);

            allocatedRoomIds.add(roomId);

            assignedRoomsByType
                    .computeIfAbsent(roomType, k -> new HashSet<>())
                    .add(roomId);

            inventory.decreaseRoom(roomType);

            System.out.println(
                    "Booking Confirmed for " +
                            reservation.getGuestName() +
                            " | Room Type: " +
                            roomType +
                            " | Room ID: " +
                            roomId
            );

        } else {
            System.out.println(
                    "No " + roomType + " rooms available for " +
                            reservation.getGuestName()
            );
        }
    }

    private String generateRoomId(String roomType) {

        int number = assignedRoomsByType
                .getOrDefault(roomType, new HashSet<>())
                .size() + 1;

        return roomType.substring(0,1).toUpperCase() + number;
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Room Allocation System\n");

        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocator = new RoomAllocationService();

        Queue<Reservation> bookingQueue = new LinkedList<>();

        bookingQueue.add(new Reservation("Anil", "Single"));
        bookingQueue.add(new Reservation("John", "Double"));
        bookingQueue.add(new Reservation("Vasanth", "Suite"));
        bookingQueue.add(new Reservation("Ravi", "Single"));

        while (!bookingQueue.isEmpty()) {

            Reservation reservation = bookingQueue.poll();

            allocator.allocateRoom(reservation, inventory);
        }
    }
}
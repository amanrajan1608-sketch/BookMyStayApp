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

            System.out.println("Booking Confirmed for " +
                    reservation.getGuestName() +
                    " | Room Type: " + roomType +
                    " | Room ID: " + roomId);

        } else {
            System.out.println("No " + roomType +
                    " rooms available for " +
                    reservation.getGuestName());
        }
    }

    private String generateRoomId(String roomType) {
        int number = assignedRoomsByType
                .getOrDefault(roomType, new HashSet<>())
                .size() + 1;

        return roomType.substring(0, 1).toUpperCase() + number;
    }
}

class AddOnService {

    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }
}

class AddOnServiceManager {

    private Map<String, List<AddOnService>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    public void addService(String reservationId, AddOnService service) {
        servicesByReservation
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public double calculateTotalServiceCost(String reservationId) {

        List<AddOnService> services =
                servicesByReservation.getOrDefault(reservationId, new ArrayList<>());

        double total = 0;

        for (AddOnService service : services) {
            total += service.getCost();
        }

        return total;
    }
}

class BookingHistory {

    private List<Reservation> confirmedReservations;

    public BookingHistory() {
        confirmedReservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return confirmedReservations;
    }
}

class BookingReportService {

    public void generateReport(BookingHistory history) {

        System.out.println("\n--- Booking Report ---");

        List<Reservation> list = history.getAllReservations();

        for (Reservation r : list) {
            System.out.println("Guest: " + r.getGuestName() +
                    " | Room Type: " + r.getRoomType());
        }

        System.out.println("Total Bookings: " + list.size());
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Full Booking System\n");

        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocator = new RoomAllocationService();
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        Queue<Reservation> bookingQueue = new LinkedList<>();

        bookingQueue.add(new Reservation("Anil", "Single"));
        bookingQueue.add(new Reservation("John", "Double"));
        bookingQueue.add(new Reservation("Vasanth", "Suite"));
        bookingQueue.add(new Reservation("Ravi", "Single"));

        int idCounter = 1;

        while (!bookingQueue.isEmpty()) {

            Reservation reservation = bookingQueue.poll();
            String reservationId = "R" + idCounter++;

            allocator.allocateRoom(reservation, inventory);

            serviceManager.addService(reservationId, new AddOnService("Breakfast", 200));
            serviceManager.addService(reservationId, new AddOnService("WiFi", 100));

            double totalCost = serviceManager.calculateTotalServiceCost(reservationId);

            System.out.println("Total Add-On Cost for " +
                    reservation.getGuestName() +
                    " = " + totalCost + "\n");

            history.addReservation(reservation);
        }

        reportService.generateReport(history);
    }
}
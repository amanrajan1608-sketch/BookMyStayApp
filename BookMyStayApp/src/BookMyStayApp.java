import java.util.*;

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

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

    public boolean isAvailable(String roomType) {
        return roomAvailability.containsKey(roomType) &&
                roomAvailability.get(roomType) > 0;
    }
}

class ReservationValidator {

    public void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty");
        }

        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty");
        }

        if (!inventory.getRoomAvailability().containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type selected");
        }

        if (!inventory.isAvailable(roomType)) {
            throw new InvalidBookingException("Selected room not available");
        }
    }
}

class RoomAllocationService {

    private Map<String, Set<String>> assignedRoomsByType = new HashMap<>();

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {

        String roomType = reservation.getRoomType();

        String roomId = generateRoomId(roomType);

        assignedRoomsByType
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        inventory.decreaseRoom(roomType);

        System.out.println("Booking Confirmed for " +
                reservation.getGuestName() +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId);
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

    public double getCost() {
        return cost;
    }
}

class AddOnServiceManager {

    private Map<String, List<AddOnService>> servicesByReservation = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        servicesByReservation
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public double calculateTotalServiceCost(String reservationId) {

        List<AddOnService> services =
                servicesByReservation.getOrDefault(reservationId, new ArrayList<>());

        double total = 0;

        for (AddOnService s : services) {
            total += s.getCost();
        }

        return total;
    }
}

class BookingHistory {

    private List<Reservation> confirmedReservations = new ArrayList<>();

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

        for (Reservation r : history.getAllReservations()) {
            System.out.println("Guest: " + r.getGuestName() +
                    " | Room Type: " + r.getRoomType());
        }

        System.out.println("Total Bookings: " +
                history.getAllReservations().size());
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Booking System with Validation\n");

        Scanner scanner = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        RoomAllocationService allocator = new RoomAllocationService();
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        try {

            System.out.print("Enter Guest Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Room Type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            validator.validate(name, roomType, inventory);

            Reservation reservation = new Reservation(name, roomType);
            String reservationId = "R1";

            allocator.allocateRoom(reservation, inventory);

            serviceManager.addService(reservationId, new AddOnService("Breakfast", 200));
            serviceManager.addService(reservationId, new AddOnService("WiFi", 100));

            double cost = serviceManager.calculateTotalServiceCost(reservationId);

            System.out.println("Total Add-On Cost = " + cost);

            history.addReservation(reservation);

            reportService.generateReport(history);

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
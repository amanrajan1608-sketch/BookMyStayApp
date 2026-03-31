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
        roomAvailability.put(roomType, roomAvailability.get(roomType) - 1);
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
            throw new InvalidBookingException("Invalid room type");
        }

        if (!inventory.isAvailable(roomType)) {
            throw new InvalidBookingException("Room not available");
        }
    }
}

class RoomAllocationService {

    private Map<String, Set<String>> assignedRoomsByType = new HashMap<>();

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {

        String roomType = reservation.getRoomType();

        if (!inventory.isAvailable(roomType)) {
            System.out.println("No " + roomType + " rooms available for " +
                    reservation.getGuestName());
            return;
        }

        String roomId = generateRoomId(roomType);

        assignedRoomsByType
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        inventory.decreaseRoom(roomType);

        System.out.println("Booking Confirmed: " +
                reservation.getGuestName() +
                " | " + roomType +
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
    private Map<String, List<AddOnService>> services = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        services.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    public double calculateTotalServiceCost(String reservationId) {
        double total = 0;
        for (AddOnService s : services.getOrDefault(reservationId, new ArrayList<>())) {
            total += s.getCost();
        }
        return total;
    }
}

class BookingHistory {
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation r) {
        reservations.add(r);
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }
}

class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\n--- Booking Report ---");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r.getGuestName() + " - " + r.getRoomType());
        }
        System.out.println("Total Bookings: " + history.getAllReservations().size());
    }
}

class CancellationService {

    private List<String> cancelledBookings = new ArrayList<>();
    private Map<String, String> reservationMap = new HashMap<>();

    public void registerBooking(String reservationId, String roomType) {
        reservationMap.put(reservationId, roomType);
    }

    public void cancelBooking(String reservationId, RoomInventory inventory) {

        if (!reservationMap.containsKey(reservationId)) {
            System.out.println("Invalid Reservation ID");
            return;
        }

        String roomType = reservationMap.get(reservationId);

        cancelledBookings.add(reservationId);

        Map<String, Integer> availability = inventory.getRoomAvailability();
        availability.put(roomType, availability.get(roomType) + 1);

        System.out.println("Booking Cancelled: " + reservationId);
    }

    public void showCancellationHistory() {
        System.out.println("\n--- Cancellation History ---");
        for (String id : cancelledBookings) {
            System.out.println(id);
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        RoomAllocationService allocator = new RoomAllocationService();
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();
        CancellationService cancelService = new CancellationService();

        try {

            System.out.print("Enter Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Room Type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            validator.validate(name, roomType, inventory);

            Reservation reservation = new Reservation(name, roomType);
            String reservationId = "R1";

            allocator.allocateRoom(reservation, inventory);

            cancelService.registerBooking(reservationId, roomType);

            serviceManager.addService(reservationId, new AddOnService("Breakfast", 200));
            serviceManager.addService(reservationId, new AddOnService("WiFi", 100));

            double totalCost = serviceManager.calculateTotalServiceCost(reservationId);
            System.out.println("Add-On Cost: " + totalCost);

            history.addReservation(reservation);

            System.out.print("Cancel booking? (yes/no): ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("yes")) {
                cancelService.cancelBooking(reservationId, inventory);
            }

            cancelService.showCancellationHistory();
            reportService.generateReport(history);

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
public abstract class BookMyStayApp {

    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public BookMyStayApp(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Number of Beds: " + numberOfBeds);
        System.out.println("Room Size (sq ft): " + squareFeet);
        System.out.println("Price per Night: " + pricePerNight);
    }

    public static void main(String[] args) {

        BookMyStayApp room = new BookMyStayApp(2, 350, 2500.0) {};

        room.displayRoomDetails();
    }
}

/**
 * The RentalCarCompany class represents a rental car company.
 * It maintains an array of Car objects representing current rentals, and provides methods to add reservations, calculate totals and averages, and search for reservations by car number.
 *
 * @author Enaya Laborn 
 * @version 1.0
 */
public class RentalCarCompany {

    // Constants declarations
 
    /* Maximum number of cars the company can rent at singular time. */
    public static final int NUM_CARS = 20;
 
    // Declare instance variables
 
    /* Array of Car objects representing current rentals. */
    private Car[] theCars;
 
    /* The name of the rental car company. */
    private String name;
 
    /* The number of cars currently rented (active entries in theCars). */
    private int rentCnt;
 
    // Constructors!!
 
    // Default constructor. Initializes the car array and sets defaults.
  
    public RentalCarCompany() {
        theCars = new Car[NUM_CARS];
        name = "";
        rentCnt = 0;
    }
 
    /* Parameterized constructor. Sets the company name and initializes the car array.
     *
     * @param name the name of the rental car company
     */
    
    public RentalCarCompany(String name) {
        theCars = new Car[NUM_CARS];
        this.name = name;
        rentCnt = 0;
    }
 
    // Mutator Methods
 
    /* @return the name of the company.
     */
    public String getName() {
        return name;
    }
 
    /*Sets the name of the company.
 
     * @param name the company name
     */
    public void setName(String name) {
        this.name = name;
    }
 
    // Operational Methods
 
    /* Adds a new car reservation to the array using a partially filled array approach.
     * rentCnt tracks the number of active reservations and serves as the next insert index.
     * Prints message if the array is full and the reservation cannot be added.
     *
     * @param carRenter the name of the car renter
     * @param carNum    the unique car ID number
     * @param carName   the model name of the car
     * @param carType   the type/category of the car
     * @param rate      the daily rental rate
     * @param days      the number of days rented
     */
    
    public void addReservation(String carRenter, int carNum, String carName, String carType, double rate, int days) {
        if (rentCnt < NUM_CARS) {
            theCars[rentCnt] = new Car(carRenter, carNum, carName, carType, rate, days);
            rentCnt++;
        } else {
            System.out.println("Reservation could not be added. The company has reached maximum capacity.");
        }
    }
 
    /* Calculates and returns the total rental sales for all current reservations.
     * Total sales is the sum of (rate * days) for each Car in the array.
     *
     * @return the total rental sales as a double
     */
    
    public double getTotalRentalSales() {
        double totalSales = 0.0;
 
        for (int i = 0; i < rentCnt; i++) {
            totalSales += theCars[i].getCarRate() * theCars[i].getNumOfDays();
        }
        return totalSales;
    }
 
    /* Calculates and returns the average number of days cars are rented.
     * Returns 0 if no cars are currently rented.
     * @return the average number of days rented as a double
     */
    
    public double getAvgDays() {
        if (rentCnt == 0) {
            return 0;
        }
 
        double totalDays = 0.0;
 
        for (int i = 0; i < rentCnt; i++) {
            totalDays += theCars[i].getNumOfDays();
        }
        return totalDays / rentCnt;
    }
 
    /* Calculates and returns the average rental rate across all current reservations.
     * Returns 0 if no cars are currently rented.
     *
     * @return the average rental rate as a double
     */
    
    public double getAvgRate() {
        if (rentCnt == 0) {
            return 0;
        }
 
        double totalRate = 0.0;
 
        for (int i = 0; i < rentCnt; i++) {
            totalRate += theCars[i].getCarRate();
        }
        return totalRate / rentCnt;
    }
 
    /* Searches for a reservation by car number.
     * If found, prints the car's full information using its toString method.
     * If not found, prints an error message indicating reservation not found.
     *
     * @param carNum the car ID number to search for
     */
    
    public void findReservation(int carNum) {
        boolean found = false;
 
        for (int i = 0; i < rentCnt; i++) {						// Search through active reservations for matching car number
            if (theCars[i].getCarNum() == carNum) {
                System.out.println("Found reservation for car number: " + carNum);
                System.out.println("Car information:");
                System.out.println(theCars[i].toString());
                found = true;
                break;
            }
        }
        if (!found) {									//action if not found
            System.out.println("Could not find reservation for this car number: " + carNum);
        }
    }
 
    /* Returns "result" or a formatted string listing all current rental reservations, preceded by the company name. Each car's information is printed using the Car toString method.
     *
     * @return a string representation of the RentalCarCompany and all its cars
     */
 
    public String toString() {
        String result = "Rental Car Company: " + name + "\n";
 
        for (int i = 0; i < rentCnt; i++) {
            result += theCars[i].toString() + "\n\n";
        }
        return result;
    }
}
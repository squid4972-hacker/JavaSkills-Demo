
/**
 * This class represents a rental car reservation.
 * It stores information about the renter, the car, the rental rate, & the number of days the car is rented.
 *
 * @author Enaya Laborn
 * @version 1.0
 */
public class Car {
    // Instance Variables below
 
    // The name of the person renting the car.
    private String carRenter;
 
    // The unique ID number assigned to the car.
    private int carNum;
 
    // The model name of the car.
    private String carName;
 
    // The type/category of the car (compact, SUV, etc.)
    private String carType;
 
    // The daily rental rate for the car. 
    private double rate;
 
    // The number of days the car is rented.
    private int days;
 
    // Constructors
 
    // Default constructor. Initializes all instance variables to default values.

    public Car() {
        carRenter = "";
        carNum = 0;
        carName = "";
        carType = "";
        rate = 0.0;
        days = 0;}
 
    /*Parameterized constructor. Initializes all instance variables with given values.
     * @param carRenter the name of the car renter
     * @param carNum    the unique car ID number
     * @param carName   the model name of the car
     * @param carType   the type/category of the car
     * @param rate      the daily rental rate
     * @param days      the number of days rented
     */
    
    public Car(String carRenter, int carNum, String carName, String carType, double rate, int days) {
        this.carRenter = carRenter;
        this.carNum = carNum;
        this.carName = carName;
        this.carType = carType;
        this.rate = rate;
        this.days = days;
    }
 
    /*Returns the name of the car renter.
     * @return the car renter's name
     */
    
    public String getCarRenter() {
        return carRenter;
    }
 
    /*Returns the car's id number.
     * @return the car number
     */
    public int getCarNum() {
        return carNum;
    }
 
    /*Returns the model name of the car.
     * @return the car name
     */
    public String getCarName() {
        return carName;
    }
 
    /*Returns the type/category of the car.
     * @return the car type
     */
    public String getCarType() {
        return carType;
    }
 
    /*Returns the daily rental rate of the car.
     * @return the rental rate
     */
    public double getCarRate() {
        return rate;
    }
 
    /*Returns the number of days the car is rented.
     * @return the number of days rented */
    public int getNumOfDays() {
        return days;
    }
 
    // Mutators below
 
    /* Sets the name of the car renter.
     * @param carRenter the car renter's name
     */
    
    public void setCarRenter(String carRenter) {
        this.carRenter = carRenter;
    }
 
    /* Sets the car's ID number.
     * @param carNum the car number
     */
    
    public void setCarNum(int carNum) {
        this.carNum = carNum;
    }
 
    /*Sets the model name of the car.
     * @param carName the car name
     */
    
    public void setCarName(String carName) {
        this.carName = carName;
    }
 
    /*Sets the type/category of the car.
     * @param carType the car type
     */
    
    public void setCarType(String carType) {
        this.carType = carType;
    }
 
    /*Sets the daily rental rate of the car.
     * @param rate the rental rate
     */
    
    public void setCarRate(double rate) {
        this.rate = rate;
    }
 
    /* Sets the number of days the car is rented.
     * @param days the number of days rented
     */
    public void setNumOfDays(int days) {
        this.days = days;
    }
 
    /* Returns a complete string containing all rental car information, with each attribute on its own line.
     * @return a string representation of the Car object
     */
    
    public String toString() {
        return "Car renter's name: " + carRenter + "\n" +
               "Car number: " + carNum + "\n" +
               "Car name: " + carName + "\n" +
               "Car type: " + carType + "\n" +
               "Rate: $" + String.format("%.2f", rate) + "\n" +
               "Rented for: " + days + " days";
    }
}

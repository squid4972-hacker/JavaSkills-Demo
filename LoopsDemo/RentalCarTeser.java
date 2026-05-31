
/* This class tests both the RentalCarCompany and Car classes.
 * And it verifies the correct operation of constructors and all public methods inn accordance to unit testing.
 *
 * @author Enaya S. Laborn
 * @version 1.0
 */
public class RentalCarTeser {
 
    /* Main method. Creates a RentalCarCompany, adds reservations, prints all rental information, and tests findReservation.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
 
        RentalCarCompany company = new RentalCarCompany("El Cheapo");			// Create the rental car company using parameterized constructor
 
     // Add values to reservations (renterName, carNum, carName, carType, rate, days)
        company.addReservation("Blake", 123, "Nissan", "compact",    23.99, 5);
        company.addReservation("Smith", 222, "Mazda",  "midsize",    44.50, 4);
        company.addReservation("Joe",   352, "Chevy",  "SUV",        54.99, 7);
        company.addReservation("Jane",  333, "Ford",   "convertible", 39.99, 5);
 
     // Print all car reservations using toString
        System.out.println(company.toString());
 
     // Print averages and total income
        System.out.printf("Average days rented out is: %.2f%n", company.getAvgDays());
        System.out.printf("Average rate is: $%.2f%n", company.getAvgRate());
        System.out.printf("Total rental income is: $%.2f%n", company.getTotalRentalSales());
 
        System.out.println();
 
     // Test findReservation method with a car number that exists
        company.findReservation(222);
 
        System.out.println();
 
     // Test findReservation method with a nonexistent car number
        company.findReservation(200);
    }}

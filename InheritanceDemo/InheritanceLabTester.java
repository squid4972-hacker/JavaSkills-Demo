/**
 * This class is to test the superclass Vacation and sub-classes AllInclusive and ALaCarte.
 * 
 * @author Enaya S. Laborn
 * @since 26 March 2026
 * 
 * Version 1.0.0
 */
// creates array and allows it to loop through each test with given values

	/*To test AllInclusive class provide the following information IN ORDER
	 * destination, budget, brand, brand's rating, cost of trip
	 */

	/*To test ALaCarte class provide the following information IN ORDER
	 * destination, budget, hotelName, roomCost, airline, airfare, meals
	 */

public class InheritanceLabTester {
	public static void main(String[] args){
		Vacation[] trips = new Vacation[4];
		trips[0] = new AllInclusive("Singapore", 2549.15, "EF Ultimate Break", 4, 2879.28);
		trips[1] = new AllInclusive("Bahamas", 3000, "Virgin Voyages", 4, 1498.97);
		trips[2] = new ALaCarte("Peru", 2250, "Matterhorn Inn", 165.53, "United Airlines", 1200.11, 1000);
		trips[3] = new ALaCarte("Bangkok", 4500, "Hotel BLM", 828.75, "Qatar Airlines", 2008.18, 700);
		for (Vacation trip : trips) {
			trip.budgetBalance();}
		}
}

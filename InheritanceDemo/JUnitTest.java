/**
 * This class is to test the superclass Vacation and sub-classes AllInclusive and ALaCarte.
 * 
 * @author Enaya S. Laborn
 * @since 26 March 2026
 * 
 * Version 1.0.0
 */
import static org.junit.Assert.*;

import org.junit.Test;

public class JUnitTest {

	/*To test AllInclusive class provide the following information IN ORDER
	 * destination, budget, brand, brand's rating, cost of trip
	 */
	@Test
	public void testAllInclusiveOB() { //OB equals "over budget," first "OB" trip test
		AllInclusive tripOB1 = new AllInclusive("Singapore", 2549.15, "EF Ultimate Break", 4, 2879.28);
		assertEquals(-330.13, tripOB1.budgetBalance(), 0.01);
	}
	@Test
	public void testAllInclusiveUB() { //UB equals "under budget," first "UB" trip test
		AllInclusive tripUB1 = new AllInclusive("Bahamas", 3000, "Virgin Voyages", 4, 1498.97);
		assertEquals(1501.03, tripUB1.budgetBalance(), 0.01);
	}
	
	/*To test ALaCarte please provide the following information IN ORDER
	 * destination, budget, hotelName, roomCost, airline, airfare, meals
	 */
	
	@Test
	public void testALaCarteOB() { //OB equals "over budget," second "OB" trip test
		ALaCarte tripOB2 = new ALaCarte("Peru", 2250, "Matterhorn Inn", 165.53, "United Airlines", 1200.11, 1000);
		assertEquals(-115.64, tripOB2.budgetBalance(), 0.01);
	}
	@Test
	public void testALaCarteUB() { //UB equals "under budget," second "UB" trip test
		ALaCarte tripUB2 = new ALaCarte("Bangkok", 4500, "Hotel BLM", 828.75, "Qatar Airlines", 2008.18, 700);
		assertEquals(963.07, tripUB2.budgetBalance(), 0.01);
	}
}

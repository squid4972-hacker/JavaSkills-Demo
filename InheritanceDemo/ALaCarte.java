/**
 * This class calculates the total cost of a vacation factoring in individual costs
 *  and manages attributes of a vacation and prints the budget 
 *  and if they are or are not over budget
 * 
 * @author Enaya S. Laborn
 * @since 26 March 2026
 * 
 * Version 1.0.0
 */

public class ALaCarte extends Vacation {
	private String hotelName;
	private double roomCost;
	private String airline;
	private double airfare;
	private double meals;
	
	//Default constructor
	public ALaCarte(){
		budget = 0.0;
		destination = "";
		hotelName = "";
		roomCost = 0.0;
		airline = "";
		airfare = 0.00;
		meals = 0.00;
	}
	
	//Parameterized constructor (what is needed to create
	public ALaCarte(String destination, double budget, String hotelName, double roomCost, String airline, double airfare, double meals){
		super(destination, budget);
		this.hotelName = hotelName;
		this.roomCost = roomCost;
		this.airline = airline;
		this.airfare = airfare;
		this.meals = meals;
	}
	
	//Accessor method
	public String getHotelName() {	//Accessor method
		return hotelName;
	}
	public void setHotelName(String hotelName) {		//Mutator method
		this.hotelName = hotelName;
	}
	public double getRoomCost() {	//Accessor method
		return roomCost;
	}
	public void setRoomCost(double roomCost) {		//Mutator method
		this.roomCost = roomCost;
	}
	public String getAirline() {		//Accessor method
		return airline;
	}
	public void setAirline(String airline) {		//Mutator method
		this.airline = airline;
	}
	public double getAirfare() {		//Accessor method
		return airfare;
	}
	public void setAirfare(double airfare) {		//Mutator method
		this.airfare = airfare;
	}
	public double getMeals() {		//@Accessor method
		return meals;
	}
	public void setMeals(double meals) {		//Mutator method
		this.meals = meals;
	}
	protected double getTotalprice() {	
		double price = roomCost + airfare + meals;
		return price;
	}	
	
	/**
	 * @overwrites informs user if they are under or over budget and by how much
	 */
	public double budgetBalance(){
		double balance = getBudget() - getTotalprice();
		if (balance < 0){
			System.out.printf("%s%.2f%n", "The vacation is not within your budget. the balance remaining is: $", balance);}
		else {System.out.printf("%s%.2f%n", "The vacation is within your budget. the balance remaining is: $", balance);}
		return balance;
		}

}

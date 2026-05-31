/**
 * This class is an abstract superclass to subsets of 
 * vacation and will calculate, budget balance.
 * 
 * @author Enaya S. Laborn
 * @since 26 March 2026
 * 
 * Version 1.0.0
 */
public abstract class Vacation {
	
	// Initialize instance variable
	protected String destination;
	protected double budget;
	
	// Default Constructor - start with default values 
	public Vacation() {
		destination = "";
		budget = 0;
	}
	/*@param Parameterized Constructor - the destination 
	 * given from object is the destination*/
	 
	public Vacation(String destination, double budget) {
		this.destination = destination;
		this.budget = budget;
	}
	//Retrieve protected instance variable, destination
	public String getDestination() {
		return destination;
	}
	//Retrieve protected instance variable, budget
	public double getBudget() {
		return budget;
	}
	//set object destination to be given parameter value
	public void setDestination(String destination) {
		this.destination = destination;
	}
	//set object budget to be given parameter value
	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	//abstract method
	public abstract double budgetBalance();
	
}

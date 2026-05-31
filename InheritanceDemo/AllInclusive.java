/**
 * This class calculates and manages attributes of an all inclusive vacation 
 * and prints the budget and if they are or are not over budget
 * 
 * @author Enaya S. Laborn
 * @since 26 March 2026
 * 
 * Version 1.0.0
 */
public class AllInclusive extends Vacation {
	private String brand;
	private int rating;
	private double price;
	
	//Default constructor
	public AllInclusive(){
		brand = "";
		rating = 0;
		price = 0.00;
	}
	
	/** @param Parameterized constructor
	 */
	public AllInclusive(String destination, double budget, String brand, int rating, double price){
		super(destination, budget);
		this.brand = brand;
		this.rating = rating;
		this.price = price;
	}
	 
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	/**@return the brand name
	  */
	public String getBrand() {
		return brand;
	}
	/**@return the brand's rating
	  */
	public int getRating() {
		return rating;
	}
	/**@return the price */
	public double getPrice() {
		return price;
	}
	
	/** @overwrite */
	public double budgetBalance(){
		double balance = getBudget() - price;
		if (balance < 0){
			System.out.printf("%s%.2f%n", "The vacation is not within your budget. the balance remaining is: $", balance);}
		else {System.out.printf("%s%.2f%n", "The vacation is within your budget. the balance remaining is: $", balance);}
		return balance;
		}
}

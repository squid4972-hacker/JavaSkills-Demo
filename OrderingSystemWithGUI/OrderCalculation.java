import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This class will have the panel and GUI code and handle event processing, including calculate and exit
 * 
 * @author sailaborn
 * @version 1.0
 * @since 20 April 2026
 */

public class OrderCalculation extends JFrame{
	
public WelcomePanel welcomePanel = new WelcomePanel();
public PizzaPanel pizzaPanel = new PizzaPanel();
public ToppingsPanel toppingsPanel = new ToppingsPanel();
public SidesPanel sidesPanel = new SidesPanel();
public JButton calculateButton = new JButton("Calculate");
public JButton exitButton = new JButton("Exit");

	protected JPanel createButtonPanel() {
	JPanel buttonPanel = new JPanel();
	
	buttonPanel.add(calculateButton);
	buttonPanel.add(exitButton);
	return buttonPanel;
	}
	
	
	public OrderCalculation() {
		setLayout(new BorderLayout());
		JPanel middlePanel = new JPanel(new GridLayout(1,3));
		middlePanel.add(pizzaPanel);
		middlePanel.add(toppingsPanel);
		middlePanel.add(sidesPanel);
		
		add(welcomePanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		setSize(900, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		calculateButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        double subtotal = calculateSubtotal();
		        double total = calculateTotal();
		        JOptionPane.showMessageDialog(null,
		            "Subtotal: $" + String.format("%.2f", subtotal) +
		            "\nTax: $" + String.format("%.2f", (total - subtotal)) +
		            "\nTotal: $" + String.format("%.2f", total));
		    }});
		
		exitButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        System.exit(0);
		    }
		});
			
	}
	
	public double calculateSubtotal() {
		double subtotal = 0;
		subtotal += pizzaPanel.getSelectedPrice();
		subtotal += sidesPanel.getSideTotals();
		subtotal += toppingsPanel.getToppingCost();
		return subtotal;
	}
	
	public double calculateTotal() {
		double totalCost = 0.00;
		final double taxRate = 0.07;
		totalCost = calculateSubtotal() + (taxRate * calculateSubtotal());
		return totalCost;
	}
	}


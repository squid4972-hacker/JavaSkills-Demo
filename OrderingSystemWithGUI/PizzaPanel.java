import javax.swing.ButtonGroup;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.*;
import java.awt.*;




/**
 * This class will design pizza size via radio buttons. Uses flow
 * 
 * @author sailaborn
 * @version 1.0
 * @since 20 April 2026
 */
public class PizzaPanel extends JPanel{
	protected JRadioButton smallButton = new JRadioButton("Small ($5.00)");
	protected JRadioButton mediumButton = new JRadioButton("Medium ($7.00)");
	protected JRadioButton largeButton = new JRadioButton("Large ($10.00)");
	
	public PizzaPanel() {
	    setLayout(new BorderLayout());
	    JLabel label = new JLabel("Pizza Size", SwingConstants.CENTER);
	    
	    JPanel buttonPanel = new JPanel((new GridLayout(3, 1))); 
	    ButtonGroup sizeGroup = new ButtonGroup();
	    sizeGroup.add(smallButton);
	    sizeGroup.add(mediumButton);
	    sizeGroup.add(largeButton);
	    buttonPanel.add(smallButton);
	    buttonPanel.add(mediumButton);
	    buttonPanel.add(largeButton);
	    
	    add(label, BorderLayout.NORTH);
	    add(buttonPanel, BorderLayout.CENTER);
	}
	public String getSelectedSize() {
        if (smallButton.isSelected()) return "Small";
        if (mediumButton.isSelected()) return "Medium";
        if (largeButton.isSelected()) return "Large";
        return "None";
    }
	
	public double getSelectedPrice() {
        if (smallButton.isSelected()) return 5.00;
        if (mediumButton.isSelected()) return 7.00;
        if (largeButton.isSelected()) return 10.00;
        return 0.00;
    }

}

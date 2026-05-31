import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;



/**
 * This class will just design the welcome header.
 * 
 * @author sailaborn
 * @version 1.0
 * @since 20 April 2026
 */
public class WelcomePanel extends JPanel{
	public WelcomePanel() {
		setLayout(new BorderLayout());  /** Create Layout*/
		JLabel welcomeLabel = new JLabel("Welcome to Pizza Planet!"); /** Create welcome panel*/
		add(welcomeLabel, BorderLayout.CENTER);  /** Have the welcome be at the top of layout*/
	}
}

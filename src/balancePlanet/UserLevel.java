package balancePlanet;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/* This is the opening screen in which the player chooses the level at which
 * he desires to play. It is called by CoreStuff when the game is reset.
 * It passes its output to CoreStuff in the integer playLevel.
*/
public class UserLevel extends Globals {
	boolean readyToGo;
	JRadioButton beginnerButton=new JRadioButton("Beginner Level");
	JRadioButton intermediateButton=new JRadioButton("Intermediate Level");
	JRadioButton advancedButton=new JRadioButton("Advanced Level");
	JLayeredPane layeredPane;
	JPanel rightLowerPanel;
	ButtonGroup levelButtons=new ButtonGroup();
	JButton makeItSoButton=new JButton("Let's Play!");
	JButton levelButton;
	
	public UserLevel() {				
		layeredPane=new JLayeredPane();
		readyToGo=false;
		
		JLabel splashScreen=new JLabel();
		String splashname=directoryName+"GameImages/SplashScreen.jpg";
		URL splashUrl = getClass().getResource(splashname);
		System.out.println("ICI: "+splashname);
		splashScreen.setIcon(new ImageIcon(splashUrl));
		splashScreen.setSize(splashScreen.getPreferredSize());
		layeredPane.add(splashScreen);

		rightLowerPanel=new JPanel();
		rightLowerPanel.setBackground(Color.black);
		rightLowerPanel.setLayout(new BoxLayout(rightLowerPanel, BoxLayout.Y_AXIS));
		rightLowerPanel.setLocation(800,500);
		
		initRadioButton(beginnerButton);
		initRadioButton(intermediateButton);
		initRadioButton(advancedButton);
				
		difficultyLevel=beginnerLevel;
		beginnerButton.setSelected(true);
		
		makeItSoButton.setMnemonic(KeyEvent.VK_ENTER);
		makeItSoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (beginnerButton.isSelected()) { difficultyLevel=beginnerLevel; }
				if (intermediateButton.isSelected()) difficultyLevel=intermediateLevel;
				if (advancedButton.isSelected()) difficultyLevel=advancedLevel;
				String iString=directoryName+"GameImages/Level"+String.valueOf(difficultyLevel)+".jpg";
			    URL url= getClass().getResource(iString);
				ImageIcon ii=new ImageIcon(url);
				levelButton=new JButton(ii);
				ss.mainFrame.remove(layeredPane);
				levelButton.setMnemonic(KeyEvent.VK_ENTER);
				levelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent g) {
						readyToGo=true;
						ss.mainFrame.setVisible(true);
					}
				});
				ss.mainFrame.add(levelButton);
				ss.mainFrame.setVisible(true);
			}
		});
		rightLowerPanel.add(makeItSoButton);		
		rightLowerPanel.setSize(rightLowerPanel.getPreferredSize());
		layeredPane.add(rightLowerPanel,new Integer(1));
		activate(false);
	}
// ************************************************************
	public void activate(boolean clearFlag) {
		if (clearFlag)
			ss.mainFrame.getContentPane().remove(ss.mainPanel);
		ss.mainFrame.getContentPane().add(layeredPane);
		ss.mainFrame.setVisible(true);
		ss.mainFrame.repaint();
	}
// ************************************************************
	public void setDifficultyLevel() {
		while (!readyToGo){ try { Thread.sleep(10); } catch (Exception e) {} }
	}
// ************************************************************
	private void initRadioButton(JRadioButton thisButton) {
		thisButton.setBackground(Color.black);
		thisButton.setForeground(Color.white);
		thisButton.setBorderPainted(false);
		thisButton.setFocusPainted(false);
		levelButtons.add(thisButton);
		rightLowerPanel.add(thisButton);
	}
// ************************************************************
	public void clearYerself() {
//		layeredPane.remove(makeItSoButton);
//		layeredPane.remove(beginnerButton);
//		layeredPane.remove(intermediateButton);
//		layeredPane.remove(advancedButton);
//		layeredPane.remove(rightLowerPanel);
		ss.mainFrame.remove(levelButton);
		ss.mainFrame.remove(layeredPane);
		readyToGo=false;
	}
// ************************************************************

}

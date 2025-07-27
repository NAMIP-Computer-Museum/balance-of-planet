package balancePlanet;

import java.awt.Color;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

public class ConnectionsMap extends Globals {
	JFrame myFrame;
	JPanel thePanel=new JPanel();
	JScrollPane connectionsScrollPanel=new JScrollPane(thePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

	public ConnectionsMap() {
		// make this layout null to position buttons
		thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
		thePanel.setBackground(Color.white);
		String fileName="";
		if (difficultyLevel==beginnerLevel)
			fileName=directoryName+"GameImages/Level1Map.jpg";
		if (difficultyLevel==intermediateLevel)
			fileName=directoryName+"GameImages/Level2Map.jpg";
		if (difficultyLevel==advancedLevel)
			fileName=directoryName+"GameImages/Level3Map.jpg";
		JLabel bigImage=new JLabel();
		try {
			System.out.println("RES: "+fileName); // CP
			URL fileURL = getClass().getResource(fileName);
			bigImage.setIcon(new ImageIcon(fileURL)); } 
		catch (Exception e) { System.out.println("could not find image: "+fileName); }
		bigImage.setLocation(0,0);
		thePanel.add(bigImage);
		myFrame=new JFrame();
		myFrame.setSize(1200, 2040);
		myFrame.setVisible(false);
		myFrame.setTitle("Connections Map");
		myFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		myFrame.getContentPane().add(connectionsScrollPanel);	
	}
}

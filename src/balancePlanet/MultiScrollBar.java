package balancePlanet;

/*
 * This is a special input device I developed for this game.
 * 
 * It's a scroll bar with multiple components. To see it in
 *   operation, look at the "Basic Research" page. It still
 *   has an initialization bug, but that will be easy to fix.
 *   The big problem is, how do we get this down to the client?
 *   This class has to operate on the client side. I don't
 *   know anything about JavaScript or client-side Java
 *   programming.
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/*
 *   This Class is used in only the four subsidy allocation pages.
 *   Please play with one before trying to figure this out.
 *   (Note: currently it doesn't initialize properly; you have
 *   to go to the page, jump away, then return.)
 *   I have no idea how we'll implement this on a web page.
 */
public class MultiScrollBar extends Globals {
	static int totalWidth=600;
	static int totalRange=536;
	int elements; 	// how many variables are controlled. There used to be 5, and I got tired of rewriting code every time I added or deleted something.
	JPanel adjustable=new JPanel();						// the primary panel
	JPanel myShell=new JPanel();						// the primary panel
	JPanel superPanel=new JPanel();					// an outer panel to provide horizontal offset
	JLabel legend[]=new JLabel[5];			// the labels for the four legend strings
	int legendSeparation[]=new int[5];	// measures the separation in pixels between the labels
	int legendWidth[]=new int[5];			// the width of the separate text strings
	JPanel legendPanel=new JPanel();		// panel containing the legend
	JPanel outerBox=new JPanel();			// a wrapper inside myPanel
	JPanel innerBox[]=new JPanel[5];		// this contains the colored rectangles
	JLabel thumb[]=new JLabel[4];			// the movable thumbs
	float percent[]=new float[5];			// the values represented by the colored rectangles
	int clickOffset[] = new int[4];		// used in calculating what the mouse click hit
	boolean isMouseDown=false;
	
// ************************************************************
	public MultiScrollBar() {
		Color darkGreen=new Color(0,128,0);
		legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.X_AXIS));
		outerBox.setLayout(null);
		adjustable.setLayout(new BoxLayout(adjustable, BoxLayout.Y_AXIS));
		adjustable.setAlignmentX(Component.CENTER_ALIGNMENT);
		myShell.setLayout(new BoxLayout(myShell, BoxLayout.X_AXIS));
		superPanel.setLayout(new BoxLayout(superPanel, BoxLayout.X_AXIS));
		
		adjustable.setBackground(myBackgroundColor);
		myShell.setBackground(myBackgroundColor);
		superPanel.setBackground(myBackgroundColor);
		legendPanel.setBackground(myBackgroundColor);
		outerBox.setBackground(myBackgroundColor);
		adjustable.setMinimumSize(new Dimension(totalWidth,20));
		outerBox.setMaximumSize(new Dimension(totalWidth,20));
		legendPanel.setMaximumSize(new Dimension(totalWidth,20));
		legendPanel.setMinimumSize(new Dimension(totalWidth,20));
		legendPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		if (difficultyLevel==beginnerLevel) elements=3;
		if (difficultyLevel==intermediateLevel) elements=4;
		if (difficultyLevel>=advancedLevel) elements=5;

		for (int i=0; (i<5); ++i) {
			percent[i]=1.0f/elements;
			innerBox[i]=new JPanel();
   		legend[i]=new JLabel();
   		legend[i].setBackground(myBackgroundColor);
   		innerBox[i].setBorder(new LineBorder(Color.black));
		}
		innerBox[0].setBackground(Color.red);
		innerBox[1].setBackground(Color.yellow);
		innerBox[2].setBackground(darkGreen);
		innerBox[3].setBackground(Color.magenta);
		innerBox[4].setBackground(Color.blue);
		
		for (int i=0; (i<4); ++i) {
			thumb[i]=new JLabel();
   		thumb[i].setIcon(new ImageIcon(directoryName+"GameImages/Thumb.png"));
   		clickOffset[i]=0;
   		thumb[i].addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
				}
				public void mouseEntered(MouseEvent e) {
				}
				public void mouseExited(MouseEvent e) {
				}
				public void mousePressed(MouseEvent e) {
					isMouseDown=true;
					for (int i=0; (i<elements-1); ++i) {
						if (thumb[i]==e.getComponent()) {
							clickOffset[i]=(int)e.getX();
						}
					}
				}
				public void mouseReleased(MouseEvent e) {
				}
			});
   		thumb[i].addMouseMotionListener(new MouseMotionListener() {
				public void mouseDragged(MouseEvent e) {
					int i=0;
					boolean gotcha=false;
					while (!gotcha & (i<elements-1)) {
						gotcha=(thumb[i]==e.getComponent());
						++i;
					}
					--i;
					if (gotcha) {
   					int oldX=thumb[i].getX();
   					int minX=0;
   					if (i>0)
   						minX=thumb[i-1].getX()+16;
   					int maxX=totalWidth-16;
   					if (i<elements-3)
   						maxX=thumb[i+1].getX()-16;
   					int proposedX=oldX+e.getX()-clickOffset[i];
   					if (proposedX<minX)
   						proposedX=minX;
   					if (proposedX>maxX)
   						proposedX=maxX;
						thumb[i].setLocation(proposedX,thumb[i].getY());
						float deltaX=(int)(thumb[i].getX()-(innerBox[i].getX()+innerBox[i].getWidth()));
						percent[i]+=deltaX/totalRange;
						percent[i+1]-=deltaX/totalRange;
						positionRects();
					}  					
				}
				public void mouseMoved(MouseEvent e) {
				}
			});
		}

		for (int i=0; (i<(elements-1)); ++i) {
			outerBox.add(innerBox[i]);
			outerBox.add(thumb[i]);
		}
		outerBox.add(innerBox[elements-1]);
		
		legend[0].setText("Research");
		legend[1].setText("Solar");
		legend[2].setText("Investment");
		legend[3].setText("Public Transport");
		legend[4].setText("Education");
		
		legend[0].setForeground(Color.red);
		legend[1].setForeground(Color.yellow);
		legend[2].setForeground(darkGreen);
		legend[3].setForeground(Color.magenta);
		legend[4].setForeground(Color.blue);
		  		
		legendWidth[0]=55;
		legendWidth[1]=31;
		legendWidth[2]=70;
		legendWidth[3]=102;
		legendWidth[4]=61;
		
		JLabel pix=new JLabel();
		// CP
		String filename=directoryName+"GameImages/Subsidy3.jpg";
		if (difficultyLevel==beginnerLevel) filename=directoryName+"GameImages/Subsidy3.jpg";
		if (difficultyLevel==intermediateLevel) filename=directoryName+"GameImages/Subsidy4.jpg";
		if (difficultyLevel>=advancedLevel) filename=directoryName+"GameImages/Subsidy5.jpg";
		System.out.println("ICI: "+filename);
		URL url = getClass().getResource(filename);
		pix.setIcon(new ImageIcon(url));
		
		myShell.add(pix);
		adjustable.add(myShell);
		adjustable.add(outerBox);
		adjustable.add(legendPanel);
		superPanel.add(Box.createHorizontalStrut(2));
		superPanel.add(adjustable);
	}
// ************************************************************
	public void positionRects() {
		// Draws the altered image with new positions for labels, rectangles, and thumbs
		int currentX=0;
		int currentSpacerX=0;
		legendPanel.removeAll();
		for (int i=0; (i<elements); ++i) {
			float width=totalRange*percent[i];
			innerBox[i].setBounds(currentX, 0, (int)width, 16);
			currentX+=(int)width;

			int textWidth=legendWidth[i];
			int boxCenter=currentX-(int)width/2;
			int textLeft=boxCenter-textWidth/2;
			legendSeparation[i]=textLeft-currentSpacerX;
			if (legendSeparation[i]<0)
				legendSeparation[i]=0;
			currentSpacerX+=textWidth+legendSeparation[i];
   		legendPanel.add(Box.createHorizontalStrut(legendSeparation[i]));
   		legendPanel.add(legend[i]);
			if (i==elements-1) { // special case for the right edge
				int xx=(int)outerBox.getBounds().getX();
				int yy=(int)outerBox.getBounds().getWidth();
				legendSeparation[4]=xx+yy-currentSpacerX;					
			}
			
			if (i<elements-1) {
				thumb[i].setBounds(currentX, 0, 16, 16);
   			currentX+=16;
			}
			Page thatPage=null;
			switch (i) {
				case 0: { thatPage=getPage("Research Subsidy Percent"); break; }
				case 1: { thatPage=getPage("Solar Subsidy Percent"); break; }
				case 2: { thatPage=getPage("Investment Fund Percent"); break; }
				case 3: { thatPage=getPage("Public Transport Subsidy Percent"); break; }
				case 4: { thatPage=getPage("Education Subsidy Percent"); break; }
			}
			thatPage.value=percent[i];
			if (thatPage==thisPage) {
				ss.valueLabel.setText(commonFormat.myFormat("Double",thisPage.value)+" "+thisPage.units);  				
			}
		}
		legendPanel.revalidate();
	}
}

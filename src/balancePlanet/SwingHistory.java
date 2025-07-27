package balancePlanet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

public class SwingHistory extends Globals {
	private static final int barWidth=10;
	private static final int dotSize=8;
	private static final int originX=20;
	private static final int originY=400;
	CoreStuff tc;
	Image xAxisImage, xAxisAlternate, multiColorLegend;
	double biggestAbsValue;
	String pageTitle;
	Bar[] bars;
	JPanel myPanel=new HistoryPanel();
	
	public SwingHistory(SwingStuff tsc) {
		ss=tsc;
		ss.setupJPanel(myPanel,false, 650,460, myBackgroundColor);
		String xAxisFileName=directoryName+"GameImages/XAxis5.png"; // CP
		System.out.println("RES: "+xAxisFileName);
		URL url= getClass().getResource(xAxisFileName);
		ImageIcon xAxisImageIcon=new ImageIcon(url);
		xAxisImage=xAxisImageIcon.getImage();
		String xAxisAltFileName=directoryName+"GameImages/BipolarXAxis5.png"; // CP		
		System.out.println("RES: "+xAxisAltFileName);
		URL alturl= getClass().getResource(xAxisFileName);
		ImageIcon xAxisAlternateIcon=new ImageIcon(alturl);
		xAxisAlternate=xAxisAlternateIcon.getImage(); 
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(100000);
/*
		pageTitle=thisPage.title;		

		bars=new Bar[gameLength];
		int howManySubBars=1;
		if (getThisPage().isMultiColor) 
			howManySubBars=getCauseCount(pageTitle);
		for (int i=0; (i<gameLength); ++i) {
			bars[i]=new Bar(howManySubBars);
		}
		*/
/*		
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
			}
			public void mouseMoved(MouseEvent e) {
				// figure out the scale of the bar graph by finding maximum value
				biggestAbsValue=0;
				for (int i=0; (i<gameLength); ++i) {
					if (Math.abs(getDisplayedValue(pageTitle,i+1))>biggestAbsValue) {
						biggestAbsValue=Math.abs(getDisplayedValue(pageTitle,i+1));
					}
				}
				
	      	setToolTipText(null);
				int x=e.getX();
				int y=e.getY();
				if (thisPage.isPositiveOnly) {
					if ((x>originX)&(x<originX+(gameLength+1)*barWidth)&(y<originY)&(y>0)) {
			   		// find matching bar and display its value
			   		for (int i=0; (i<gameLength); ++i) {
			      		double localValue=getDisplayedValue(pageTitle,i+1);
		      			int height=(int)((400*localValue)/biggestAbsValue);
			      		if ((x>originX+i*barWidth)&(x<originX+(i+1)*barWidth)&(y<originY)&(y>originY-height)) {
			      			String unitText=thisPage.units;
			      			String valueText=myFormat(localValue);
			      			if (pageTitle.endsWith("Subsidy")) {
			      				valueText=integerFormat.format(localValue);
			      				unitText="billion dollars";
			      			}
			      			setToolTipText(valueText+" "+unitText);
			      		}
			   		}
					}
				}
				else { // special case handling for bipolar graphs
					if ((x>originX)&(x<originX+(gameLength+1)*barWidth)&(y<originY)&(y>0)) {
			   		double biggestAbsoluteValue=Math.abs(thisPage.value);
			   		for (int i=0; (i<gameLength); ++i) {
			   			if (Math.abs(thisPage.history[i+1])>biggestAbsoluteValue) {
			   				biggestAbsoluteValue=Math.abs(thisPage.history[i+1]);
			   			}
			   		}
			   		for (int i=0; (i<gameLength); ++i) {
			      		double localValue=thisPage.history[i+1];
		      			int height=(int)((200*localValue)/biggestAbsoluteValue);
			      		boolean positiveBar=(height>0);
			      		boolean t1=positiveBar & (y>midY-height) & (y<midY);
			      		boolean t2=!positiveBar & (y>midY) & (y<midY-height);
			      		boolean withinTheRightEdge=(x<=originX+(i+1)*barWidth);
			      		boolean withinTheLeftEdge=(x>=originX+i*barWidth);
			      		if (withinTheLeftEdge&withinTheRightEdge&(t1|t2)) {
			      			setToolTipText(myFormat(localValue)+" "+thisPage.units);
			      		}
			   		}
					}
					
				}
			}
		});
		*/
	}
//---------------------------------------------------------
	private double getDisplayedValue(String title, int year) {
		// This figures out the value that is actually displayed based on its matching coefficient
		double value=0.0f;
		if (thisPage.isMultiColor) { // must multiply by adjusting coefficients
			double positiveValue=0.0f;
			double negativeValue=0.0f;
			for (int i=0; (i<thisPage.getCauseCount()); ++i) {
					Page subPage=getPage(thisPage.getCause(i));
					double componentValue=subPage.getHistoryValue(year)*thisPage.getCoefficientValue(i);
					if (componentValue>0)
						positiveValue+=componentValue;
					else
						negativeValue-=componentValue;
				}
			if (positiveValue>negativeValue)
				value=positiveValue;
			else
				value=negativeValue;
		}
		else value=Math.abs(getPage(title).getHistoryValue(year));
		return value;
	}
//---------------------------------------------------------
	public void calculateBars() {
		String pgTitle=thisPage.title;

		// auto-scaling; initialize to the current value, then handle the history
		double biggestAbsoluteValue=0.0f;
		for (int i=0; (i<gameLength); ++i) {
			double value=getDisplayedValue(pgTitle,i);
			if (Math.abs(value)>biggestAbsoluteValue) {
					biggestAbsoluteValue=Math.abs(value);
			}
		}
		for (int i=0; (i<(thisYear-firstYear)); ++i) {
   		int height=0;
			height=(int)((400*getDisplayedValue(pgTitle,i))/biggestAbsoluteValue);
   		if (thisPage.isPositiveOnly) { // the case with only positive values   			
   			// general routine for painting multi-colored bar graphs
   			if (thisPage.isMultiColor) {
   				int cCauses=thisPage.getCauseCount();
   				int runningHeight=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
    					// don't graph tax values
    					double value;
    					double sourceValue=0.0f;
    					double coefficient=0.0f;
 						sourceValue=getPage(title).getHistoryValue(i);
 						coefficient=getPage(pgTitle).getCoefficientValue(j);
 						value=sourceValue*coefficient;
 						if (value<0) value=0;
   					int localHeight=(int)((400*value)/biggestAbsoluteValue);
   					int bottom=originY-runningHeight;
   					int top=bottom-localHeight;
   	   			bars[i].setAllValues(j,getPage(title).getButtonColor(), top, localHeight);
   	   			runningHeight+=localHeight;
   				}
   			}
   			else { // the normal single-color case
   	   		bars[i].setAllValues(0,Color.blue, originY-height, height);
   			}
   		}
   		else { // the case in which both positive and negative values exist
   			// This comprises "Economic Growth", "Annual Score", "Total Score", and "Quality of Life Score"
				if (thisPage.equals("Annual Score")) {
					// What's unique to this situation is the fact that one of 
					// the scores is positive and the others are negative.
   				int cCauses=thisPage.getCauseCount();   				
   				int runningDepth=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
 						double sourceValue=getPage(title).getHistoryValue(i);
 						double coefficient=getPage(pgTitle).getCoefficientValue(j);
 						double value=sourceValue*coefficient;
   					int localHeight=(int)((200*value)/biggestAbsoluteValue);
						if (localHeight>0) { // the positive score
 	   	   			bars[i].setAllValues(j,getPage(title).getButtonColor(), originY-195-localHeight, localHeight);
 						}
 						else { // the negative scores. Remember, localHeight<0
	   					int top=originY-194-runningDepth;
 	   	   			bars[i].setAllValues(j,getPage(title).getButtonColor(), top, localHeight);
	   	   			runningDepth+=localHeight;
 						}
   				}
   				// add the net result indicator
					double value=getPage(pgTitle).getHistoryValue(i);
					int localHeight=(int)((200*value)/biggestAbsoluteValue);
					if (localHeight>0) { // positive net value
						history.bars[i].setDotHeight(originY-197-localHeight);
					}
					else { // negative net value. Remember, localHeight<0
						history.bars[i].setDotHeight(originY-196-localHeight);
					}  				
				}
				else if ((pgTitle.equals("Poor Death Score"))|(pgTitle.equals("Rich Death Score"))) {
					// What's unique to this case is the fact that all factors at work are negative
   				int cCauses=thisPage.getCauseCount();   				
   				int runningDepth=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
 						double sourceValue=getPage(title).getHistoryValue(i);
 						double coefficient=getPage(pgTitle).getCoefficientValue(j);
 						double value=sourceValue*coefficient;
   					int localHeight=(int)((200*value)/biggestAbsoluteValue);
   					int top=originY-194+runningDepth;
	   	   		bars[i].setAllValues(j,getPage(title).getButtonColor(), top, localHeight);
   	   			runningDepth+=localHeight;
   				}
				}

				else if ((pgTitle.equals("Quality of Life Score"))|(pgTitle.equals("Gaia Score"))) { 
   				int cCauses=thisPage.getCauseCount();   				
   				int runningHeight=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
 						double sourceValue=getPage(title).getHistoryValue(i);
 						double coefficient=getPage(pgTitle).getCoefficientValue(j);
 						double value=sourceValue*coefficient;
   					int localHeight=(int)((200*value)/biggestAbsoluteValue);
   					int bottom=originY-runningHeight;
   					int top=bottom-localHeight;
						if (localHeight>0) { // the positive score
 	   	   			bars[i].setAllValues(j,getPage(title).getButtonColor(), top-195, localHeight);
 	   	   			runningHeight+=localHeight;
 						}
 						else { // the single negative score (Economic Growth). Remember, localHeight<0
	   					top=originY-194;
 	   	   			bars[i].setAllValues(j,getPage(title).getButtonColor(), top, -localHeight);
 						}
   				}					
				}
				else { // This covers "Economic Growth" and "Total Score"
						// Neither of these has multiple causes
	   			if (i<thisYear-firstYear)
	   				height=(int)((200*thisPage.history[i])/biggestAbsoluteValue);
	   			else
	   				height=(int)((200*thisPage.value)/biggestAbsoluteValue);  				
	   			if (height<0) {
	   	   		bars[i].setAllValues(0,Color.red, originY-194, -height);
	   			}
	   			else {
	   	   		bars[i].setAllValues(0,Color.red, originY-195-height,height);
	   			}
	   		}
   		}
		}
	}
// ---------------------------------------------------------
	public class HistoryPanel extends JPanel {
	private static final long serialVersionUID = 1L;

// ---------------------------------------------------------
	public void paint(Graphics g) {
		g.setColor(myBackgroundColor);
		g.fillRect(0,0,650,460);
		g.drawRect(originX,originY-390,1,390);
		g.drawRect(originX,originY,550,1);
		String pgTitle=thisPage.title;
		if (thisPage.isPositiveOnly)
			g.drawImage(xAxisImage,originX,originY,myBackgroundColor,null);
		else
			g.drawImage(xAxisAlternate,originX,originY-223,myBackgroundColor,null);

		// draw legend if need be
		if (thisPage.isMultiColor) {
			String filename = directoryName+"GameImages/Legends/"+pgTitle+difficultyLevel+".png"; // CP
			System.out.println("RES: "+filename);
			URL url= getClass().getResource(filename);
			ImageIcon energyLegendIcon=new ImageIcon(filename);
			multiColorLegend=energyLegendIcon.getImage();   		
			g.drawImage(multiColorLegend,originX,originY+36,myBackgroundColor,null);			
		}
		// auto-scaling; initialize to the current value, then handle the history
		double biggestAbsoluteValue=0.0f;
		for (int i=0; (i<(thisYear-firstYear)); ++i) {
			double value=getDisplayedValue(pgTitle,i);
			if (Math.abs(value)>biggestAbsoluteValue) {
					biggestAbsoluteValue=Math.abs(value);
			}
		}
		for (int i=0; (i<(thisYear-firstYear)); ++i) {
   		int height=0;
			g.setColor(thisPage.getButtonColor());
			height=(int)((400*getDisplayedValue(pgTitle,i))/biggestAbsoluteValue);
   		if (thisPage.isPositiveOnly) { // the case with only positive values   			
   			// general routine for painting multi-colored bar graphs
   			if (thisPage.isMultiColor) {
   				int cCauses=thisPage.getCauseCount();
   				int runningHeight=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
    					// don't graph tax values
    					if (!title.endsWith("Tax")) {
    						Color x=getPage(title).getButtonColor();
       					g.setColor(x);
    					}
    					double value;
    					double sourceValue=0.0f;
    					double coefficient=0.0f;
 						sourceValue=getPage(title).getHistoryValue(i);
 						coefficient=getPage(pgTitle).getCoefficientValue(j);
 						value=sourceValue*coefficient;
 						if (value<0) value=0;
   					int localHeight=(int)((400*value)/biggestAbsoluteValue);
   					int bottom=originY-runningHeight;
   					int top=bottom-localHeight;
   	   			g.fillRect(originX+i*barWidth,top,barWidth,localHeight); 
   	   			runningHeight+=localHeight;
   				}
	   			g.setColor(Color.black);
	   			int leftEdge=originX+i*barWidth;
	   			int topEdge=originY-runningHeight;
	   			g.drawRect(leftEdge,topEdge,barWidth,runningHeight);
   			}
   			else { // the normal single-color case
	   			g.fillRect(originX+i*barWidth,originY-height,barWidth,height);
	   			g.setColor(Color.black);
	   			g.drawRect(originX+i*barWidth,originY-height,barWidth,height);
   			}
   		}
   		else { // the case in which both positive and negative values exist
   			// This comprises "Economic Growth", "Annual Score", "Total Score", and "Quality of Life Score"
				if (pgTitle.equals("Annual Score")) {
					// What's unique to this situation is the fact that one of 
					// the scores is positive and the others are negative.
   				int cCauses=thisPage.getCauseCount();   				
   				int runningDepth=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
    					g.setColor(getPage(title).getButtonColor());
						double sourceValue=getPage(title).getHistoryValue(i);
 						double coefficient=getPage(pgTitle).getCoefficientValue(j);
 						double value=sourceValue*coefficient;
   					int localHeight=(int)((200*value)/biggestAbsoluteValue);
						if (localHeight>0) { // the positive score
 			   			g.fillRect(originX+i*barWidth,originY-195-localHeight,barWidth,localHeight);
 			   			g.setColor(Color.black);
 			   			g.drawRect(originX+i*barWidth,originY-195-localHeight,barWidth,localHeight);
 						}
 						else { // the negative scores. Remember, localHeight<0
	   					int top=originY-194-runningDepth;
	   	   			g.fillRect(originX+i*barWidth,top,barWidth,-localHeight); 
	   	   			g.setColor(Color.black);
	   	   			g.drawRect(originX+i*barWidth,top,barWidth,-localHeight);
	   	   			runningDepth+=localHeight;
 						}
   				}
   				// add the net result indicator
					double value=getPage(pgTitle).getHistoryValue(i);
					int localHeight=(int)((200*value)/biggestAbsoluteValue);
					if (localHeight>0) { // positive net value
		   			g.setColor(Color.white);
		   			g.fillOval(originX+i*barWidth+1,originY-197-localHeight,dotSize,dotSize);
		   			g.setColor(Color.black);
		   			g.drawOval(originX+i*barWidth+1,originY-197-localHeight,dotSize,dotSize);
					}
					else { // negative net value. Remember, localHeight<0
   					int top=originY-196-localHeight;
   	   			g.setColor(Color.white);
   	   			g.fillOval(originX+i*barWidth+1,top,dotSize,dotSize); 
   	   			g.setColor(Color.black);
   	   			g.drawOval(originX+i*barWidth+1,top,dotSize,dotSize);
					}  				
				}
				else if ((pgTitle.equals("Poor Death Score"))|(pgTitle.equals("Rich Death Score"))) {
					// What's unique to this case is the fact that all factors at work are negative
   				int cCauses=thisPage.getCauseCount();   				
   				int runningDepth=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
 						Page causePage=getPage(title);
 						Page cause2Page=getPage(causePage.getCause(0));
    					g.setColor(cause2Page.getButtonColor());
 						double sourceValue=getPage(title).getHistoryValue(i);
 						double coefficient=getPage(pgTitle).getCoefficientValue(j);
 						double value=sourceValue*coefficient;
   					int localHeight=(int)((200*value)/biggestAbsoluteValue);
   					int top=originY-194+runningDepth;
   	   			g.fillRect(originX+i*barWidth,top,barWidth,localHeight); 
   	   			g.setColor(Color.black);
   	   			g.drawRect(originX+i*barWidth,top,barWidth,localHeight);
   	   			runningDepth+=localHeight;
   				}
				}

				else if ((pgTitle.equals("Quality of Life Score"))|(pgTitle.equals("Gaia Score"))) { 
   				int cCauses=thisPage.getCauseCount();   				
   				int runningHeight=0;
   				for (int j=0; (j<cCauses); ++j) { // each causal factor
    					String title=thisPage.getCause(j);
    					g.setColor(getPage(title).getButtonColor());
 						double sourceValue=getPage(title).getHistoryValue(i);
 						double coefficient=getPage(pgTitle).getCoefficientValue(j);
 						double value=sourceValue*coefficient;
   					int localHeight=(int)((200*value)/biggestAbsoluteValue);
   					int bottom=originY-runningHeight;
   					int top=bottom-localHeight;
						if (localHeight>0) { // the positive score
 			   			g.fillRect(originX+i*barWidth,top-195,barWidth,localHeight);
 			   			g.setColor(Color.black);
 			   			g.drawRect(originX+i*barWidth,top-195,barWidth,localHeight);
 	   	   			runningHeight+=localHeight;
 						}
 						else { // the single negative score (Economic Growth). Remember, localHeight<0
	   					top=originY-194;
	   	   			g.fillRect(originX+i*barWidth,top,barWidth,-localHeight); 
	   	   			g.setColor(Color.black);
	   	   			g.drawRect(originX+i*barWidth,top,barWidth,-localHeight);
 						}
   				}					
   				// add the net result indicator
					double value=getPage(pgTitle).getHistoryValue(i);
					int localHeight=(int)((200*value)/biggestAbsoluteValue);
					if (localHeight>0) { // positive net value
		   			g.setColor(Color.white);
		   			g.fillOval(originX+i*barWidth+1,originY-197-localHeight,dotSize,dotSize);
		   			g.setColor(Color.black);
		   			g.drawOval(originX+i*barWidth+1,originY-197-localHeight,dotSize,dotSize);
					}
					else { // negative net value. Remember, localHeight<0
   					int top=originY-196-localHeight;
   	   			g.setColor(Color.white);
   	   			g.fillOval(originX+i*barWidth+1,top,dotSize,dotSize); 
   	   			g.setColor(Color.black);
   	   			g.drawOval(originX+i*barWidth+1,top,dotSize,dotSize);
					}
				}
				else { // This covers "Economic Growth" and "Total Score"
						// Neither of these has multiple causes
	   			if (i<(thisYear-firstYear))
	   				height=(int)((200*thisPage.history[i])/biggestAbsoluteValue);
	   			else
	   				height=(int)((200*thisPage.value)/biggestAbsoluteValue);  				
	   			if (height<0) {
	   	   		g.setColor(Color.red);
		   			g.fillRect(originX+i*barWidth,originY-194,barWidth,-height);
		   			g.setColor(Color.black);
		   			g.drawRect(originX+i*barWidth,originY-194,barWidth,-height);
	   			}
	   			else {
		   			g.fillRect(originX+i*barWidth,originY-195-height,barWidth,height);
		   			g.setColor(Color.black);
		   			g.drawRect(originX+i*barWidth,originY-195-height,barWidth,height);
	   			}
	   		}
   		}
		}
	}
	}
// ---------------------------------------------------------

}

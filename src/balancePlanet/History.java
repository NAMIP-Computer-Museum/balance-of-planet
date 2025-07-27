package balancePlanet;

/*
 *   This class draws the history bar chart. It's pretty complicated because there
 *   are two possible variations on the basic bar chart:
 *   isMultiColor: this is really messy: the bar graph is composed of a number of
 *      subfactors, each of which is represented by its own sub-bar; the vertical
 *      stack of sub-bars constitutes the entire bar.
 *   isPositiveOnly: default value is TRUE, which draws a normal bar chart
 *      A value of FALSE denotes a bar graph with both positive and negative values.
 *      
 *   Particularly bothersome will be the tooltip that presents the value of each bar
 *      as the mouse moves over the bars. For the time being, I have disabled this
 *      feature and I now doubt that I will revive it.
 *      
 *   Also, there's a little circle displayed in a few graphs representing the sum
 *      of the positive and negative values. Applies only to graphs for which
 *      isMultiColor is TRUE and isPositiveOnly is FALSE.
 */

import java.awt.Color;
public class History extends Globals {
	private static final int originY=400;
	double biggestAbsValue;
	public Bar[] bars;
	
	public History() {
		bars=new Bar[gameLength];
	}
//---------------------------------------------------------
	private double getDisplayedValue(Page page, int year) {
		// This figures out the value that is actually displayed based on its matching coefficient
		double value=0.0f;
		if (thisPage.isMultiColor) { // must multiply by adjusting coefficients
			double positiveValue=0.0f;
			double negativeValue=0.0f;
			for (int i=0; (i<thisPage.getCauseCount()); ++i) {
				String subTitle=thisPage.getCause(i);
				double componentValue=getPage(subTitle).getHistoryValue(year)*getPage(page.title).getCoefficientValue(i);
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
		else value=Math.abs(page.getHistoryValue(year));
		return value;
	}
//---------------------------------------------------------
	public void calculateBars() {
		Page page=thisPage;
		String pgTitle=page.title;
		int howManySubBars=1;
		if (page.isMultiColor) 
			howManySubBars=page.getCauseCount();
		for (int i=0; (i<gameLength); ++i) {
			bars[i]=new Bar(howManySubBars);
		}

		// auto-scaling; initialize to the current value, then handle the history
		double biggestAbsoluteValue=0.0f;
		for (int i=0; (i<gameLength); ++i) {
			double value=getDisplayedValue(page,i);
			if (Math.abs(value)>biggestAbsoluteValue) {
					biggestAbsoluteValue=Math.abs(value);
			}
		}
		for (int i=0; (i<gameLength); ++i) {
   		int height=0;
			height=(int)((400*getDisplayedValue(page,i))/biggestAbsoluteValue);
   		if (thisPage.isPositiveOnly) { // the case with only positive values   			
   			// general routine for painting multi-colored bar graphs
   			if (thisPage.isMultiColor) {
   				int cCauses=page.getCauseCount();
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
				if (page.equals("Annual Score")) {
					// What's unique to this situation is the fact that one of 
					// the scores is positive and the others are negative.
   				int cCauses=page.getCauseCount();   				
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
						bars[i].setDotHeight(originY-197-localHeight);
					}
					else { // negative net value. Remember, localHeight<0
						bars[i].setDotHeight(originY-196-localHeight);
					}  				
				}
				else if ((page.equals("Poor Death Score"))|(page.equals("Rich Death Score"))) {
					// What's unique to this case is the fact that all factors at work are negative
   				int cCauses=page.getCauseCount();   				
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

				else if ((page.equals("Quality of Life Score"))|(page.equals("Gaia Score"))) { 
   				int cCauses=page.getCauseCount();   				
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
	   	   		bars[i].setAllValues(0,Color.blue, originY-195-height,height);
	   			}
	   		}
   		}
		}
	}
}

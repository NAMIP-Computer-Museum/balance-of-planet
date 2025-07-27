package balancePlanet;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;
/*
 *  This is the basic data structure for the game. It corresponds to a
 *  single page or screenful in the display. It also contains a single
 *  factor or variable used in the simulation. What makes it complicated
 *  is the set of internal variables associated with it.
 */
public class Page extends CoreStuff {
	public boolean isMultiColor, isPositiveOnly; // what kind of history graph is displayed
	String title;				// page title and label

	String mainText;			// main explanatory text
	String formulaText;		// text presented in formula; not properly implemented yet
	String referenceText;	// links for references
	String units;				// unit of measurement
	String format;				// data type of value: currency, percent, etc

	private ArrayList<String> causes=new ArrayList<String>();				// list of causal factors
	public ArrayList<String> effects=new ArrayList<String>();				// list of affected factors
	ArrayList<String> coefficients=new ArrayList<String>();		// coefficients used in the formula
	ArrayList<String> coefficientUnits=new ArrayList<String>();	// units of measurement of each coefficient
	ArrayList<Double> coefInitialValue=new ArrayList<Double>();	// default value of coefficient
	ArrayList<Double> coefCurrentValue=new ArrayList<Double>();	// value of coefficient in use
	ArrayList<Double> coefMinValue=new ArrayList<Double>();			// minimum allowable value of coefficient
	ArrayList<Double> coefMaxValue=new ArrayList<Double>();			// maximum allowable value of coefficient
	ArrayList<Double> influence=new ArrayList<Double>(); 	// influence exerted by each cause

	double initialValue; 	// value of this variable at outset of game
	double value;			// current value of this variable
	double minValue;		// minimum permissible value of this variable
	double maxValue;		// maximum permissible value of this variable
	ImageIcon mainImage;	// 650h x 460v image shown in main display
	public String valueDisplayed; // specifies whether total, average, or no aggregate value should be displayed
	double[] history=new double[lastYear-firstYear];	// historical record of all values of this variable by year.
	int visitCount;
	int level;
	int x, y;
	String buttonColor; // color to be used for the button of this variable in the index display
	Color cloverColor=new Color(0,128,0);
	Color fernColor=new Color(64,128,0);
	Color tangerineColor=new Color(255,128,0);
	Color mossColor=new Color(0,128,64);
	Color springColor=new Color(0,255,0);
	Color limeColor=new Color(128,255,0);
	Color magentaColor=new Color(255,0,255);
	Color redColor=new Color(255,0,0);
	Color grapeColor=new Color(128,0,255);
	Color eggplantColor=new Color(64,0,128);
	Color orchidColor=new Color(102,102,255);
	Color strawberryColor=new Color(255,0,128);
	Color grayColor=new Color(76,76,76);
	Color lightgrayColor=new Color(127,127,127);
	Color mochaColor=new Color(128,64,0);
	Color turquoiseColor=new Color(0,255,255);
	Color asparagusColor=new Color(128,128,0);
	Color salmonColor=new Color(255,102,102);
	Color tealColor=new Color(0,128,128);
	Color maroonColor=new Color(128,0,64);
	Color bubblegumColor=new Color(255,102,255);
	Color blackColor=new Color(0,0,0);
	Color aquaColor=new Color(0,128,255);
	Color floraColor=new Color(102,255,102);
	Color spindriftColor=new Color(102,255,204);
	Color blueberryColor=new Color(0,0,255);
	Color iceColor=new Color(102,255,255);
	Color oceanColor=new Color(0,64,128);
	Color seafoamColor=new Color(0,255,128);
	Color lavenderColor=new Color(204,102,255);
	Color skyColor=new Color(102,204,255);
	
	public Page(String tLabel) {
		title=tLabel;
		mainText="";
		formulaText="";
		referenceText="";
		units="";
		format="";
		causes.clear();
		effects.clear();
		coefficients.clear();
		coefficientUnits.clear();
		coefInitialValue.clear();
		coefCurrentValue.clear();
		coefMinValue.clear();
		coefMaxValue.clear();
		influence.clear();
		initialValue=0.0f;
		value=0.0f;
		minValue=0.0f;
		maxValue=0.0f;
		for (int i=0; (i<59); ++i) history[i]=0.0f;	
		visitCount=0;
		level=0;
		x=0;
		y=0;
	}
// ************************************************************
	public class Bezier {
		int p1x,p1y,p2x,p2y,p3x,p3y,p4x,p4y;
		public Bezier(int t1x, int t1y, int t2x, int t2y, int t3x, int t3y, int t4x, int t4y) {
			p1x=t1x;
			p1y=t1y;
			p2x=t2x;
			p2y=t2y;
			p3x=t3x;
			p3y=t3y;
			p4x=t4x;
			p4y=t4y;
		}
	}
// ************************************************************
	public double getValue() {
		return value;
	}
// ************************************************************
	public void setValue(double newValue) {
		value=newValue;
	}
// ************************************************************
	public double getHistoryValue(int index) {
		return history[index];
	}
// ************************************************************
	public double getPreviousValue() {
		return getHistoryValue(thisYear-firstYear);
	}
// ************************************************************
	public double getInitialValue() {
		return initialValue;
	}
// ************************************************************
	public double getCoefficientValue(int tIndex) {
		if (tIndex>=coefCurrentValue.size()) {
			System.out.println("Page getCoefficientValue error: "+tIndex+" is out of range for "+title);
			return 0.0f;				
		}
		double currentValue=coefCurrentValue.get(tIndex);
		return currentValue;
	}
// ************************************************************
	public int getCauseCount() {
		return causes.size();	
	}
// ************************************************************
	public String getCause(int index) {
		return causes.get(index);
	}
// ************************************************************
	public void addCause(String newCause) {
		causes.add(newCause);
	}
// ************************************************************
	public void deleteCause(int index) {
		causes.remove(index);
	}
// ************************************************************
	public double getInfluence(int index) {
		return influence.get(index);
	}
// ************************************************************
	public void setInfluence(int index, double newValue) {
		influence.set(index, newValue);
	}
// ************************************************************
	public int getEffectCount() {
		return effects.size();		
	}
// ************************************************************
	public String getEffect(int index) {
		return effects.get(index);
	}
// ************************************************************
	public void addEffect(String newEffect) {
		effects.add(newEffect);
	}
// ************************************************************
	public String getTitle() {
		return title;
	}
// ************************************************************
	public String getFormat() {
		return format;
	}
// ************************************************************
	public String getUnits() {
		return units;
	}	
// ************************************************************
	public String getMainText() {
		return mainText;
	}	
// ************************************************************
	public String getReferenceText() {
		return referenceText;
	}	
// ************************************************************
	public double getMinValue() {
		return minValue;
	}
// ************************************************************
	public double getMaxValue() {
		return maxValue;
	}
// ************************************************************
	public int getX() {
		return x;
	}
// ************************************************************
	public int getY() {
		return y;
	}
// ************************************************************
	public Color getButtonColor() {
		if (buttonColor.equals("clover")) return cloverColor;
		if (buttonColor.equals("fern")) return fernColor;
		if (buttonColor.equals("tangerine")) return tangerineColor;
		if (buttonColor.equals("moss")) return mossColor;
		if (buttonColor.equals("spring")) return springColor;
		if (buttonColor.equals("lime")) return limeColor;
		if (buttonColor.equals("magenta")) return magentaColor;
		if (buttonColor.equals("red")) return redColor;
		if (buttonColor.equals("grape")) return grapeColor;
		if (buttonColor.equals("eggplant")) return eggplantColor;
		if (buttonColor.equals("orchid")) return orchidColor;
		if (buttonColor.equals("strawberry")) return strawberryColor;
		if (buttonColor.equals("gray")) return grayColor;
		if (buttonColor.equals("lightgray")) return lightgrayColor;
		if (buttonColor.equals("mocha")) return mochaColor;
		if (buttonColor.equals("turquoise")) return turquoiseColor;
		if (buttonColor.equals("asparagus")) return asparagusColor;
		if (buttonColor.equals("salmon")) return salmonColor;
		if (buttonColor.equals("teal")) return tealColor;
		if (buttonColor.equals("maroon")) return maroonColor;
		if (buttonColor.equals("bubblegum")) return bubblegumColor;
		if (buttonColor.equals("black")) return blackColor;
		if (buttonColor.equals("aqua")) return aquaColor;
		if (buttonColor.equals("flora")) return floraColor;
		if (buttonColor.equals("spindrift")) return spindriftColor;
		if (buttonColor.equals("blueberry")) return blueberryColor;
		if (buttonColor.equals("ice")) return iceColor;
		if (buttonColor.equals("ocean")) return oceanColor;
		if (buttonColor.equals("seafoam")) return seafoamColor;
		if (buttonColor.equals("lavender")) return lavenderColor;
		if (buttonColor.equals("sky")) return skyColor;
		if (buttonColor.equals("yellow")) return Color.yellow;
		if (buttonColor.equals("cyan")) return Color.cyan;
		System.out.println("error in buttonColor: "+buttonColor);
		return Color.white;
	}
// ************************************************************	
}

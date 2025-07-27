package balancePlanet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.UIManager;

public class Globals {
	static final int beginnerLevel=1;
	static final int intermediateLevel=2;
	static final int advancedLevel=3;
	static final int expertLevel=4;

	public static final int turnLength=60; // how many years per turn.
	// There are three in-game display screens, and the program switches
	//  between them. The Main Display presents the photograph with text.
	//  The History Display presents the history graph at the end of the game.
	//  The Formula Display presents the formula for the user to edit.
	public static final int mainDisplay=1;		// code for the main display
	public static final int historyDisplay=2;	// code for the history display
	public static final int formulaDisplay=3;	// code for the formula display
	public static int displayType;			// contains the index for the display type (1, 2, or 3)
	
	static final int maxCauses=20;	// maximum number of causal factors
	static final int firstYear=2013;			// first year of play
	public static int thisYear;							// the year being analyzed
	static final int lastYear=2073;
	static final int gameLength=59;
	
	boolean quitFlag;		// indicates that the program should be terminated
	boolean newGameFlag;	// indicates that a new game should be started
	static boolean startGame;	// indicates that it's time to start the game	
	
	static public int difficultyLevel;		// game level
	static double cumulativeCO2Emissions;

	static String directoryName=System.getProperty("user.dir")+"/res/";
   // The primary data structure, contains all the data regarding each
	//  of the factors that goes into the simulation. This data is
	//  read from the file BotP.xml
	static public ArrayList<Page> pages=new ArrayList<Page>();
	static Page thisPage;      
	static HashMap<String, Page> pageMap=new HashMap<String, Page>(); // maps the page title to the page itself
	static public ArrayList<String> alphabetizedPages=new ArrayList<String>(); // alphabetized list of pages
	static public ArrayList<String> alphabetizedBackgrounders=new ArrayList<String>(); // alphabetized list of backgrounders
   
	static CoreStuff theGame;	
	static SwingStuff ss; // this Class writes Swing pages
	static UserLevel ul;
	static FormatStuff commonFormat;
	static public History history; 	// defined in History.java; extends JPanel
	static public CentralInput ci; // the central input class
	static Date currentTime;
	static String startingTime;
	static public String userName="Guest";
   
	public static Color myBackgroundColor=new Color(176,210,255);
	public static Color alternateColor=new Color(65,160,255);	
   public static HashMap<String, String> backgrounderText=new HashMap<String, String>();
   public static HashMap<String, String> backgrounderReference=new HashMap<String, String>();
   
	static ArrayList<String> pagePathStack=new ArrayList<String>(); // permits backtracking through previously visited pages
	static int pagePathStackIndex;
  
	public static ArrayList<String> backgrounderPathStack=new ArrayList<String>(); // permits backtracking through previously visited backgrounders
	static int backgrounderPathStackIndex;
   
// ************************************************************
	// Returns a specific page
	public Page getPage(String pageTitle) {
		Page foundPage=null;
		if (pageMap.containsKey(pageTitle))
			foundPage = pageMap.get(pageTitle);
		else
			System.out.println("getPage error: "+pageTitle+" is not a valid key");
		return foundPage;
	}	
// ************************************************************
	public String padText(String baseText, String referenceText) {
	// returns the standard terminating text to append to the mainText
	String displayText="<p style=\"font-size:110%\">"+baseText+"</p>" +
			"<br><br><br><p>SOURCES:<br>"+referenceText+"<br><br><br>" +
			"<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>" +
			"<span style=font:8px Trebuchet MS; >" +
			"I have made every effort to insure that images used in Balance of the Planet" +
			" have been properly credited to their sources; if you believe that " +
			"an image has been used improperly, please send email to " +
			"balance@erasmatazz.com</p>";
	return displayText;
	}
// ************************************************************
	public String getUpperGraphText() {
		double sum=0;
		for (int year=0; (year<gameLength); ++year) {
			double x=thisPage.getHistoryValue(year);
			sum+=x;
		}
		String upperLabel="";
		if (thisPage.valueDisplayed.equals("Percent")) {
			upperLabel=commonFormat.myFormat(thisPage.format,thisPage.getValue());
		}
		else {
			if (thisPage.valueDisplayed.equals("Normal")) 
				upperLabel="total: "+commonFormat.myFormat(thisPage.format,sum)+" "+thisPage.units;
			if (thisPage.valueDisplayed.equals("Average"))
				upperLabel="average: "+commonFormat.myFormat(thisPage.format,sum/(thisYear-firstYear))+" "+thisPage.units;
		}
		return upperLabel;
	}
// ************************************************************
	public String getLowerGraphText() {
		double max=0;
		double min=0;
		for (int year=0; (year<gameLength); ++year) {
			double x=thisPage.getHistoryValue(year);
			if (x>max) max=x;
			if (x<min) min=x;
		}
		String lowerLabel="";
		if (max>-min)
			lowerLabel="highest: "+commonFormat.myFormat(thisPage.format,max)+" "+thisPage.units;					
		else
			lowerLabel="lowest: "+commonFormat.myFormat(thisPage.format,min)+" "+thisPage.units;										
		return lowerLabel;
	}
// ************************************************************
	public void jumpToPage(String pageTitle, boolean fromNavigationButton) {
		thisPage=getPage(pageTitle);
		++thisPage.visitCount;
		if (!fromNavigationButton) {
			pagePathStack.add(pageTitle);
			++pagePathStackIndex;
			ss.enableNavBackButton(true);
		}
		ss.swingJumpToPage(pageTitle, fromNavigationButton);
	}	
// ************************************************************
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch(Exception evt) {}
		theGame=new CoreStuff();
		theGame.init();
		do {
			while (!theGame.quitFlag & !theGame.newGameFlag) { 
				try { 
					Thread.sleep(10);
					} 
				catch (Exception e) {} 
			}
			if (theGame.newGameFlag) {
				theGame.reset();
			}

		}
		while (!theGame.quitFlag);
		System.exit(0);
	}
// ************************************************************

}

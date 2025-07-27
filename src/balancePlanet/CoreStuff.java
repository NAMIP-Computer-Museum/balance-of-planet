package balancePlanet;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This is the central class that everything else supports. It was originally set up
 * to handle the Java-based display, but since we're moving to a client-server 
 * system, all that Java-based display (the focus of which is method "ComposeMainDisplay")
 * is obsolete and only retained for development purposes. The output that you'll
 * be working with is all in WriteHTML.java. That rather clumsily writes the HTML pages,
 * in a file currently called "Output.html". Obviously you'll need to restructure
 * all of that. There's probably a more elegant way to write the HTML files,
 * so please advise as to how I can bring my design into something a bit more 
 * modern in approach.
 * 
 * My expectation is that most of this code will reside on the server, with
 * a simple system to send the output html pages to the client. However, the
 * input from the user gets us into Java territory that I'm unfamiliar with,
 * and again I'll need your help to find the right approach. I can modify the
 * current code if you'll just explain what I need to do.
 * 
 * Some basic concepts:
 * 	Page: the basic data object in the simulation. See Page.java for full definition
 * 	Internal display: the original display system, before I decided
 * 		to serve the game over the Internet. Uses Swing. Messy.
 * 		Retained ONLY for my own testing and debugging;
 * 		Controlled with the isInternalDisplayActive flag
 * 	External display: the display system used to serve web pages to players.
 * 		Delivers HTML plus some Java for the on-page controllers.
 * 		Controlled with the isExternalDisplayActive flag
 * 		(Note: We sure have come a long ways from the days when every
 * 		variable label had to be no longer than 8 characters.)
 * 	
 */


public class CoreStuff extends Globals {
   Model theModel;   // the engine that calculates the simulation, stored in Model.java
	double richToPoorRatio; // this value is changed in one of the play levels
	
// ************************************************************
	public CoreStuff() {
	}
// ************************************************************
	public void init() {
		startGame=false;
		readBackgrounds(); // reads the backgrounder information
		ss=new SwingStuff();
		history=new History();
		commonFormat=new FormatStuff();
		ul=new UserLevel();
		theModel = new Model();
		ci=new CentralInput();		
		reset(); // resets the game
//		jumpToPage("Taxes", false);
	}
// ************************************************************	
	public void reset() {
		// resets all game elements to their initial conditions
		thisYear=firstYear;
		displayType=mainDisplay;
		pagePathStackIndex=-1;
		pagePathStack.clear();
		pages.clear();
		pageMap.clear();
		alphabetizedPages.clear();
		backgrounderPathStackIndex=0;
		backgrounderPathStack.clear();
		quitFlag=false;
		cumulativeCO2Emissions=0;
		richToPoorRatio=100.0f;
		currentTime=new Date();
		startingTime=currentTime.toString();

		ul.activate(newGameFlag);
		ul.setDifficultyLevel();
		ul.clearYerself();
		startGame=true;
		newGameFlag=false;
				
		readXMLFile();
		thisPage=pageMap.get("Taxes");
		ss.postReset();
		jumpToPage("Taxes", false);
	}
// ************************************************************	
	private void readXMLFile() {
		// conventional reading of main data file
		FileInputStream fis=null;
		String testName=directoryName+"Pages.xml";
		try { fis = new FileInputStream(testName); }
		catch (Exception e) { 
			e.printStackTrace(); 
		}
		DocumentBuilder builder = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try { builder = factory.newDocumentBuilder(); }
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		Document doc = null;
		try { doc = builder.parse(fis); }
		catch (Exception e) { 
			e.printStackTrace(); 
		}
		doc.getDocumentElement().normalize();
		NodeList pageList = doc.getElementsByTagName("Page");
		for(int i=0;i<pageList.getLength();i++) {
			final Node pageNode = pageList.item(i);
			if (pageNode!=null) {
				String frameLabel=pageNode.getAttributes().getNamedItem("Title").getNodeValue();
				int pageLevel=(int)(new Integer(getNodeText(pageNode, "Level")));
				if (pageLevel<=difficultyLevel) {
					Page pg=new Page(frameLabel);
					boolean coefficientsLeft=true;
					int cCoefficients=0;
					while (coefficientsLeft) {
						try {
							Node coeffNode=getIndexedChildByTagName(pageNode, "Coefficient", cCoefficients++);
							pg.coefficients.add(coeffNode.getAttributes().getNamedItem("name").getNodeValue());
							double x=new Double(coeffNode.getAttributes().getNamedItem("initValue").getNodeValue());
							pg.coefInitialValue.add(x);
							pg.coefCurrentValue.add(x);
							pg.coefMinValue.add(new Double(coeffNode.getAttributes().getNamedItem("minValue").getNodeValue()));
							pg.coefMaxValue.add(new Double(coeffNode.getAttributes().getNamedItem("maxValue").getNodeValue()));
							pg.coefficientUnits.add(coeffNode.getAttributes().getNamedItem("units").getNodeValue());
						} catch (NullPointerException e) { coefficientsLeft=false; }
					}
					pg.mainText=getNodeText(pageNode, "MainText");
					pg.formulaText=getNodeText(pageNode, "FormulaText");
					pg.referenceText=getNodeText(pageNode, "ReferenceText");
					pg.units=getNodeText(pageNode, "Units");
					pg.format=getNodeText(pageNode, "Format");
					pg.initialValue=new Double(getNodeText(pageNode, "InitialValue"));
					pg.value=pg.initialValue;
					pg.history[0]=pg.value;
					pg.minValue=new Double(getNodeText(pageNode, "MinimumValue"));
					pg.maxValue=new Double(getNodeText(pageNode, "MaximumValue"));
					pg.isMultiColor=new Boolean(getNodeText(pageNode, "Multicolor"));
					pg.isPositiveOnly=new Boolean(getNodeText(pageNode, "PositiveGraph"));
					pg.valueDisplayed=getNodeText(pageNode, "ValueDisplayed");
					Node testNode=null;
					Node locationNode=getChildByTagName(pageNode, "Location");
					pg.x=new Integer(locationNode.getAttributes().getNamedItem("x").getNodeValue());
					pg.y=new Integer(locationNode.getAttributes().getNamedItem("y").getNodeValue());
					pg.buttonColor=locationNode.getAttributes().getNamedItem("color").getNodeValue();
					int counter=0;
					do {
						testNode=getIndexedChildByTagName(pageNode, "CausalFactor", counter);
						try {
							pg.addCause(testNode.getFirstChild().getNodeValue());
						} catch (NullPointerException e) {}
						++counter;					
					} while (testNode!=null);
					pages.add(pg);
					pageMap.put(pg.title, pg);
					alphabetizedPages.add(pg.title);
				}
			}
		}
		// purge causal factors not included in this playLevel
		for (Page testPage: pages) {
			int index=0;
			 while (index<testPage.getCauseCount()) {
				String listedCause=testPage.getCause(index);
				if (pageMap.containsKey(listedCause))
					++index;
				else
					testPage.deleteCause(index);
			 }
		}
		// Work backwards to determine effects lists from causes data
		for (Page outerPage: pages) {
			for (Page innerPage: pages) {
				for (int i=0; (i<innerPage.getCauseCount()); ++i) {
					if (innerPage.getCause(i).equals(outerPage.title))
						outerPage.addEffect(innerPage.title);
				}
			}
		}
		
		// check for pages that have some flaw in their causal linkages
		for (Page pg: pages) {
			if ((pg.getCauseCount()==0)
					&(!pg.title.endsWith("Tax"))
					&(!pg.title.endsWith("Percent")))
				System.out.println(pg.title+" has no causes");
			if ((pg.getEffectCount()==0)&(!pg.title.equals("Total Score")))
					System.out.println(pg.title+" has no effects");
			for (int j=0; (j<pg.getCauseCount()); ++j) {
				String st=pg.getCause(j);
				int i=0;
				while ((i<pages.size())&&!st.equals(pages.get(i).title)) ++i;
				if (i==pages.size()) 
					System.out.println("missing page: "+st);
			}
			for (int j=0; (j<pg.getEffectCount()); ++j) {
				String st=pg.getEffect(j);
				int i=0;
				while ((i<pages.size())&&!st.equals(pages.get(i).title)) ++i;
				if (i==pages.size()) 
					System.out.println("missing page: "+st);
			}
		}
		Collections.sort(alphabetizedPages);
	}	
// ************************************************************	
	private void readBackgrounds() {
		// conventional method for read the backgrounder information
		FileInputStream fis=null;
		String testName=directoryName+"Backgrounds.xml";
		try { fis = new FileInputStream(testName); }
		catch (Exception e) { 
			e.printStackTrace(); 
		}
		DocumentBuilder builder = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try { builder = factory.newDocumentBuilder(); }
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		Document doc = null;
		try { doc = builder.parse(fis); }
		catch (Exception e) { 
			e.printStackTrace(); 
		}
		doc.getDocumentElement().normalize();
		NodeList termList = doc.getElementsByTagName("Term");
		for(int i=0;i<termList.getLength();i++) {
			final Node termNode = termList.item(i);
			if (termNode!=null) {
				String termLabel=termNode.getAttributes().getNamedItem("Title").getNodeValue();
				backgrounderText.put(termLabel, getNodeText(termNode, "Text"));
				backgrounderReference.put(termLabel, getNodeText(termNode, "ReferenceText"));
				alphabetizedBackgrounders.add(termLabel);
			}
		}		
		Collections.sort(alphabetizedBackgrounders);
	}	
// ************************************************************
	private static String getNodeText(Node parent, String tagName) {
		Node localNode=getChildByTagName(parent, tagName);
		String localString="";
		try {
			localString=localNode.getFirstChild().getNodeValue();
		} catch (NullPointerException e) {}
		return localString;
		
	}
// ************************************************************
	/** 
	 * @return the first child with a given tag name, or null if
	 * no such child exists.
	 * */
	
	private static Node getChildByTagName(Node parent,String tagName){
		NodeList nl = parent.getChildNodes();
		for (int i=0;i<nl.getLength();i++) {
			Node c = nl.item(i);
			if (c.getNodeName().equals(tagName))
				return c;
		}
		return null;
	}
	
// ************************************************************
	/** 
	 * @return the first child with a given tag name, or null if
	 * no such child exists.
	 * */
	
	private static Node getIndexedChildByTagName(Node parent,String tagName, int index){
		NodeList nl = parent.getChildNodes();
		int counter=0;
		for (int i=0;i<nl.getLength();i++) {
			Node c = nl.item(i);
			if (c.getNodeName().equals(tagName)) {
				if (counter==index)
					return c;
				else ++counter;
			}
		}
		return null;
	}	
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
	public void createHistoryDisplay() {
		ss.historyPanel.myPanel.repaint();		
	}
// ************************************************************	
	public String getWebImageFileName(String pageTitle) {
		String label="";
		if (pageTitle.equals("Annual Score")|pageTitle.equals("Total Score")) {
			double score=getPage("Total Score").getValue();
			label="Fireworks";
			if (score<0) label="Sinking";
			double level=Math.abs(score)/1000.0f;
			if (level>4) level=4;
			label+=String.valueOf((int)level);
		}
		else label=pageTitle;
		return "PageImages/"+label+".jpg";
	}
// ************************************************************
	public String getScoreImageFileName() {
		double score=getPage("Total Score").getValue();
		String label="Fireworks";
		if (score<0) label="Sinking";
		double level=Math.abs(score)/1000.0f;
		if (level>4) level=4;
		label+=String.valueOf((int)level);	
		
		return "PageImages/"+label+".jpg";
	}
// ************************************************************
	public void runSimulation() {
		Page savePage=thisPage;
		int nextYear=thisYear+turnLength;
		while ((thisYear<nextYear)&(thisYear<(firstYear+gameLength))) {
			for (Page pg: pages) {
				if (pg.title.endsWith("Tax")) {
					// special case for taxes, which are relaxed into place
					pg.history[thisYear-firstYear]=theModel.taxRelax(pg.value);
				}
				else
					pg.history[thisYear-firstYear]=pg.value;
			}
			for (Page pg: pages) {
				if (!pg.title.endsWith("Tax") & !pg.title.endsWith("Percent")
						& !pg.title.equals("Taxes") & !pg.title.equals("Subsidies") 
						 & !pg.title.equals("")) {
					pg.value=theModel.executePageFormula(pg.title);
					pg.history[thisYear-firstYear+1]=pg.value;
				}
			}
			++thisYear;
		}
		jumpToPage(savePage.title, false);	
		if (nextYear>=lastYear) { // end game
			ss.runButton.setText("Game over!");
			ss.runButton.setEnabled(false);
			displayType=historyDisplay;
			jumpToPage("Total Score", false);
			UserData userData=new UserData(this);
			currentTime=new Date();
			userData.writeUserDataFile(startingTime, currentTime.toString());
			ss.endGame();
		}
	}
// ************************************************************

}

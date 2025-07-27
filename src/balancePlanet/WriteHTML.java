package balancePlanet;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
/*
 *  This writes the HTML pages that will be served to the client.
 *  It has to differentiate between the various types of pages,
 *  which it figures out from the two String inputs.
 *  Currently it writes everything to a single file called "outputFile.html".
 *  I expect you'll be changing this arrangement quite a bit.
 */
public class WriteHTML extends Globals {
	FileWriter myFileWriter;
	String directoryName=System.getProperty("user.dir")+"/res/";
	BufferedWriter myBufferedWriter;
	Page thisPage;
	FormatStuff commonFormat;
	
	public WriteHTML() {
		commonFormat=new FormatStuff();
	}
// ************************************************************
	public void setupNewHTMLPage() {
		try {
			myFileWriter=new FileWriter(directoryName+"/outputFile.html");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
		myBufferedWriter=new BufferedWriter(myFileWriter);
		writeSingleLine("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		writeSingleLine("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		writeSingleLine("<head>");
	}
// ************************************************************	
	public void addCSSReference(String cssPage) {
		writeSingleLine("<link rel=\"stylesheet\" type=\"text/css\" href=\"CSSFiles/"+cssPage+".css\" />");
		writeSingleLine("</head>");
		writeSingleLine("<body>");		
	}
// ************************************************************
		public void closeHTMLPage() {
		writeSingleLine("</body>");
		writeSingleLine("</html>");
		writeSingleLine("");
		try {
			myBufferedWriter.flush();
			myBufferedWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
// ************************************************************
	public void writeMainPage(String title) {
		setupNewHTMLPage();
		addCSSReference("main");
		writeSingleLine("<div class=\"uppersection\">");
		writeSingleLine("<p class=\"titl\">"+title+"</p>");
		String valueString="";
		if ((thisPage.title.endsWith("Percent")))
			valueString=commonFormat.percentFormat.format(thisPage.getValue());
		else 
			valueString=commonFormat.myFormat(thisPage.format,thisPage.value);				
		writeSingleLine("<p class=\"subt\">"+valueString+" "+thisPage.units+"</p>");
		String positionString="";
		if (thisPage.title.endsWith("Percent"))
			positionString="style=\"position:relative; top:183px\"";
		else if (thisPage.title.endsWith("Tax"))
			positionString="style=\"position:relative; top:196px\"";
//		String fileName=getImageFileName(thisPage.title);
//		writeSingleLine("<img class=\"primary\" "+positionString+"src=\""+fileName+"\">");
		writeSingleLine("<div class=\"longtext\">");
		writeSingleLine(padText(thisPage.mainText,thisPage.referenceText));
		writeSingleLine("</div>");
		writeSingleLine("</div>");
		
		writeBottomPanel(true);
		closeHTMLPage();
	}
// ************************************************************	
	public void writeHistoryPage(String title) {
		history.calculateBars();
		setupNewHTMLPage();
		addCSSReference("history");
		writeSingleLine("<div class=\"uppersection\">");
		writeSingleLine("<p class=\"titl\">"+title+"</p>");
		
		String valueString="";
		if ((thisYear>2020)&(!thisPage.valueDisplayed.equals("None"))) {
			String upperString=getUpperGraphText();
			String lowerString=getLowerGraphText();
			writeSingleLine("<p class=\"subt\">"+upperString+"</p>");
			writeSingleLine("<p class=\"sub2t\">"+lowerString+"</p>");
		}
		else { // standard display, not bar chart display
			if ((thisPage.title.endsWith("Percent")))
				valueString=commonFormat.percentFormat.format(thisPage.getValue());
			else 
				valueString=commonFormat.myFormat(thisPage.format,thisPage.value);				
			writeSingleLine("<p class=\"subt\">"+valueString+" "+thisPage.units+"</p>");
		}

		// draw X-Axis
		if (thisPage.isPositiveOnly) 
			writeSingleLine("<img class=\"positiveonly\" src=\"GameImages/XAxis5.png\">");
		else
			writeSingleLine("<img class=\"positiveandnegative\" src=\"GameImages/BipolarXAxis5.png\">");

		// draw legend if necessary
		if (thisPage.isMultiColor) 
			writeSingleLine("<img class=\"legend\" src=\"GameImages/Legends/"+title+".png\">");
		
		// Herewith we write all 58 bars
		for (int i=0; (i<gameLength); ++i) {
			if (thisPage.isMultiColor) {
				for (int j=0; (j<thisPage.getCauseCount()); ++j) {
					int height=Math.abs(history.bars[i].getHeight(j));
					int top=history.bars[i].getTop(j)+88;
//					String barColor=history.bars[i].getColor(j);
					String barColor="blue";
					writeSingleLine("<iframe class=\""+barColor+"\";  scrolling=\"no\"; " +
							"height=\""+height+"\"; style=\"left:"+(27+i*10)+"px; top:"
							+top+"px\"></iframe>");
				}
			}
			else {
				int height=Math.abs(history.bars[i].getHeight(0));
				int top=history.bars[i].getTop(0)+88;
				writeSingleLine("<iframe class=\"blue\";  scrolling=\"no\"; " +
						"height=\""+height+"\"; style=\"left:"+(27+i*10)+"px; top:"
						+top+"px\"></iframe>");
			}
			// add net total dots
			if (title.equals("Annual Score")) {
				int dotHeight=history.bars[i].getDotHeight()+88;
				writeSingleLine("<img class=\"dot\" src=\"GameImages/dot.png\"; style=\"left:"+(27+i*10)+"px; top:"+dotHeight+"px\">");					
			}
		}

		writeSingleLine("<div class=\"longtext\">");
		writeSingleLine(padText(thisPage.mainText,thisPage.referenceText));
		writeSingleLine("</div>");
		writeBottomPanel(false);
		closeHTMLPage();
	}
// ************************************************************
	public void writeBackgrounderPage(String title) {		
		setupNewHTMLPage();
		addCSSReference("backgrounder");
		writeSingleLine("<p class=\"titl\">"+title+"</p>");
		writeSingleLine("<div class=\"backbutton\">");
		writeSingleLine("<button style=\"font-size:14px;\">Back!</button><br><br>");
		writeSingleLine("</div>");
		writeSingleLine("<img class=\"primary\" src=\"BackgroundImages/"+title+".jpg\">");
		writeSingleLine("<div class=\"longtext\">");
		writeSingleLine(padText(backgrounderText.get(title), backgrounderReference.get(title)));
		writeSingleLine("</textarea>");
		closeHTMLPage();
	}
// ************************************************************
	public void writeSplashScreenPage() {		
		setupNewHTMLPage();
		addCSSReference("splashscreen");
		writeSingleLine("<img src=\"GameImages/SplashScreena.jpg\">");
		writeSingleLine("<button class=\"level1\">Level 1: Connections</button>");
		writeSingleLine("<button class=\"level2\">Level 2: Basic Game</button>");
		writeSingleLine("<button class=\"level3\">Level 3: Environmentalist Bias</button>");
		writeSingleLine("<button class=\"level4\">Level 4: Industrialist Bias</button>");
		writeSingleLine("<button class=\"level5\">Level 5: Do Poor People Count?</button>");
		writeSingleLine("<button class=\"level6\">Level 6: For Geeks</button>");
		closeHTMLPage();
	}
// ************************************************************	
	public void writeIntroScreenPage(String title) {		
		setupNewHTMLPage();
		addCSSReference("introscreen");
		writeSingleLine("<img src=\"GameImages/"+title+"a.jpg\">");
		closeHTMLPage();
	}
// ************************************************************	
	public void writeBottomPanel(boolean isMainDisplay) {
		writeSingleLine("<div class=\"causebox\">");
		writeSingleLine("<p style=\"font-size:18px; font-family:arial; position:relative; top:-19px\">Causes</p>");		
		for (int i=0; (i<thisPage.getCauseCount()); ++i) {
			writeSingleLine("<button style=\"font-size:13px; position:relative; top:-40px\">"+thisPage.getCause(i)+"</button><br>");
		}						
		writeSingleLine("</div>");			
		
		writeSingleLine("<div class=\"navigationbox\">");
		writeSingleLine("<button style=\"font-size:13px; position:relative; top:20px\">Back</button>");
		writeSingleLine("<button style=\"font-size:13px; position:relative; top:20px\">Forward</button><br/>");
		if (isMainDisplay)
			writeSingleLine("<button style=\"font-size:13px; position:relative; top:20px\">Tallyho!</button><br/>");
		else
			writeSingleLine("<button style=\"font-size:13px; position:relative; top:20px\">Try Again</button><br/>");			
		writeSingleLine("<button style=\"font-size:13px; position:relative; top:20px\">Index</button><br>");
		writeSingleLine("<button style=\"font-size:13px; position:relative; top:20px\">Stupid game! I quit!</button><br>");
		writeSingleLine("</div>");
		
		writeSingleLine("<div class=\"effectbox\">");
		writeSingleLine("<p style=\"font-size:18px; font-family:arial; position:relative; top:-19px\">Effects</p>");
		for (int i=0; (i<thisPage.getEffectCount()); ++i) {
			writeSingleLine("<button style=\"font-size:12px; position:relative; top:-40px\">"+thisPage.getEffect(i)+"</button><br>");				
		}
		writeSingleLine("</div>");					
	}
// ************************************************************
	public void writeSingleLine(String theText) {
		try {
			myBufferedWriter.write(theText);
			myBufferedWriter.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}

package balancePlanet;

import java.lang.reflect.Method;

import javax.swing.JOptionPane;

public class CentralInput extends Globals {
	
// ************************************************************
	public void backToMainButton() {
		String linkName=backgrounderPathStack.get(backgrounderPathStack.size()-1);
		backgrounderPathStack.remove(backgrounderPathStack.size()-1);
		if (backgrounderPathStack.size()==0) { jumpToPage(linkName, false); }
		else { ss.drawBackgroundPanel(linkName); }
	}
// ************************************************************
	public void backButton() {
		--pagePathStackIndex;
		jumpToPage(pagePathStack.get(pagePathStackIndex), true);
		System.out.println(pagePathStackIndex+" "+pagePathStack.get(pagePathStackIndex));
		ss.navForwardButton.setEnabled(true);
		if (pagePathStackIndex==1)
			ss.navBackButton.setEnabled(false);
	}
// ************************************************************
	public void forwardButton() {
		jumpToPage(pagePathStack.get(++pagePathStackIndex), true);
		ss.navBackButton.setEnabled(true);
		if (pagePathStackIndex==(pagePathStack.size()-1))
			ss.navForwardButton.setEnabled(false);
	}
// ************************************************************
	public void newGameButton() {
		newGameFlag=true;
	}
// ************************************************************
	public void indexButton() {
		ss.indexFrame.setVisible(true);
	}
// ************************************************************
	public void connectionsButton() {
		ss.connectionsFrame.myFrame.setVisible(true);
	}
// ************************************************************
	public void quitButton() {
		quitFlag=true;		
	}
// ************************************************************
	public void aboutButton() {
		backgrounderPathStack.add(thisPage.title); // the title of the page about to be changed
		ss.drawBackgroundPanel("About Balance of the Planet");
	}
// ************************************************************
	public void nextTurnButton() {
		theGame.runSimulation();
	}
// ************************************************************
	// Currently handles both causes and effects from the web app
	public void causesButton(String cause) {
		jumpToPage(cause, false);
	}
// ************************************************************
	public void effectsButton(String effect) {
		jumpToPage(effect, false);
	}
// ************************************************************
	public void backgrounderButton(String backgrounderLabel) {
		// This is entered when the user clicks on a button 
		//   in the Index window
		backgrounderPathStack.add(thisPage.title); // the title of the page about to be changed
		ss.drawBackgroundPanel(backgrounderLabel);
		ss.mainFrame.toFront();		
	}
// ************************************************************
	public void indexMainButton(String pageTitle) {
		jumpToPage(pageTitle, false);
		ss.mainFrame.toFront();
	}
// ************************************************************
	public void hyperlinkButton(String url) {
		// This is entered when the user clicks on a hyperlink
		if (url.startsWith("http:")) {
			openURL(url);
		}
		else {
		// the title of the page about to be changed	
			backgrounderPathStack.add(ss.titleLabel.getText()); 			
			ss.drawBackgroundPanel(url);
		}
		
	}
// ************************************************************
   public static void openURL(String url) {
      String osName = System.getProperty("os.name");
      try {
         if (osName.startsWith("Mac OS")) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL",
               new Class[] {String.class});
            openURL.invoke(null, new Object[] {url});
            }
         else if (osName.startsWith("Windows"))
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
         else { //assume Unix or Linux
            String[] browsers = {
               "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
               if (Runtime.getRuntime().exec(
                     new String[] {"which", browsers[count]}).waitFor() == 0)
                  browser = browsers[count];
            if (browser == null)
               throw new Exception("Could not find web browser");
            else
               Runtime.getRuntime().exec(new String[] {browser, url});
            }
         }
      catch (Exception e) {
         JOptionPane.showMessageDialog(null, "Error attempting to launch web browser:\n" + e.getLocalizedMessage());
         }

   }
// ************************************************************
// ************************************************************
// ************************************************************
// ************************************************************
// ************************************************************
// ************************************************************
// ************************************************************

}

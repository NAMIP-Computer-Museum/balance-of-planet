package balancePlanet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;

public class SwingStuff extends Globals {
	static final int frameWidth=1024;	// width of Swing window
	static final int frameHeight=746;	// height of Swing window
	static final int maxCauses=20;	// maximum number of causal factors
	static final int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	JFrame mainFrame;			// primary window frame for the program
	JFrame indexFrame;		// window frame for the Index window
	ConnectionsMap connectionsFrame;		// window frame for the Index window

	// These are JPanels that compose the Swing display;
	//  I could make a map of them but I think it unnecessary given
	//  our intention to deliver output in HTML and use Swing only for testing.
	JPanel mainPanel=new JPanel();
	JPanel leftPanel=new JPanel();
	JPanel rightPanel=new JPanel();
	JPanel titleBox=new JPanel();
	JPanel bottomLeftPanel=new JPanel();
	JPanel bottomRightPanel=new JPanel();
	JPanel indexPanel=new JPanel();
	JPanel causesBox=new JPanel();
	JPanel navigationBox=new JPanel();
	JScrollPane causesScrollPane=new JScrollPane(causesBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	JPanel effectsBox=new JPanel();
	JScrollPane effectsScrollPane=new JScrollPane(effectsBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	JPanel taxSubPanel=new JPanel();
	JLabel dollarImage=new JLabel();
	JLabel titleLabel= new JLabel();
	JLabel valueLabel=new JLabel();
	JLabel totalLabel=new JLabel();
	JLabel dollarsLabel=new JLabel();
	JLabel causesLabel= new JLabel();
	JLabel effectsLabel= new JLabel();
	JLabel yearLabelA=new JLabel();
	JLabel yearLabelB=new JLabel();
	JButton aboutButton=new JButton("About this game");
	JButton indexButton=new JButton("Index");
	JButton connectionsButton=new JButton("Connections");
	JButton runButton=new JButton("Make it so!");
	JButton quitButton=new JButton("Quit");
	JButton newGameButton=new JButton("New Game");
	JButton backToGame=new JButton("Back");
	JButton navBackButton=new JButton("Back");
	JButton navForwardButton=new JButton("Forward");
	JButton taxesButton=new JButton("Taxes");
	JButton subsidiesButton=new JButton("Subsidies");
	JButton[] causesButtons=new JButton[maxCauses];
	JButton[] effectsButtons=new JButton[maxCauses];
//	ButtonGroup displayButtons=new ButtonGroup();
//	JRadioButton mainDisplayButton=new JRadioButton("Main Display");
//	JRadioButton historyDisplayButton=new JRadioButton("History Display");
	JTextPane mainTextPane=new JTextPane();
	JScrollPane mainScrollPane=new JScrollPane(mainTextPane);
	JScrollPane indexScrollPanel=new JScrollPane(indexPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	MultiScrollBar multiScrollBar; // defined in MultiScrollBar.java
	Formula formulaPanel; 	// defined in Formula.java; extends JPanel
	FormatStuff commonFormat;	
	SwingHistory historyPanel;
	String[] taxLabel=new String[4];
	String[] taxUnits=new String[4];
	JLabel[] taxLegend=new JLabel[4];
	JScrollBar[] taxScrollBar=new JScrollBar[5];
	int taxCount;
// ************************************************************
	public SwingStuff() {
//		System.setProperty("apple.awt.antialiasing", "on");
		System.setProperty("apple.awt.textantialiasing", "on");			
		mainFrame=new JFrame();
		mainFrame.setSize(frameWidth, frameHeight);
		mainFrame.setTitle("Balance of the Planet");
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		historyPanel=new SwingHistory(this);
		commonFormat=new FormatStuff();		
		initWindow(); // sets up the various JPanels
		mainFrame.remove(mainPanel);
	}
// ************************************************************
   // A general-purpose routine that sets the basic parameters of JPanels
   //  used in the display
   public void setupJPanel(JPanel subject, boolean verticalLayout, int width, int height, Color backgroundColor) {
   	subject.setBackground(backgroundColor);
   	if (verticalLayout)
   		subject.setLayout(new BoxLayout(subject, BoxLayout.Y_AXIS));
   	else
   		subject.setLayout(new BoxLayout(subject, BoxLayout.X_AXIS));
   	subject.setMinimumSize(new Dimension(width,height));
   	subject.setPreferredSize(new Dimension(width,height));
   	subject.setMaximumSize(new Dimension(width,height));
   }
// ************************************************************	
	public void postReset() {
		// resets all game elements to their initial conditions
		runButton.setText("Run Simulation");
		runButton.setEnabled(true);
		mainFrame.add(mainPanel);
		connectionsFrame=new ConnectionsMap();
				
		indexPanel.add(Box.createVerticalStrut(2));
		JLabel titleIndex=new JLabel("Pages");
		titleIndex.setFont(new Font("sansserif", Font.PLAIN, 24));
		titleIndex.setAlignmentX(Component.CENTER_ALIGNMENT);
		indexPanel.add(titleIndex);
		indexPanel.add(Box.createVerticalStrut(5));
		for (int i=1; (i<alphabetizedPages.size()); ++i) {
			String title=alphabetizedPages.get(i);
			JButton jb=new JButton(title);
			jb.setAlignmentX(Component.CENTER_ALIGNMENT);
				jb.setForeground(getPage(title).getButtonColor());
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ci.indexMainButton(e.getActionCommand());
				}
			});
			indexPanel.add(jb);
			indexPanel.add(Box.createVerticalStrut(2));
		}
		if (difficultyLevel==beginnerLevel) taxCount=1;
		if (difficultyLevel==intermediateLevel) taxCount=3;
		if (difficultyLevel==advancedLevel) taxCount=4;
		// set up tax sliders
		taxSubPanel.setLayout(new BoxLayout(taxSubPanel, BoxLayout.Y_AXIS));
		taxSubPanel.setBackground(myBackgroundColor);
		taxSubPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		taxSubPanel.removeAll();
		for (int i=0; (i<taxCount); ++i) {
			taxSubPanel.add(Box.createHorizontalStrut(24));
			taxSubPanel.add(taxLegend[i]);
			JPanel microPanel=new JPanel();
			microPanel.setLayout(new BoxLayout(microPanel, BoxLayout.X_AXIS));
			microPanel.setBackground(myBackgroundColor);
			microPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
			microPanel.add(Box.createHorizontalStrut(24));
			microPanel.add(taxScrollBar[i]);
			microPanel.add(Box.createHorizontalStrut(24));
			taxSubPanel.add(microPanel);
		}
		taxSubPanel.add(Box.createHorizontalStrut(24));
		mainFrame.pack();
   				
		// add the Backgrounder Index
		indexPanel.add(Box.createVerticalStrut(10));
		JLabel titleBack=new JLabel("Backgrounders");
		titleBack.setFont(new Font("sansserif", Font.PLAIN, 24));
		titleBack.setAlignmentX(Component.CENTER_ALIGNMENT);
		indexPanel.add(titleBack);
		indexPanel.add(Box.createVerticalStrut(5));
		for (int i=0; (i<alphabetizedBackgrounders.size()); ++i) {
			String title=alphabetizedBackgrounders.get(i);
			JButton jb=new JButton(title);
			jb.setForeground(new Color(48,0,0));
			jb.setAlignmentX(Component.CENTER_ALIGNMENT);
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ci.backgrounderButton(e.getActionCommand());
				}
			});
			indexPanel.add(jb);
			indexPanel.add(Box.createVerticalStrut(2));
		}
		indexScrollPanel.getVerticalScrollBar().setUnitIncrement(8);
		indexFrame.getContentPane().add(indexScrollPanel);
		multiScrollBar=new MultiScrollBar();
		composeMainDisplay();
		mainFrame.pack();
		mainFrame.repaint();
	}
// ************************************************************
	public void endGame() {
		runButton.setText("Game over!");
		runButton.setEnabled(false);
	}
// ************************************************************	
	// This implements all jumps from one page to another
	public void swingJumpToPage(String pageTitle, boolean fromNavigationButton) {
		for (int i=0; (i<maxCauses); ++i) {
			if (i<thisPage.getCauseCount()) {
				causesButtons[i].setText(thisPage.getCause(i));
				causesButtons[i].setVisible(true);
			}
			else {
				causesButtons[i].setVisible(false);
			}
			if (i<thisPage.getEffectCount()) {
				effectsButtons[i].setText(thisPage.getEffect(i));
				effectsButtons[i].setVisible(true);
			}
			else {
				effectsButtons[i].setVisible(false);
			}
		}

//		formulaButton.setEnabled(playLevel>4);
//		if (difficultyLevel==expertLevel)// we don't need to reset the formula page
//			formulaPanel.reset();
		composeMainDisplay();
	}
// ************************************************************
	public void initWindow() {
		// This method sets up the basic arrangement of the window that
		//   does not change during the course of play.
		//   Elements of the display that do change are handled with DrawWindow()
		

		indexFrame=new JFrame();
		indexFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		indexFrame.setSize(300, frameHeight-50);
		indexFrame.setLocationRelativeTo(mainFrame);
		indexFrame.setVisible(false);
		indexFrame.setTitle("Index");
		indexPanel.setLayout(new BoxLayout(indexPanel, BoxLayout.Y_AXIS));
		indexPanel.setBackground(Color.white);

		titleBox.add(titleLabel);
		titleBox.add(valueLabel);
		titleBox.add(totalLabel);
		titleBox.add(Box.createRigidArea(new Dimension(50,10)));

		taxLabel[0]="Carbon Dioxide";
		taxLabel[1]="Air Pollution";
		taxLabel[2]="Gasoline";	
		taxLabel[3]="Radioactive Emissions";
		taxUnits[0]="ton";
		taxUnits[1]="ton";
		taxUnits[2]="gallon";
		taxUnits[3]="Sievert";
		
		for (int i=0; (i<4); ++i) {
			taxScrollBar[i]=new JScrollBar(JScrollBar.HORIZONTAL, 0,0,0,100);
			taxLegend[i]=new JLabel(taxLabel[i]+": 0 dollars per "+taxUnits[i]);
			taxScrollBar[i].addAdjustmentListener(new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent arg0) {
					int iScrollBar=0;
					while ((iScrollBar<5)&(arg0.getAdjustable()!=taxScrollBar[iScrollBar])) ++iScrollBar;
					Page taxPage=getPage(taxLabel[iScrollBar]+" Tax");
					taxPage.value=arg0.getValue()*taxPage.maxValue/100.0f;
					taxLegend[iScrollBar].setText(taxLabel[iScrollBar]+" Tax: "+commonFormat.myFormat(taxPage.format,taxPage.value)+" "+taxPage.units);
				}				
			});
		}
						
		navigationBox.add(navBackButton);
		navigationBox.add(Box.createRigidArea(new Dimension(150,10)));
		navigationBox.add(navForwardButton);
		navigationBox.add(Box.createRigidArea(new Dimension(150,10)));
		navigationBox.add(taxesButton);
		navigationBox.add(Box.createRigidArea(new Dimension(150,10)));
		navigationBox.add(subsidiesButton);

		bottomLeftPanel.add(causesScrollPane);
		bottomLeftPanel.add(navigationBox);
		bottomLeftPanel.add(effectsScrollPane);

		setupJPanel(mainPanel,false,frameWidth,frameHeight, myBackgroundColor);
		setupJPanel(leftPanel,true,650,frameHeight, myBackgroundColor);
		setupJPanel(rightPanel,true,374,frameHeight, myBackgroundColor);
		setupJPanel(titleBox,true,650,107, myBackgroundColor);
		setupJPanel(bottomLeftPanel,false,650,179, alternateColor);
		setupJPanel(bottomRightPanel,false,374,179, alternateColor);
		
		causesBox.setLayout(new BoxLayout(causesBox, BoxLayout.Y_AXIS));
		navigationBox.setLayout(new BoxLayout(navigationBox, BoxLayout.Y_AXIS));
		effectsBox.setLayout(new BoxLayout(effectsBox, BoxLayout.Y_AXIS));
		
		causesScrollPane.setBorder(new LineBorder(new Color(0,0,0)));
		effectsScrollPane.setBorder(new LineBorder(new Color(0,0,0)));
		
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		dollarImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		dollarsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		causesBox.setAlignmentY(Component.TOP_ALIGNMENT);
		navBackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		navForwardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		taxesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		subsidiesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		navigationBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		effectsBox.setAlignmentY(Component.TOP_ALIGNMENT);
		causesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		effectsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		indexButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		connectionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		runButton.setAlignmentX(Component.CENTER_ALIGNMENT);		
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);		
		newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);		
		
		titleLabel.setBackground(myBackgroundColor);
		valueLabel.setBackground(myBackgroundColor);
		dollarImage.setBackground(myBackgroundColor);
		totalLabel.setBackground(myBackgroundColor);
		dollarsLabel.setBackground(myBackgroundColor);
		causesBox.setBackground(alternateColor);
		navigationBox.setBackground(alternateColor);
		causesScrollPane.setBackground(alternateColor);
		effectsBox.setBackground(alternateColor);
		causesLabel.setBackground(myBackgroundColor);
		effectsLabel.setBackground(myBackgroundColor);
		mainTextPane.setBackground(myBackgroundColor);
//		mainDisplayButton.setBackground(alternateColor);
//		historyDisplayButton.setBackground(alternateColor);
//		formulaDisplayButton.setBackground(alternateColor);
//		formulaDisplayButton.setEnabled(false);

		titleLabel.setFont(new Font("serif", Font.PLAIN, 40));
		valueLabel.setFont(new Font("serif", Font.PLAIN, 24));
		totalLabel.setFont(new Font("serif", Font.PLAIN, 24));
		dollarsLabel.setFont(new Font("serif", Font.PLAIN, 30));
		causesLabel.setText("Causes");
		effectsLabel.setText("Effects");
		causesLabel.setFont(new Font("sansserif", Font.PLAIN, 24));
		effectsLabel.setFont(new Font("sansserif", Font.PLAIN, 24));
		
		causesBox.add(causesLabel);
		effectsBox.add(effectsLabel);
		causesBox.add(Box.createVerticalStrut(10));
		effectsBox.add(Box.createVerticalStrut(10));
		for (int i=0; (i<maxCauses); ++i) {
			causesButtons[i]=new JButton();
			causesButtons[i].setAlignmentX(JButton.CENTER_ALIGNMENT);
			causesButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ci.causesButton(e.getActionCommand());
				}
			});
			causesBox.add(causesButtons[i]);
			causesBox.add(Box.createVerticalStrut(4));

			effectsButtons[i]=new JButton();
			effectsButtons[i].setAlignmentX(JButton.CENTER_ALIGNMENT);
			effectsButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ci.effectsButton(e.getActionCommand());
				}
			});
			effectsBox.add(effectsButtons[i]);
			effectsBox.add(Box.createVerticalStrut(4));
		}
		causesBox.add(Box.createHorizontalStrut(250));
		effectsBox.add(Box.createHorizontalStrut(250));

		dollarImage.setIcon(new ImageIcon(System.getProperty("user.dir")+"/res/GameImages/dollar.jpg"));
		
		JPanel controlPanel=new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.setBackground(alternateColor);
		controlPanel.add(Box.createVerticalStrut(10));
		
		JPanel leftSubPanel=new JPanel();
		leftSubPanel.setLayout(new BoxLayout(leftSubPanel, BoxLayout.Y_AXIS));
		leftSubPanel.setBackground(alternateColor);	
		yearLabelA.setFont(new Font("serif", Font.PLAIN, 18));
		yearLabelA.setText("The year is:");
		
		yearLabelB.setFont(new Font("sansserif", Font.PLAIN, 36));
		yearLabelB.setText(((Integer)(thisYear)).toString());
		
		leftSubPanel.add(yearLabelA);
//		leftSubPanel.add(Box.createVerticalStrut(20));
//		leftSubPanel.add(mainDisplayButton);
		leftSubPanel.add(yearLabelB);
		leftSubPanel.add(Box.createVerticalStrut(40));
//		leftSubPanel.add(historyDisplayButton);
		leftSubPanel.add(aboutButton);
		leftSubPanel.add(Box.createVerticalStrut(10));
//		leftSubPanel.add(formulaDisplayButton);
//		leftSubPanel.add(Box.createVerticalStrut(10));

		JPanel rightSubPanel=new JPanel();
		rightSubPanel.setLayout(new BoxLayout(rightSubPanel, BoxLayout.Y_AXIS));
		rightSubPanel.setBackground(alternateColor);
		rightSubPanel.setAlignmentX(JButton.LEFT_ALIGNMENT);
		rightSubPanel.add(Box.createVerticalStrut(10));
		rightSubPanel.add(runButton);
		rightSubPanel.add(Box.createVerticalStrut(10));
		rightSubPanel.add(indexButton);
		rightSubPanel.add(Box.createVerticalStrut(10));
		rightSubPanel.add(connectionsButton);
		rightSubPanel.add(Box.createVerticalStrut(10));
		rightSubPanel.add(newGameButton);
		rightSubPanel.add(Box.createVerticalStrut(10));
		rightSubPanel.add(quitButton);
		rightSubPanel.add(Box.createVerticalStrut(10));
		
		controlPanel.add(leftSubPanel);
		controlPanel.add(rightSubPanel);
		
		bottomRightPanel.add(controlPanel);
		bottomRightPanel.add(Box.createHorizontalStrut(4));
		bottomRightPanel.add(Box.createHorizontalStrut(4));
		bottomRightPanel.setMaximumSize(new Dimension(372,180));
		
		HyperlinkListener fako=new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent h) {
				if (h.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
					ci.hyperlinkButton(h.getDescription());
				}
			}
		};
		
		mainTextPane.addHyperlinkListener(fako);
		mainTextPane.setContentType("text/html; charset=EUC-JP");
		DefaultCaret myCaret=(DefaultCaret)mainTextPane.getCaret();
		myCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		mainTextPane.setFont(new Font("serif", Font.PLAIN, 18));
		mainTextPane.setMargin(new Insets(20,20,8,20));
		mainTextPane.setEditable(false);
		
		rightPanel.add(mainScrollPane);
		rightPanel.add(bottomRightPanel);
		
		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		mainFrame.add(mainPanel);
		
		indexButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.indexButton();
			}
		});
		connectionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.connectionsButton();
			}
		});
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.nextTurnButton();
			}
		});
		aboutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.aboutButton();
			}
		});

//		mainDisplayButton.setSelected(true);
//		displayButtons.add(mainDisplayButton);
//		displayButtons.add(historyDisplayButton);
//		displayButtons.add(formulaDisplayButton);
/*	
		mainDisplayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayType=mainDisplay;
				composeMainDisplay();
				mainFrame.repaint();
			}
		});
		historyDisplayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayType=historyDisplay;
				composeMainDisplay();
			}
		});
		*/
		/*
		formulaDisplayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayType=formulaDisplay;
				formulaPanel.reset();
				composeMainDisplay();
				bottomLeftPanel.revalidate();
			}
		});
		*/
		backToGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.backToMainButton();
			}
		});

		navBackButton.setEnabled(false);
		navBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.backButton();
			}
		});
		
		navForwardButton.setEnabled(false);
		navForwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.forwardButton();
			}
		});

		taxesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jumpToPage("Taxes", false);
			}
		});
		
		subsidiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jumpToPage("Subsidies", false);
			}
		});

		newGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				indexFrame.setVisible(false);
				indexPanel.removeAll();
				ci.newGameButton();
			}
		});

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ci.quitButton();
			}
		});
}
// ************************************************************
	public void composeMainDisplay() {
		// primary drawing routine for pages	
		yearLabelB.setText(((Integer)(thisYear)).toString());
		bottomRightPanel.setVisible(true);
		valueLabel.setVisible(true);
		totalLabel.setVisible(true);
		JLabel imageLabel= new JLabel();
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageLabel.setBackground(myBackgroundColor);
		String fileName=getImageFileName(thisPage.title);
		try { imageLabel.setIcon(new ImageIcon(fileName)); } 
		catch (Exception e) { System.out.println("could not find image: "+fileName); }
		
		mainTextPane.setText(padText(thisPage.mainText, thisPage.referenceText));
		mainScrollPane.getVerticalScrollBar().setValue(0);
		
		titleBox.removeAll();
		titleBox.add(titleLabel);
		titleBox.add(valueLabel);
		titleLabel.setText(thisPage.title);
		valueLabel.setText("");
		// the second boolean term is a special-case provision for Taxes, Subsidies, and "Total Score"
		if ((thisYear>2020)&(!thisPage.valueDisplayed.equals("None"))) {
			totalLabel.setText(getUpperGraphText());
			valueLabel.setText(getLowerGraphText());
			titleBox.add(totalLabel);
		}
		else { // standard display, not bar chart display
			if ((thisPage.title.endsWith("Percent"))) {
				valueLabel.setText(commonFormat.percentFormat.format(thisPage.getValue()));
			}	
			else 
				valueLabel.setText(commonFormat.myFormat(thisPage.format,thisPage.value)+" "+thisPage.units);				
		}			
		
		leftPanel.removeAll();
		leftPanel.add(Box.createRigidArea(new Dimension(650,0)));
		leftPanel.add(titleBox);
		leftPanel.add(Box.createVerticalStrut(22));				
		if (displayType==historyDisplay) {
			leftPanel.add(historyPanel.myPanel);
			historyPanel.myPanel.repaint();
		}
		if (displayType==formulaDisplay) {
			leftPanel.add(formulaPanel);
		}
		if (displayType==mainDisplay) {
			if (thisPage.title.endsWith("Tax")) {
				leftPanel.add(Box.createVerticalStrut(130));
				leftPanel.add(imageLabel);
				leftPanel.add(Box.createVerticalStrut(130));
//				double x=100.0f*thisPage.value/thisPage.maxValue;
//				taxRegScrollBar.setValue((int)x);
//				taxRegScrollBar.setMinimum(0);
//				taxRegScrollBar.setMaximum(100);
//				taxRegScrollBar.setUnitIncrement(1);
//				taxRegScrollBar.setBlockIncrement(10);
//				leftPanel.add(taxSubPanel);
			}
			else if (thisPage.title.equals("Taxes")) {
				titleBox.removeAll();
				titleBox.add(titleLabel);
				leftPanel.add(dollarImage);
				for (int i=0; (i<taxCount); ++i) {
					Page taxPage=getPage(taxLabel[i]+" Tax");
					double x=100.0f*taxPage.value/taxPage.maxValue;
					taxScrollBar[i].setValue((int)x);
					taxScrollBar[i].setMinimum(0);
					taxScrollBar[i].setMaximum(100);
					taxScrollBar[i].setUnitIncrement(1);
					taxScrollBar[i].setBlockIncrement(10);
				}
				leftPanel.add(taxSubPanel);
			}
			else if (thisPage.title.endsWith("Percent")) {
				leftPanel.add(dollarsLabel);
				leftPanel.add(Box.createVerticalStrut(84));
				leftPanel.add(imageLabel);
				leftPanel.add(Box.createVerticalStrut(175));
			}
			else if (thisPage.title.equals("Subsidies")) {
				titleBox.remove(valueLabel);
				multiScrollBar.positionRects();
				leftPanel.add(multiScrollBar.superPanel);
				leftPanel.add(Box.createVerticalStrut(140));				
			}
			else // this is the default condition
				leftPanel.add(imageLabel);
		}
		leftPanel.add(bottomLeftPanel);	
		imageLabel.revalidate();
		leftPanel.repaint();
		mainFrame.repaint();
	}
// ************************************************************
	public void drawBackgroundPanel(String title) {
		JLabel imageLabel=new JLabel(new ImageIcon(directoryName+"BackgroundImages/"+title+".jpg")); 
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageLabel.setBackground(myBackgroundColor);
		valueLabel.setVisible(false);
		totalLabel.setVisible(false);
		bottomRightPanel.setVisible(false);
		titleLabel.setText(title);
		mainScrollPane.getVerticalScrollBar().setValue(0);
		mainTextPane.setText(padText(backgrounderText.get(title), backgrounderReference.get(title)));
		leftPanel.removeAll();
		leftPanel.add(Box.createRigidArea(new Dimension(650,0)));
		leftPanel.add(titleLabel);
		leftPanel.add(Box.createRigidArea(new Dimension(650,10)));
		leftPanel.add(backToGame);
		leftPanel.add(Box.createRigidArea(new Dimension(650,15)));
		leftPanel.add(imageLabel);
		leftPanel.add(Box.createVerticalStrut(60));
	}
// ************************************************************
	// This is an unused experiment to see if it might be
	// a better way to show the current score. It seems too subtle
	// in effect to do the job.
	public BufferedImage mergedEarths(double weight) {
      BufferedImage goodEarth=null;
      BufferedImage badEarth=null;
		try {
			goodEarth=ImageIO.read(new File(directoryName+"GameImages/GoodEarth.png"));		
			badEarth=ImageIO.read(new File(directoryName+"GameImages/BadEarth.png"));		
      } catch (Exception e) { }
	   int width = goodEarth.getWidth ();
	   int height = goodEarth.getHeight ();

	   BufferedImage merger = new BufferedImage (width, height,BufferedImage.TYPE_INT_RGB);
	   int [] rgbim1 = new int [width];
	   int [] rgbim2 = new int [width];
	   int [] rgbim3 = new int [width];

	   for (int row = 0; row < height; row++)
	   {
	   	goodEarth.getRGB (0, row, width, 1, rgbim1, 0, width);
	   	badEarth.getRGB (0, row, width, 1, rgbim2, 0, width);

	      for (int col = 0; col < width; col++)
	      {
	        int rgb1 = rgbim1 [col];
	        int r1 = (rgb1 >> 16) & 255;
	        int g1 = (rgb1 >> 8) & 255;
	        int b1 = rgb1 & 255;

	        int rgb2 = rgbim2 [col];
	        int r2 = (rgb2 >> 16) & 255;
	        int g2 = (rgb2 >> 8) & 255;
	        int b2 = rgb2 & 255;

	        int r3 = (int) (r1*weight+r2*(1.0-weight));
	        int g3 = (int) (g1*weight+g2*(1.0-weight));
	        int b3 = (int) (b1*weight+b2*(1.0-weight));
	        rgbim3 [col] = (r3 << 16) | (g3 << 8) | b3;
	      }

	      merger.setRGB (0, row, width, 1, rgbim3, 0, width);
	   }
	   return merger;
	}
// ************************************************************
	public String getImageFileName(String pageTitle) {
		String label="";
		if (pageTitle.equals("Annual Score")|pageTitle.equals("Total Score")) {
			double score=getPage(pageTitle).getValue();
			label="Fireworks";
			if (score<0) label="Sinking";
			double level=Math.abs(score)/1000.0f;
			if (level>4) level=4;
			label+=String.valueOf((int)level);
		}
		else label=pageTitle;
		return directoryName+"PageImages/"+label+".jpg";
	}
// ************************************************************
	public void enableNavBackButton(boolean newValue) {
		navBackButton.setEnabled(newValue);
	}
// ************************************************************
	public void enableNavForwardButton(boolean newValue) {
		navForwardButton.setEnabled(newValue);
	}
// ************************************************************
// ************************************************************

}

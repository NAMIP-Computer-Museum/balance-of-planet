package balancePlanet;

/*
 * This class is way out of date; its purpose is to present a
 * page showing the formulas used in a page of the model. I'll
 * be completely rewriting it once I get the more basic stuff
 * in better shape. 
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;

public class Formula extends JPanel {
	private static final long serialVersionUID=1L;
	Globals gb;
	JTextPane theText=new JTextPane();
	ArrayList<CoefficientBox> cBoxes=new ArrayList<CoefficientBox>();
	
	public Formula(Globals gbls) {
		gb=gbls;
//		gb.ss.setupJPanel(this,true,650,460, gb.getMyBackgroundColor());
		theText.setMargin(new Insets(20,20,8,20));
		theText.setEditable(false);
		theText.setBackground(Globals.myBackgroundColor);				
		theText.setContentType("text/html; charset=EUC-JP");
		DefaultCaret myCaret=(DefaultCaret)theText.getCaret();
		myCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		
		add(theText);
		for (int i=0; (i<gb.thisPage.coefficients.size()); ++i) {
			cBoxes.add(new CoefficientBox(i));
			add(cBoxes.get(i));
		}
	}
// ************************************************************
	public void reset() {
		removeAll();
		cBoxes.clear();
		theText.setText("<p style=\"font-size:130%\">"+gb.thisPage.formulaText+"</p>");
		add(theText);
		for (int i=0; (i<gb.thisPage.coefficients.size()); ++i) {
			cBoxes.add(new CoefficientBox(i));
			cBoxes.get(i).reset();
			add(cBoxes.get(i));
		}
	}
// ************************************************************	
	private class CoefficientBox extends JPanel {
		private static final long serialVersionUID=1L;
		double coefficient;
		JLabel title;
		JLabel value;
		JLabel units;
		JScrollBar scroller;
		double scaler;
		int index;
	   NumberFormat doubleFormat=NumberFormat.getInstance();
				
		public CoefficientBox(int tIndex) {
			gb.ss.setupJPanel(this,true,600,80, gb.myBackgroundColor);
			JPanel upperHalf=new JPanel();
			gb.ss.setupJPanel(upperHalf,false,600,40, gb.myBackgroundColor);
			index=tIndex;
			title=new JLabel();
			title.setAlignmentX(Component.CENTER_ALIGNMENT);
			title.setFont(new Font("Georgia", Font.PLAIN, 24));
			title.setForeground(Color.red);
			upperHalf.add(title);
		
			coefficient=0;
			value=new JLabel(doubleFormat.format(coefficient));
			value.setAlignmentX(Component.CENTER_ALIGNMENT);
			value.setFont(new Font("Georgia", Font.PLAIN, 24));
			upperHalf.add(Box.createRigidArea(new Dimension(20,10)));
			upperHalf.add(value);
			
			units=new JLabel();
			units.setFont(new Font("Georgia", Font.PLAIN, 24));
			upperHalf.add(units);
				
			scroller=new JScrollBar(JScrollBar.HORIZONTAL, 0,0,0,10000);
			scroller.setBlockIncrement(1000);
			scroller.setUnitIncrement(100);
			scroller.addAdjustmentListener(new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent arg0) {
					coefficient=((double)scroller.getValue())/scaler;
					gb.thisPage.coefCurrentValue.set(index,coefficient);
					value.setText(doubleFormat.format(coefficient));
				}				
			});
			scroller.setAlignmentX(Component.CENTER_ALIGNMENT);

			add(upperHalf);
			add(scroller);
		}
//---------------------------------------------------------
		public void reset() {
			title.setText(gb.thisPage.coefficients.get(index)+":");
			coefficient=gb.thisPage.getCoefficientValue(index);
			
			double maxValue=gb.thisPage.coefMaxValue.get(index);
			double minValue=gb.thisPage.coefMinValue.get(index);
			scaler=10000/(maxValue-minValue);
			int initialValue=(int)(coefficient*scaler);
			scroller.setValue(initialValue);
			int minimumValue=(int)(minValue*scaler);
			scroller.setMinimum(minimumValue);
			int maximum=(int)(maxValue*scaler);
			scroller.setMaximum(maximum);
			scroller.setBlockIncrement(maximum/20);
			scroller.setUnitIncrement(maximum/200);

			double x=Math.log10(coefficient);
			if (x<0) {
				int ix=(int)-x;
				doubleFormat.setMaximumFractionDigits(ix+2);
			}
			else 				
				doubleFormat.setMaximumFractionDigits(2);
			value.setText(doubleFormat.format(coefficient));
			units.setText(" "+gb.thisPage.coefficientUnits.get(index));
			
			revalidate();
		}
	}
// ************************************************************	
	/*
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.white);
		g.fillRect(0,0,650,460);
		g.setColor(Color.black);
		setText(gb.thisPage.formulaText);
//		g.drawString(gb.thisPage.formulaText, 100,100);
	}
	*/
}

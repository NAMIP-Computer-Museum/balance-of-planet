package balancePlanet;

import java.awt.Color;

/*
 * This is a tiny class used to represent the bars on the history bar charts.
 */
public class Bar {
	final static int maxSubBars=8;
	Color[] color=new Color[maxSubBars];
	int[] top=new int[maxSubBars];
	int[] height=new int[maxSubBars];
	int dotHeight;
	int subBarCount;
//-----------------------------------------------------------------	
	public Bar(int howManySubBars) {
		subBarCount=howManySubBars;
	}
//-----------------------------------------------------------------
	public Color getColor(int index) {
		return color[index];
	}
//-----------------------------------------------------------------	
	public int getTop(int index) {
		return top[index];
	}
//-----------------------------------------------------------------	
	public int getHeight(int index) {
		return height[index];
	}
//-----------------------------------------------------------------	
	public int getSubBarCount() {
		return subBarCount;
	}
//-----------------------------------------------------------------	
	public int getDotHeight() {
		return dotHeight;
	}
//-----------------------------------------------------------------	
	public void setDotHeight(int newHeight) {
		dotHeight=newHeight;
	}
//-----------------------------------------------------------------	
	public void setAllValues(int index, Color newColor, int newTop, int newHeight) {
		color[index]=newColor;
		top[index]=newTop;
		height[index]=newHeight;
	}
//-----------------------------------------------------------------	

}

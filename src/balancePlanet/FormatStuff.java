package balancePlanet;

import java.text.NumberFormat;

public class FormatStuff {
   public NumberFormat integerFormat=NumberFormat.getIntegerInstance();
   public NumberFormat doubleFormat=NumberFormat.getInstance();
   public NumberFormat currencyFormat=NumberFormat.getCurrencyInstance();
   public NumberFormat percentFormat=NumberFormat.getPercentInstance();
   public NumberFormat percentFormat2=NumberFormat.getPercentInstance();
   public NumberFormat fractionFormat=NumberFormat.getInstance();
   
// ************************************************************
   public FormatStuff() {
		integerFormat.setMaximumFractionDigits(0);
		doubleFormat.setMaximumFractionDigits(2);
		currencyFormat.setMaximumFractionDigits(0);
		percentFormat.setMaximumFractionDigits(0);
		percentFormat2.setMaximumFractionDigits(2);
		fractionFormat.setMaximumFractionDigits(4);
   }
// ************************************************************
	public String myFormat(String formatType, double inValue) {
		// formats a double value for the particular page
		if (formatType.equals("Integer"))
			return integerFormat.format(inValue);
		else if (formatType.equals("Double"))
			return doubleFormat.format(inValue);
		else if (formatType.equals("Currency"))
			return currencyFormat.format(inValue);
		else if (formatType.equals("Percent"))
			return percentFormat.format(inValue);
		else if (formatType.equals("Percent2"))
			return percentFormat2.format(inValue);
		else if (formatType.equals("Fraction"))
			return fractionFormat.format(inValue);
		else return "bad Format: "+formatType;
	}

}

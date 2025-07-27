package balancePlanet;

/*
 * This is analogous to the Engine class in Storytron. It's all
 * computations and is really hairy and need not concern you in
 * the slightest.
 */
public class Model extends Globals {
	double result;
	String title;
// ************************************************************
	public Model() { // Level 3
	}
// ************************************************************
	public double executePageFormula(String pageTitle) {
		java.lang.reflect.Method thisMethod=null;
		title=pageTitle;
		try {
			if (!pageTitle.equals("no method")) {
				String codeLabel=pageTitle.toLowerCase();
				codeLabel=codeLabel.replace(' ','_');
				try {
					thisMethod=Model.class.getMethod(codeLabel, (Class<?>[])null);
				} catch (java.lang.NoSuchMethodException e) {
					System.out.println("error: no such method: " + codeLabel);
				}
			}
			return (Double)thisMethod.invoke(this, (Object[])null);
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("IllegalArgumentException with " + pageTitle);
		} catch (java.lang.IllegalAccessException e) {
			System.out.println("IllegalAccessException with " + pageTitle);
		} catch (java.lang.reflect.InvocationTargetException e) {
			System.out.println("InvocationTargetException with " + pageTitle);
		} catch (java.lang.NullPointerException e) {
			System.out.println("NullPointerException: " + pageTitle);
//			throw new RuntimeException(e);
		}
		return result;
	}
// ************************************************************
	double getPageValue(String pageTitle) {
		return getPage(pageTitle).getValue();
	}
// ************************************************************
	double getInitialValue(String pageTitle) {
		return getPage(pageTitle).getInitialValue();
	}
// ************************************************************
	double getPreviousValue(String pageTitle) {
		return getPage(pageTitle).getPreviousValue();
	}
// ************************************************************
	double getPageCoefficientValue(String pageTitle, int index) {
		return getPage(pageTitle).getCoefficientValue(index);
	}
// ************************************************************
	private double relax(double sluggishness) {
		double currentValue=getPage(title).getValue();
		return (currentValue+(result-currentValue)/sluggishness);
	}
// ************************************************************
	public double air_pollution_tax_income() { // Level 2
		double x=getPageValue("Air Pollution Tax");
		double y=getPageValue("Air Pollution")/1000.0f;
		result=x*y;
		return result;
	}
// ************************************************************
	public double radioactive_emissions_tax_income() { // Level 2
		double x=getPageValue("Radioactive Emissions Tax");
		double y=getPageValue("Radioactive Emissions")/1000000000.0f;
		result=x*y;
		return result;
	}
// ************************************************************
	public double carbon_dioxide_tax_income() { // Level 0
		double x=getPageValue("Carbon Dioxide Tax");
		double y=getPageValue("Anthropogenic CO2 Emissions");
		result=x*y;
		return result;
	}
// ************************************************************
	public double gasoline_tax_income() { // Level 2
		double x=getPageValue("Gasoline Tax");
		double y=getPageValue("Gasoline Use")/1000.0f;
		result=x*y;
		return result;
	}
// ************************************************************
	public double research_subsidy_amount() { // Level 1
		double percent=getPageValue("Research Subsidy Percent");
		double piggyBank=getPageValue("Piggy Bank");
		return percent*piggyBank;
	}
// ************************************************************
	/*
	public double conservation_subsidy_amount() { // Level 3
		double percent=getPageValue("Conservation Subsidy Percent");
		double piggyBank=getPageValue("Piggy Bank");
		return percent*piggyBank;
	}
	*/
// ************************************************************
	public double investment_fund_amount() { // Level 1
		double percent=getPageValue("Investment Fund Percent");
		double piggyBank=getPageValue("Piggy Bank");
		return percent*piggyBank;
	}
// ************************************************************
	public double solar_subsidy_amount() { // Level 1
		double percent=getPageValue("Solar Subsidy Percent");
		double piggyBank=getPageValue("Piggy Bank");
		return percent*piggyBank;
	}
// ************************************************************
	public double education_subsidy_amount() { // Level 3
		double percent=getPageValue("Education Subsidy Percent");
		double piggyBank=getPageValue("Piggy Bank");
		return percent*piggyBank;
	}
// ************************************************************
	public double public_transport_subsidy_amount() { // Level 2
		double percent=getPageValue("Public Transport Subsidy Percent");
		double piggyBank=getPageValue("Piggy Bank");
		return percent*piggyBank;
	}
// ************************************************************
	public double piggy_bank() { // Level 1
		double sum=getPageValue("Carbon Dioxide Tax Income");
		if (difficultyLevel>beginnerLevel) {
			sum+=getPageValue("Air Pollution Tax Income");
			sum+=getPageValue("Radioactive Emissions Tax Income");
			if (difficultyLevel>intermediateLevel) {
				sum+=getPageValue("Gasoline Tax Income");
			}
		}
		return sum;
	}
// ************************************************************
	public double educational_level() { // Level 3
		double population=getPageValue("Population");
		double classSize=population/60.0f;
		double subsidy=getPageValue("Education Subsidy Amount");
		double globalPercent=getPageCoefficientValue(title,0);
		double bangForBuck=getPageCoefficientValue(title,1);
		double gdp=1000.0f*getPageValue("Global GDP"); // gdp is in trillions, spending in billions
		double educationalSpending=(subsidy+gdp*globalPercent)*bangForBuck;
		int grade=0;
		while (educationalSpending>(classSize*cost(grade))) {
			educationalSpending-=classSize*cost(grade);
			++grade;
		}
		double partialGrade=educationalSpending/(classSize*cost(grade));
		result=(double)grade+partialGrade;	
		return relax(4);
	}
// ************************************************************
	private double cost(int grade) {
		// cost figures are taken from this report:
		// http://www-rohan.sdsu.edu/~jimazeki/papers/ILcost.pdf
		if (grade<9) return 3000.0;
		else if (grade<13) return 10000.0;
		else if (grade<17) return 30000.0;
		else return 100000.0; // let's make sure it doesn't rise much higher than grad school
	}
// ************************************************************
	public double taxRelax(double fullRate) {
		double yearsElapsed=thisYear-firstYear;
		fullRate*=(0.5f+yearsElapsed)/gameLength;
		return fullRate;
	}
// ************************************************************
	public double energy_price() { // Level 0
		double demand=getPageValue("Net Energy Production");
		double growth=1.0f+(getPageValue("Economic Growth"))/100.0f;
		double currentPrice=getPageValue("Energy Price");
		demand*=growth;
		double conservation=getPageValue("Energy Conservation");
		double previousConservation=getPreviousValue("Energy Conservation");
		demand*=previousConservation/conservation;
		
		double[] netSupply=new double[8];
		for (int i=0; (i<7); ++i) netSupply[i]=0;
		double supply=0.0f;
		boolean isMaxedOut=false;
		int loopCounter=0;
		while ((supply<demand)&!isMaxedOut&(loopCounter<1000)) {
			++loopCounter;
			isMaxedOut=true;
			String energySource="";
			currentPrice+=.001;
			for (int sourceIndex=0; (sourceIndex<8); ++sourceIndex) {
				double maximumGrowthRate=1.2;
				double researchResults=1;
				double tax=0;
				switch (sourceIndex) {
					case 0: { 
						energySource="Solar Energy Production"; 
						maximumGrowthRate=1.4;
						break; 
					}
					case 1: { energySource="Geothermal Energy Production"; break; }
					case 2: { energySource="Hydro Energy Production"; break; }
					case 3: { energySource="Wind Energy Production"; break; }
					case 4: { 
						energySource="Nuclear Energy Production";
						if (difficultyLevel>beginnerLevel) {
							double taxRate=getPageValue("Radioactive Emissions Tax");
							taxRate=taxRelax(taxRate);
							getPage(energySource).setValue(taxRate);
							double emissionsRate=getPageCoefficientValue("Radioactive Emissions",0);
							tax=taxRate*emissionsRate;
						}
						maximumGrowthRate=1.05;
						break; 
					}
					case 5: { 
						energySource="Natural Gas Production"; 
						double taxRate=getPageValue("Carbon Dioxide Tax");
						taxRate=taxRelax(taxRate);
						double emissionsRate=getPageCoefficientValue("Anthropogenic CO2 Emissions",2);
						tax=taxRate*emissionsRate;
						break; 
						}
					case 6: {
						energySource="Oil Production";
						double taxRate=getPageValue("Carbon Dioxide Tax");
						taxRate=taxRelax(taxRate);
						double emissionsRate=getPageCoefficientValue("Anthropogenic CO2 Emissions",1);
						tax+=taxRate*emissionsRate;
						if (difficultyLevel>beginnerLevel) {
							taxRate=getPageValue("Air Pollution Tax");
							taxRate=taxRelax(taxRate);
							emissionsRate=getPageCoefficientValue("Air Pollution",1);
							tax+=taxRate*emissionsRate;
						}
						break; 
					}
					case 7: { 
						energySource="Coal Production";
						double taxRate=getPageValue("Carbon Dioxide Tax");
						taxRate=taxRelax(taxRate);
						double emissionsRate=getPageCoefficientValue("Anthropogenic CO2 Emissions",0);
						tax+=taxRate*emissionsRate;
						if (difficultyLevel>beginnerLevel) {
							taxRate=getPageValue("Air Pollution Tax");
							taxRate=taxRelax(taxRate);
							emissionsRate=getPageCoefficientValue("Air Pollution",0);
							tax+=taxRate*emissionsRate;
						}
						break; 
					}
				}
				double thisSupply=0;
				/*
				 * Nonrenewable energy sources must integrate all the energy
				 * from the initial price to the currentPrice to get its final
				 * result.
				 * But renewables deliver the amount actually specified in their
				 * function, so they need not be integrated.
				 * Because the fundamental algorithm at work is an integration,
				 * the renewables must subtract their previous contribution before
				 * adding their currentPrice contribution.
				 */
				if (sourceIndex>3) { // nonrenewable energy source
					double maximum=getPageCoefficientValue(energySource, 0);
					double priceAtMaximum=getPageCoefficientValue(energySource, 1);
					double width=getPageCoefficientValue(energySource, 2);
					double exponent=currentPrice-10*tax-priceAtMaximum;
					thisSupply=maximum*Math.exp(-exponent*exponent/width)/200;
					if (thisSupply<0) thisSupply=0;
					double pastSupply=getPreviousValue(energySource);
					double maximumSupply=pastSupply*maximumGrowthRate;
					if (netSupply[sourceIndex]>maximumSupply+1)
						thisSupply=0;
					else
						isMaxedOut=false;
					supply+=thisSupply;
					netSupply[sourceIndex]+=thisSupply;
				}
				else { // renewable energy source
					double linearMultiplier=getPageCoefficientValue(energySource, 0);
					double exponentialMultiplier=getPageCoefficientValue(energySource, 1);
					double subtractor=getPageCoefficientValue(energySource, 2);
					thisSupply=researchResults*linearMultiplier*Math.exp(exponentialMultiplier*currentPrice)-subtractor;
					if (thisSupply<0) thisSupply=0;
					// apply ceiling to growth
					double pastSupply=getPreviousValue(energySource);
					double maximumSupply=pastSupply*maximumGrowthRate;
					if (thisSupply>maximumSupply+0.1)
						thisSupply=maximumSupply;
					else
						isMaxedOut=false;
					// obviate overshoot
					if (supply-netSupply[sourceIndex]+thisSupply>demand)
						thisSupply=demand-supply+netSupply[sourceIndex];
					supply-=netSupply[sourceIndex];
					supply+=thisSupply;
					netSupply[sourceIndex]=thisSupply;
				}
				if (thisSupply<0) thisSupply=0;
				getPage(energySource).setValue(netSupply[sourceIndex]);
			}
		}
		if (isMaxedOut)
			currentPrice*=1.2; // the penalty for too-steep price increases
		return currentPrice;
	}
// ************************************************************
	public double scientific_progress() { // Level 1
		double previousValue=getPreviousValue(title);
		double research=getPageValue("Research Subsidy Amount");
		double educationalLevel=6; // initial value of educational level
		if (difficultyLevel>intermediateLevel)
			educationalLevel=getPageValue("Educational Level");
		double multiplier=getPageCoefficientValue(title, 0)*educationalLevel/6;
		double divider=getPageCoefficientValue(title, 1);
		result=previousValue+Math.log(1+multiplier*research)/divider;
		return relax(5);
	}
// ************************************************************
	public double energy_conservation() { // Level 0
		double price=getPageValue("Energy Price");
		double originalPrice=getPage("Energy Price").getHistoryValue(0);
		double elasticity=getPageCoefficientValue(title,1);
		double priceMotivatedConservation=(((price-originalPrice)/originalPrice)*elasticity);
		double technicalConservation=0;
		if (difficultyLevel>=beginnerLevel) {
			double scientificProgress=getPageValue("Scientific Progress");
			double divider=getPageCoefficientValue(title,0);
			technicalConservation=Math.log(1+scientificProgress)/divider;
		}
		
//		double conservationDollars=getPageValue("Conservation Subsidy Amount");
//		double scaler=getPageCoefficientValue(title,2);
//		double subsidizedConservation=Math.log(1+conservationDollars)/scaler;

		result=1+priceMotivatedConservation+technicalConservation;
		return relax(5);
	}
// ************************************************************
	public double average_mpg() { // Level 2
		double conservation=getPageValue("Energy Conservation");
		double coefficient=getPageCoefficientValue(title,0);
		double originalMPG=getInitialValue(title);
		result=originalMPG*(conservation*coefficient);
		return result;
	}
// ************************************************************
	public double transportation_needs() { // Level 2
		double gdp=getPageValue("Global GDP");
		double price=getPageValue("Energy Price");
		double rate=getPageCoefficientValue(title, 0);
		result=rate*gdp*getInitialValue("Energy Price")/price;
		return relax(5);
	}
// ************************************************************
	public double public_transport() { // Level 2
		double need=getPageValue("Transportation Needs");
		double oldTransport=getPageValue("Public Transport");
		double subsidy=getPageValue("Public Transport Subsidy Amount");
		double effectiveness=getPageCoefficientValue(title,0);
		result=oldTransport+(double)Math.sqrt(subsidy)*effectiveness;
		if (result>need) result=need;
		return relax(5);
	}
// ************************************************************
	public double electric_car_use() { // Level 2
		double need=getPageValue("Transportation Needs")-getPageValue("Public Transport");
		double netPrice=getPageValue("Energy Price")+getPageValue("Gasoline Tax");
//		double highTech=getPageValue("High Technology");
		double trigger=getPageCoefficientValue(title, 0);
		result=need*netPrice/trigger;
		if (result>need) result=need;
		return relax(10);
	}
// ************************************************************
	public double gasoline_use() { // Level 2
		double transportation=getPageValue("Transportation Needs");
		double pubtrans=getPageValue("Public Transport");
		double electricCar=getPageValue("Electric Car Use");
		double milesRequired=transportation-pubtrans-electricCar;
		double mpg=getPageValue("Average MPG");
		if (milesRequired<0.1) milesRequired=0.1f;
		result=1000*milesRequired/mpg;
		return result;		
	}
// ************************************************************
	public double coal_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double oil_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double natural_gas_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double nuclear_energy_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double solar_energy_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double wind_energy_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double geothermal_energy_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double hydro_energy_production() { // Level 0
		return getPage(title).getValue();
	}
// ************************************************************
	public double net_energy_production() { // Level 0
		double coal=getPageValue("Coal Production");
		double oil=getPageValue("Oil Production");
		double gas=getPageValue("Natural Gas Production");
		double nuclear=getPageValue("Nuclear Energy Production");
		double solar=getPageValue("Solar Energy Production");
		double wind=getPageValue("Wind Energy Production");
		double geothermal=getPageValue("Geothermal Energy Production");
		double hydro=getPageValue("Hydro Energy Production");
		return  coal+oil+gas+nuclear+solar+wind+geothermal+hydro;				
	}
// ************************************************************
	public double air_pollution() { // Level 2
		double coal=getPageValue("Coal Production");
		double oil=getPageValue("Oil Production");
		double gasoline=getPageValue("Gasoline Use");
		double coalDirt=getPageCoefficientValue(title,0);
		double oilDirt=getPageCoefficientValue(title,1);
		double gasolineDirt=getPageCoefficientValue(title,2);
		result=coal*coalDirt+oil*oilDirt+gasoline*gasolineDirt;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double acid_rain() { // Level 2
		double x=getPageValue("Air Pollution");
		double a=getPageCoefficientValue(title,0);
		result=a*x;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double healthy_wetlands() { // Level 2
		double x=getPageValue("Acid Rain");
		double current=getPage(title).getValue();
		double a=getPageCoefficientValue(title,0);
		result=current-a*x;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double global_gdp() { // Level 0
		double energyPrice=getPageValue("Energy Price");
		double previousPrice=getPreviousValue("Energy Price");
		double previousGDP=getPreviousValue("Global GDP");
		double climateChangeFraction=0;
		double investment=0;
		climateChangeFraction=getPageValue("Climate Change Costs");
		investment=getPageValue("Investment Fund Amount");			
		double discountRate=getPageCoefficientValue(title,0);
		double priceImpact=getPageCoefficientValue(title,1);
		double priceEffectExponent=getPageCoefficientValue(title,2);
		double ccEffect=1-climateChangeFraction;
		double priceRise=Math.pow(energyPrice/previousPrice, priceEffectExponent);
		double priceEffect=((priceImpact+energyPrice)/(priceImpact+4))*priceRise;
		double investmentBenefit=discountRate*investment/1000; // converting billions to trillions, remember
		result=investmentBenefit+discountRate*previousGDP*ccEffect/priceEffect;
		if (result<0) result=0;
		return result;				
		
	}
// ************************************************************
	public double economic_growth() { // Level 0
		double lastYearsGDP=getPreviousValue("Global GDP");
		double currentGDP=getPageValue("Global GDP");
		result=100.0f*(currentGDP-lastYearsGDP)/currentGDP;
		return result;				
	}
// ************************************************************
	public double radioactive_emissions() { // Level 2
		double x=getPageValue("Nuclear Energy Production");
		double a=getPageCoefficientValue(title,0);
		result=a*x;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double radiation_cancer_deaths() { // Level 2
		double x=getPageValue("Radioactive Emissions");
		double a=getPageCoefficientValue(title,0);
		result=a*x;
		if (result<0) result=0;
		return relax(10);				
	}
// ************************************************************
	public double rainforest_clearing() { // Level 3
		double population=getPageValue("Population");
		double food=getPageValue("Food Production");
		double minimumCaloricRequirement=getPageCoefficientValue(title,1)/1000.0f;
		double populationPressure=minimumCaloricRequirement-food/population;
		double clearingRate=getPageCoefficientValue(title,0);
		double farmlandCreation=0;
		if (populationPressure>0) {
			farmlandCreation=populationPressure*clearingRate;
		}
		double forestRemaining=getPageValue("Rainforest Land");
		double initialForestLand=getInitialValue("Rainforest Land");
		double fraction=farmlandCreation/initialForestLand;
		result=fraction*forestRemaining;
		if (result<0) result=0;
		return relax(3);				
	}
// ************************************************************
	public double rainforest_land() { // Level 3
		double farmlandCreation=getPageValue("Rainforest Clearing");
		result=getPageValue("Rainforest Land")-farmlandCreation/1000000.0f;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double farm_land() { // Level 3
		double deforestation=getPageValue("Rainforest Clearing")/1000000;
		double previousFarmLand=getPageValue("Farm Land");
		double seaLevel=getPageValue("Sea Level");
		double seaLevelRate=getPageCoefficientValue(title,0);
		double seaLevelLoss=seaLevel*seaLevelRate;
		return previousFarmLand-seaLevelLoss+deforestation;
	}
// ************************************************************
	public double agricultural_productivity() { // Level 3
		double science=getPageValue("Scientific Progress");
		double scienceValue=getPageCoefficientValue(title,0);
		result=scienceValue*science;
		return result;				
	}
// ************************************************************
	public double food_production() { // Level 3
		double productivity=getPageValue("Agricultural Productivity");
		double farmLand=getPageValue("Farm Land");
		result=productivity*farmLand;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double population() { // Level 3
		double population=getPage(title).getValue();
		double education=getPageValue("Educational Level");
		double malnutrition=getPageValue("Malnutrition Deaths");
		double uneducatedGrowthRate=getPageCoefficientValue(title,0);
		double powerOfEducation=getPageCoefficientValue(title,1);
		double growthRate=1+(uneducatedGrowthRate-powerOfEducation*education)/100.0f;
		if (growthRate<1) growthRate=1;
		result=population*growthRate-malnutrition/1000000000.0f;
		if (result<0) result=0;
		return relax(3);				
	}
// ************************************************************
	public double malnutrition_deaths() { // Level 3
		double foodProduction=getPageValue("Food Production");
		double population=getPageValue("Population");
		double caloriesPerCapita=foodProduction/population;
		double intercept=getPageCoefficientValue(title,0);
		double slope=getPageCoefficientValue(title,1);
		result=slope*caloriesPerCapita+intercept;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double anthropogenic_co2_emissions() { // Level 0
		double x=getPageValue("Coal Production");
		double y=getPageValue("Oil Production");
		double z=getPageValue("Natural Gas Production");
		double t=0;
		if (difficultyLevel>intermediateLevel)
			t=getPageValue("Rainforest Clearing");
		double a=getPageCoefficientValue(title,0);
		double b=getPageCoefficientValue(title,1);
		double c=getPageCoefficientValue(title,2);
		double d=getPageCoefficientValue(title,3);
		result=a*x+b*y+c*z+d*t;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double induced_co2_emissions() { // Level 1
		double x=getPageValue("Forest Fires");
		double y=getPageValue("Permafrost Emissions");
		double a=getPageCoefficientValue(title,0);
		double b=getPageCoefficientValue(title,1);
		result=a*x+b*y;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double net_co2_emissions() { // Level 1
		double x=getPageValue("Anthropogenic CO2 Emissions");
		double y=getPageValue("Induced CO2 Emissions");
		result=x+y;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double average_global_temperature() { // Level 1
		double currentEmissions=getPageValue("Net CO2 Emissions");
		double snowCover=getPageValue("Snow Cover");
		double initialTemperature=getInitialValue(title);
		double forcingConstant=(1.0f-snowCover)*getPageCoefficientValue(title,0);
		cumulativeCO2Emissions+=currentEmissions;
		result=initialTemperature+forcingConstant*cumulativeCO2Emissions;
		if (result<0) result=0;
		return relax(10);				
	}
// ************************************************************
	public double snow_cover() { // Level 1
		double initialArea=getInitialValue(title);
		double currentTemperature=getPageValue("Average Global Temperature");
		double initialTemperature=getInitialValue("Average Global Temperature");
		double lossPerDegree=getPageCoefficientValue(title,0);
		result=initialArea-lossPerDegree*(currentTemperature-initialTemperature);
		if (result<0) result=0;
		return relax(10);				
	}
// ************************************************************
	public double permafrost_emissions() { // Level 1
		double currentTemperature=getPageValue("Average Global Temperature");
		double emissionsPerDegree=getPageCoefficientValue(title,0);
		double initiationTemperature=getPageCoefficientValue(title,1);
		result=emissionsPerDegree*(currentTemperature-initiationTemperature);
		if (result<0) result=0;
		return relax(10);				
	}
// ************************************************************
	public double climate_change_costs() { // Level 1
		double currentTemperature=getPageValue("Average Global Temperature");
		double initialTemperature=getInitialValue("Average Global Temperature");
		double costPerDegree=getPageCoefficientValue(title,0);
		double fraction=1-1/(1+costPerDegree*(currentTemperature-initialTemperature));
		result=fraction;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double forest_fires() { // Level 1
		double currentTemperature=getPageValue("Average Global Temperature");
		double initialTemperature=getInitialValue("Average Global Temperature");
		double baseBurnArea=getInitialValue("Forest Fires");
		double heatSensitivity=getPageCoefficientValue(title,0);
		double fullBurn=baseBurnArea+heatSensitivity*(currentTemperature-initialTemperature);
		double forestRemaining=getPageValue("Temperate Forest Land");
		double initialForestLand=getInitialValue("Temperate Forest Land");
		double fraction=fullBurn/initialForestLand;
		result=fraction*forestRemaining;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double temperate_forest_land() { // Level 1
		double forestFires=getPageValue("Forest Fires");
		result=getPage(title).getValue()-forestFires/1000000.0f;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double ocean_acidification() { // Level 2
		double currentAcidification=getPage(title).getValue();
		double carbonDioxide=getPageValue("Net CO2 Emissions");
		double sensitivity=getPageCoefficientValue(title,0);
		result=currentAcidification+sensitivity*carbonDioxide;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double sea_level() { // Level 3
		double originalTemperature=getInitialValue("Average Global Temperature");
		double currentTemperature=getPageValue("Average Global Temperature");
		double a=getPageCoefficientValue(title,0);
		result=a*(currentTemperature-originalTemperature);
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double flood_deaths() { // Level 3
		double seaLevel=getPageValue("Sea Level");
		double deathRate=getPageCoefficientValue(title,0);
		result=deathRate*seaLevel;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double extreme_weather() { // Level 1
		double initialTemperature=getInitialValue("Average Global Temperature");
		double currentTemperature=getPageValue("Average Global Temperature");
		double a=getPageCoefficientValue(title,0);
		result=a*(currentTemperature-initialTemperature);
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double extreme_weather_deaths() { // Level 1
		double x=getPageValue("Extreme Weather");
		double a=getPageCoefficientValue(title,0);
		result=a*x;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	/*
	public double particulate_emissions() { // Level 3
		double coal=getPageValue("Coal Production");
		double gasoline=getPageValue("Gasoline Use");
		double coalContent=getPageCoefficientValue(title,0);
		double gasolineContent=getPageCoefficientValue(title,1);
		result=coalContent*coal+gasolineContent*gasoline;
		if (result<0) result=0;
		return result;				
	}
	*/
// ************************************************************
	public double lung_disease_deaths() { // Level 3
		double airPollution=getPageValue("Air Pollution");
		double pollutionToxicity=getPageCoefficientValue(title,0);
		result=pollutionToxicity*airPollution;
		if (result<0) result=0;
		return result;				
	}
// ************************************************************
	public double extinction() { // Level 2
		double x=getPageValue("Acid Rain");
		double y=getPageValue("Ocean Acidification");
		double z=0;
		if (difficultyLevel>intermediateLevel)
			z=getPageValue("Rainforest Clearing");
		double a=getPageCoefficientValue(title,0);
		double b=getPageCoefficientValue(title,1);
		double c=getPageCoefficientValue(title,2);
		result=a*x+b*y+c*z;
		return result;				
	}
// ************************************************************
	public double quality_of_life_score() { // Level 0
		double x=(getPageValue("Economic Growth"));
		double a=getPageCoefficientValue(title,0);
		result=a*x;
		return result;				
	}
// ************************************************************
	public double poor_death_score() { // Level 3
		double x=getPageValue("Lung Disease Deaths");
		double y=getPageValue("Malnutrition Deaths");
		double z=getPageValue("Flood Deaths");
		double a=getPageCoefficientValue(title,0);
		double b=getPageCoefficientValue(title,1);
		double c=getPageCoefficientValue(title,2);
		result=-(a*x+b*y+c*z);
		return result;				
	}
// ************************************************************
	public double rich_death_score() { // Level 1
		double x=getPageValue("Extreme Weather Deaths");
		double y=0;
		if (difficultyLevel>beginnerLevel)
			y=getPageValue("Radiation Cancer Deaths");
		double a=getPageCoefficientValue(title,0);
		double b=getPageCoefficientValue(title,1);
		result=-(a*x+b*y);
		return result;				
	}
// ************************************************************
	public double gaia_score() { // Level 2
		double extinction=getPageValue("Extinction");
		double a=getPageCoefficientValue(title,0);
		double wetlands=getPageValue("Healthy Wetlands");
		double b=getPageCoefficientValue(title,1);
		double temperateForest=getPageValue("Temperate Forest Land");
		double c=getPageCoefficientValue(title,2);
		double rainforest=0;
		if (difficultyLevel>intermediateLevel)
			rainforest=getPageValue("Rainforest Land");
		double d=getPageCoefficientValue(title,3);
		result=a*extinction+b*wetlands+c*temperateForest+d*rainforest;
		return result;				
	}
// ************************************************************
	public double annual_score() { // Level 0
		double sum=getPageValue("Quality of Life Score");
		if (difficultyLevel>=intermediateLevel) {
			sum+=getPageValue("Gaia Score");
			sum+=getPageValue("Rich Death Score");
			if (difficultyLevel>intermediateLevel)
				sum+=getPageValue("Poor Death Score");
		}
		return sum;				
	}
// ************************************************************
	public double total_score() { // Level 0
		double x=getPageValue("Annual Score");
		double y=getPageValue("Total Score");
		result=x+y;
		return result;				
	}
// ************************************************************
// ************************************************************
// ************************************************************
// ************************************************************
// ************************************************************
// ************************************************************

}

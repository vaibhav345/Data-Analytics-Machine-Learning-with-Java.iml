
import java.io.IOException;
import smile.classification.LogisticRegression;
import smile.math.Math;
import smile.validation.LOOCV;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

/** Logistic Regression
 */

public class LogisticRegressionExample {
	
	public LogisticRegressionExample() { }

	public static Table loadingTable(String mytable) throws IOException {
		
		String tablename = mytable + ".csv";
		Table adultsDataset  = Table.read().csv("DataSets/" + tablename);
	 	
		return adultsDataset;
	}
	/** Marital Status classification to : single, Married, Not Currently Married
	 */	
	public static Table classifyMaritalStatus (Table mytable) {
		StringColumn maritalStatus = mytable.stringColumn("marital-status");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("Never-married"), "Single");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("Married-civ-spouse"), "Married");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("Married-AF-spouse"), "Married");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("Married-spouse-absent"), "Married");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("Divorced"), "Not_curr_Married");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("Separated"), "Not_curr_Married");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("Widowed"), "Not_curr_Married");
		maritalStatus.set(maritalStatus.equalsIgnoreCase("?"), "NAN");
		Table newTable = (Table) mytable.removeColumns("marital-status");
		newTable.addColumns(maritalStatus);	
		return newTable;
	}
	
	public static Table classifyWorkingClass (Table mytable) {
		StringColumn workingClass = mytable.stringColumn("workclass");
		workingClass.set(workingClass.equalsIgnoreCase("Without-pay"), "-1");
		workingClass.set(workingClass.equalsIgnoreCase("Never-worked"),"-1");
		workingClass.set(workingClass.equalsIgnoreCase("State-gov"), "0");
		workingClass.set(workingClass.equalsIgnoreCase("Local-gov"), "0");
		workingClass.set(workingClass.equalsIgnoreCase("Federal-gov"),"0");
		workingClass.set(workingClass.equalsIgnoreCase("Self-emp-inc"), "1");
		workingClass.set(workingClass.equalsIgnoreCase("Self-emp-not-inc"), "1");
		workingClass.set(workingClass.equalsIgnoreCase("Private"), "1");
		workingClass.set(workingClass.equalsIgnoreCase("?"), "NAN");
		Table newTable = (Table) mytable.removeColumns("workclass");
		newTable.addColumns(workingClass);	
		return newTable;
	}
	
	/** Countries to Regions Classification
	 */
	
	public static Table classifyCountries (Table mytable) {
		StringColumn countryRegion = mytable.stringColumn("native-country");
		countryRegion.set(countryRegion.isIn("Canada", "Cuba", "Dominican-Republic", "El-Salvador", 
				"Guatemala" ,"Haiti", "Honduras", "Jamaica", "Mexico", "Nicaragua", "Outlying-US(Guam-USVI-etc)",
				"Puerto-Rico", "Trinadad&Tobago","United-States"), "NorthAmerica");
		
		countryRegion.set(countryRegion.isIn("England", "France", "Germany", "Greece", 
				"Holand-Netherlands", "Hungary", "Ireland", "Italy", "Poland", "Portugal",
				"Scotland", "Yugoslavia"),"Europe");
		
		countryRegion.set(countryRegion.isIn("Columbia", "Ecuador", "Peru"),"SouthAmerica");
		countryRegion.set(countryRegion.isIn("Cambodia", "China", "Hong", "India", "Iran", "Japan", "Laos",
		          "Philippines", "Taiwan", "Thailand", "Vietnam"),"Asia");
		countryRegion.set(countryRegion.isIn("?", "South"),"Other");
		
		Table newTable = (Table) mytable.removeColumns("native-country");
		newTable.addColumns(countryRegion);	
		return newTable;
	}

	/** Income Classification
	 */
	public static Table classifyIncome( Table mytable) {
		StringColumn theIncome = mytable.stringColumn("income");
		theIncome.set(theIncome.equalsIgnoreCase("<=50K"), "0");
		theIncome.set(theIncome.equalsIgnoreCase(">50K"), "1");
		Table newTable = (Table) mytable.removeColumns("income");
		newTable.addColumns(theIncome);	
		return newTable;
	
	}
	
	/** Removing rows with Missing data
	 */
	public static Table removeNANS (Table mytable) {
		Table cleanTable = mytable.dropRowsWithMissingValues();
		return cleanTable;
	}

	public static void main(String[] args) throws IOException {
		
		//Calling the function to load the table
		Table adultsDataset = loadingTable("adult");

		//Classify Marital Status
		Table maritalClassified =classifyMaritalStatus(adultsDataset);

		//Classify working class
		Table classifiedTable =classifyWorkingClass(maritalClassified);

		//Classify Countries
		Table classifiedMost = classifyCountries(classifiedTable);

		//Classify Income
		Table classified = classifyIncome(classifiedMost);

		// removing empty rows
		Table AdultCleanDataset = removeNANS(classified);
		
		Table tableTail5 = AdultCleanDataset.last(5);
		System.out.println(tableTail5);
		String tableShape5 = AdultCleanDataset.shape();
		System.out.println(tableShape5);	
		System.out.println("We used new Table. ");
		
		Table cleanAdultDataset = adultsDataset.dropRowsWithMissingValues();

		//Table Shape
		String tableShape = cleanAdultDataset.shape();
		System.out.println(tableShape);	
		
		//Table Structure
		Table Structure =  cleanAdultDataset.structure();
		System.out.println(Structure);
		
		/** Separating the dataset into independent and dependent Variables
		 */
		StringColumn adultDependent = cleanAdultDataset.stringColumn("income");
		Table adultIndependent = (Table)cleanAdultDataset.removeColumns("income");

		/** Splitting the data to training and Testing set, using 70:30 split
		 */

		//Independent
		Table adultIndependentTrain = (Table)adultIndependent.where(Selection.withRange(1,20000));
		Table adultIndependentTest = (Table)adultIndependent.where(Selection.withRange(20001,48835));
		
		//Dependent
		StringColumn adultDependentTrain = (StringColumn)adultDependent.where(Selection.withRange(1,20000));
		StringColumn adultDependentTest =  (StringColumn)adultDependent.where(Selection.withRange(20001,48835));
		
		
		/** Changing the train and test from table to double
		 */
		
		double [][] adultIndependentTrainArr = adultIndependentTrain.as().doubleMatrix();
		int[]  adultDependentTrainArr =adultDependentTrain.asIntArray();

		/** Fitting the Model to linear regression directly
		 */
		smile.classification.LogisticRegression logit1 = new smile.classification.LogisticRegression(adultIndependentTrainArr, adultDependentTrainArr);
		
		int logit1error = 0;
		for(int i = 0; i < adultIndependentTrainArr.length; i++) {
			if(adultDependentTrainArr[i]!=logit1.predict(adultIndependentTrainArr[i])) {
				logit1error++;
			}
		}
		
		System.out.println("Logistic Regression is:- " + logit1error);
		
		/** Fitting the Model, using cross Validation
		 */
		
		int k = adultIndependentTrainArr.length;
		
		LOOCV loocv = new LOOCV(k);
		int logisticError = 0;
		
		for (int i = 0; i < k; i++) {
            double[][] trainx = Math.slice(adultIndependentTrainArr, loocv.train[i]);
            int[] trainy = Math.slice(adultDependentTrainArr, loocv.train[i]);
            
            smile.classification.LogisticRegression logit = new smile.classification.LogisticRegression(trainx, trainy);

            if (adultDependentTrainArr[loocv.test[i]] != logit.predict(adultIndependentTrainArr[loocv.test[i]]))
            	logisticError++;
        }

		System.out.println("Logistic Regression is:- " + logit1error);
		System.out.println("Logistic Regression  with LOOCV error is:- " + logisticError);
	}
}

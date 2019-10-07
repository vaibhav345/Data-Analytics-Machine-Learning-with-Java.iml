import java.io.IOException;
import java.net.URL;
import java.sql.*;
import tech.tablesaw.api.Table;
import java.io.*;

/** Loading data from different sources.
 */
public class LoadingData {

	public LoadingData() { }
	
	public static void main(String[] args)throws IOException {
		
		/** Loading the entire CSV. Give the path that the csv is located. */
		Table hrAnalytics  = Table.read().csv("File-Path");
			
			
		/** Getting the structure of the table, using Table saw. */
		Table structure = hrAnalytics.structure();
		System.out.println("Printing the File Structure.");
		System.out.println(structure);
	
		/** Getting the data from  mysql database USING JDBC, from local DB. */
		String DB_URL = "jdbc:mysql://localhost:3306/customers";
		Connection conn= null;
		try {
			conn = DriverManager.getConnection(DB_URL,"root","");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Table DBcustomers = null;
		try (Statement myStament = conn.createStatement()) {
			String sql = "SELECT * FROM Customer";
			try (ResultSet results = myStament.executeQuery(sql)) {
				DBcustomers = Table.read().db(results, "Customer");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Testing the database table
		Table DBstructure = DBcustomers.structure();
		System.out.println(DBstructure);
		System.out.println("This two are good , off to streaming");
		
		/** Loading the data from the API. We will be using the Stream API function
		 * The data has to be passed through the Stream Interface
		 * You need to have the link to where the data is located and the name of the file that you want to retrieve. */
		
		String location =  "https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/bush.csv";
		Table HRAnalyticsTable;
		try (InputStream input = new URL(location).openStream()) {
			HRAnalyticsTable = Table.read().csv(input, "bush");					
		}
		
		//Testing  the output from the API
		Table streamStructure = HRAnalyticsTable.structure();
		System.out.println(streamStructure);
	}
	

	
	
	
	
	
	
	
	
	
	
	
}

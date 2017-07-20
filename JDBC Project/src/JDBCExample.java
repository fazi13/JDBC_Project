/*Dylan Nguyen
CS 435 
Lab 4
*/
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.microsoft.sqlserver.jdbc.SQLServerException;

public class JDBCExample {
	public static void main(String[] args) throws ParseException {
		Connection conn;
		Statement stmt;
		Scanner scan = new Scanner(System.in);
		String input = "";
		//connect to the db
		try {
			 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	         String conURL = "jdbc:sqlserver://localhost; databaseName=Lab4; user=sa; password=password;";
	         conn = DriverManager.getConnection(conURL);
	         //System.out.println("connected");
	         stmt = conn.createStatement();
	         //rs = statement.executeQuery(queryString);
	         //addTripOffering(stmt);
	         
	         //ask user what to do
	         displayAllCommands();
	         while(true){
	        	 System.out.print("Command: ");
	        	 input = scan.nextLine();
	        	 if(input.trim().equals("ds"))
	        		 displaySchedule(stmt);
	        	 else if(input.trim().equals("dt"))
	        		 deleteTripOffering(stmt);
	        	 else if(input.trim().equals("at"))
	        		 addTripOffering(stmt);
	        	 else if(input.trim().charAt(0) == 'x')
	        		 System.exit(0);
	        	 else if(input.trim().equals("cd"))
	        		 changeDriver(stmt);
	        	 else if(input.trim().equals("cb"))
	        		 changeBus(stmt);
	        	 else if(input.trim().equals("ds"))
	        		 displayTripStops(stmt);
	        	 else if(input.trim().equals("ad"))
	        		 addDriver(stmt);
	        	 else if(input.trim().equals("ab"))
	        		 addBus(stmt);
	        	 else if(input.trim().equals("db"))
	        		 deleteBus(stmt);
	        	 else if(input.trim().equals("dw"))
	        		 displayWeekly(stmt);
	        	 else if(input.trim().equals("it"))
	        		 insertTripData(stmt);
	        	 else
	        		 displayAllCommands();
	         }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		scan.close();
	}
	
	private static void displayAllCommands(){
		System.out.println("ds:\tDisplay a Schedule");
        System.out.println("dt:\tDelete a Trip Offering");
        System.out.println("at:\tAdd a Trip Offering");
        System.out.println("cd:\tChange a Driver");
        System.out.println("cb:\tChange a Bus");
        System.out.println("ds:\tDisplay Trip Stops");
        System.out.println("dw:\tDisplay Weekly Schedule for Driver");
        System.out.println("ad:\tAdd a Driver");
        System.out.println("ab:\tAdd a Bus");
        System.out.println("db:\tDelete a Bus");
        System.out.println("it:\tInsert Actual Trip Info");
        System.out.println("h:\tDisplay all commands");
        System.out.println("x:\tExit program");
        //System.out.print("Command: ");
	}
	
	//Display the schedule of all trips for a given StartLocationName and Destination Name, and Date
	public static void displaySchedule(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Start Location Name: ");
		String startLoc = sc.nextLine().trim();
		System.out.print("Destination Name: ");
		String destLoc = sc.nextLine().trim();
		System.out.print("Date: ");
		String date = sc.nextLine().trim();
		
		//get table data
		try{
			ResultSet rs = stmt.executeQuery("SELECT T0.ScheduledStartTime, T0.ScheduledArrivalTime, T0.DriverName, T0.BusID " +
										"FROM TripOffering T0, Trip T1 " +
										"WHERE T1.StartLocationName LIKE '" + startLoc + "' AND " +
										"T1.DestinationName LIKE '" + destLoc + "' AND " +
										"T0.Date = '" + date + "' AND " +
										"T1.TripNumber = T0.TripNumber " + 
										"Order by ScheduledStartTime ");
			
			//get column names to print
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			for(int i = 1; i <= colCount; i++){
				System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			
			//print out rows
			while(rs.next()){
				for(int i = 1; i <= colCount; i++)
					System.out.print(rs.getString(i) + "\t\t");
				System.out.println();
			}
			rs.close();
			System.out.println("------------------------------------------------------");
		}catch (SQLServerException e){
			System.out.println("No schedule from " + startLoc + " to " + destLoc + " on " + date);
		}
	}
	
	//Delete a trip offering specified by Trip#, Date, and ScheduledStartTime
	public static void deleteTripOffering(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Start Trip Number: ");
		String tripNo = sc.nextLine().trim();
		System.out.print("Date: ");
		String date = sc.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = sc.nextLine().trim();
		
		try{
			//if delete returns 0 that means no rows found matching that data so output an error
			if(stmt.executeUpdate("DELETE TripOffering " + 
								"WHERE TripNumber = '" + tripNo + "' AND " + 
								"Date = '" + date + "' AND " +
								"ScheduledStartTime = '" + startTime + "'") == 0){
				System.out.println("No Trip Offering with Trip Number: " + tripNo + " on "+ date + " starting at "+ startTime);
			}else
			//if delete returns any other value, that means something was deleted
				System.out.println("Successfully deleted Trip Offering");
		}catch (SQLServerException e){
			//if some error occurs check input
			System.out.println("No Trip Offering with Trip Number: " + tripNo + " on "+ date + " starting at "+ startTime);
		}
	}
	
	//Add a set of trip offerings assuming the values of all attributes are given 
	public static void addTripOffering(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		while(true){
			System.out.print("Enter Trip Number: ");
			String tripNo = sc.nextLine().trim();
			System.out.print("Date: ");
			String date = sc.nextLine().trim();
			System.out.print("Scheduled Start Time: ");
			String startTime = sc.nextLine().trim();
			System.out.print("Scheduled Arrival Time: ");
			String arrivalTime = sc.nextLine().trim();
			System.out.print("Driver Name: ");
			String driver = sc.nextLine().trim();
			System.out.print("Bus ID: ");
			String bus = sc.nextLine().trim();

			//insert into trip offering
			try{
				stmt.execute("INSERT INTO TripOffering VALUES ('" + tripNo + "', '" + date + "', '" + startTime + "', '" + arrivalTime + "', '" + driver + "', '" + bus + "')");
				System.out.print("Successfully added a new Trip Offering");
			}catch (SQLServerException e){
				System.out.println("Check input formatting");
			}
			//loops and asks user to add another
			System.out.print("Add another Trip Offering? (y/n): ");
			String input = sc.nextLine();
			if(input.trim().charAt(0) == 'y'){
				//do nothing
			}else{
				break;
			}
		}
	}
	
	//- Change the driver for a given Trip offering 
	public static void changeDriver(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("New Driver Name: ");
		String driver = sc.nextLine().trim();
		System.out.print("Start Trip Number: ");
		String tripNo = sc.nextLine().trim();
		System.out.print("Date: ");
		String date = sc.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = sc.nextLine().trim();
		
		try{
			if(stmt.executeUpdate("UPDATE TripOffering " + 
								"SET DriverName = '" + driver + "' " +
								"WHERE TripNumber = '" + tripNo + "' AND " + 
								"Date = '" + date + "' AND " +
								"ScheduledStartTime = '" + startTime + "'") == 0){
				System.out.println("No Trip Offering with Trip Number: " + tripNo + " on "+ date + " starting at "+ startTime);
			}else
				System.out.println("Successfully updated Driver");
		}catch (SQLServerException e){
			//if some error occurs check input
			//e.printStackTrace();
			System.out.println("No such Trip Offering or Driver in database");
		}
	}
	
	//Change the bus for a given Trip offering
	public static void changeBus(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("New Bus Number: ");
		String bus = sc.nextLine().trim();
		System.out.print("Start Trip Number: ");
		String tripNo = sc.nextLine().trim();
		System.out.print("Date: ");
		String date = sc.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = sc.nextLine().trim();
		
		try{
			if(stmt.executeUpdate("UPDATE TripOffering " + 
								"SET BusID = '" + bus + "' " +
								"WHERE TripNumber = '" + tripNo + "' AND " + 
								"Date = '" + date + "' AND " +
								"ScheduledStartTime = '" + startTime + "'") == 0){
				System.out.println("No Trip Offering with Trip Number: " + tripNo + " on "+ date + " starting at "+ startTime);
			}else
				System.out.println("Successfully updated Bus");
		}catch (SQLServerException e){
			//if some error occurs check input
			//e.printStackTrace();
			System.out.println("No such Trip Offering or Bus Number in database");
		}
	}
	
	public static void displayTripStops(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Trip Number: ");
		String tripNo = sc.nextLine().trim();
		
		//get table data
		try{
			ResultSet rs = stmt.executeQuery("SELECT * " +
										"FROM TripStopInfo " +
										"WHERE TripNumber = '" + tripNo + "' " +
										"Order By SequenceNumber ");
			
			//get column names to print
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			for(int i = 1; i <= colCount; i++){
				System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			
			//print out rows
			while(rs.next()){
				for(int i = 1; i <= colCount; i++)
					System.out.print(rs.getString(i) + "\t\t");
				System.out.println();
			}
			rs.close();
			System.out.println("------------------------------------------------------");
		}catch (SQLServerException e){
			System.out.println("Trip Number: '" + tripNo + "' does not exist");
		}
	}
	
	public static void displayWeekly(Statement stmt) throws ParseException, SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Driver name: ");
		String driver = sc.nextLine().trim();
		System.out.print("Date: ");
		String dateStr = sc.nextLine().trim();
		
		//convert date string to calendar object so it can be incremented for the week
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		Calendar date = new GregorianCalendar();
		date.setTime(df.parse(dateStr));
		
		for(int i = 0; i < 7; i++){
			//convert back to a string
			dateStr = df.format(date.getTime());
			
			//query for selected driver and date
			try{
				ResultSet rs = stmt.executeQuery("SELECT TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, BusID " +
											"FROM TripOffering " +
											"WHERE DriverName LIKE '" + driver + "' " +
											"AND Date = '" + dateStr + "' " +
											"Order By ScheduledStartTime ");
				ResultSetMetaData rsmd = rs.getMetaData();
				int colCount = rsmd.getColumnCount();
				//if first then print out the column names
				if(i == 0){
					System.out.println("----------------------Day 1----------------------------");
					//get column names to print
					for(int j = 1; j <= colCount; j++){
						if(j == 1 || j == 3)
							System.out.print(rsmd.getColumnName(j) + "\t");
						else
							System.out.print(rsmd.getColumnName(j) + "\t\t");
					}
					System.out.println();
				}
				
				//print out rows
				while(rs.next()){
					for(int j = 1; j <= colCount; j++)
						System.out.print(rs.getString(j) + "\t\t");
					System.out.println();
				}
				rs.close();
			}catch(SQLServerException e){
				System.out.println("Check input formatting");
			}
			
			//increment date by 1 at the end
			date.add(Calendar.DATE, 1);
			//add separator for each day of the week
			if(i < 6)
				System.out.println("----------------------Day " + (i+2) + "----------------------------");
		}
		System.out.println("------------------------------------------------------");
	}
	
	public static void addDriver(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Driver name: ");
		String driver = sc.nextLine().trim();
		System.out.print("Phone number: ");
		String phone = sc.nextLine().trim();
		
		//insert into driver
		try{
			stmt.execute("INSERT INTO Driver VALUES ('" + driver + "', '" + phone + "')");
			System.out.println("Successfully added a new Driver");
		}catch (SQLServerException e){
			System.out.println("Check input formatting");
		}
	}
	
	public static void addBus(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Bus ID: ");
		String bus = sc.nextLine().trim();
		System.out.print("Bus model: ");
		String model = sc.nextLine().trim();
		System.out.print("Bus year: ");
		String year = sc.nextLine().trim();
		
		//insert into bus
		try{
			stmt.execute("INSERT INTO Bus VALUES ('" + bus + "', '" + model + "', '" + year + "')");
			System.out.println("Successfully added a new Bus");
		}catch (SQLServerException e){
			System.out.println("Check input formatting");
		}
	}
	
	public static void deleteBus(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Bus ID: ");
		String bus = sc.nextLine().trim();
		
		try{
			//if delete returns 0 that means no rows found matching that data so output an error
			if(stmt.executeUpdate("DELETE Bus " + 
								"WHERE BusID = '" + bus + "'") == 0){
				System.out.println("No Bus ID = " + bus);
			}else{
				System.out.println("Successfully deleted");
			}
		}catch(SQLServerException e){
			System.out.println("No Bus ID = " + bus);
		}
	}
	
	public static void insertTripData(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Trip Number: ");
		String tripNo = sc.nextLine().trim();
		System.out.print("Date: ");
		String date = sc.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = sc.nextLine().trim();
		System.out.print("Stop Number: ");
		String stop = sc.nextLine().trim();
		System.out.print("Scheduled Arrival Time: ");
		String arrivalTime = sc.nextLine().trim();
		System.out.print("Actual Start Time: ");
		String actualStart = sc.nextLine().trim();
		System.out.print("Actual Arrival Time: ");
		String actualArrival = sc.nextLine().trim();
		System.out.print("Passengers in: ");
		String passIn = sc.nextLine().trim();
		System.out.print("Passengers out: ");
		String passOut = sc.nextLine().trim();
		
		try{
			//add that info into actual trip stop
			stmt.execute("INSERT INTO ActualTripStopInfo VALUES ('" + tripNo + "', '" + date + "', '" + startTime + "', '" + stop + "', '" + arrivalTime
					 + "', '" + actualStart + "', '" + actualArrival + "', '" + passIn + "', '" + passOut + "')");
		}catch(SQLServerException e){
			System.out.println("Check input formatting");
		}
		System.out.println("Successfully recorded data");
	}
}

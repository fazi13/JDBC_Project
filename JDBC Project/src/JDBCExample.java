import java.sql.*;
import java.util.*;

import com.microsoft.sqlserver.jdbc.SQLServerException;

public class JDBCExample {
	public static void main(String[] args) {
		ResultSet rs;
		Connection conn;
		Statement stmt;
		Scanner sc = new Scanner(System.in);
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
	         System.out.println("d: Display Schedule given Start Location, Destination, and Date");
	         while(true){
	        	 input = sc.nextLine();
	        	 if(input.trim().charAt(0) == 'd')
	        		 displaySchedule(stmt);
	         }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void addTripOffering(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		String input;
		System.out.print("Enter Trip Number, Date, Scheduled Start Time, Scheduled Arrival Time, Driver Name, Bus ID: ");
		input = sc.nextLine();
		String[] info = input.split(", ");
		stmt.execute("INSERT INTO TripOffering VALUES " + "(info[0], info[1], info[2], info[3], info[4], info[5])");
	}
	
	public static void displaySchedule(Statement stmt) throws SQLException{
		Scanner sc = new Scanner(System.in);
		String input;
		System.out.print("Start Location Name: ");
		String startLoc = sc.nextLine().trim();
		System.out.print("Destination Name: ");
		String destLoc = sc.nextLine().trim();
		System.out.print("Date: ");
		String date = sc.nextLine().trim();
		try{
			ResultSet rs = stmt.executeQuery("SELECT T0.ScheduledStartTime, T0.ScheduledArrivalTime, T0.DriverName, T0.BusID " +
										"FROM TripOffering T0, Trip T1 " +
										"WHERE T1.StartLocationName LIKE '" + startLoc + "' AND " +
										"T1.DestinationName LIKE '" + destLoc + "' AND " +
										"T0.Date = '" + date + "' AND " +
										"T1.TripNumber = T0.TripNumber;");
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			for(int i = 1; i <= colCount; i++){
				System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			while(rs.next()){
				for(int i = 1; i <= colCount; i++)
					System.out.print(rs.getString(i) + "\t\t");
				System.out.println();
			}
			rs.close();
		}catch (SQLServerException e){
			System.out.println("No schedule from " + startLoc + " to " + destLoc + " on " + date);
		}
	}
}

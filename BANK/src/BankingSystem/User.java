package BankingSystem;
import java.sql.*;
import java.util.Scanner;

public class User {
	private  Connection connection;
	private  Scanner scanner;
	public User(Connection connection, Scanner scaneer) {
		//super();
		this.connection = connection;
		this.scanner = scaneer;
	}
	public void Register() {
		scanner.nextLine();
		System.out.println("Enter Full_Name");
		String Full_Name=scanner.next();
		System.out.println("Enter email");
		String Email=scanner.next();
		System.out.println("Paasword");
		String Password=scanner.next();
		  System.out.println("Confirm Security Pin:");
		    String Confirm_Pin = scanner.next();

		    // Check if security pins match
		    if(!Password.equals(Confirm_Pin)) {
		        System.out.println("Security Pins do not match. Please try again.");
		        return;
		    }
		
		if( user_exits(Email)) {
			System.out.println("Email already exits");
			return;
		}
		String Register_query="INSERT INTO USER(Full_Name,Email,Password) VALUES (?,?,?)";
		try {
			PreparedStatement preparedstatement=connection.prepareStatement(Register_query);
//			Connection coneection=
			preparedstatement.setString(1,Full_Name);
			preparedstatement.setString(2,Email);
			preparedstatement.setString(3, Password);
			int rowaffected=preparedstatement.executeUpdate();
			if(rowaffected>0) {
				System.out.println("Registartion is succesfull");
			}
			else{
				System.out.println("Registration is not succesfull!!! do it again");
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	 public String login() {
		 scanner.nextLine();
		 System.out.println("Enter Email: ");
		 String Email=scanner.next();
		 System.out.println("Enter Password: ");
		 String Password=scanner.next();
	String login_query="SELECT * FROM USER WHERE EMAIL=? AND PASSWORD=?";
	try {
		PreparedStatement preparedstatement=connection.prepareStatement(login_query);
		preparedstatement.setString(1, Email);
		preparedstatement.setString(2, Password);
		ResultSet resultset=preparedstatement.executeQuery();
		 if(resultset.next()){
             return Email;
         }else{
             return null;
         }
	}
		 catch(SQLException e) {
			 System.out.println(e.getMessage());
		 }
	return null;
	}
	 
	 public  boolean user_exits(String Email) {
		// scanner.nextLine();
		 String query = "SELECT * FROM user WHERE email = ?";
		  try{
	          //  PreparedStatement preparedStatement = connection.prepareStatement(query);
			  PreparedStatement preparedStatement = connection.prepareStatement(query);
	            preparedStatement.setString(1, Email);
	            ResultSet resultSet = preparedStatement.executeQuery();
	            if(resultSet.next()){
	                return true;
	            }
	            else{
	                return false;
	            }
	        }catch (SQLException e){
	            e.printStackTrace();
	        }
	        return false;
	    }
	 
	 }

	


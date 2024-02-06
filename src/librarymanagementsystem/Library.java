package librarymanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Library {
	static Connection con = null;
	static PreparedStatement ps = null;
	static Scanner sc = new Scanner(System.in);

	// Method for new user registeration.
	public static void userRegister() {
		System.out.println("WELCOME TO REGISTERATION");
		boolean isExistingUser = false; // To check the user is new or existing.
		System.out.println("Please Provide your emailId");
		// Getting email id from user
		sc.nextLine();
		String emailId = sc.nextLine();
		// JDBC Connection
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarymanagement", "root", "root");
			// Checking whether the emailid is already registered or not.
			String query = "select emailid from logincredentials where emailid = ?";
			ps = con.prepareStatement(query);
			ps.setString(1, emailId);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				// If result is not null, then there is a existing user with this emailId.
				isExistingUser = true;
				// Updating existinguser to true.
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		if (!isExistingUser) {
			// If the user is not existing, then user should register.
			boolean isPasswordMatching = false;
			
				System.out.println("Enter userName :");
				String userName = sc.nextLine(); // Getting user name as input.
				do {
				System.out.println("Set your password : ");
				String firstPassword = sc.nextLine(); // Getting user password.
				System.out.println("ReEnter your password : ");
				String confirmPassword = sc.nextLine(); // Ensuring to retype the password for confirmation.
				// If first and confirm password matches,
				if (firstPassword.equals(confirmPassword)) {
					String insertQuery = "Insert into logincredentials (name,password,emailid,status) values (?,?,?,?)";
					isPasswordMatching = true;
					// Inserting the user emailid, name, password and status as 1 into
					// logincredentials table.
					try {
						ps = con.prepareStatement(insertQuery);
						ps.setString(1, userName);
						ps.setString(2, confirmPassword);
						ps.setString(3, emailId);
						ps.setInt(4, 1);
						int result = ps.executeUpdate();
						System.out.println(result == 1 ? "Registered Successfully" : "Failed");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else {
					System.out.println("Entered password and confirm password should match!");
					isPasswordMatching = false;
				}
			} while (!isPasswordMatching);
		}
		// If the user is already registered then prompting user to login.
		// And login method is called.
		else {
			System.out.println("Already registered user!!! Please Login..");
			Library.userLogin();
		}
	}

	// Login for already registered user.
	public static void userLogin() {
		boolean isExistingUser = false; // To check whether, user is registered or new.
		boolean isPasswordCorrect = false; // To validate password.
		System.out.println("WELCOME TO LOGIN");
		System.out.println("Enter your emailId : ");
		sc.nextLine();
		String emailId = sc.nextLine(); // Getting email id as input
		// JDBC Connection
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarymanagement", "root", "root");
			// Ensuring the user is already a existing or not.
			String query = "select * from logincredentials where emailid = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, emailId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("emailid").equals(emailId)) { 
					// Checking emailid provided by user is already present
					// in logincredentials Database.
					// If so, the user is registered and ready to login
					isExistingUser = true; // updating the status to true.
					do {
						System.out.println("Enter the password");
						String userPassword = sc.nextLine(); // Getting password from user.
						// Validating the password
						if (rs.getString("password").equals(userPassword)) {
							// If password matches,
							System.out.println("Sucessfully LoggedIn");
							isPasswordCorrect = true;
							// Creating the object form user.
							User obj = new User(rs.getInt("id"), rs.getString("emailid"), rs.getString("name"),
									rs.getString("password"));
							UserOperations operations = new UserOperations(obj, con, ps);
							operations.userOperations();
							// Invoking user operations method.
							break;
						}
						// Mismatch password
						else {
							System.out.println("Your password is Incorrect");
							System.out.println("1. Retry password");
							System.out.println("2. Reset Password");
							System.out.println("What you want to try?");
							int option = sc.nextInt();
							sc.nextLine();
							switch (option) {
							case 1:
								// Retry password.
								// Goto line 110.
								break;
							case 2:
								// Reset password,
								System.out.println("Enter new password : ");
								String newPassword = sc.nextLine();
								// Getting new password from user.
								String updatePasswordQuery = "update logincredentials set password = ? where emailid = ?";
								// Updating new password in logincredentails table.
								ps = con.prepareStatement(updatePasswordQuery);
								ps.setString(1, newPassword);
								ps.setString(2, emailId);
								int result = ps.executeUpdate();
								System.out.println(result == 1 ? "Password changed successfull" : "Failed");
								// Updating status to true.
								isPasswordCorrect = true;
								Library.userLogin();
								break;
							default:
								System.out.println("Invalid operation");
							}
						}
					} while (!isPasswordCorrect);
					// loop breaks when the password matches or password updated.
				}
			}
			// If the email id is not correct.
			else {
				System.out.println("No User Found!!!");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Admin Login
	public static void adminLogin() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarymanagement", "root", "root");
		} catch (Exception e) {
			System.out.println(e);
		}
		// Creating obj for admin class.
		Admin adminObj = new Admin();
		String adminName = "";
		do {
			System.out.println("Enter the Admin name : ");
			adminName = sc.nextLine(); // Getting adminname as input till the admin name is correct.
		} while (!adminName.equals(adminObj.getAdminName()));
		boolean isPasswordMatching = false;
		do {
			if (adminName.equals(adminObj.getAdminName())) {
				// Getting the admin password
				System.out.println("Enter password : ");
				String adminPassword = sc.nextLine();
				// Validating password
				if (adminPassword.equals(adminObj.getAdminPassword())) {
					// Password is correct
					isPasswordMatching = true;
					AdminOperations operation = new AdminOperations(con, ps);
					// object is created.
					operation.showMenu(); // showMenu is called.
				} else {
					// Incorrect password
					System.out.println("Incorrect password");
				}
			}
			// Repeats till the admin password is correct.
		} while (!isPasswordMatching);
	}

	public static void main(String[] args) {
		int userType = 0;
		int option = 0;		
		do {			
			try {				
			System.out.println("WELCOME TO LIBRARY!!!");
			System.out.println("Please choose the type of user");
			System.out.println("1. Admin \n2. User\n3. Exit");
			userType = sc.nextInt();// Getting user type as input
			sc.nextLine();
			switch (userType) {
			case 1:
				// Admin
				System.out.println("WELCOME ADMIN");
				adminLogin();
				break;
			case 2:
				// User
				System.out.println("WELCOME USER");
				do {
					System.out.println("1. New User? Please Register!");
					System.out.println("2. Existing User? Please Login!");
					System.out.println("3. Exit");		
					option = sc.nextInt();
					switch (option) {
					case 1:
						// New user - Registeration
						Library.userRegister();
						break;
					case 2:
						// Existing user - Login
						Library.userLogin();
						break;
					case 3:
						// Exit
						System.out.println("Thank You \nVisit Again");
						break;
					default:
						System.out.println("Invalid Operations");
						break;
					}
				} while (option != 3);
				break;
			case 3:
				System.out.println("Exit");
				break;
			default:
				System.out.println("Invalid userType");
				break;
			}		
			}
			catch(InputMismatchException e) {
				System.out.println("Enter valid input");
				break;
			}		
		} while (userType != 3);
		
	}
}

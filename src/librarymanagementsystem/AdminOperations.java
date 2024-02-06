package librarymanagementsystem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class AdminOperations {
	private int choice = 6;
	private Connection con;
	private PreparedStatement ps;
	Scanner sc = new Scanner(System.in);
	
	// Invoking the AdminOperation constructor.
	public AdminOperations(Connection con, PreparedStatement ps) {
		super();
		this.con = con;
		this.ps = ps;
	}
	
	public void showMenu() {
		// Displays the admin operations
		do {
			System.out.println("ADMIN OPERATIONS");
			System.out.println("1. View Member");
			System.out.println("2. Delete member");
			System.out.println("3. Add Books");
			System.out.println("4. Delete Books");
			System.out.println("5. Explore Books");
			System.out.println("6. Back");
			System.out.println("Enter your choice : ");
			choice = sc.nextInt();
			try {	
				switch(choice) {
				case 1:
					viewMember();
					// View particular user or view all users.
					break;
				case 2:
					// Delete the particular member using id.
					deleteMember();
					break;
				case 3:
					// Add new book in books Database.
					addBooks();
					break;
				case 4:
					// Delete particular book in db using book id.
					deleteBook();
					break;
				case 5:
					// Displays some more opertaions from Booksoperation class.
					new BookOperations(con, ps, null).viewOperations();
					// Creating anonymous object and calling view operation method.
					break;
				case 6:
					// Retuns back
					break;
				default:
					// Invalid options
					System.out.println("Invalid Operation!!!");
				}
			}
			catch(SQLException e) {
				System.out.println("SQL Exception Occured, check the query!");
				e.printStackTrace();
			}
		}while(choice != 6);	
	}
	
	public void viewMember() throws SQLException {
		int option = 3;
		do {
			System.out.println("VIEW MEMBER");
			System.out.println("1. View all Member");
			System.out.println("2. View particular member");
			System.out.println("3. Back");
			System.out.println("Enter the option : ");
			option = sc.nextInt();
			switch(option) {
			case 1:
				// Displays all details of existing users.
				viewAllMember();
				break;
			case 2:
				// Gets the user id and displays all details of that particular member.
				viewParticularMember();
				break;
			case 3:
				// Returns back
				break;
			default:
				// Invalid option
				System.out.println("Invalid operation");	
				break;
			}
		}while(option != 3);	
	}
	
	public void viewParticularMember() throws SQLException {
		// Displays a particular member.
		System.out.println("VIEW PARTICULAR MEMBER/USER ");
		System.out.println("Enter user id you want to view:");
		int adminInput =  sc.nextInt();
		// Getting user id to display that user details.
		String query = "select name from logincredentials where id = ? and status = 1";
		// Finding whether the user id is avaiable or not.
		ps = con.prepareStatement(query);
		ps.setInt(1, adminInput);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			// If rs is not null,
			// then there is a user with that userid provided by user.
			System.out.println(adminInput +" MEMBER DETAILS");
			// Selecting id, name, emailid, totalbooks borrowed, total fine
			String selectMemberQuery = "SELECT \r\n"
					+ "    lc.id AS id,\r\n"
					+ "    lc.name AS name,\r\n"
					+ "    lc.emailid AS emailid,\r\n"
					+ "    SUM(COALESCE(bb.bookcount, 0)) AS total_bookcount,\r\n"
					+ "    SUM(COALESCE(bb.bookfine, 0)) AS total_bookfine\r\n"
					+ "FROM \r\n"
					+ "    logincredentials lc\r\n"
					+ "LEFT JOIN \r\n"
					+ "    borrowedbooks bb ON lc.id = bb.userid\r\n"
					+ "WHERE \r\n"
					+ "    lc.status = 1 and lc.id = ?\r\n"
					+ "GROUP BY \r\n"
					+ "    lc.id, lc.name, lc.emailid;";					
			ps = con.prepareStatement(selectMemberQuery);
			// Setting values for query.
			ps.setInt(1, adminInput);
			// Executing the query.
			ResultSet resultSet = ps.executeQuery();
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.printf("%5s %20s %15s %35s %15s", "ID","NAME","EMAIL","BORROWED BOOK COUNT","TOTAL FINE\n");	
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------");
			// Displaying the details from resultset
			while(resultSet.next()) {
				System.out.printf("%5s %20s %25s %10s %25s",
						resultSet.getInt("id"),resultSet.getString("name"),resultSet.getString("emailid"),
						resultSet.getInt("total_bookcount"),resultSet.getInt("total_bookfine")+"\n");
			}
		}
		else {
			// If resultset is null, then no id is present.
			System.out.println("No Id found");
		}
	}	
	
	public void viewAllMember() throws SQLException {
		// Displays all existing authorized user.
		System.out.println("VIEW ALL MEMBER");
		// Fetching id, name, emailid, totalbookcount, bookfine for all users with status as 1.
		String viewMemberQuery = "SELECT \r\n"
				+ "    lc.id AS id,\r\n"
				+ "    lc.name AS name,\r\n"
				+ "    lc.emailid AS emailid,\r\n"
				+ "    SUM(COALESCE(bb.bookcount,0)) AS total_bookcount,\r\n"
				+ "    SUM(COALESCE(bb.bookfine, 0)) AS total_bookfine\r\n"
				+ "FROM \r\n"
				+ "    logincredentials lc\r\n"
				+ "LEFT JOIN \r\n"
				+ "    borrowedbooks bb ON lc.id = bb.userid\r\n"
				+ "WHERE \r\n"
				+ "    lc.status = 1\r\n"
				+ "GROUP BY \r\n"
				+ "    lc.id, lc.name, lc.emailid";	
		ps = con.prepareStatement(viewMemberQuery);
		ResultSet rs = ps.executeQuery();
		// Executing the query.
		System.out.println(
				"------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%5s %20s %15s %35s %15s", "ID","NAME","EMAIL","BORROWED BOOK COUNT","TOTAL FINE\n");
		System.out.println(
				"------------------------------------------------------------------------------------------------------------------------------------------------");
		// Displaying the details in table format.
		while(rs.next()) {
			System.out.printf("%5s %20s %25s %10s %25s",
					rs.getInt("id"),rs.getString("name"),rs.getString("emailid"),
					rs.getInt("total_bookcount"),rs.getInt("total_bookfine")+"\n");	
		}
	}
	
	public void deleteMember() throws SQLException {
		// Deletes the particular user. 
		System.out.println("DELETE MEMBER");
		System.out.println("Enter id of user to be deleted: ");
		int deleteId = sc.nextInt();
		// Getting userid to be deleted.
		String checkAvailableUser = "select * from logincredentials where id = ? and status = 1";
		// Checking the user id provided by admin is existing or not.
		ps = con.prepareStatement(checkAvailableUser);
		ps.setInt(1, deleteId);
		// Setting the values for query.
		ResultSet rs = ps.executeQuery();
		// Executing the query.
		// rs is null, then no user is present.
		if(rs.next()) {
			// Resultset is not null,
			// So there is a user with the id provided by admin.
			String deleteQuery = "UPDATE logincredentials SET status = 0 WHERE (id = ?);";
			// Updating the status as 0 for the corresponding user id.
			ps = con.prepareStatement(deleteQuery);
			ps.setInt(1, deleteId); // Setting values for query.
			int result = ps.executeUpdate();
			// Executing the query.
			System.out.println(result==1?"Deleted "+deleteId +" successfully":" No member Id found");
		}
		else {
			// No user is present.
			System.out.println("No userid found");
		}	}
	
	public void addBooks() throws  SQLException {
		//Adds new book to database.
		sc.nextLine();
		System.out.println("ADD BOOKS");
		System.out.println("Enter BookName : ");
		String bookName = sc.nextLine(); // Input book name
		System.out.println("Enter BookPrice : ");
		int bookPrice = sc.nextInt(); // Input book price
		sc.nextLine();
		System.out.println("Enter BookGenre : ");
		String bookGenre = sc.nextLine(); // Input book genre
		System.out.println("Enter AuthorName : ");
		String authorName = sc.nextLine(); // Input Author name
		System.out.println("Enter publication : ");
		String publication = sc.nextLine(); // Input publication
		Date publishedDate = null;
		try {
			// Used try block because of IllegalArgumentException.
			System.out.println("Enter publishDate(YYYY-MM-DD) :");
			publishedDate = Date.valueOf(sc.nextLine()); // Getting publish date.
//			throw new IllegalArgumentException("Date format is incorrect");
		}
		catch(IllegalArgumentException e) {
			// Catching the exception
			System.out.println("Date format is not matching");
			System.out.println("Enter publishDate(YYYY-MM-DD) :");
			publishedDate = Date.valueOf(sc.nextLine());
			}	
		System.out.println("Enter book Edition :"); 
		int bookEdition = sc.nextInt(); // Input book edition
		System.out.println("Enter Book Count :"); 
		int bookCount = sc.nextInt();	// Input book count	
		// Creating insert query to add the book details.
		String insertBookQuery = "Insert into books (`bookName`, `bookPrice`, `bookType`, `authorname`, `publication`, `publishdate`, `bookedition`, `bookCount`,`status`) values (?,?,?,?,?,?,?,?,?)";
		ps = con.prepareStatement(insertBookQuery);
		// Setting the values for the query.
		ps.setString(1, bookName);
		ps.setInt(2, bookPrice);
		ps.setString(3, bookGenre);
		ps.setString(4, authorName);
		ps.setString(5, publication);
		ps.setDate(6, publishedDate);
		ps.setInt(7, bookEdition);
		ps.setInt(8, bookCount);
		ps.setInt(9, 1);
		// Executing the query.
		int result = ps.executeUpdate();
		System.out.println(result == 1 ? "Book added successfully!!":"Not added");
	}
	
	private void deleteBook() throws SQLException {
		// Deletes the book with book id.
		System.out.println("DELETE BOOK");
		System.out.println("Enter book id to be deleted: ");
		int bookId = sc.nextInt(); // Admin inputs book id to be deleted.
		String deleteQuery = "UPDATE books SET status = 0 WHERE (bookid = ?);";
		// Updating the book status to 0 for the particular book id.
		ps = con.prepareStatement(deleteQuery);
		ps.setInt(1, bookId);
		// Setting bookid to query.
		int result = ps.executeUpdate();
		// Executing the query.
		System.out.println(result==1?"Deleted book "+bookId +" successfully!!":"No BookId found");
		
	}

}

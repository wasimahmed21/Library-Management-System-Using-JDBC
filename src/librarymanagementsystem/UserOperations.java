package librarymanagementsystem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

public class UserOperations {
	private User user;
	private Connection con;
	private PreparedStatement ps;
	Scanner sc = new Scanner(System.in);

	// Constructor
	public UserOperations(User user, Connection con, PreparedStatement ps) {
		super();
		this.user = user;
		this.con = con;
		this.ps = ps;
	}

	// Displays the useroperations menu
	public void userOperations() {
		int option = 7;
		do {
			System.out.println("Enter the choice of operation to be performed");
			System.out.println("1. Explore books");
			System.out.println("2. Place Order");
			System.out.println("3. View Borrowed Book");
			System.out.println("4. Return Book");
			System.out.println("5. View Fine");
			System.out.println("6. Exit");
			System.out.println("Enter the option : ");
			option = sc.nextInt();
			try {
				switch (option) {
				case 1:
						new BookOperations(con,ps,user).viewOperations();
						// view operations from bookoperations is called.
						break;
				case 2:
					placeOrder();
					// Borrow book
					break;
				case 3:
					viewBorrowedBooks();
					// Displays the borrowed book.
					break;
				case 4:
					returnBook();
					// Return the borrowed book
					break;
				case 5:
					viewFine();
					// Calculates fine
					break;
				case 6:
					// Returns back
					System.out.println("Hope you enjoyed!!");
					break;
				default:
					System.out.println("Invalid Operations");
				}
			}catch(SQLException e) {
				System.out.println("SQL Exception Occured, Contact the Admin!");
				e.printStackTrace();
			}
		} while (option != 6);
	}
	
	public void placeOrder() throws SQLException {
		System.out.println("PLACE ORDER");
		boolean isOrderPlaced = false;
		do {		
			int availableBooks = 0;
			// Getting user choice of book id to be borrowed.
			System.out.println("Enter the book Id: ");
			int bookId = sc.nextInt();
			// Checking whether the book id is available or not.
			String searchBookByIdQuery = "select bookName, bookCount from books where bookId = ? and status = 1";
			ps = con.prepareStatement(searchBookByIdQuery);
			// Setting values into query.
			ps.setInt(1, bookId);
			// Executing the query.
			ResultSet rs = ps.executeQuery(); 
			int result = 0;
			// If result set is not null,
			// then there is a book with the id provided by user.
			if(rs.next()) {
				// Book exists
				System.out.println(rs.getString("bookName") +"\t\t Count: "+rs.getInt("bookCount"));
				// Displaying the bookname and bookcount for the book id provided by user. 
				availableBooks = rs.getInt("bookCount");	
				if(availableBooks > 0) // Checking the availabity of books
				{ 
					// If books is available
					do {				
						System.out.println("Enter the book quantity");
						int quantity = sc.nextInt();
						// Getting quantity of books to be borrowed from user.
						if(quantity <= availableBooks && quantity > 0) {
							// Checking user provided quantity is < than avaibooks and not negative number
							isOrderPlaced = true;
							// Updating books table by reducing the quantity borrowed from user.
							String bookCountUpdateQuery = "UPDATE books SET bookCount = "
														+ "? WHERE bookId = ? and status = 1";
							ps = con.prepareStatement(bookCountUpdateQuery);
							ps.setInt(1, availableBooks - quantity);
							ps.setInt(2, bookId);
							// Setting values
							result = ps.executeUpdate();
							// Executing the query.
							System.out.println(result == 1 ?
									"Book name: " + rs.getString("bookName") + " Quantity:" + quantity + " Borrowed Successfully" :
									"Borrow Failed!");	
							// Inserting a record into borrowedbooks with userid, bookid, bookcount, borroweddate, return status as 0.
							if(result == 1) {
								String insertBorrowedQuery = "INSERT INTO borrowedbooks "
										+ "(`userid`, `bookid`, `bookcount`, `borroweddate`, `bookfine`,`returnStatus`) "
										+ "VALUES "
										+ "(?,?,?,?,?,?)";						
								ps = con.prepareStatement(insertBorrowedQuery);
								ps.setInt(1, user.getId());
								ps.setInt(2,bookId);
								ps.setInt(3, quantity);
								LocalDate date = LocalDate.now();
								ps.setDate(4,Date.valueOf(date));
								ps.setInt(5, 0);
								ps.setInt(6, 1);
								// Setting value to query
								int insertResult = ps.executeUpdate();
								// Executing query.
							}	
						// If bookcount is more than avail books or negtive count entered else is executed.
						}else {
							System.out.println("Enter valid book count!");
						}
					}while(result != 1);
				// Book not available temporily
				}else {
					System.out.println("Currently book not available");
				}	
			// Wrong book id.
			}else {
				System.out.println("Enter a Valid Book ID");
				isOrderPlaced = false;
			}
		}while(!isOrderPlaced);
	}

	public void viewBorrowedBooks() throws SQLException {
		System.out.println("VIEW BORROWED BOOKS");
		// selecting bookname, bookcount and book id from books
		// left joins with id.
		String borrowedBookQuery = "select bookName,borrowedbooks.bookCount, borrowedbooks.borrowedid from books "
				+ " left join borrowedbooks on books.bookId = borrowedbooks.bookId "
				+ " where borrowedbooks.userid = ? and borrowedbooks.returnStatus = 1";
		ps = con.prepareStatement(borrowedBookQuery);
		ps.setInt(1, user.getId());
		// Setting the values
		ResultSet rs = ps.executeQuery();
		// Executing the query.
		if(rs.next()) {
			// If resultset is not null
			System.out.println("You borrowed these books: ");
			System.out.println("Borrowed Id : " + rs.getString(3));
			System.out.println("Book Name : " + rs.getString(1));
			System.out.println("Book Count : "+rs.getInt(2));	
			while(rs.next()) {
				System.out.println();
				System.out.println("Borrowed Id : " + rs.getString(3));
				System.out.println("Book Name : " + rs.getString(1));
				System.out.println("Book Count : "+rs.getInt(2));
			}		
		}
		else {
			// If resultset is null
			System.out.println("No Borrowed books found!!");
		}	
	}

	public void viewFine() throws SQLException {
		System.out.println("VIEW FINE");
		System.out.println("Calculating your fine...");
		// Selecting the total sum for the particular id whose status is 0
		String calculateFineQuery = "select sum(bookfine) as bookfine from borrowedbooks where userid = ?";
		ps = con.prepareStatement(calculateFineQuery);
		// Setting values
		ps.setInt(1, user.getId());
		// Executing the query
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			System.out.println("Your total fine amount is : "+rs.getInt("bookfine"));	
		}
	}

	public void returnBook() throws SQLException {
		System.out.println("RETURN BOOKS");	
		// Selects the bookcount from borrowedbooks
		String getBorrowedBookCount = " SELECT sum(bookCount) as bookCount from borrowedbooks where userid = ? ";
		// Checking whether there is a book in that particular user id.
		ps = con.prepareStatement(getBorrowedBookCount);
		ps.setInt(1, user.getId());
		// Setting the values into query
		ResultSet res = ps.executeQuery();
		// Executing the query
		if(res.next() && res.getInt("bookCount") <= 0) {
			// If bookcount is 0 and resultset is null, no books available in the user id.
			System.out.println("No Borrowed Books to return");
		}
		else {
			// If not null,
			viewBorrowedBooks();
			// Calling view borrowedbooks for displaying the book details.
			System.out.println("Enter the borrowed id to be returned..");
			int borrowedId = sc.nextInt();
			// Getting input as borrowedid.
			String searchBook = "select bookCount,borroweddate,borrowedid from borrowedbooks where borrowedid = ? and userid = ? "
					+ "and returnStatus = 1";
			// Selecting count,date,borrowid for that userid with status as 1.
			ps = con.prepareStatement(searchBook);
			ps.setInt(1, borrowedId);
			ps.setInt(2, user.getId());
			ResultSet rs = ps.executeQuery();
			// Executing the query.
			int returnCount = 0;
			if(rs.next()) {
				System.out.println("Enter the number of books to be returned : ");
				returnCount = sc.nextInt();
				// Getting the book quantity to be retuned.
				if((returnCount <= rs.getInt("bookcount") && returnCount > 0)) {				
					String updateStatus = "" ;					
					if(rs.getInt("bookcount") == returnCount) {
						updateStatus = "update borrowedbooks set returnStatus = 0, bookcount = ? where borrowedid = ?";	
					}
					else {
						updateStatus = "update borrowedbooks set bookcount = ? where borrowedid = ? ";
					}					
					LocalDate returnDate = LocalDate.now();
					// Finding return date
					LocalDate borrowedDate = rs.getDate("borroweddate").toLocalDate();
					Period difference = Period.between(borrowedDate,returnDate);
					// Calculating the differemce between two dates.
					ps = con.prepareStatement(updateStatus);
					ps.setInt(1, rs.getInt("bookcount")-returnCount);
					ps.setInt(2, borrowedId);
					int returnResult = ps.executeUpdate();
					// excuting the query to update borrowedbooks.
					System.out.println(returnResult==1?"Returned sucessfully!!":"Failed");
					// Calculating fine
					if(difference.getDays()>10) {
						// If days more than 10, adding fine for each day.
						int extraDaysCount = difference.getDays()-10;
						int oneDayFineAmount = 2;
						int totalFine = extraDaysCount * oneDayFineAmount * returnCount;
						// Total fine is calculated.
						String updateFineQuery = "update borrowedbooks set bookfine = ? where userid = ? and borrowedid = ?";
						// Updates the fine in borrowedbooks for the current userid.
						ps = con.prepareStatement(updateFineQuery);
						ps.setInt(1, totalFine);
						ps.setInt(2, user.getId());
						ps.setInt(3, borrowedId);
						// Setting values into query.
						int result = ps.executeUpdate();
						// Executing the query.
						System.out.println(result==1?"Fine Added!":"No fine!");
					}
					else {
						System.out.println("No Fine!!");
					}	
				}			
			}
			else {
				System.out.println("Invalid operation performed");
			}
		}	
	}
}

package librarymanagementsystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookOperations {
	Scanner sc = new Scanner(System.in);
	private Connection con;
	private PreparedStatement ps;
	private User user;
	
	// Constructor 
	public BookOperations(Connection con, PreparedStatement ps, User user) {
		super();
		this.con = con;
		this.ps = ps;
		this.user = user;
	}

	// Displays the operations menu
	public void viewOperations() {
		int option = 7;
		do {
			System.out.println("1. Display Books");
			System.out.println("2. Display authors");
			System.out.println("3. Display Book genre");
			System.out.println("4. Filter book name");
			System.out.println("5. Filter book author");
			System.out.println("6. Filter book genre");
			System.out.println("7. Back");
			System.out.println("Enter your choice : ");
			option = sc.nextInt();
			try {
				switch(option) {
				case 1:
					// Displays all books from books db.
					displayBooks();
					break;
				case 2:
					// Displays all authors from books db.
					displayAuthor();
					break;
				case 3:
					// Displays all genre from books db.
					displayBookGenre();
					break;
				case 4:
					// Inputs bookname from user
					// Filters based on the bookname provided by user.
					filterByBookName();
					break;
				case 5:
					// Inputs book author from user
					// Filters based on the book author provided by user.
					filterByBookAuthor();
					break;
				case 6:
					// Inputs book genre from user
					// Filters based on the book genre provided by user.
					filterByBookGenre();
					break;
				case 7:
					// Returns back
					System.out.println("Back");
					break;
				default:
					// Invalid option
					System.out.println("Invalid opertion!");
				}
			}catch(SQLException e) {
				System.out.println("SQL Exception Occured, Contact the Admin!");
				e.printStackTrace();
			}
		}while(option != 7);
	}

	
	public void displayBooks() throws SQLException {
		// Displays all available books.
		System.out.println("DISPLAY BOOKS");
		String viewBookQuery = "select * from books where status = 1";
		// Selecting all books whose status = 1 from books db.
		ps = con.prepareStatement(viewBookQuery);
		// Executing the query.
		ResultSet rs = ps.executeQuery();
		// Displaying bookid, bookname, bookedition, bookprice, bookauthor, bookgenre, bookpublication, publishdate, bookcount.
		System.out.println(
				"------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%5s %20s %15s %10s %15s %20s %25s %20s", "BOOKID", "BOOKNAME", "EDITION", "PRICE",
				"AUTHORNAME", "GENRE", "PUBLICATION", "PUBLISHDATE", "BOOKCOUNT");
		System.out.println();
		System.out.println(
				"-------------------------------------------------------------------------------------------------------------------------------------------------");
		// If book is present
		// Displaying the book details in table format.
		if(rs.next()) {
			System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
					rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
					rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
					rs.getDate("publishdate"), rs.getInt("bookCount"));
			while (rs.next()) {
				System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
						rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
						rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
						rs.getDate("publishdate"), rs.getInt("bookCount"));
			}
		}
		// No books available in books db.
		else {
			System.out.println("No Book found...");
		}	
	}

	private void displayAuthor() throws SQLException {
		// Displays all unique author from book db.
		System.out.println("DISPLAY AUTHOR");
		// Selecting the unique authors from books db.
		String displayAuthorQuery = "select distinct authorname from books order by authorname";
		ps = con.prepareStatement(displayAuthorQuery);
		// Executing the query.
		ResultSet rs = ps.executeQuery();
		System.out.println("----------------");
		System.out.println("List of Authors");
		System.out.println("----------------");
		// Displaying the list of authors fetched from query.
		while(rs.next()) {
			System.out.println(rs.getString(1));
		}
	}

	private void displayBookGenre() throws SQLException {
		System.out.println("DISPLAY BOOK GENRE");
		// Displays all unique genre.
		String displayGenreQuery = "select distinct bookType from books order by bookType";
		// selecting unique book from book db.
		ps = con.prepareStatement(displayGenreQuery);
		ResultSet rs = ps.executeQuery();
		// Execute the query.
		System.out.println("----------------");
		System.out.println("List of Genre");
		System.out.println("----------------");
		while(rs.next()) {
			System.out.println(rs.getString(1));
		}	
	}

	private void filterByBookName() throws SQLException {
		// Filters the bookname based on the user input.
		System.out.println("FILTER BY BOOK NAME");
		System.out.println("Enter book name to be searched:");
		sc.nextLine();
		String bookName = sc.nextLine(); // Inputs bookname
		String searchBookQuery = "select * from books where bookName like CONCAT( '%',?,'%')";
		// Fetches the book name from books db like bookname provided by user.
		ps = con.prepareStatement(searchBookQuery);
		ps.setString(1, bookName);
		// Setting the values for query.
		ResultSet rs = ps.executeQuery();
		// Executing the query
		System.out.println(
				"--------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%5s %20s %15s %10s %15s %20s %25s %20s", "BOOKID", "BOOKNAME", "EDITION", "PRICE",
				"AUTHORNAME", "GENRE", "PUBLICATION", "PUBLISHDATE", "BOOKCOUNT");
		System.out.println();
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------------");
		// Printing the bookname, edition, author, bookid, price, genre, publications, date.
		// If resultset is not null, then displays.
		if(rs.next()) {
			System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
					rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
					rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
					rs.getDate("publishdate"), rs.getInt("bookCount"));
			while (rs.next()) {
				System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
						rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
						rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
						rs.getDate("publishdate"), rs.getInt("bookCount"));
			}
		}// If resultset is null, no book is found.
		else {
			System.out.println("No Book Found!");
		}	
	}

	private void filterByBookAuthor() throws SQLException {
		// Filters unique book author from books db.
		System.out.println("FILTER BY BOOK AUTHOR");
		System.out.println("Enter book Author to be searched:");
		sc.nextLine();
		String authorName = sc.nextLine();
		// Getting authorname as input from user.
		String searchAuthorQuery = "select * from books where authorName like CONCAT( '%',?,'%')";
		// selecting the author name from books db.
		ps = con.prepareStatement(searchAuthorQuery);
		ps.setString(1, authorName);
		// Setting values for query.
		ResultSet rs = ps.executeQuery();
		// Executing the query.
		System.out.println(
				"--------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%5s %20s %15s %10s %15s %20s %25s %20s", "BOOKID", "BOOKNAME", "EDITION", "PRICE",
				"AUTHORNAME", "GENRE", "PUBLICATION", "PUBLISHDATE", "BOOKCOUNT");
		System.out.println();
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------------");
		// If result set is not empty it displays the details.
		if(rs.next()) {
			System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
					rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
					rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
					rs.getDate("publishdate"), rs.getInt("bookCount"));
			while (rs.next()) {
				System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
						rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
						rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
						rs.getDate("publishdate"), rs.getInt("bookCount"));
			}
		}
		// If resultset is null
		else {
			System.out.println("No Author Found!");
		}
		
	}

	private void filterByBookGenre() throws SQLException {
		// Filters the book genre provided by user.
		System.out.println("FILTER BY BOOK GENRE");
		System.out.println("Enter book Genre to be searched:");
		sc.nextLine();
		String bookGenre = sc.nextLine(); // Inputting the genre from user
		String searchGenreQuery = "select * from books where bookType like CONCAT( '%',?,'%')";
		// Query for fetching genre from books table like user genre type
		ps = con.prepareStatement(searchGenreQuery);
		ps.setString(1, bookGenre);
		// Executing the query
		ResultSet rs = ps.executeQuery();
		System.out.println(
				"--------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%5s %20s %15s %10s %15s %20s %25s %20s", "BOOKID", "BOOKNAME", "EDITION", "PRICE",
				"AUTHORNAME", "GENRE", "PUBLICATION", "PUBLISHDATE", "BOOKCOUNT");
		System.out.println();
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------------");
		// If resultset is not null, atleast one value is present.
		if(rs.next()) {
			System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
					rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
					rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
					rs.getDate("publishdate"), rs.getInt("bookCount"));
			while (rs.next()) {
				System.out.printf("%5s %25s %10s %10s %15s %20s %25s %20s \n", rs.getInt("bookId"),
						rs.getString("bookName"), rs.getInt("bookedition"), rs.getInt("bookPrice"),
						rs.getString("authorname"), rs.getString("bookType"), rs.getString("publication"),
						rs.getDate("publishdate"), rs.getInt("bookCount"));
			}
		}
		// If resultset is null, no genre is found.
		else {
			System.out.println("No Genre Found!");
		}	
	}
}

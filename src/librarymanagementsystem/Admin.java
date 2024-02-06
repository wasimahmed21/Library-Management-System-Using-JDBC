package librarymanagementsystem;

public class Admin {
	private String adminName;
	private String adminPassword;
	
	// Constructor
	public Admin(String adminName, String adminPassword) {
		super();
		this.adminName = adminName;
		this.adminPassword = adminPassword;
	}
	
	// Hardcoded admin name and password for Demo.
	public Admin() {
		setAdminName("admin");
		setAdminPassword("adminpwd1$");
	}
	
	// Getting admin name
	public String getAdminName() {
		return adminName;
	}

	// Setting admin name
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	// Getting admin password
	public String getAdminPassword() {
		return adminPassword;
	}

	// Setting admin password
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	
}

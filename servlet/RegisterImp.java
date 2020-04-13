import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterImp {
	String DRIVER = "com.mysql.jdvc.Driver";
	String URL = "jdbc:mysql://173.52.92.21:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	String USER = "root";
	String PASSWORD = "ar4298";
	String sql = "INSERT INTO login (username, password) VALUE (?,?)";
	int i;

	public int registerUser(UserBeam user) {
		// TODO Auto-generated method stub
		String u = user.getUsername();
		String p = user.getPassword();
		
		// Connection to Database and store value in login table
		try {
			Class.forName(DRIVER);
			Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
			PreparedStatement ps= connection.prepareStatement(sql);
			ps.setString(1, u);
			ps.setString(2, p);
			i = ps.executeUpdate();
			
			
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}

}

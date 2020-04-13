import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.catalina.startup.UserDatabase;

import com.mysql.cj.protocol.Resultset;

public class LoginImp {
	String DRIVER = "com.mysql.jdbc.Driver";
	String URL = "jdbc:mysql://173.52.92.21:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	String USER = "root";
	String PASSWORD = "ar4298";
	
	Connection con;
	PreparedStatement ps;
	String SQL = "SELECT * FROM login where username=? AND password=?";
	ArrayList<UserBeam>userdatalist = new ArrayList<>();

	public ArrayList<UserBeam> gotoLogin(UserBeam b) {
		// TODO Auto-generated method stub
		String u = b.getUsername();
		String p = b.getPassword();
		
		try {
			Class.forName(DRIVER);
			con = DriverManager.getConnection(URL, USER, PASSWORD);
			ps = con.prepareStatement(SQL);
			ps.setString(1, u);
			ps.setString(2, p);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				UserBeam user = new UserBeam();
				user.setUsername(rs.getString(1));
				user.setPassword(rs.getString(2));
				
				userdatalist.add(user);
				
				System.out.println("UserDatabase was Retrieved");
				
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userdatalist;
		
	}

}

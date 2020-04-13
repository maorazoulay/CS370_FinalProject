
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.mysql.cj.xdevapi.JsonArray;

public class Login extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String u = req.getParameter("username");
		String p = req.getParameter("password");
		
		UserBeam b = new UserBeam();
		b.setPassword(p);
		b.setUsername(u);
		
		LoginImp loginimp = new LoginImp();
		ArrayList<UserBeam> userdata = loginimp.gotoLogin(b);
		
		for (UserBeam b1:userdata) {
			if (b1.getUsername().equals(u) && b1.getPassword().equals(p)) {
				JSONArray array = new JSONArray(userdata);
				// Send JSON array response
				PrintWriter pWriter = resp.getWriter();
				pWriter.write(array.toString());
				System.out.println("JSONarray data " + array.toString());
				
			}
		}
	}
}

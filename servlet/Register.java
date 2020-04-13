import java.io.IOException;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
		
		String u = req.getParameter("k1");
		String p = req.getParameter("k2");
		
		//username and password set in UserBeam
		
		UserBeam user = new UserBeam();
		user.setUsername(u);
		user.setPassword(p);
		
		RegisterImp rImp = new RegisterImp();
		int i = rImp.registerUser(user);
		
		if (i>0) {
			System.out.println("Register Successful");
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("REGISTER", "SUCCESS");
				resp.getWriter();
				PrintWriter pWriter = resp.getWriter();
				pWriter.write(jsonObject.toString());
				pWriter.print(jsonObject.toString());
				
				System.out.println("Register Successful" + jsonObject.toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

package com.rt.rk.ftn.tims;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rt.rk.ftn.tims.model.Korpa;
import com.rt.rk.ftn.tims.model.Proizvod;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		// BAYA PODATAKA
		Map<Integer, Proizvod> baya = new HashMap<Integer, Proizvod>();
		Proizvod p1 = new Proizvod(1, "Proizvod 1", 1500);
		Proizvod p2 = new Proizvod(2, "Proizvod 2", 2500);
		baya.put(p1.getId(), p1);
		baya.put(p2.getId(), p2);
		getServletContext().setAttribute("baya", baya);
		
		
		
	}



	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if("admin".equals(username) && "admin".equals(password)) {
			
			Korpa korpa = new Korpa();
			HttpSession session = request.getSession();
			session.setAttribute("korpa", korpa);
			response.sendRedirect("pregled.jsp");
			
		} else {
			response.sendRedirect("login.html");
		}
		
		
	}

}

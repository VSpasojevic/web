package com.rt.rk.ftn.tims;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SesijaServlet
 */
public class SesijaServlet extends HttpServlet {
	private static final long serialVersionUIDb = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SesijaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie cookie = new Cookie("language", "English");
		
		response.addCookie(cookie);
		
		HttpSession sesija = request.getSession();
		if(sesija.getAttribute("brojac") != null) {
			int brojac = (Integer) sesija.getAttribute("brojac");
			brojac++;
			sesija.setAttribute("brojac", brojac);
		} else {
			sesija.setAttribute("brojac", 1);
		}
	
		response.sendRedirect("cookie.jsp");
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

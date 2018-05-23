package com.rt.rk.ftn.tims;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rt.rk.ftn.tims.model.Korpa;
import com.rt.rk.ftn.tims.model.Proizvod;
import com.rt.rk.ftn.tims.model.ProizvodUKorpi;

/**
 * Servlet implementation class DodajUKorpuServlet
 */
public class DodajUKorpuServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DodajUKorpuServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String kolicina = request.getParameter("kolicina");
		int idInt = Integer.parseInt(id);
		int kolicinaInt = Integer.parseInt(kolicina);
		
		Map<Integer, Proizvod> baya = 
				(Map<Integer, Proizvod>) getServletContext().getAttribute("baya");
		
		HttpSession session = request.getSession();
		Korpa korpa = (Korpa) session.getAttribute("korpa");
		// IZVUCEMO IZ BAYE PROIZCOD KOJI SE DODAJE U KORPU 
		Proizvod proizvod = baya.get(idInt);
		/////////////////////////////////////////////
		ProizvodUKorpi proizvodKorpa = new ProizvodUKorpi();
		proizvodKorpa.setProizvod(proizvod);
		proizvodKorpa.setKolicina(kolicinaInt);
		/////////////////////////////////////////
		korpa.getProizvodi().add(proizvodKorpa);
		
		////////////////////
		response.sendRedirect("pregled.jsp");
		
		
		
		
		
	}

}

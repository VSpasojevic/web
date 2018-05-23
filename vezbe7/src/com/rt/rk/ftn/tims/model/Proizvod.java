package com.rt.rk.ftn.tims.model;

public class Proizvod {

	
	private String naziv;
	private int cena;
	private int id;
	
	
	public Proizvod() {
		
	}

	public Proizvod(int id, String naziv, int cena) {
		this.id = id;
		this.naziv = naziv;
		this.cena = cena;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public int getCena() {
		return cena;
	}

	public void setCena(int cena) {
		this.cena = cena;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
	
	
	
	
	

}

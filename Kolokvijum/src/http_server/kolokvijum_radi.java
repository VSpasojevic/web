package http_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Jednostavan web server
 */
public class httpd {

	private static List<Car> cars = new ArrayList<Car>();

	public static void main(String args[]) throws IOException {
		int port = 80;
		
		@SuppressWarnings("resource")
		ServerSocket srvr = new ServerSocket(port);

		System.out.println("httpd running on port: " + port);
		System.out.println("document root is: "
				+ new File(".").getAbsolutePath() + "\n");

		Socket skt = null;

		while (true) {
			try {
				skt = srvr.accept();
				InetAddress addr = skt.getInetAddress();

				String resource = getResource(skt.getInputStream());
				// zastita od praznih zahteve od browsera 
				if (resource == null) {
					continue;
				}
				// localhost /pocetna
				if (resource.equals(""))
					resource = "index.html";

				System.out.println("Request from " + addr.getHostName() + ": "
						+ resource);

				sendResponse(resource, skt.getOutputStream());
				skt.close();
				skt = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	static String getResource(InputStream is) throws IOException {
		BufferedReader dis = new BufferedReader(new InputStreamReader(is));
		String s = dis.readLine();
		System.out.println(s);
		// zastita od praznih zahteve od browsera 
		if (s == null) {
			return null;
		}

		String[] tokens = s.split(" ");

		// prva linija HTTP zahteva: METOD /resurs HTTP/verzija
		// obradjujemo samo GET metodu
		String method = tokens[0];
		if (!method.equals("GET")) {
			return null;
		}

		String rsrc = tokens[1];

		// izbacimo znak '/' sa pocetka
		rsrc = rsrc.substring(1);

		// ignorisemo ostatak zaglavlja
		String s1;
		while (!(s1 = dis.readLine()).equals(""))
			System.out.println(s1);

		return rsrc;
	}

	
	/***
	 * Funkcija koja parsira HTTP parametre
	 * Povratna vrednost je Map objekat u kom je kljuc naziv parametra, a vrednost pod tim kljucem, uneta vrednost sa forme
	 * 
	 * Primer upotrebe:
	 * Ako je resource="index.html?ime=Pera&prezime=Peric&operacija=dodaj
	 * Map<String, String> parametri = getParams(resource);
	 * String ime = parametri.get("ime");  // ovo ce biti "Pera"
	 * String prezime = parametri.get("prezime"); // ovo ce biti "Peric"
	 * 
	 * @param resource
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> getParams(String resource) throws UnsupportedEncodingException {
		  final Map<String, String> queryPairs = new LinkedHashMap<String,String>();
		  // nadji ?
		  int queryStartIndex = resource.indexOf("?");
		  // ako nema ? , vrati praznu mapu
		  if(queryStartIndex == -1) {
			  return queryPairs;
		  }
		  
		  // u suprotnom parsiraj resource nakon ?
		  // odseci nakon ? ceo stalo
		  String query = resource.substring(queryStartIndex + 1);
		  
		  // cepaj po &
		  final String[] pairs = query.split("&");
		  
		  // napravi parove gde ce kljuc biti naziv parama, a vrednost pod tim kljucem ce biti vrednost parametra iz URL-a
		  for (String pair : pairs) {
		    final int idx = pair.indexOf("=");
		    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
		    final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
		    queryPairs.put(key, value);
		  }
		  return queryPairs;
		}
	
	static void sendResponse(String resource, OutputStream os)
			throws IOException {
		PrintStream ps = new PrintStream(os);
		File file = null;
		// sadrzaj HTTP odgovora za klijenta
		String retVal = "";
		
		Map<String, String> parametri = getParams(resource);
		// DA LI VRACAMO FAJL ILI PARSIRAMO HTTP ZAHTEV SA FORME?
			if (resource.startsWith("index.html")) {
				retVal = "HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=UTF-8\r\n\r\n";
				retVal += "<html><head><link href='style.css' rel='stylesheet' type='text/css'></head>\n";
				retVal += "<body>";
				/////////////////////////
				// PRAZNA STRANICA+
				
				retVal += "<h1 style='color:red'>Prodaja vozila</h1>";
				
				retVal += ispisiFormuProkletu(null);
				
				retVal += "<h1 style='color:purple'>VOZILA</h1>";
				
				retVal += obradiOperaciju(resource, parametri);
				
				retVal += ispisiTabeluProkletu(cars);
				// koja je operacija itd?
				
				retVal += "</body>";
				retVal += "</html>";
				// print u output stream da bi se poslalo...
				ps.print(retVal);
				
				

			}
			else if(resource.startsWith("prodaj")) {
				retVal = "HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=UTF-8\r\n\r\n";
				retVal += "<html><head><link href='style.css' rel='stylesheet' type='text/css'></head>\n";
				retVal += "<body>";
				/////////////////////////
				// PRAZNA STRANICA
				
				retVal += "<h1 style='color:red'>Prodaja vozila</h1>";
				
				retVal += ispisiFormuProkletu(null);
				
				retVal += "<h1 style='color:purple'>VOZILA</h1>";
				
				String[] parts = resource.split("\\?");
				// fetch the other part
				// ime=x&prezime=&email=&grad=NS&kredit=&operacija=dodaj
				String params = parts[1];
				
				String[] part = params.split("=");
				String regN = part[1];
				System.out.println(regN);
				
				for (Car zaIzmenu : cars) {
					if (zaIzmenu.getRegNum().equals(regN)) {
						zaIzmenu.setSold(true);
					}
				}

				retVal += ispisiTabeluProkletu(cars);
				retVal += "</body>";
				retVal += "</html>";
				// print u output stream da bi se poslalo...
				ps.print(retVal);
			}
			else if(resource.startsWith("izmeni")) {
				retVal = "HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=UTF-8\r\n\r\n";
				retVal += "<html><head><link href='style.css' rel='stylesheet' type='text/css'></head>\n";
				retVal += "<body>";
				/////////////////////////
				// PRAZNA STRANICA
				
				retVal += "<h1 style='color:red'>Prodaja vozila</h1>";
				
				String[] parts = resource.split("\\?");
				// fetch the other part
				// ime=x&prezime=&email=&grad=NS&kredit=&operacija=dodaj
				String params = parts[1];
				
				String[] part = params.split("=");
				String regN = part[1];
				
				System.out.println(regN);
				
				for (Car zaIzmenu : cars) {
					if (zaIzmenu.getRegNum().equals(regN)) {
						retVal += ispisiFormuProkletu(zaIzmenu);
					}
				}
				
				retVal += "<h1 style='color:purple'>VOZILA</h1>";
				
				retVal += ispisiTabeluProkletu(cars);
				retVal += "</body>";
				retVal += "</html>";
				
				ps.print(retVal);
			}
			else if(resource.startsWith("obrisi")) {
				boolean brisi = false;
				int auto = 0;
				
				retVal = "HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=UTF-8\r\n\r\n";
				retVal += "<html><head><link href='style.css' rel='stylesheet' type='text/css'></head>\n";
				retVal += "<body>";
				/////////////////////////
				// PRAZNA STRANICA
				
				retVal += "<h1 style='color:red'>Prodaja vozila</h1>";
				
				retVal += ispisiFormuProkletu(null);
				
				String[] parts = resource.split("\\?");
				// fetch the other part
				// ime=x&prezime=&email=&grad=NS&kredit=&operacija=dodaj
				String params = parts[1];
				
				String[] part = params.split("=");
				String regN = part[1];
				
				System.out.println(regN);
				
				for (Car zaIzmenu : cars) {
					if (zaIzmenu.getRegNum().equals(regN)) {
						brisi = true;
						auto = cars.indexOf(zaIzmenu);
					}
				}
				
				if(brisi)
					cars.remove(auto);
				
				retVal += "<h1 style='color:purple'>VOZILA</h1>";
				
				retVal += ispisiTabeluProkletu(cars);
				retVal += "</body>";
				retVal += "</html>";
				
				ps.print(retVal);
			}
				
			
			else {
				// NIJE SE POKLOPILO NI SA JEDNIM URLom za prihvatanje podataka sa forme
				// zamenimo web separator sistemskim separatorom
				resource = resource.replace('/', File.separatorChar);
				file = new File(resource);
				
				// AKO NIJE ZAHTEV SA FORME I NIJE FAJL , ONDA VRACAMO 404, NEMA NICEG NA TOM URL
				// POYYY
				if(!file.exists()) {
					// ako datoteka ne postoji, vratimo kod za gresku
					ps.print("HTTP/1.0 404 File not found\r\n"
							+ "Content-type: text/html; charset=UTF-8\r\n\r\n<b>404 Нисам нашао:"
							+ file.getName() + "</b>");
					// ps.flush();
					System.out.println("Could not find resource: " + file);
					return;
				}
				
				// ispisemo zaglavlje HTTP odgovora
				ps.print("HTTP/1.0 200 OK\r\n\r\n");

				// a, zatim datoteku
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[8192];
				int len;

				while ((len = fis.read(data)) != -1) {
					ps.write(data, 0, len);
				}
				fis.close();
			} 
	

		

		ps.flush();
		
	}
	
	
	private static String obradiOperaciju(String resource, Map<String, String> parametri) {
		String retVal = "";


		if (resource.contains("operacija")) {
			// index.hmtl?ime=&prezime=&email=&grad=NS&kredit=&operacija=dodaj
			String[] parts = resource.split("\\?");
			// fetch the other part
			// ime=x&prezime=&email=&grad=NS&kredit=&operacija=dodaj
			String params = parts[1];
			String[] paramParts = params.split("&");
			// ["ime=pera", "prezime=peric" ]
			for (int i = 0; i < paramParts.length; i++) {
				// ime=Pera
				String part = paramParts[i];
				String[] keyValue = part.split("=");
				System.out.println("SPLIT:" + keyValue);
				// "ime" "Pera"
				parametri.put(keyValue[0], URLDecoder.decode(keyValue[1]));
			}

			System.out.println(parametri.toString());

			switch (parametri.get("operacija")) {
			case "DODAJ": {
				// NIJE LOSE STAVITI CASE unutar { } , da bi mogli da
				// koristimo
				// lokalne varijable sa istim imenima u svakom case-u, inace
				// bi imali problem

				Car c = new Car();
				c.setRegNum(parametri.get("brReg"));
				c.setYear(Integer.parseInt(parametri.get("godiste")));
				c.setBrand(parametri.get("marka"));
				c.setPrice(Integer.parseInt(parametri.get("cena")));
				c.setSold(false);
				
				cars.add(c);

				break;
			}
			case "SNIMI": { /*
							 * snimi salje podatke sa forme za izmenu
							 * postojeceg korisnika
							 */

				// nadjemo korisnika koga menjamo
				for (Car zaIzmenu : cars) {
					if (zaIzmenu.getRegNum().equals(parametri.get("brReg"))) {
						zaIzmenu.setBrand(parametri.get("marka"));
						zaIzmenu.setPrice(Integer.parseInt(parametri.get("cena")));
						zaIzmenu.setYear(Integer.parseInt(parametri.get("godiste")));
					}
				}

				break;
			}
			
			default: {
				// odustani bi ovde islo, ne radimo nista svakako
				break;
			}
			}
		}

		return retVal;

	}
	
	private static String ispisiFormuProkletu(Car popuniSaOvim) {

		String retVal = "";

		// AKO JE NULL, PRAZNO:
		if (popuniSaOvim == null) {

			retVal += "<form action='/index.html' >";
			retVal += "<table border='1'>";

			retVal += "<tr>";
			retVal += "<td>Broj registracije:</td>";
			retVal += "<td><input type='text' name='brReg' required></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Godiste:</td>";
			retVal += "<td><input type='number' name='godiste' required></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Marka:</td>";
			retVal += "<td><select  name='marka' required>";
			retVal += "<option value='BMW'>BMW</option>";
			retVal += "<option value='MERCEDES'>MERCEDES</option>";
			retVal += "<option value='YUGO'>YUGO</option>";
			retVal += "</select></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Cena:</td>";
			retVal += "<td><input type='number' name='cena' required></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td  colspan='2'     ><input type='submit' name='operacija' value='DODAJ'>"
					+ "<input type='submit' name='operacija' value='SNIMI'>"
					+ "<a href='/index.html'> ODUSTANI </a>"
					+ "</td>";
			retVal += "</tr>";
			retVal += "</table>";
			retVal += "</form>";
		} else {
			retVal += "<form action='/index.html' >";
			retVal += "<table border='1'>";

			retVal += "<tr>";
			retVal += "<td>Broj registracije:</td>";
			retVal += "<td><input type='text' name='brReg' required readonly value='"
					+ popuniSaOvim.getRegNum() + "'></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Godiste:</td>";
			retVal += "<td><input type='number' name='godiste' required value='"
					+ popuniSaOvim.getYear() + "'></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Marka:</td>";
			retVal += "<td><select  name='marka' required>";
			// AKO SE GRAD POKLAPA, TREBA MU DODATI selected
			// AKO SE NE POKLAPA, NISTA moze ovako, moze i preko switch case kao
			// u tabeli

			retVal += "<option value='BMW' "
					+ (popuniSaOvim.getBrand().equals("BMW") ? "selected" : "")
					+ ">BMW</option>";
			retVal += "<option value='MERCEDES' "
					+ (popuniSaOvim.getBrand().equals("MERCEDES") ? "selected" : "")
					+ ">MERCEDES</option>";
			retVal += "<option value='YUGO' "
					+ (popuniSaOvim.getBrand().equals("YUGO") ? "selected" : "")
					+ ">YUGO</option>";
			retVal += "</select></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Cena:</td>";
			retVal += "<td><input type='number' name='cena' required value='"
					+ popuniSaOvim.getPrice() + "'></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td  colspan='2'     ><input type='submit' name='operacija' value='DODAJ'>"
					+ "<input type='submit' name='operacija' value='SNIMI'>"
					+ "<a href='/index.html'> ODUSTANI </a>"
					+ "</td>";
			retVal += "</tr>";
			retVal += "</table>";
			retVal += "</form>";
		}

		return retVal;
	}
	
	private static String ispisiTabeluProkletu(List<Car> cars)
			throws UnsupportedEncodingException {

		String retVal = "";

		if (cars.isEmpty()) {
			retVal += "<h2>Nema vozila</h2>";
		} else {
			// IPAK IH IMA>...
			retVal += "<table border='1'>";
			
			retVal += "<tr style='font-weight: bold'>";
			retVal += "<td>Broj registracije vozila</td>";
			retVal += "<td>Godiste</td>";
			retVal += "<td>Marka</td>";
			retVal += "<td>Cena</td>";
			retVal += "<td>Prodaj</td>";
			retVal += "<td>Izmeni</td>";
			retVal += "<td>Brisi</td>";
			retVal += "</tr>";
			
			for (Car car : cars) {
				// ako treba filtirati neki if..
				int godina = 2017 - car.getYear();
				if(godina > 11) {
					retVal += "<tr style='background-color:red'>";
				}
				else if(godina > 5 && godina < 11) {
					retVal += "<tr style='background-color:orange'>";
				}
				else if(godina < 5) {
					retVal += "<tr style='background-color:green'>";
				}
				
				if(car.isSold()) {
					retVal += "<tr style='background-color:gray'>";
				}
				
				retVal += "<td>" + car.getRegNum() + "</td>";
				retVal += "<td>" + car.getYear() + "</td>";
				retVal += "<td>" + car.getBrand() + "</td>";
				retVal += "<td>" + car.getPrice() + "</td>";
				retVal += "<td>" + "<a href='prodaj?kogaProdati=" 
						+ URLEncoder.encode(car.getRegNum(), "UTF-8") 
						+ "'>Prodaj</a>";
				retVal += "</td>";
				retVal += "<td>" + "<a href='izmeni?kogaIzmeniti=" 
						+ URLEncoder.encode(car.getRegNum(), "UTF-8") 
						+ "'>Izmeni</a>";
				retVal += "</td>";
				retVal += "<td>" + "<a href='obrisi?kogaObrisati=" 
						+ URLEncoder.encode(car.getRegNum(), "UTF-8") 
						+ "'>Brisi</a>";
				retVal += "</td>";
			}
			
			retVal += "</tr>";
		}

		return retVal;
	}


}
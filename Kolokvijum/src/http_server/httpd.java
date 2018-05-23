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

	//private static List<Car> cars = new ArrayList<Car>();

	private static List<Proizvod> proizvodi = new ArrayList<Proizvod>();

	//int suma=0;
	
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
				
				retVal += "<h1 style='color:red'>Kupovina</h1>";
				
				retVal += ispisiFormuProkletu(null);
				
				retVal += "<h1 style='color:purple'>Spisak poridzbina</h1>";
				
				retVal += obradiOperaciju(resource, parametri);
				
				retVal += ispisiTabeluProkletu(proizvodi);
				// koja je operacija itd?
				
				retVal += "</body>";
				retVal += "</html>";
				// print u output stream da bi se poslalo...
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
			
				
				
				System.out.println("DODAO SAM");
				
				Proizvod p= new Proizvod();
				
				
				
				
				p.setId(Integer.parseInt(parametri.get("id")));
				p.setNaziv(parametri.get("naziv"));
				p.setCena(Integer.parseInt(parametri.get("cena")));
				p.setKolicina(Integer.parseInt(parametri.get("kolicina")));
				
				//setam se kroz listu proizvoda da vidim da li postoji vec proizvod sa unetim id-em
				
				
				//proizvodi.add(p);
				
				
				int flag=0;
				
				
				
				if(proizvodi.isEmpty()) {
					proizvodi.add(p);
				}else {
				
				
				for(Proizvod pro:proizvodi) {
					//System.out.println(pro.getId());
					//System.out.println(p.getId());
					
					if(pro.getId()!=p.getId()) {
						flag=0;
						
					}else {
						flag=1;
					
					}
					
				}
				
					if(flag==0) {
						proizvodi.add(p);
					}else {
						System.out.println("Vec postoji taj id!!!");
					}
					System.out.println("Flag:");
					
					System.out.println(flag);
					
				}
				
				
				break;
			}case "UPLATI":{
				System.out.println("Uplata");
				int uplata=Integer.parseInt(parametri.get("uplata"));
				
				
			}
			
			
			
			
			
			default: {
				// odustani bi ovde islo, ne radimo nista svakako
				break;
			}
			}
		}

		return retVal;

	}
	
	private static String ispisiFormuProkletu(Proizvod popuniSaOvim) {

		String retVal = "";

		// AKO JE NULL, PRAZNO:
		if (popuniSaOvim == null) {

			
			
			
			retVal += "<form action='/index.html' >";
			retVal += "<table>";

			retVal += "<tr>";
			retVal += "<td>Id:</td>";
			retVal += "<td><input type='number' name='id' required></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Naziv proizvoda:</td>";
			retVal += "<td><input type='text' name='naziv' required></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Kolicina:</td>";
			retVal += "<td><input type='number' name='kolicina' required></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td>Cena po komadu:</td>";
			retVal += "<td><input type='number' name='cena' required></td>";
			retVal += "</tr>";

			retVal += "<tr>";
			retVal += "<td  ><input type='submit' name='operacija' value='DODAJ'></td>";
			retVal += "</tr>";
			retVal += "</table>";
			retVal += "</form>";
			
			
			
			
		}

		return retVal;
	}
	
	private static String ispisiTabeluProkletu(List<Proizvod> proizvodi)
			throws UnsupportedEncodingException {

		String retVal = "";
		
		
		int suma=0;

		if (proizvodi.isEmpty()) {
			retVal += "<h2>Nema porudzbina</h2>";
		} else {
			// IPAK IH IMA>...
			retVal += "<table border='1'>";
			
			retVal += "<tr style='font-weight: bold'>";
			
			retVal += "<td colspan='3' align='center'>Spisak</td>";
			

			retVal += "</tr>";
			
			retVal += "<tr>";
			
			retVal += "<td>Id</td>";
			retVal += "<td>Naziv proizvoda</td>";
			retVal += "<td>Ukupna cena proizvoda(din)</td>";
			retVal += "</tr>";
			
			
			
					
			for(Proizvod pro:proizvodi) {
			
				
				
				System.out.println(pro.getKolicina());
				
				suma+=pro.getKolicina() * pro.getCena();
				
				//suma+=ukupno;
				
				retVal+="<tr>";
				retVal+="<td>" + pro.getId()+"</td>";
				retVal+="<td>" + pro.getNaziv()+"</td>";
				retVal+="<td align='center'>" +pro.getKolicina() * pro.getCena()+"</td>";
				retVal+="</tr>";
				
				
			}
			
			
			retVal+="<tr><td colspan='2'>Ukupna cena:</td><td align='center'>" + suma + "</td>"; 
			
			
			retVal += "</tr>";
			
			retVal+="</table>";
			
			retVal+="<form><p>Plati racun: <input type='number' name='uplata' required>"
					+ " <input type='submit' name='operacija' value='UPLATI'> </p></form>";
			retVal+="<a href='/index.html'>Unesi novi proizvod</a>";
			
			
		}

		return retVal;
	}


}
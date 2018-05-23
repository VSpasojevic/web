<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
	import="com.rt.rk.ftn.tims.model.Korpa"

    %>
<%@ page import="com.rt.rk.ftn.tims.model.Proizvod" %>
<%@ page import="com.rt.rk.ftn.tims.model.ProizvodUKorpi" %>
<%@ page import="java.util.List" %>

<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

               
               
   
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<form action="dodaj-u-korpu" 
method="POST">
<input type="text" name="id" required>
<input type="number" name="kolicina" required>
<input type="submit" value="dodaj u korpu">
</form>

<% Korpa korpa = (Korpa)session.getAttribute("korpa"); %>
<table border="1">
<tr>
<th>Naziv</th>
<th>Cena</th>
<th>Kolicina</th>
</tr>

<%  for(ProizvodUKorpi proizvodK : korpa.getProizvodi()) { %>
<tr>
<td> <%=proizvodK.getProizvod().getNaziv() %> </td>
<td> <%=proizvodK.getProizvod().getCena() %> </td>
<td> <%=proizvodK.getKolicina() %> </td>
</tr>

<% } %>
</table>

<c:forEach items="${korpa.proizvodi}" var="proizvodK">
   <tr>
<td> ${proizvodK.proizvod.naziv} </td>

</tr>
 
</c:forEach>

</body>
</html>
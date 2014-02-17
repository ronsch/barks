<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="uk.ac.dundee.computing.aec.stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head id="Header">
    <link rel="stylesheet" type="text/css" href="css/styleSheet.css"/>
    <meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
	<meta name="author" content="Ron S" />
	<meta name="description" content="Barks Header" />
	<meta name="keywords " content="Barks" />
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
	
	<title>Barks</title>
</head>

<body background="BG2.jpg">

	<div id="header">
		<h1>Such Details:</h1>
	</div>
	
	<div id=navigation>
		<a href="/testes/login.jsp">Back</a>
	</div>

 	<div id="mainContent">
		<% /* Display welcome message to user */
		String message = (String)request.getAttribute("warningMessage");
		if(message != null)
		if(message.equals("null"))
		{
			//do nothing
		}else{ //do only if there is a content
			%>
			<FONT COLOR="FF0000"><%=request.getAttribute("warningMessage")%></FONT>
			<%
		}
		%>
		
		<form action="members" method="post" name="logMeIn">
	  	Your Email Address: <input type="text" name="NewEmailAddress"><br>
	  	Much username: <input type="text" name="userName"><br>
	  	A Password: <input type="password" name="NewPassword"><br>
	  	so Duplicate: <input type="password" name="RepeatPassword"><br>
	  	<input type="submit" value="to the mooooon">
		</form>
	</div>
	
	<div id="footer">
	    <p>Barks - The Twitter of Doge</p>
	</div>
</body>
</html>
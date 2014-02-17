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

<%
	//if the username is null, meaning there is no user logged in - redirect user to login screen
	if(request.getAttribute("username").equals("null"))
	{
		RequestDispatcher rd = request.getRequestDispatcher("/login.jsp"); 
		rd.forward(request, response);
	}
%>

<body background="BG2.jpg">

	<div id="header">
		<h1>Change Password</h1>
	</div>
	
	<div id=navigation>
		<form name="goToMembers" action="members"  method="POST">
			<input type="submit" value="Back to members page" name="goToMembers"/>
		</form>
		
		<form name="logOff" action="members"  method="POST">
			<input type="submit" value="log off" name="logOff"/>
		</form>
	</div>

	<div id="mainContent">
		<h1>Write new Bark</h1>
		<%
		String username = (String)request.getAttribute("username");
		if(username != null)//check if null
		{
			%> 
			<form action="members" method="post">
			  	Your name: <p><%=username%></p><br>
			  	<input type="hidden" name="name" value="<%=username%>" >
			  	
			  	Bark: <input type="text" name="bark"><br>
			  	<input type="submit" value="Bark Bark!" name="insertNewBark">
			</form>
			<%
		}else
		{
			%>
			<p>It seems like there was a problem with your session, please log back in</p>
			<%
		}
		%>
	</div>
	
	<div id="footer">
	    <p>Barks - The Twitter of Doge</p>
	</div>
</body>
</html>
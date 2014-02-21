<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="com.abc.stores.*" %>
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
		<h1>Welcome to Barks</h1>
	</div>
	
	<div id=navigation>
		<a href="register.jsp">Create new Account</a>
	</div>
	
	<div id="mainContent">
		<h1>Login to your dog crib:</h1>
		<% /* Display error message to user - if applicable */
		String message = (String)request.getAttribute("warningMessage");
		if(message != null)
		if(message.equals("null"))
		{
			//do nothing
		}else{ //do only if there is a content
			%>
			<FONT COLOR="FF0000"><%=message%></FONT>
			<%
		}
		%>
		
		<form action="members" method="post" name="logMeIn">
			<table>
				<tr>
					<td>
						Email:
					</td>
					<td>
						<input type="text" name="emailAddress">
					</td>
				</tr>
				<tr>
					<td>
						Passbark:
					</td>
					<td>
						<input type="password" name="password">
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<input type="submit" value="grrrrrr" name="logMeIn">
					</td>
				</tr>
			</table>
		</form>
	</div>

	<div id="footer">
	    <p>Barks - The Twitter of Doge</p>
	</div>
</body>
</html>
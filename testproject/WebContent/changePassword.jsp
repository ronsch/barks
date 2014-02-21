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

<%
	//if the username is null, meaning there is no user logged in - redirect user to login screen
	HttpSession Session = request.getSession();
	LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
	
	String userName = thisUser.getUsername();
	System.out.println("userName: " + userName);
	if(userName == null)
	{
		System.out.println("userName is null!");
		RequestDispatcher rd = request.getRequestDispatcher("/login.jsp"); 
		rd.forward(request, response);
	}else{
		System.out.println("userName: " + userName);
	}
%>

<body background="BG2.jpg">

	<div id="header">
		<h1>Change Password</h1>
	</div>
	
	<div id=navigation>
		<table id="headerTable" >
			<tr>
				<td style="text-align:left;">
					<form name="goToMembers" action="members"  method="get">
						<input type="submit" value="Return to Home" name="goToMembers"/>
					</form>
				</td>
				<td style="text-align:right;">
					<form name="logOff" action="members"  method="POST">
						<input type="submit" value="log off" name="logOff"/>
					</form>
				</td>
			</tr>
		</table>
	</div>

	<div id="mainContent">
		<p>for user: <%=userName%></p>
	
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
		
		<form action="members" method="post" name="changeUserPassword">
			<table>
				<tr>
					<td>
						Old Password: 
					</td>
					<td>
					  	<input type="password" name="oldPassword">
					</td>
				</tr>
				<tr>
					<td>
						new Password:
					</td>
					<td>
						<input type="password" name="NewUserPassword">
					</td>
				</tr>
				<tr>
					<td>
						much Duplicate: 
					</td>
					<td>
						<input type="password" name="RepeatUserPassword">
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<input type="submit" value="Change Password" name="changeUserPassword">
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
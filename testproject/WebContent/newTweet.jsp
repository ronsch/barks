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
		<h1>Bark!</h1>
	</div>
	
	<div id=navigation>
		<table id="headerTable" >
			<tr>
				<td style="text-align:left;">
					<form name="goToMembers" action="members"  method="get">
						<input type="submit" value="Back to members page" name="goToMembers"/>
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
		<h1>!! Bark:</h1>
		<%
		if(userName != null)//check if null
		{
			%> 
			<form action="members" method="post">
			  	
			  	<input type="hidden" name="name" value="<%=userName%>" >
			  	<input type="text" size="35" name="bark">
			  	<br>
			  	<br>
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
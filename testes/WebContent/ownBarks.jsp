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
		<h1>Your barks</h1>
	</div>
	<div id=navigation>
		<table id="headerTable" >
			<tr>
				<td>
					<form name="barkYourself" action="members"  method="POST">
						<input type="submit" value="Bark Yourself" name="barkYourself"/>
					</form>
				</td>
				<td>
					<form name="changePassword" action="members"  method="POST">
						<input type="submit" value="Change Password" name="changePassword"/>
					</form>
				</td>
				<td>
					<form name="goToMembers" action="members"  method="POST">
						<input type="submit" value="Return to Home" name="goToMembers"/>
					</form>
				</td>
				<td>
					<form name="logOff" action="members"  method="POST">
						<input type="submit" value="log off" name="logOff"/>
					</form>
				</td>
			</tr>
		</table>
	</div>
		
	<div id="mainContent">
		<%
		int i = 0;
		List<TweetStore> ownBarkList = (List<TweetStore>)request.getAttribute("ownBarks");
		if (ownBarkList==null) //if the tweet list is empty
		{
			%><p>No Barks found</p> <% 
		}
		else
		{
			Iterator<TweetStore> iterator;
			iterator = ownBarkList.iterator();     
			while (iterator.hasNext())
			{
				TweetStore ts = (TweetStore)iterator.next();
	
				%>
				<div id=oneBark>
					<p><u><b> <%=ts.getUser()%> </b></u>
					 	<%=ts.getTweet()%></p>
					<p> <%=ts.getDate()%>
					
					<!-- Show delete button next to bark -->
					<form name="deleteBark" action="members"  method="POST">
					    <input type="hidden" name="deleteBark" value="<%=i%>" >
					    <input type="hidden" name="Delete" value="Delete" >
						<input type="submit" value="Delete Bark" name="deleteBark"  />
					</form>
				</div>
				<% 
				i++; //increase counter which is added to deletebutton name
			}
		}%>
		<br>
	</div>
	
	<div id="footer">
	    <p>Barks - The Twitter of Doge</p>
	</div>
</body>
</html>
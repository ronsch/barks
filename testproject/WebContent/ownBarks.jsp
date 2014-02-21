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
		<h1>Your barks</h1>
	</div>
	<div id=navigation>
		<table id="headerTable" >
			<tr>
				<td>
					<form name="barkYourself" action="members"  method="get">
						<input type="submit" value="Bark Yourself" name="barkYourself"/>
					</form>
				</td>
				<td style="text-align:center;">
					<form name="changePassword" action="members"  method="get">
						<input type="submit" value="Change Password" name="changePassword"/>
					</form>
				</td>
				<td style="text-align:center;">
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
		<%
		int i = 0;
		List<TweetStore> ownBarkList = (List<TweetStore>) Session.getAttribute("ownBarks");//(List<TweetStore>)request.getAttribute("ownBarks");
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
					<table width=100%>
						<tr>
							<td>
								<u><b><%=ts.getUser()%></b></u>
							</td>
							<td rowspan="2" width="max">
								<%=ts.getTweet()%>
							</td>
						</tr>
						<tr>
							<td>
								<%=ts.getDate()%>
							</td>
							<td>
								<!-- empty cell -->
							</td>
							<td>
								<!-- Show delete button next to bark -->
								<form name="deleteBark" action="members"  method="post" align="right">
								    <input type="hidden" name="deleteBark" value="<%=i%>" >
								    <input type="hidden" name="Delete" value="Delete" >
									<input type="submit" value="Delete Bark" name="deleteBark"  />
								</form>
							</td>
						</tr>
					</table>
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
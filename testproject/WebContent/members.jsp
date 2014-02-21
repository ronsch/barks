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
	//System.out.println("userName: " + userName);
	if(userName == null)
	{
		System.out.println("userName is null!");
		RequestDispatcher rd = request.getRequestDispatcher("/login.jsp"); 
		rd.forward(request, response);
	}else{
		System.out.println("logged in user: " + userName);
	}
%>

<body background="BG2.jpg">

	<div id="header">
		<h1>Much Home Screen</h1>
	</div>
	
	<div id=navigation>
		<table id="headerTable" >
			<tr>
				<td style="text-align:left;">
					<form name="barkYourself" action="members"  method="get">
						<input type="hidden" name="userName" value="<%=userName%>" >
						<input type="submit" value="Bark Yourself" name="barkYourself"/>
					</form>
				</td>
				<td style="text-align:center;">
					<form name="changePassword" action="members"  method="get">
						<input type="submit" value="Change Password" name="changePassword"/>
					</form>
				</td>
				<td style="text-align:center;">
					<form name="viewOwnBarks" action="members"  method="get">
						<input type="submit" value="View Own Barks" name="viewOwnBarks"/>
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
		<H1>Welcome home <%=userName%></H1>
							<!-- Display followed users' barks-->
							<p>Barks from people you are following:</p>
	 	<%
		int i = 0;
	 
	 	try{
	 		if(Session.getAttribute("followedBarks") != null)
	 		{
	 			//Object O = request.getAttribute("followedBarks");
		 		List<TweetStore> followedBarks = (List<TweetStore>)Session.getAttribute("followedBarks"); //get list of barks from followed users
		 		if (followedBarks==null) //if the tweet list is empty
				{
					%><p>No Barks found. Are you following anyone at all?</p><% 
				}
				else
				{
					for(TweetStore ts : followedBarks)
					{
						%>
						<div id=oneBark>
							<table width=100%>
								<tr>
									<td>
										<u><b><%=ts.getUser()%></b></u>
									</td>
									<td rowspan="2">
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
									<td style="text-align:left;">
										<!-- Show unfollow button next to bark -->
										<form name="unfollowUser" action="members"  method="POST" align="right">
										    <input type="hidden" name="unfollowThisUser" value="<%=i%>" >
										    <input type="hidden" name="unfollow" value="unfollow" >
											<input type="submit" value="Unfollow User" name="unfollowUser"/>
										</form>	
									</td>
								</tr>
							</table>
						</div>
						<% 
						i++; //increase counter which is added to deletebutton name
					}
				}
	 		}
	 	}catch(Exception e)
	 	{
	 		System.out.println("an error occured trying to print out the followed barks: " + e);
	 	}
		
		/* Follow-members function */
		
		%>
		<br>
		<p>Search for Celebrities, friends or family to follow:</p>
		<form action="members" method="post" name="searchUsernameUser">
			username: <input type="text" name="searchUsernameUser">
			<input type="submit" value="Search">
		</form>
		<br>
		
		<%
		int j = 0;
		try{
			List<usersStore> userList = (List<usersStore>)Session.getAttribute("memberList"); //get list of users that match search
			if (userList==null) //if the user list is empty
			{
				%><p>No Users found</p> <% 
			}
			else
			{
				Iterator<usersStore> iterator;
				iterator = userList.iterator();
				
				while (iterator.hasNext())
				{
					usersStore uSt = (usersStore)iterator.next();
						%>
						<div id=oneUser>
					
							 <p><b> <%=uSt.getUsername()%> </b></p> <!-- normal users only get to see the username -->
							<% 
							if(userName.equals("admin"))
							{ //if the current user is the admin, show delete buttons next to user name
								%>
								 <p>Email Address: <u><%=uSt.getEmail()%> </u></p> 
								<% 
							}
							%>
							<!-- Show Follow button next to username -->
							<form name="followUser" action="members"  method="POST">
							    <input type="hidden" name="followUser" value="<%=j%>" >
							    <input type="hidden" name="Follow" value="Follow" >
								<input type="submit" value="Follow" name="followUser"  />
							</form>
						
							<% 
							if(userName.equals("admin") && !uSt.getUsername().equals("admin"))	//only if admin is logged in, but dont display the admin-delete eoption
							{ //if the current user is the admin, show delete buttons next to user name
							%>
								<!-- Show delete button next to username -->
								<form name="deleteUser" action="members"  method="POST">
								    <input type="hidden" name="deleteUser" value="<%=j%>" >
								    <input type="hidden" name="Delete" value="Delete" >
									<input type="submit" value="Delete User" name="deleteUser"  />
								</form>
								<%
							}
							%>
						</div>
						<%					 
						j++; //increase counter which is added to deletebutton value
					
				}
			}
		}catch(Exception e)
		{
	 		System.out.println("an error occured trying to print out the user list: " + e);
		}
		%>
	</div>
	
	<div id="footer">
	    <p>Barks - The Twitter of Doge</p>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="uk.ac.dundee.computing.aec.stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Barks</title>
</head>
<body>

<h1>All Barks</h1>
<%
	//System.out.println("Printing Barks");
	int i = 0;
	List<TweetStore> lTweet = (List<TweetStore>)request.getAttribute("barksList");
	if (lTweet==null) //if the tweet list is empty
	{
		%><p>No Barks found</p> <% 
	}
	else
	{
		Iterator<TweetStore> iterator;
		iterator = lTweet.iterator();     
		while (iterator.hasNext())
		{
			TweetStore ts = (TweetStore)iterator.next();

			%>
			<p><u><b> <%=ts.getUser()%> </b></u>
			 	<%=ts.getTweet()%></p>
			<p> <%=ts.getDate()%>
			
			<!-- Show delete button next to bark -->
			<form name="deleteBark" action="Tweet"  method="POST">
			    <input type="hidden" name="deleteBark" value="<%=i%>" >
			    <input type="hidden" name="Delete" value="Delete" >
				<input type="submit" value="Delete Bark" name="deleteBark"  />
			</form>
			
			<% 
			i++; //increase counter which is added to deletebutton name
		}
	}%>

	<!-- <form action="Tweet" method="POST" >
 		Number of barks shown per page: <input type="text" name="numberOfPosts" value="10" size=1 maxlength=3>
  		<input type="submit" value="Show" name="changeView"> 
  		<br>
		<input type="button" value="Previous Page" name="prevPage">
		<input type="button" value="Next Page" name="nextPage">
	</form> -->
	
	<a href="/testes/newTweet.jsp">Bark yourself!</a>
	<a href="/testes/login.jsp">login as user</a>
</body>
</html>
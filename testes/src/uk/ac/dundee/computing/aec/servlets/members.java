package uk.ac.dundee.computing.aec.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import uk.ac.dundee.computing.aec.lib.*;
import uk.ac.dundee.computing.aec.models.*;
import uk.ac.dundee.computing.aec.stores.TweetStore;
import uk.ac.dundee.computing.aec.stores.usersStore;

/**
 * Servlet implementation class login
 * handles all IO for login screen
 */
@WebServlet({ "/members" })
public class members extends HttpServlet 
{
	
	LinkedList<usersStore> userList = new LinkedList<usersStore>();
	LinkedList<TweetStore> barks = new LinkedList<TweetStore>();
	LinkedList<TweetStore> ownBarks = new LinkedList<TweetStore>(); //if user sees own barks
	String currentUserName;
	String currentUserEmailAddress;
	boolean userloggedIn;
	private static final long serialVersionUID = 1L;
    private Cluster cluster;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public members() 
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init(ServletConfig config) throws ServletException 
    {
		cluster = CassandraHosts.getCluster();
	}
    
    private void openMemberHome(HttpServletRequest request,	HttpServletResponse response, String userName) throws ServletException, IOException
    {
    	memberModel mM = new memberModel();
		mM.setCluster(cluster);

		currentUserName = userName; //get username
		userloggedIn = true;
		currentUserEmailAddress = request.getParameter("emailAddress"); //save current users email locally
		
		userList = new LinkedList<usersStore>();//empty userlist

		//check if there was a user search involved
		if(request.getParameter("searchUsernameUser") != null)
		{
			String searchterm = request.getParameter("searchUsernameUser").toLowerCase(); //get requested username, but lower case
			if(!searchterm.equals(""))
			{ //there is a value
				userList = mM.getUsers(searchterm, currentUserName);
			}else{ //valueless search
				userList = mM.getUsers("|", currentUserName);
			}
		}

		barks = new LinkedList<TweetStore>(); //final list (empty it)
		LinkedList<TweetStore> tempStore = new LinkedList<TweetStore>(); //temp list to be added to final list
		LinkedList<String> followers = new LinkedList<String>(); //will hold the names of users this user follows
		followers = mM.getFollowers(userName); //pass username to find followed users
		
		//print barks from followed members
		for(String followed : followers)
		{
			//System.out.println("followed user: " + followed);
			tempStore = mM.getBarks(followed);
			for(TweetStore ts : tempStore)
			{
				barks.add(ts); //add temp content to final list
			}
		}
		Collections.sort(barks, Collections.reverseOrder());//sort barks list by date
		
		request.setAttribute("followedBarks", barks); //Set a bean with the list in it
		request.setAttribute("username", currentUserName); //Set a bean with the username in it
		request.setAttribute("memberList", userList); //Set a bean with the list in it
		request.setAttribute("emailAddress", currentUserEmailAddress); //Set a bean with the email address in it		
		
    	RequestDispatcher rd = request.getRequestDispatcher("/members.jsp"); 
		rd.forward(request, response);
	}
    
    /*
     * This page shows the last barks, will be obsolete soon as its not really feasible or useful
     */
    private void openTweetPage(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException 
    {
		memberModel mM = new memberModel();
		mM.setCluster(cluster);
		barks = new LinkedList<TweetStore>();//empty tweetlist
		barks = mM.getBarks();
		request.setAttribute("barksList", barks); //Set a bean with the list in it
		RequestDispatcher rd = request.getRequestDispatcher("/RenderTweets.jsp"); 
		rd.forward(request, response);
	}
    
    /*
     * Open new login screen / dont pass anything, is going to be empty anyways
     */
    private void openLoginScreen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	RequestDispatcher rd = request.getRequestDispatcher("/login.jsp"); 
    	logout(); //just in case this hasnt happened already
    	//reset these beans with the new empty data
    	request.setAttribute("emailAddress", currentUserEmailAddress); 
    	request.setAttribute("username", currentUserName);
		rd.forward(request, response);
	}
    
    /*
     * Log off user - reset some values
     */
    private void logout()
    {
    	System.out.println("logging off");
    	currentUserEmailAddress = null;
    	currentUserName = null;
    	userloggedIn = false;
    }
    
    /*
     * Open register-new-member screen
     */
    private void openRegisterScreen(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException
    {
    	RequestDispatcher rd = request.getRequestDispatcher("/register.jsp"); 
    	logout();//just in case
    	request.setAttribute("emailAddress", currentUserEmailAddress); 
    	request.setAttribute("username", currentUserName);
		rd.forward(request, response);
	}
    
    /*
     * Change password screen
     */
    private void openChangePasswordScreen(HttpServletRequest request, HttpServletResponse response, String warningMessage2) throws ServletException, IOException 
    {
    	RequestDispatcher rd = request.getRequestDispatcher("/changePassword.jsp"); 
    	request.setAttribute("warningMessage", warningMessage2); //set warning message
    	request.setAttribute("username", currentUserName); //Set a bean with the username in it
    	rd.forward(request, response);
	}
    
    /*
     * Open bark-yourself screen
     */
    private void openBarkYourself(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
    {
    	RequestDispatcher rd = request.getRequestDispatcher("/newTweet.jsp");
    	request.setAttribute("username", currentUserName); //Set a bean with the username in it
		rd.forward(request, response);
	}
    
    /*
     * Open screen which shows own barks
     */
    private void openOwnBarkScreen(HttpServletRequest request,	HttpServletResponse response)  throws ServletException, IOException
    {
		//get list of own barks
    	memberModel mM = new memberModel();
    	//TweetModel tM = new TweetModel();
		mM.setCluster(cluster);
		//tM.setCluster(cluster);
		ownBarks = new LinkedList<TweetStore>(); //empty list to avoid duplications
		ownBarks = mM.getOwnBarks(currentUserName);
		
    	RequestDispatcher rd = request.getRequestDispatcher("/ownBarks.jsp");
    	request.setAttribute("username", currentUserName); //Set a bean with the username in it
    	request.setAttribute("ownBarks", ownBarks); //Set a bean with the username in it

    	rd.forward(request, response);
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * gets the tweet from the jsp page, from the user and sends it on to the tweetmodel where it is stored in the db
	 * gets the login data from the login screen and directs it to the login procedure
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		memberModel mM = new memberModel();
		mM.setCluster(cluster);
		request.setAttribute("userloggedIn", userloggedIn); //set bean whether user is logged in or not

		if(request.getParameter("logMeIn") != null) 
		{//if the request is to verify login // login a user
			String isLogin = mM.login(request.getParameter("emailAddress"), request.getParameter("password"));
			if(isLogin != null)
			{
				openMemberHome(request, response, isLogin);//open members page
			}else{
				request.setAttribute("warningMessage", "username or password are incorrect"); 
				openLoginScreen(request, response);//back to login screen
			}
		}
		else if (request.getParameter("NewEmailAddress") != null && request.getParameter("userName") != null && request.getParameter("NewPassword") != null && request.getParameter("RepeatPassword") != null)
		{//if a new user is to be created
			String warningMessage = ""; //the message the user gets displayed
			boolean success = false;
			if(request.getParameter("NewPassword").equals(request.getParameter("RepeatPassword")))
			{			//add user data to db
				success = mM.newUser(request.getParameter("NewEmailAddress"), request.getParameter("userName"), request.getParameter("RepeatPassword"));
			}else{
				warningMessage = "The passwords did not match";
			}
			
			if(success) //if the user was added successfully
			{
				warningMessage = "Your account has been created!";
				request.setAttribute("warningMessage", warningMessage); //set bean whether user is logged in or not

				openMemberHome(request, response, request.getParameter("userName"));//open members page
			}else{
				if(warningMessage.equals(""))
				{//some other problem occured
					warningMessage = "Maybe your email/username combination exists already?";
				}else{
					//the message is already statingthat passwords are not alike, leave it that way
				}
				request.setAttribute("warningMessage", warningMessage); 
				openRegisterScreen(request, response);//open register page again
			}
		}
		else if(request.getParameter("insertNewBark") != null && request.getParameter("name") != null && request.getParameter("bark") != null)
		{
			System.out.println("Add tweet");
			mM.addBark(request.getParameter("name"), request.getParameter("bark"));
			
			request.setAttribute("followedBarks", barks); //Set a bean with the list in it
			request.setAttribute("username", currentUserName); //Set a bean with the username in it
			request.setAttribute("memberList", userList); //Set a bean with the list in it
			request.setAttribute("emailAddress", currentUserEmailAddress); //Set a bean with the email address in it		
			
			openOwnBarkScreen(request, response);
		}
		else if(request.getParameter("searchUsernameUser") != null) //if the request is to search for a user, by a user
		{
    		openMemberHome(request, response, currentUserName);
		}
		else if(request.getParameter("deleteUser") != null) //if delete user was requested
		{
			//deleteUser
			System.out.println("Delete user: " + request.getParameter("deleteUser"));
			
			String userToBeDeleted = request.getParameter("deleteUser");
			int foo = Integer.parseInt(userToBeDeleted); //find which bark to delete
			mM.deleteUser(userList.get(foo).getEmail(), userList.get(foo).getUsername()); //delete user in list at foo
			openMemberHome(request, response, currentUserName);
		}
		else if(request.getParameter("unfollowUser") != null) //if a user needs to be unfollowed
		{//unfollowUser
			String userToBeUnfollowed = request.getParameter("unfollowThisUser");
			System.out.println("Your name: " + currentUserName);
			System.out.println("unfollow this user: " + userToBeUnfollowed);
			int foo = Integer.parseInt(userToBeUnfollowed); //find which bark to delete
			mM.unfollowUser(currentUserName, barks.get(foo).getUser()); //delete user
    		openMemberHome(request, response, currentUserName);
		}
		else if(request.getParameter("followUser") != null) 
		{//if a user wants to follow another user
			if(userList != null)
			{
				String userToBefollowed = request.getParameter("followUser");
				int foo = Integer.parseInt(userToBefollowed); //find which bark to delete
				mM.followUser(currentUserName, userList.get(foo).getUsername()); //delete user
	    		openMemberHome(request, response, currentUserName);
			}else{
	    		openMemberHome(request, response, currentUserName);
			}
		}
		else if(request.getParameter("barkYourself") != null)
		{//user wants to bark
    		openBarkYourself(request, response);
		}
		else if(request.getParameter("goToMembers") != null)
		{//directly open members page
    		openMemberHome(request, response, currentUserName);
		}
		else if(request.getParameter("logOff") != null)
		{//log user off
			openLoginScreen(request, response);
		}//"viewOwnBarks"
		else if(request.getParameter("viewOwnBarks") != null)
		{//user wants to see own barks
			openOwnBarkScreen(request, response);
		}else if(request.getParameter("deleteBark") != null) //if the request is to delete a own bark
		{
			System.out.println("Delete Bark");
			String barkToBeDeleted = request.getParameter("deleteBark");
			int foo = Integer.parseInt(barkToBeDeleted); //find which bark to delete
			mM.deleteBark(ownBarks.get(foo).getUUID().toString(), ownBarks.get(foo).getUser().toString()); //delete bark
			openOwnBarkScreen(request, response);
		}else if(request.getParameter("changePassword") != null)
		{//open changePassword window
			openChangePasswordScreen(request, response, "   ");
		}else if(request.getParameter("changeUserPassword") != null)
		{//change the password
			boolean success = false;
			String warningMessage = "";
			if(request.getParameter("NewUserPassword").equals(request.getParameter("RepeatUserPassword")))
			{			//change password in db
				//will return false if old password did not match
				success = mM.changePassword(request.getParameter("oldPassword"), request.getParameter("NewUserPassword"), currentUserName, currentUserEmailAddress);
			}else{
				warningMessage = "The new passwords did not match";
			}
			
			if(success) //if the password was changed successfully
			{
				warningMessage = "Your Password has been changed";
			}else{
				if(warningMessage.equals(""))
				{//some other problem occured - probably incorrect old password
					warningMessage = "Was the old password correct?";
				}
			}
			//finally open change-password screen so user can see the success or failure message
			openChangePasswordScreen(request, response, warningMessage);
		}//changePassword
	}
}

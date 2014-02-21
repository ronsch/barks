package com.abc.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.datastax.driver.core.Cluster;
import com.abc.lib.*;
import com.abc.models.*;
import com.abc.stores.LoginBean;
import com.abc.stores.TweetStore;
import com.abc.stores.usersStore;

/**
 * Servlet implementation class login
 * handles all IO for login screen
 */
@WebServlet(urlPatterns={"/members" })
public class members extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public members() 
    {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException 
    {
		cluster = CassandraHosts.getCluster();
	}
    
    private void openMemberHome(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException
    {
    	memberModel mM = new memberModel();
		mM.setCluster(cluster);
		
		HttpSession Session = request.getSession();
		LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
		
		LinkedList<usersStore> userList = new LinkedList<usersStore>();//new empty userlist

		if(thisUser != null) //check if user is logged in
		{
			//the following gets a list of users that match the search term, if any
						//check if there was a user search involved
						if(request.getParameter("searchUsernameUser") != null || Session.getAttribute("searchedUser") != null)
						{
							Session.setAttribute("searchedUser", request.getParameter("searchUsernameUser")); //save searched user in session
							
							String searchterm = "";
							try{
								searchterm = (String)Session.getAttribute("searchedUser");
							}catch(Exception e)
							{
								System.out.println("failed to parse username searchterm");
							}
							
							try{
								searchterm = searchterm.toLowerCase(); //get requested username, but lower case
							}catch(Exception e)
							{
								System.out.println("cannot lowercase");
							}
							
							if(searchterm == null)//on repeated searched, it's "Null" frequently
							{
								searchterm = "";
							}else{
								System.out.println("searchterm not null");
							}
			
							userList = mM.getUsers(searchterm, thisUser.getUsername());
							//System.out.println("userlist is populated");
							Session.setAttribute("memberList", userList); //send list of users that search returned
							
						}else{
							System.out.println("no user search, continue without: " + request.getParameter("searchUsernameUser"));
						}
				
	
			LinkedList<TweetStore> barks = new LinkedList<TweetStore>(); //new final list
			LinkedList<TweetStore> tempStore = new LinkedList<TweetStore>(); //temp list to be added to final list
			
			LinkedList<String> followers = new LinkedList<String>(); //will hold the names of users this user follows
			//System.out.println("username = " + thisUser.getUsername());
			followers = mM.getFollowers(thisUser.getUsername()); //get list of followed users names
			
			//the following gets the barks form followed users
						//print barks from followed members
						for(String followed : followers)
						{
							//System.out.println("getting barks for user: " + followed);
							tempStore = mM.getBarks(followed);
							for(TweetStore ts : tempStore)
							{
								barks.add(ts); //add temp content to final list
							}
						}
						Collections.sort(barks, Collections.reverseOrder());//sort barks list by date
						
						//System.out.println("first element in results List: " + barks.get(1).getTweet());
						Session.setAttribute("followedBarks", barks); //send list of barks from follower users
						
					
	    	RequestDispatcher rd = request.getRequestDispatcher("members.jsp"); 
			rd.forward(request, response);
		}else{
			openLoginScreen(request, response);
		}
	}
    
    /*
     * Open screen which shows own barks
     */
    private void openOwnBarkScreen(HttpServletRequest request,	HttpServletResponse response)  throws ServletException, IOException
    {
		//get list of own barks
    	memberModel mM = new memberModel();
		mM.setCluster(cluster);
		
		LinkedList<TweetStore> ownBarks = new LinkedList<TweetStore>(); //new empty list
		
		HttpSession Session = request.getSession();
		LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
		
		String userName = thisUser.getUsername();
		ownBarks = mM.getOwnBarks(userName);
    	RequestDispatcher rd = request.getRequestDispatcher("ownBarks.jsp");
		Session.setAttribute("ownBarks", ownBarks);
    	rd.forward(request, response);
	}
   
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * gets the tweet from the jsp page, from the user and sends it on to the tweetmodel where it is stored in the db
	 * gets the login data from the login screen and directs it to the login procedure
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		memberModel mM = new memberModel();
		mM.setCluster(cluster);
		//request.setAttribute("userloggedIn", userloggedIn); //set bean whether user is logged in or not

		if(request.getParameter("logMeIn") != null) 
		{// login a user
			LoginBean thisUser = new LoginBean();
			thisUser.setEmail(request.getParameter("emailAddress"));
			thisUser.setPassword(request.getParameter("password"));
			
			String userName = mM.login(thisUser.getEmail(),thisUser.getPassword());
			
			if(userName != null) //user is valid
			{
				thisUser.setUserName(userName);
				thisUser.setLoggedIn(true);
				
				HttpSession Session = request.getSession();
				Session.setAttribute("currentUser", thisUser); //current user bean is now saved in session
				openMemberHome(request, response);//open members page
			}
			else
			{
				thisUser.setLoggedIn(false);
				request.setAttribute("warningMessage", "username or password are incorrect"); 
				openLoginScreen(request, response);//back to login screen
			}
		}
		else if (request.getParameter("NewEmailAddress") != null &&
				 request.getParameter("NewPassword") != null &&
				 request.getParameter("RepeatPassword") != null &&
				 request.getParameter("userName") != null)
		{//if a new user is to be created
			String email = 			request.getParameter("NewEmailAddress");
			String password = 		request.getParameter("NewPassword");
			String repeatPassword = request.getParameter("RepeatPassword");
			String username = 		request.getParameter("userName");
			
			String warningMessage = ""; //the message the user gets displayed
			boolean success = false;
			if(password.equals(repeatPassword))
			{
				success = mM.newUser(email, username, repeatPassword);
			}else{
				warningMessage = "The passwords did not match";
			}
			
			if(success) //if the user was added successfully
			{
				LoginBean thisUser = new LoginBean();
				thisUser.setEmail(email);
				thisUser.setPassword(password);
				thisUser.setLoggedIn(true);
				thisUser.setUserName(username);
				
				HttpSession Session = request.getSession();
				Session.setAttribute("currentUser", thisUser); //current user bean is now saved in session
				
				System.out.println("account created successfully");
				openMemberHome(request, response);//open members page
			}
			else
			{
				System.out.println("not successful");
				if(warningMessage.equals(""))
				{//some other problem occured
					warningMessage = "Maybe your email/username combination exists already?";
				}else{
					//the message is already stating that passwords are not alike, leave it that way
				}
				request.setAttribute("warningMessage", warningMessage); 
				openRegisterScreen(request, response);//open register page again
			}
		}
		else if(request.getParameter("insertNewBark") != null && request.getParameter("name") != null && request.getParameter("bark") != null)
		{ //post a new bark
			
			System.out.println("Add bark");
			mM.addBark(request.getParameter("name"), request.getParameter("bark"));
			
			openOwnBarkScreen(request, response);
		}
		else if(request.getParameter("searchUsernameUser") != null) //if the request is to search for a user, by a user
		{
    		openMemberHome(request, response);
		}
		else if(request.getParameter("followUser") != null) 
		{//if a user wants to follow another user
			//System.out.println("follow a user" + request.getParameter("followUser") + thisUser.getUsername());
			HttpSession Session = request.getSession();
			LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser");
			LinkedList<usersStore> userList = new LinkedList<usersStore>();
			userList = (LinkedList<usersStore>) Session.getAttribute("memberList");
			if(userList != null)
			{
				String userToBefollowed = request.getParameter("followUser");
				int foo = Integer.parseInt(userToBefollowed); //find which bark to delete
				mM.followUser(thisUser.getUsername(), userList.get(foo).getUsername()); //delete user
	    		openMemberHome(request, response);
			}else{
	    		openMemberHome(request, response);
			}
		}
		else if(request.getParameter("logOff") != null)												//post
		{//log user off
			
			LoginBean thisUser = new LoginBean();
			thisUser.setUserName(null);
			HttpSession Session = request.getSession();
			Session.setAttribute("currentUser", thisUser); //current user bean is now saved in session
			
			openLoginScreen(request, response);
		}
		else if(request.getParameter("changeUserPassword") != null)
		{//change the password
			boolean success = false;
			
			String warningMessage = "";
			if(request.getParameter("NewUserPassword").equals(request.getParameter("RepeatUserPassword")))
			{			//change password in db
				//will return false if old password did not match
				HttpSession Session = request.getSession();
				LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
				success = mM.changePassword(request.getParameter("oldPassword"), request.getParameter("NewUserPassword"), thisUser.getUsername(), thisUser.getEmail());
			}else{
				warningMessage = "The new passwords did not match";
			}
			
			if(success) //if the password was changed successfully, also change it in the bean
			{
				HttpSession Session = request.getSession();
				LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser");
				thisUser.setPassword(request.getParameter("NewPassword"));
				Session.setAttribute("currentUser", thisUser); //current user bean is now saved in session
				
				request.setAttribute("warningMessage", "Your Password has been changed");
				openChangePasswordScreen(request, response);
			}else{
				if(warningMessage.equals(""))
				{//some other problem occured - probably incorrect old password
					warningMessage = "Was the old password correct?";
				}
				request.setAttribute("warningMessage", warningMessage);
				openChangePasswordScreen(request, response);
			}
		}
		else if(request.getParameter("deleteUser") != null) //if delete user was requested
		{//deleteUser
			HttpSession Session = request.getSession();
			System.out.println("Delete user: " + request.getParameter("deleteUser"));
			LinkedList<usersStore> userList = (LinkedList<usersStore>) Session.getAttribute("memberList");

			String userToBeDeleted = request.getParameter("deleteUser");
			int foo = Integer.parseInt(userToBeDeleted); //find which bark to delete
			mM.deleteUser(userList.get(foo).getEmail(), userList.get(foo).getUsername()); //delete user in list at foo
			openMemberHome(request, response);
		}
		else if(request.getParameter("unfollowUser") != null) //if a user needs to be unfollowed
		{//unfollowUser - delete follower entry
			HttpSession Session = request.getSession();
			LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser");
			//do only if user is logged in
			if(thisUser != null)
			{
				String userToBeUnfollowed = request.getParameter("unfollowThisUser");
				String currentUserName = (String) thisUser.getUsername();
				LinkedList<TweetStore> barks = (LinkedList<TweetStore>) Session.getAttribute("followedBarks");
				int foo = Integer.valueOf(userToBeUnfollowed); //find which bark to delete
				mM.unfollowUser(currentUserName, barks.get(foo).getUser()); //delete user
				openMemberHome(request, response);
			}else{
				openLoginScreen(request, response);
			}
		}
		else if(request.getParameter("deleteBark") != null) //if the request is to delete a own bark
		{
			HttpSession Session = request.getSession();
			LinkedList<TweetStore> ownBarks = (LinkedList<TweetStore>) Session.getAttribute("ownBarks");			
			String barkToBeDeleted = request.getParameter("deleteBark");
			int foo = Integer.valueOf(barkToBeDeleted); //find which bark to delete
			mM.deleteBark(ownBarks.get(foo).getUUID().toString(), ownBarks.get(foo).getUser().toString()); //delete bark
			openOwnBarkScreen(request, response);
		}
		else{ //if nothing was selected or there was some problem - go to login screen
			openLoginScreen(request, response);
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * if something should be deleted, use delete
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * if its just a redirection, and the change should/can be shown in the URL, then use get
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(request.getParameter("viewOwnBarks") != null)
		{//user wants to see own barks
			openOwnBarkScreen(request, response);
		}else if(request.getParameter("barkYourself") != null)
		{//user wants to bark
    		openBarkYourself(request, response);
		}else if(request.getParameter("changePassword") != null)
		{//open changePassword window
			openChangePasswordScreen(request, response);
		}else if(request.getParameter("goToMembers") != null)
		{//directly open members page
    		openMemberHome(request, response);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//								The following only stupidly open pages, they wont ever need servicing
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
     * Open new login screen / dont pass anything, is going to be empty anyways
     */
    private void openLoginScreen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	RequestDispatcher rd = request.getRequestDispatcher("login.jsp"); 
		rd.forward(request, response);
	}
    
    /*
     * Open register-new-member screen
     */
    private void openRegisterScreen(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException
    {
    	RequestDispatcher rd = request.getRequestDispatcher("register.jsp"); 
		rd.forward(request, response);
	}
    
    /*
     * Change password screen
     */
    private void openChangePasswordScreen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	RequestDispatcher rd = request.getRequestDispatcher("changePassword.jsp"); 
    	rd.forward(request, response);
	}
    
    /*
     * Open bark-yourself screen
     */
    private void openBarkYourself(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
    {
    	RequestDispatcher rd = request.getRequestDispatcher("newTweet.jsp");
		rd.forward(request, response);
	}
}

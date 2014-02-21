package com.abc.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 * CREATE TABLE Tweets (
 * user varchar,
 *  interaction_time timeuuid,
 *  tweet varchar,
 *  PRIMARY KEY (user)
 * ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */

import java.util.Collections;
import java.util.LinkedList;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.abc.stores.TweetStore;
import com.abc.stores.usersStore;

public class memberModel 
{
	Cluster cluster;
	public memberModel()
	{
	}

	public void setCluster(Cluster cluster)
	{
		this.cluster=cluster;
	}
	
	/*
	 * Populate linked list of users
	 */
	public LinkedList<usersStore> getUsers(String term, String user) 
	{
		LinkedList<usersStore> userList = new LinkedList<usersStore>(); //future list of users
		Session session = createSession();
		String searchterm = term;
		String currentUser = user;
		//create statement
		String prepared = "SELECT * from users";
		System.out.println("searchterm: " + searchterm);
		if(searchterm.equals("") || searchterm == null)//if searchterm is empty - "" was entered as a default
		{			//conduct empty search
			prepared += " LIMIT 100";
		}
		else{ //if a searchterm was entered
			prepared += " WHERE username='" + searchterm + "' LIMIT 100 ALLOW FILTERING";
		}
		
		PreparedStatement statement = session.prepare(prepared); //get all users that resemble search term

		LinkedList<String> followedUsers = getFollowers(currentUser);//get followed users of this user
		
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no barks
		{
			System.out.println("No users found!");
		} 
		else 
		{
			for (Row row : rs) //for each row in user table, create a user object
			{
				boolean isFound = false;
				
				//loop through followed user list and see if user is already in list, only add if not in list
				for(String userN: followedUsers)
				{
					//check if the followed user is already followed or the user him/herself
					if(userN.equals(row.getString("username")) || row.getString("username").equals(currentUser))
					{
						isFound = true;
					}
				}
				
				if(isFound)
				{
					//user is already in list of followers, dont add again
				}else{
					usersStore uS = new usersStore();
					uS.setEmail(row.getString("email"));	//populate user object
					uS.setUserName(row.getString("username"));
					userList.add(uS);//add bark to bark list
				}
			}
		}
		session.close();
		
		Collections.sort(userList); //sort linked list alphabetically by username
		return userList;
	}
	
	/*
	 * Populate linked list of followers
	 */
	public LinkedList<String> getFollowers(String origuser) 
	{
		LinkedList<String> userList = new LinkedList<String>(); //future list of users the origuser follows
		Session session = createSession();
		
		PreparedStatement statement = session.prepare("SELECT * from followers WHERE user='" + origuser + "' ALLOW FILTERING"); //get all users that resemble search term

		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no barks
		{
			System.out.println("No followers found!");
		} 
		else 
		{
			for (Row row : rs) //for each row in bark table
			{
				userList.add(row.getString("otherUser"));//add bark to bark list
			}
		}
		session.close();
		
		//Collections.sort(userList, Collections.reverseOrder()); //sort linked list alphabetically by username
		return userList;
	}

	/*
	 * Creates new session
	 */
	private Session createSession() 
	{
		Session session = cluster.connect("barks");
		return session;
	}
	
	/*
	 * Add new user to db
	 */
	public boolean newUser(String email, String username, String password){
		boolean success = false;
		Session session = createSession();
		//first find out whether the username or email exists already, 
		//while the combination must be unique, they could exist as long as one of the two is different
		PreparedStatement statement = session.prepare("SELECT * FROM users WHERE username = '" + username + "' ALLOW FILTERING"); //create select query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no users with the same username
		{
			statement = session.prepare("SELECT * FROM users WHERE email = '" + email + "' ALLOW FILTERING"); //create select query
			boundStatement = new BoundStatement(statement);
			rs = session.execute(boundStatement); //execute command
			if (rs.isExhausted()) //if there are no users with the same email address
			{
				statement = session.prepare("INSERT INTO users (email, password, username) VALUES ('" + email + "', '" + password + "', '" + username + "')"); //create add query
				try{
					boundStatement = new BoundStatement(statement);
					session.execute(boundStatement);
					success = true;
				}catch(Exception e)
				{
					System.out.println("something else went wrong");
					success = false;
				}
			}else{
				System.out.println("email Address already exists");
				success = false;
			}
		} else{
			System.out.println("username already exists!");
			success = false;
		}
		session.close();
		return success;
	}
	
	/*
	 * handles login routine
	 */
	public String login(String email, String password) 
	{
		String username = null;
		
		Session session = createSession();
		PreparedStatement statement = session.prepare("SELECT * FROM users WHERE email = '" + email + "'"); //create add query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no barks
		{
			System.out.println("No user found with matching credentials!");
		} 
		else 
		{
			for (Row row : rs) //for each row in barks table
			{
				System.out.println("user found, db password is:" + row.getString("password") + " entered password is:" + password);
				if(row.getString("password").equals(password))
				{
					username = row.getString("username");
				}
			}
		}
		session.close();
		//System.out.println("username is: " + username + " password is: " + dbpassword);
		return username;
	}
	
	/*
	 * handles a change-password request
	 */
	public boolean changePassword(String oldPassword, String newPassword, String currentUserName, String email) 
	{
		boolean success = false;
		boolean correctOldPassword = false;
		//find out if old password matches with entered password
		Session session = createSession();
		PreparedStatement statement = session.prepare("SELECT * FROM users WHERE email = '" + email + "'"); //create add query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no barks
		{
			System.out.println("No user found with matching credentials!");
		} 
		else 
		{
			for (Row row : rs) //for each row in users table
			{
				if(row.getString("password").equals(oldPassword))
				{
					correctOldPassword = true;
				}
			}
		}
		
		System.out.println("reached this in change password");
		//if the old password matches
		if(correctOldPassword)
		{
			try{
				//System.out.println("setting new password");
				Session session2 = createSession();
				PreparedStatement statement2 = session2.prepare("UPDATE users SET password = '" + newPassword + "' WHERE username='" + currentUserName + "' AND email = '" + email + "'"); //create update query
				//System.out.println("prepared statement");
				BoundStatement boundStatement2 = new BoundStatement(statement2);
				session.execute(boundStatement2); //execute command
				//System.out.println("executed command");
				success = true; //success, no problems, password is changed
			}catch(Exception e)
			{
				System.out.println("error: " + e);
				success = false; //there was a problem, set to false
			}
		}
		System.out.println("reached this in change password2");
		session.close();
		return success;
	}

	/*
	 * takes care of all deletion necessary when removing a user
	 */
	public boolean deleteUser(String email, String nameOfUser) 
	{
		boolean first = false;
		boolean second = false;
		boolean third = false;
		boolean success = false;
		
		Session session = createSession();
		System.out.println("deleting user: " + nameOfUser);

		//first: delete the user
		try{
			System.out.println("trying to delete");
			PreparedStatement statement = session.prepare("DELETE FROM users WHERE email = '" + email + "'"); //create delete query
			BoundStatement boundStatement = new BoundStatement(statement);
			session.execute(boundStatement); //execute command
			first = true;
		}catch(Exception e)
		{
			System.out.println("deleting user: " + e);
		}
		
		//second: delete the followers table
		try{
			System.out.println("deleting followers");
			PreparedStatement statement = session.prepare("DELETE FROM followers WHERE user = '" + nameOfUser + "'"); //create delete query
			BoundStatement boundStatement = new BoundStatement(statement);
			session.execute(boundStatement);
			second = true;
		}catch(Exception e)
		{
			System.out.println("deleting followers: " + e);
		}
		
		//third: delete barks
		try{
			System.out.println("deleting barks");
			PreparedStatement statement = session.prepare("DELETE FROM barks WHERE user = '" + nameOfUser + "'"); //create delete query
			BoundStatement boundStatement = new BoundStatement(statement);
			session.execute(boundStatement);
			third = true;
		}catch(Exception e)
		{
			System.out.println("deleting barks: " + e);
		}
		
		session.close();
		if(first && second && third) //only if all three succeeded
		{
			success = true;
			System.out.println("complete success!");

		}
		return success;
	}

	/*
	 * Unfollow user with the name of the paramter given
	 */
	public void unfollowUser(String user, String userToBeUnfollowed) 
	{
		//first get all the rows that correspond to one user, so all the users that are followed by the user
		//from that get the uuid of the relation we're deleting, then delete that relation
		Session session = createSession();
		String uuid = null;
		PreparedStatement statement = session.prepare("SELECT * FROM followers WHERE user = '" + user + "'"); //create select query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no barks
		{
			System.out.println("No user found with matching credentials!");
		} 
		else 
		{
			for (Row row : rs) //for each row in barks table
			{
				//if the followed-name is equal to the name we're searching for, get the uuid of that relation
				//System.out.println("followers found: " + row.getString("otheruser"));
				//System.out.println("searched user:   " + userToBeUnfollowed);

				if(row.getString("otheruser").equals(userToBeUnfollowed))
				{
					uuid = row.getUUID("id").toString();
					//System.out.println("uuid found: " + uuid);
				}
			}
		}
		//now delete
		if(!uuid.equals(null))//if a uuid was found previously (should always be true)
		{
			//System.out.println("Its here alright");
			statement = session.prepare("DELETE FROM followers WHERE id = " + uuid + " AND user='" + user + "'"); //create delete query (must include both keys)
			boundStatement = new BoundStatement(statement);
			session.execute(boundStatement); //execute command
		}else{
			System.out.println("An error occured");
		}
		
		session.close();
	}

	/*
	 * Follow a user
	 */
	public void followUser(String UserName, String followThisUsername) 
	{
		//first find out whether the user is already following the user - we dont want to create a duplicate
		boolean found = false;
		Session session = createSession();
		PreparedStatement statement = session.prepare("SELECT * FROM followers WHERE user = '" + UserName + "'"); //create select query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command

		if (rs.isExhausted()) //if there are no barks
		{
			statement = session.prepare("INSERT INTO followers (user, id, otheruser) VALUES ('" + UserName + "', now() , '" + followThisUsername + "')"); //create delete query (must include both keys)
			boundStatement = new BoundStatement(statement);
			session.execute(boundStatement); //execute command
		}
		else 
		{
			for (Row row : rs) //for each row in barks table
			{
				if(row.getString("otheruser").equals(followThisUsername) || followThisUsername.equals("null"))//also check if user-error
				{
					System.out.println("The user is already followed");
					found = true;
				}
			}
			
			if(!found)//if the user is following poeple, but not this person
			{
				statement = session.prepare("INSERT INTO followers (user, id, otheruser) VALUES ('" + UserName + "', now() , '" + followThisUsername + "')"); //create delete query (must include both keys)
				boundStatement = new BoundStatement(statement);
				session.execute(boundStatement); //execute command
			}
		}
		session.close();
	}
	
	/*
	 * Delete Bark from DB
	 */
	public void deleteBark(String uuID, String user)
	{
		Session session = createSession();
		System.out.println("uuid: " + uuID);
		System.out.println("username: " + user);

		PreparedStatement statement = session.prepare("DELETE FROM barks.barks WHERE interaction_time = " + uuID + " AND user= '" + user + "'"); //create delete query (must include both keys)
		
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement); //execute command
		session.close();
	}
	
	/*
	 * Adds bark to db
	 */
	public void addBark(String name, String bark) //takes in bark information
	{
		Session session = createSession();
		long currentDate = System.currentTimeMillis();
		System.out.println("name: " + name);
		System.out.println("bark: " + bark);

		//whenever there is a ' in the string, it has to be replaced with ''
		bark = bark.replaceAll("'", "''");
		String st = "INSERT INTO barks (user, interaction_time, thetimestamp, bark) VALUES ('" + name + "', now(), " + currentDate + ", '" + bark + "')";
		PreparedStatement statement = session.prepare(st); //create add query
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement); //execute command
		session.close();
	}
	
	/*
	 * returns a list of all barks
	 */
	public LinkedList<TweetStore> getBarks() 
	{
		LinkedList<TweetStore> tweetList = new LinkedList<TweetStore>(); //future list of tweets
		Session session = createSession();

		PreparedStatement statement = session.prepare("SELECT * from barks LIMIT 100"); //get all tweets

		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no barks
		{
			System.out.println("No Barks found!");
		} 
		else 
		{
			for (Row row : rs) //for each row in bark table
			{
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("bark"));	//populate tweet object
				ts.setUser(row.getString("user"));
				ts.setDate(row.getDate("thetimestamp"));
				ts.setUUID(row.getUUID("interaction_time"));
				tweetList.add(ts);//add bark to bark list
			}
		}
		session.close();
		Collections.sort(tweetList, Collections.reverseOrder()); //sort linked list
		return tweetList;
	}
	
	/*
	 * Polymorphism used to allow double naming: in this case we pass a username to get only barks from one user
	 * This method gets the barks from one specific user
	 */
	public LinkedList<TweetStore> getBarks(String username) 
	{
		LinkedList<TweetStore> tweetList = new LinkedList<TweetStore>(); //future list of tweets
		Session session = createSession();

		PreparedStatement statement = session.prepare("SELECT * from barks WHERE user='"+ username +"' LIMIT 100"); //get all tweets from that user

		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no barks
		{
			System.out.println("No Barks found!");
		} 
		else 
		{
			for (Row row : rs) //for each row in bark table
			{
				//System.out.println("bark found, adding to list");
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("bark"));	//populate tweet object
				ts.setUser(row.getString("user"));
				ts.setDate(row.getDate("thetimestamp"));
				ts.setUUID(row.getUUID("interaction_time"));
				tweetList.add(ts);//add bark to bark list
			}
		}
		session.close();
		Collections.sort(tweetList, Collections.reverseOrder()); //sort linked list
		return tweetList;
	}

	/*
	 * Return a list of own barks
	 */
	public LinkedList<TweetStore> getOwnBarks(String currentUserName) 
	{
		LinkedList<TweetStore> ownBarks = new LinkedList<TweetStore>();
		Session session = createSession();
		System.out.println("Showing barks for user: " + currentUserName);
		PreparedStatement statement = session.prepare("SELECT * FROM barks WHERE user = '" + currentUserName + "'"); //create select query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command

		if (rs.isExhausted()) //if there are no barks
		{
			//no barks found
		}
		else 
		{
			for (Row row : rs) //for each row in barks table
			{
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("bark"));	//populate bark object
				ts.setUser(row.getString("user"));
				ts.setDate(row.getDate("thetimestamp"));
				ts.setUUID(row.getUUID("interaction_time"));
				ownBarks.add(ts);//add bark to bark list
			}
		}
		session.close();
		
		return ownBarks;
	}
}
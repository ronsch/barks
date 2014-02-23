package com.abc.lib;

import com.datastax.driver.core.*;

public final class Keyspaces {
	public Keyspaces(){
	}
	
	public static void SetUpKeySpaces(Cluster c)
	{
		String dropkeySpace="drop keyspace barks";
		String createkeyspace="create keyspace if not exists barks  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
		String createBarks="create table IF NOT EXISTS barks.barks (user varchar, interaction_time timeuuid, bark varchar, thetimestamp timestamp, PRIMARY KEY (user, interaction_time)) with clustering order by (interaction_time DESC)";
		String createFollowers = "create table IF NOT EXISTS barks.followers (user varchar, id timeuuid, otheruser varchar, PRIMARY KEY (user, id)) with clustering order by (id DESC)";
		String createUsers = "create table IF NOT EXISTS barks.users (email varchar, username varchar, password varchar, PRIMARY KEY (email, username)) with clustering order by (username DESC)";
		
		
		 com.datastax.driver.core.Session session = c.connect();
		try{
			 com.datastax.driver.core.SimpleStatement cqlQuery = new  com.datastax.driver.core.SimpleStatement(createkeyspace);
			session.execute(cqlQuery);
			//session.execute("DESCRIBE KEYSPACES;");
			
			/*cqlQuery = new SimpleStatement(dropkeySpace);
			session.execute(cqlQuery);
			
			cqlQuery = new SimpleStatement(createkeyspace);
			session.execute(cqlQuery);
			*/
			
			cqlQuery = new SimpleStatement(createBarks);
			session.execute(cqlQuery);
			
			cqlQuery = new SimpleStatement(createFollowers);
			session.execute(cqlQuery);
			
			cqlQuery = new SimpleStatement(createUsers);
			session.execute(cqlQuery);
			System.out.println("all tables created if applicable"); //

		}catch(Exception et){
			System.out.println("Can't create db "+et);
		}
		session.close();
		
		/*try{
			
			Session session = c.connect();//			   CREATE KEYSPACE IF NOT EXISTS barks  WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}
			PreparedStatement statement = session.prepare("create keyspace if not exists barks  WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}"); //create keyspace if it doesnt exist
			System.out.println("keyspace created"); //
			BoundStatement boundStatement = new BoundStatement(statement);
			session.execute(boundStatement); //execute command
			
			//create the tables, if they don't exist already
			statement = session.prepare("create table IF NOT EXISTS barks.barks (user varchar, interaction_time timeuuid, bark varchar, thetimestamp timestamp, PRIMARY KEY (user, interaction_time)) with clustering order by (interaction_time DESC)"); //create barks table
			System.out.println("barks table created");
			boundStatement = new BoundStatement(statement);
			session.execute(boundStatement);
			
			statement = session.prepare("create table IF NOT EXISTS barks.followers (user varchar, id timeuuid, otheruser varchar, PRIMARY KEY (user, id)) with clustering order by (id DESC)"); //create followers table
			System.out.println("followers table created");
			boundStatement = new BoundStatement(statement);
			session.execute(boundStatement);
			
			statement = session.prepare("create table IF NOT EXISTS barks.users (email varchar, username varchar, password varchar, PRIMARY KEY (email, username)) with clustering order by (username DESC)"); //create users table
			System.out.println("users table created");
			boundStatement = new BoundStatement(statement);
			session.execute(boundStatement);
			
			session.shutdown();
		}catch(Exception et){
			System.out.println("Other keyspace or column definition error: " +et);
		}*/
	}
}
package uk.ac.dundee.computing.aec.lib;

import com.datastax.driver.core.*;

public final class Keyspaces {
	public Keyspaces(){
	}
	
	public static void SetUpKeySpaces(Cluster c){
		try{
			//Add some keyspaces here	
		}catch(Exception et){
			System.out.println("Other keyspace or column definition error: " +et);
		}
	}
}
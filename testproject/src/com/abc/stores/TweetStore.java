package com.abc.stores;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class TweetStore implements Comparable<TweetStore>
{
     String Tweet;
     String User;
     UUID uUID;
     java.util.Date timeOfTweet;
     
     public String getDate() //returns string that is the date of the tweet
     {
    	 String stringDate = "No Date entered";
    	 try{
    		 DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    		 stringDate = df.format(timeOfTweet); //doesn't seem to care what type of date
    	 }
    	 catch(Exception e)
    	 {
    	 }
    	 return stringDate;
     }
     
     @Override
     public int compareTo(TweetStore tw) {
 		if(this.timeOfTweet.after(tw.timeOfTweet))
 		{
 			return 1;
 		}else if(this.timeOfTweet.before(tw.timeOfTweet))
 		{
 			return -1;
 		}else{
 			return 0;
 		}
 	 }
     
     public UUID getUUID()
     {
    	 return uUID;
     }
     
     public void setUUID(UUID newID)
     {
    	 this.uUID = newID;
     }
    
     public String getTweet()
     {
    	 return Tweet;
     }
     public String getUser()
     {
    	 return User;
     }
     
     public void setDate(java.util.Date date)
     {
    	 this.timeOfTweet = date;
     }
     
     public void setTweet(String Tweet)
     {
    	 this.Tweet=Tweet;
     }
     public void setUser(String User)
     {
    	 this.User=User;
     }   
}

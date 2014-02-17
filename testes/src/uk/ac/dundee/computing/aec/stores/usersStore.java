package uk.ac.dundee.computing.aec.stores;

public class usersStore implements Comparable<usersStore>
{
     String email;
     String username;
     
     @Override
     public int compareTo(usersStore uS) 
     {
 		return this.email.compareTo(uS.email);
 	 }
     
     public String getUsername()
     {
    	 return username;
     }
    
     public String getEmail()
     {
    	 return email;
     }
     
     public void setEmail(String mail)
     {
    	 this.email=mail;
     }
     
     public void setUserName(String name)
     {
    	 this.username=name;
     }   
}
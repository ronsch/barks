package com.abc.stores;

public class LoginBean
{
     String userName;
     String password;
     String email;
     Boolean loggedIn;
     
     public String getUsername()
     {
    	 return userName;
     }
    
     public String getPassword()
     {
    	 return password;
     }
     
     public String getEmail()
     {
    	 return email;
     }
     
     public boolean getLoggedIn()
     {
    	 return loggedIn;
     }
     
     /////////////////////////
     
     public void setLoggedIn(boolean isLoggedIn)
     {
    	 this.loggedIn=isLoggedIn;
     }
     
     public void setEmail(String nemail)
     {
    	 this.email=nemail;
     }
     
     public void setPassword(String password)
     {
    	 this.password=password;
     }
     
     public void setUserName(String name)
     {
    	 this.userName=name;
     }   
}
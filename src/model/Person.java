package model;

public abstract class Person {
    
    
protected String id, firstName, lastName, phone, emailId;

public Person(String id, String firstName, String lastName, String phoneNum, String emailId) {
        
        
this.id = id;
this.firstName = firstName;
this.lastName = lastName;
this.phone = phoneNum;
this.emailId = emailId;
    
}

    
public String getId() { 
return id; 
}
     
    
public String getFirstName() { 
return firstName; 
}
    
    
public String getLastName() { 
return lastName; 
}
}
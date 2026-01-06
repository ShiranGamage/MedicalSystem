package model;


public class Patient extends Person {
     
private String gp_Id; // Id of General Practitioner
    
private String nhsNumber; //National Health Service number

private String address_Info; // The patient's address

    
public Patient(String id, String firstName, String lastName, String phone, String email, 
    String nhsNumber, String address, String gpId) {
       
    super(id, firstName, lastName, phone, email);
        
//patientspecific attributes

    this.nhsNumber = nhsNumber;
    this.address_Info = address;
    this.gp_Id = gpId;
       
}

    
public String getNhsNumber() {
return nhsNumber;
}
    
   
public String getAddress() {
return address_Info;
 }
    
  
public String getGpId() {
return gp_Id;
}
    

//Converting the patient infor to .CSV format.
     
public String toCSV() {
return String.join(",", id, firstName, lastName, nhsNumber, address_Info, gp_Id);
}
}
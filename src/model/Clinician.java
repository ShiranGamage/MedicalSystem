package model;

public class Clinician extends Person {
   
private String specialityInfo, gmcNumber;

public Clinician(String id, String fName, String lName, String speciality, String gmc_Num) {
        
super(id, fName, lName, "", ""); 
        
this.specialityInfo = speciality; // GP , Surgeon, etc.
this.gmcNumber = gmc_Num;
}

   

public String getGmcNumber() { 
return gmcNumber; 
}
public String getSpeciality() { 
return specialityInfo; 
}

}
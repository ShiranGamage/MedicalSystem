package model;

public class Prescription {
   
private String patientId;
private String medicationInfo;
private String dosageInfo;
private String PrescriptionId;

public Prescription(String PrescriptionId, String patientId, String medicationInfo, String dosageInfo) {
        
       
this.PrescriptionId = PrescriptionId;
this.dosageInfo = dosageInfo;
this.patientId = patientId;
this.medicationInfo = medicationInfo;

}

public String getPrescriptionId() { 
return PrescriptionId; 
}
    
public String getPatientId() {
return patientId;
}
    
public String getMedicationInfo() { 
return medicationInfo; 
}
    
public String getDosageInfo() { 
return dosageInfo; 
}

}
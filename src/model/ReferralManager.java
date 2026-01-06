package model;

import java.io.*;
import java.time.LocalDateTime;

public class ReferralManager {

    private static ReferralManager instance;
private final String FILE_PATH = "referrals_output.txt";

private ReferralManager() 
{

}

public static synchronized ReferralManager getInstance() {
if (instance == null) {
instance = new ReferralManager();
}
return instance;
}

    
public void createReferral(String patientId, String frmClinician, String toSpecialist, String reason) {
   
    String header = "--- NEW REFERRAL RECORD ---";
    String referral_Data = header + "\n" + "Date: " + LocalDateTime.now() + "\n" +
    "Patient ID: " + patientId + "\n" +"From Clinician: " + frmClinician + "\n" +
    "To Specialist: " + toSpecialist + "\n" +"Reason: " + reason + "\n";

    // File update with error handling
    
    try {
        FileWriter fileWrite = new FileWriter(FILE_PATH, true);
        BufferedWriter bWriter = new BufferedWriter(fileWrite);
        PrintWriter out = new PrintWriter(bWriter);
        
    out.println(referral_Data);
    out.close(); 
        
    System.out.println("Success : Referral was saved to the system.");
        
    } catch (IOException e) {
     System.out.println("Error: writing errored. " + e.getMessage());
    }
}


}
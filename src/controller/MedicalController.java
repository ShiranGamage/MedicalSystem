package controller;



import java.io.*;
import java.time.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.ReferralManager;
import view.AppointmentsFrame;
import view.CliniciansFrame;
import view.MainFrame;
import view.PatientFrame;
import view.PrescriptionsFrame;
import view.ReferralsFrame;
import view.StaffFrame; 

public class MedicalController {
    
    // Headers to data files
    private static final String[] PATIENT_HEADERS = {"patient_id", "first_name", "last_name", "date_of_birth", 
   "nhs_number", "gender", "phone_number", "email", "address", "postcode", "emergency_contact_name", 
   "emergency_contact_phone", "registration_date", "gp_surgery_id"};
    
    private static final String[] CLINICIAN_HEADERS = {"clinician_id", "first_name", "last_name", "title", 
   "speciality", "gmc_number", "phone_number", "email", "workplace_id", "workplace_type", 
   "employment_status", "start_date"};
    
    private static final String[] APPOINTMENT_HEADERS = {"appointment_id", "patient_id", "clinician_id", 
    "facility_id", "appointment_date", "appointment_time", "duration_minutes", "appointment_type", 
        "status", "reason_for_visit", "notes", "created_date", "last_modified"};
    
    private static final String[] PRESCRIPTION_HEADERS = {"prescription_id", "patient_id", "clinician_id", 
    "appointment_id", "prescription_date", "medication_name", "dosage", "frequency", "duration_days", 
        "quantity", "instructions", "pharmacy_name", "status", "issue_date", "collection_date"};
    
    private static final String[] STAFF_HEADERS = {"staff_id", "first_name", "last_name", "role", "department", 
      "facility_id", "phone_number", "email", "employment_status", "start_date", "line_manager", "access_level"};
    
    private static final String[] REFERRAL_HEADERS = {"referral_id", "patient_id", "referring_clinician_id", 
    "referred_to_clinician_id", "referring_facility_id", "referred_to_facility_id", "referral_date", 
      "urgency_level", "referral_reason", "clinical_summary", "requested_investigations", "status", 
        "appointment_id", "notes", "created_date", "last_updated"};
    
    // Frame references
    private MainFrame view;
    private ReferralManager referralManager;
    private PatientFrame patientFrame;
    private CliniciansFrame cliniciansFrame;
    private AppointmentsFrame appointmentsFrame;
    private PrescriptionsFrame prescriptionsFrame;
    private StaffFrame staffFrame;
    private ReferralsFrame referralsFrame;

    private String csvEscape(String value) {
        if (value == null) return "";
        if (!value.contains(",") && !value.contains("\"") && !value.contains("\n")) {
            return value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public MedicalController(MainFrame mainView) {
        this.view = mainView;
        this.referralManager = ReferralManager.getInstance();
        this.loadAppointmentsOnMainWindow();
        this.setupModuleListeners();
    }

    private void loadAppointmentsOnMainWindow() {
        String[] paths = {"appointments.csv", "src/appointments.csv", "../appointments.csv"};
        
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) continue;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String headerLine = reader.readLine();
                if (headerLine == null) continue;
                
                List headerList = this.parseCsvLine(headerLine);
                String[] headers = (String[]) headerList.toArray(new String[0]);
                this.view.getAppointmentsTableModel().setRowCount(0);
                
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    List dataList = this.parseCsvLine(line);
                    if (dataList.size() == 0) continue;
                    
                    Object[] rowData = new Object[headers.length];
                    for (int i = 0; i < headers.length; i++) {
                        rowData[i] = i < dataList.size() ? ((String) dataList.get(i)).trim() : "";
                    }
                    this.view.getAppointmentsTableModel().addRow(rowData);
                    count++;
                }
                System.out.println("Loaded " + count + " appointments");
                return;
            } catch (Exception ex) {
                System.out.println("Error reading " + path + ": " + ex.getMessage());
            }
        }
        System.out.println("appointments.csv not found");
    }

    private void setupModuleListeners() {
        this.view.getPatientsBtn().addActionListener(e -> openPatientsWindow());
        this.view.getCliniciansBtn().addActionListener(e -> openCliniciansWindow());
        this.view.getAppointmentsBtn().addActionListener(e -> openAppointmentsWindow());
        this.view.getPrescriptionsBtn().addActionListener(e -> openPrescriptionsWindow());
        this.view.getStaffBtn().addActionListener(e -> openStaffWindow());
        this.view.getReferralsBtn().addActionListener(e -> openReferralsWindow());
    }

    // Open patients management window
    private void openPatientsWindow() {
        if (this.patientFrame == null || !this.patientFrame.isVisible()) {
            this.patientFrame = new PatientFrame();
            this.setupPatientListeners();
            this.patientFrame.setVisible(true);
            this.loadPatients();
        }
    }
    
    private void setupPatientListeners() {
        this.patientFrame.getAddBtn().addActionListener(e -> addPatientDialog());
        this.patientFrame.getEditBtn().addActionListener(e -> editPatientDialog());
        this.patientFrame.getDeleteBtn().addActionListener(e -> deletePatientRecord());
        this.patientFrame.getSaveBtn().addActionListener(e -> savePatientsData());
        this.patientFrame.getCloseBtn().addActionListener(e -> patientFrame.dispose());
    }

    private String getInput(String prompt) {
        String input = JOptionPane.showInputDialog(patientFrame, prompt);
        return input == null ? "" : input;
    }

    private void addPatientDialog() {
        try {
            String firstName = JOptionPane.showInputDialog(patientFrame, "First name:");
            if (firstName == null || firstName.trim().isEmpty()) return;

            String lastName = JOptionPane.showInputDialog(patientFrame, "Last name:");
            if (lastName == null || lastName.trim().isEmpty()) return;

            String dob = getInput("Date of Birth (YYYY-MM-DD):");
            String nhsNumber = getInput("NHS Number:");
            String gender = getInput("Gender:");
            String phone = getInput("Phone (optional):");
            String email = getInput("Email (optional):");
            String address = getInput("Address (optional):");
            String postcode = getInput("Postcode (optional):");
            String emergencyContact = getInput("Emergency Contact (optional):");
            String emergencyPhone = getInput("Emergency Phone (optional):");
            String gpSurgeryId = getInput("GP Surgery ID (optional):");

            String patientId = getNextPatientId();
            String regDate = LocalDate.now().toString();
            patientFrame.getTableModel().addRow(new Object[]{patientId, firstName, lastName, dob, 
                nhsNumber, gender, phone, email, address, postcode, emergencyContact, emergencyPhone, regDate, gpSurgeryId});
            JOptionPane.showMessageDialog(patientFrame, "Patient added. Click Save to persist.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(patientFrame, "Error: " + ex.getMessage());
        }
    }

    private void editPatientDialog() {
        int row = patientFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(patientFrame, "Please select a patient to edit");
            return;
        }
        
        try {
            DefaultTableModel model = patientFrame.getTableModel();
            String[] names = {"ID", "First Name", "Last Name", "DOB", "NHS No", "Gender", 
                "Phone", "Email", "Address", "Postcode", "Emergency Contact", "Emergency Phone", 
                "Registration Date", "GP Surgery ID"};
            
            for (int i = 0; i < model.getColumnCount(); i++) {
                String current = String.valueOf(model.getValueAt(row, i));
                String newValue = JOptionPane.showInputDialog(patientFrame, names[i] + ":", current);
                if (newValue != null) {
                    model.setValueAt(newValue, row, i);
                }
            }
            JOptionPane.showMessageDialog(patientFrame, "Patient updated. Click Save to persist.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(patientFrame, "Error: " + ex.getMessage());
        }
    }

    private void deletePatientRecord() {
        int row = patientFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(patientFrame, "Please select a patient to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(patientFrame, "Delete this patient?");
        if (confirm == 0) {
            patientFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(patientFrame, "Patient deleted. Click Save to persist.");
        }
    }

    private void savePatientsData() {
        try {
            String dir = System.getProperty("user.dir");
            File file = dir.endsWith("src") ? new File(dir + "/../patients.csv") : new File("patients.csv");
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(String.join(",", PATIENT_HEADERS));
                
                DefaultTableModel model = patientFrame.getTableModel();
                for (int row = 0; row < model.getRowCount(); row++) {
                    String[] data = new String[PATIENT_HEADERS.length];
                    for (int col = 0; col < data.length; col++) {
                        Object value = model.getValueAt(row, col);
                        data[col] = value == null ? "" : value.toString();
                    }
                    writer.println(String.join(",", data));
                }
            }
            JOptionPane.showMessageDialog(patientFrame, "Saved to " + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(patientFrame, "Error saving: " + ex.getMessage());
        }
    }

    private void loadPatients() {
        String[] paths = {"patients.csv", "src/patients.csv", "../patients.csv"};
        
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists() || file.length() < 100) continue;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String headerLine = reader.readLine();
                if (headerLine == null) continue;
                
                patientFrame.getTableModel().setRowCount(0);
                String line;
                int count = 0;
                
                while ((line = reader.readLine()) != null) {
                    List dataList = parseCsvLine(line);
                    if (dataList.isEmpty()) continue;
                    
                    Object[] rowData = new Object[PATIENT_HEADERS.length];
                    for (int i = 0; i < PATIENT_HEADERS.length; i++) {
                        rowData[i] = i < dataList.size() ? ((String) dataList.get(i)).trim() : "";
                    }
                    patientFrame.getTableModel().addRow(rowData);
                    count++;
                }
                System.out.println("Loaded " + count + " patients");
                return;
            } catch (Exception ex) {
                System.out.println("Error reading " + path + ": " + ex.getMessage());
            }
        }
        System.out.println("patients.csv not found");
    }

    private String getNextPatientId() {
        String[] paths = {"patients.csv", "src/patients.csv", "../patients.csv"};
        int maxId = 0;
        
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) continue;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.readLine();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String id = parts[0].trim();
                        if (id.startsWith("P")) {
                            try {
                                int num = Integer.parseInt(id.substring(1));
                                if (num > maxId) maxId = num;
                            } catch (Exception e) {}
                        }
                    }
                }
                break;
            } catch (Exception e) {}
        }
        return String.format("P%03d", maxId + 1);
    }

   private void openCliniciansWindow() {
      if (this.cliniciansFrame == null || !this.cliniciansFrame.isVisible()) {
         this.cliniciansFrame = new CliniciansFrame();
         this.loadClinicians();
         this.setupCliniciansListeners();
         this.cliniciansFrame.setVisible(true);
      }

   }

    private void setupCliniciansListeners() {
        cliniciansFrame.getAddBtn().addActionListener(e -> addClinicianDialog());
        cliniciansFrame.getEditBtn().addActionListener(e -> editClinicianDialog());
        cliniciansFrame.getDeleteBtn().addActionListener(e -> deleteClinicianRecord());
        cliniciansFrame.getSaveBtn().addActionListener(e -> saveCliniciansData());
        cliniciansFrame.getCloseBtn().addActionListener(e -> cliniciansFrame.dispose());
    }

    private void addClinicianDialog() {
        try {
            String id = JOptionPane.showInputDialog(cliniciansFrame, "Clinician ID:");
            if (id == null || id.trim().isEmpty()) return;
            
            String firstName = JOptionPane.showInputDialog(cliniciansFrame, "First Name:");
            if (firstName == null || firstName.trim().isEmpty()) return;
            
            String lastName = JOptionPane.showInputDialog(cliniciansFrame, "Last Name:");
            if (lastName == null || lastName.trim().isEmpty()) return;
            
            String title = getInput("Title:");
            String specialty = getInput("Specialty:");
            String gmcNumber = getInput("GMC Number:");
            String phone = getInput("Phone:");
            String email = getInput("Email:");
            String workplaceId = getInput("Workplace ID:");
            String workplaceType = getInput("Workplace Type:");
            String status = getInput("Employment Status:");
            String startDate = getInput("Start Date (YYYY-MM-DD):");
            
            cliniciansFrame.getTableModel().addRow(new Object[]{id, firstName, lastName, title, 
                specialty, gmcNumber, phone, email, workplaceId, workplaceType, status, startDate});
            JOptionPane.showMessageDialog(cliniciansFrame, "Clinician added. Click Save to persist.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Error: " + ex.getMessage());
        }
    }

    private void editClinicianDialog() {
        int row = cliniciansFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Please select a clinician to edit");
            return;
        }
        
        try {
            DefaultTableModel model = cliniciansFrame.getTableModel();
            String[] names = {"ID", "First Name", "Last Name", "Title", "Speciality", "GMC Number", 
                "Phone", "Email", "Workplace ID", "Workplace Type", "Employment Status", "Start Date"};
            
            for (int i = 0; i < model.getColumnCount(); i++) {
                String current = String.valueOf(model.getValueAt(row, i));
                String newValue = JOptionPane.showInputDialog(cliniciansFrame, names[i] + ":", current);
                if (newValue != null) {
                    model.setValueAt(newValue, row, i);
                }
            }
            JOptionPane.showMessageDialog(cliniciansFrame, "Clinician updated. Click Save to persist.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Error: " + ex.getMessage());
        }
    }

    private void deleteClinicianRecord() {
        int row = cliniciansFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Please select a clinician to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(cliniciansFrame, "Delete this clinician?");
        if (confirm == 0) {
            cliniciansFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(cliniciansFrame, "Clinician deleted. Click Save to persist.");
        }
    }

    private void saveCliniciansData() {
        try {
            String dir = System.getProperty("user.dir");
            File file = dir.endsWith("src") ? new File(dir + "/../clinicians.csv") : new File("clinicians.csv");
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(String.join(",", CLINICIAN_HEADERS));
                
                DefaultTableModel model = cliniciansFrame.getTableModel();
                for (int row = 0; row < model.getRowCount(); row++) {
                    String[] data = new String[CLINICIAN_HEADERS.length];
                    for (int col = 0; col < data.length; col++) {
                        Object value = model.getValueAt(row, col);
                        data[col] = csvEscape(value == null ? "" : value.toString());
                    }
                    writer.println(String.join(",", data));
                }
            }
            JOptionPane.showMessageDialog(cliniciansFrame, "Saved to " + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Error saving: " + ex.getMessage());
        }
    }

    private void loadClinicians() {
        String[] paths = {"clinicians.csv", "src/clinicians.csv", "../clinicians.csv"};
        
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists() || file.length() < 100) continue;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String headerLine = reader.readLine();
                if (headerLine == null || !headerLine.toLowerCase().contains("clinician")) continue;
                
                String line;
                while ((line = reader.readLine()) != null) {
                    List dataList = parseCsvLine(line);
                    if (dataList.size() >= CLINICIAN_HEADERS.length) {
                        Object[] data = dataList.toArray(new Object[0]);
                        cliniciansFrame.getTableModel().addRow(data);
                    }
                }
                System.out.println("Clinicians loaded from: " + file.getAbsolutePath());
                return;
            } catch (Exception ex) {
                System.out.println("Error loading clinicians: " + ex.getMessage());
            }
        }
        System.out.println("clinicians.csv not found");
    }

   private void openAppointmentsWindow() {
      if (this.appointmentsFrame == null || !this.appointmentsFrame.isVisible()) {
         this.appointmentsFrame = new AppointmentsFrame();
         this.loadAppointments();
         this.setupAppointmentsListeners();
         this.appointmentsFrame.setVisible(true);
      }

   }

    private void setupAppointmentsListeners() {
        appointmentsFrame.getAddBtn().addActionListener(e -> addAppointmentDialog());
        appointmentsFrame.getEditBtn().addActionListener(e -> editAppointmentDialog());
        appointmentsFrame.getDeleteBtn().addActionListener(e -> deleteAppointmentRecord());
        appointmentsFrame.getSaveBtn().addActionListener(e -> saveAppointmentsData());
        appointmentsFrame.getCloseBtn().addActionListener(e -> appointmentsFrame.dispose());
    }

    private void addAppointmentDialog() {
        try {
            String id = JOptionPane.showInputDialog(appointmentsFrame, "Appointment ID:");
            if (id == null || id.trim().isEmpty()) return;
            
            String patientId = JOptionPane.showInputDialog(appointmentsFrame, "Patient ID:");
            if (patientId == null || patientId.trim().isEmpty()) return;
            
            String clinicianId = JOptionPane.showInputDialog(appointmentsFrame, "Clinician ID:");
            if (clinicianId == null || clinicianId.trim().isEmpty()) return;
            
            String date = getInput("Date (YYYY-MM-DD):");
            String time = getInput("Time (HH:MM):");
            String status = getInput("Status:");
            String duration = getInput("Duration (minutes):");
            String reason = getInput("Reason:");
            String location = getInput("Location:");
            String notes = getInput("Notes:");
            
            appointmentsFrame.getTableModel().addRow(new Object[]{id, patientId, clinicianId, date, 
                time, status, duration, reason, location, notes});
            JOptionPane.showMessageDialog(appointmentsFrame, "Appointment added. Click Save to persist.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(appointmentsFrame, "Error: " + ex.getMessage());
        }
    }

   private void editAppointmentDialog() {
      int var1 = this.appointmentsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.appointmentsFrame, "Please select a row to edit");
      } else {
         try {
            DefaultTableModel model = this.appointmentsFrame.getTableModel();
            String[] columnNames = {"ID", "Patient ID", "Clinician ID", "Facility ID", "Date", "Time", "Duration", "Type", "Status", "Reason", "Notes", "Created Date", "Last Modified"};
            
            // Edit each column
            for (int i = 0; i < model.getColumnCount(); i++) {
               String currentValue = String.valueOf(model.getValueAt(var1, i));
               String newValue = JOptionPane.showInputDialog(this.appointmentsFrame, columnNames[i] + ":", currentValue);
               if (newValue != null) {
                  model.setValueAt(newValue, var1, i);
               }
            }
            
            JOptionPane.showMessageDialog(this.appointmentsFrame, "Row updated. Click Save to persist changes.");
         } catch (Exception var4) {
            JOptionPane.showMessageDialog(this.appointmentsFrame, "Error: " + var4.getMessage());
         }

      }
   }

   private void deleteAppointmentRecord() {
      int var1 = this.appointmentsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.appointmentsFrame, "Please select a row to delete");
      } else {
         this.appointmentsFrame.getTableModel().removeRow(var1);
         JOptionPane.showMessageDialog(this.appointmentsFrame, "Row deleted. Click Save to persist.");
      }
   }

   private void saveAppointmentsData() {
      try {
         String var1 = System.getProperty("user.dir");
         File var2 = !var1.endsWith("src") && !var1.endsWith("src\\") && !var1.endsWith("src/") ? new File("appointments.csv") : new File(var1 + "/../appointments.csv");
         PrintWriter var3 = new PrintWriter(new BufferedWriter(new FileWriter(var2, false)));

         try {
            var3.println(String.join(",", APPOINTMENT_HEADERS));

            for(int var4 = 0; var4 < this.appointmentsFrame.getTableModel().getRowCount(); ++var4) {
               String[] var5 = new String[APPOINTMENT_HEADERS.length];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  Object var7 = this.appointmentsFrame.getTableModel().getValueAt(var4, var6);
                  var5[var6] = this.csvEscape(var7 == null ? "" : var7.toString());
               }

               var3.println(String.join(",", var5));
            }
         } catch (Throwable var9) {
            try {
               var3.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var3.close();
         JOptionPane.showMessageDialog(this.appointmentsFrame, "Appointments data saved to " + var2.getAbsolutePath());
      } catch (Exception var10) {
         JOptionPane.showMessageDialog(this.appointmentsFrame, "Error saving: " + var10.getMessage());
      }

   }

   private void loadAppointments() {
      String var1 = System.getProperty("user.dir");
      ArrayList var2 = new ArrayList();
      if (var1.endsWith("src") || var1.endsWith("src\\") || var1.endsWith("src/")) {
         var2.add("../appointments.csv");
         var2.add(var1 + "/../appointments.csv");
      }

      var2.add("appointments.csv");
      var2.add("../appointments.csv");
      var2.add(var1 + "/appointments.csv");
      var2.add(var1 + "/../appointments.csv");
      Iterator var3 = var2.iterator();

      while(true) {
         File var5;
         do {
            do {
               if (!var3.hasNext()) {
                  System.out.println("appointments.csv not found");
                  return;
               }

               String var4 = (String)var3.next();
               var5 = new File(var4);
            } while(!var5.exists());
         } while(var5.length() <= 100L);

         try {
            BufferedReader var6 = new BufferedReader(new FileReader(var5));

            label64: {
               try {
                  String var7 = var6.readLine();
                  if (var7 != null && var7.toLowerCase().contains("appointment_id")) {
                     String var8;
                     while((var8 = var6.readLine()) != null) {
                        String[] var9 = var8.split(",");
                        if (var9.length >= 6) {
                           this.appointmentsFrame.getTableModel().addRow(var9);
                        }
                     }

                     System.out.println("Appointments loaded from: " + var5.getAbsolutePath());
                     break label64;
                  }
               } catch (Throwable var11) {
                  try {
                     var6.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               var6.close();
               continue;
            }

            var6.close();
            return;
         } catch (Exception var12) {
            PrintStream var10000 = System.out;
            String var10001 = var5.getAbsolutePath();
            var10000.println("Error loading appointments from " + var10001 + ": " + var12.getMessage());
         }
      }
   }

   private void openPrescriptionsWindow() {
      if (this.prescriptionsFrame == null || !this.prescriptionsFrame.isVisible()) {
         this.prescriptionsFrame = new PrescriptionsFrame();
         this.loadPrescriptions();
         this.setupPrescriptionsListeners();
         this.prescriptionsFrame.setVisible(true);
      }

   }

   private void setupPrescriptionsListeners() {
      this.prescriptionsFrame.getAddBtn().addActionListener(e -> {
         this.addPrescriptionDialog();
      });
      this.prescriptionsFrame.getEditBtn().addActionListener(e -> {
         this.editPrescriptionDialog();
      });
      this.prescriptionsFrame.getDeleteBtn().addActionListener(e -> {
         this.deletePrescriptionRecord();
      });
      this.prescriptionsFrame.getGenerateOutputBtn().addActionListener(e -> {
         this.generatePrescriptionOutput();
      });
      this.prescriptionsFrame.getSaveBtn().addActionListener(e -> {
         this.savePrescriptionsData();
      });
      this.prescriptionsFrame.getCloseBtn().addActionListener(e -> {
         this.prescriptionsFrame.dispose();
      });
   }

   private void addPrescriptionDialog() {
      try {
         String var1 = JOptionPane.showInputDialog(this.prescriptionsFrame, "Prescription ID:");
         if (var1 != null && !var1.trim().isEmpty()) {
            String var2 = JOptionPane.showInputDialog(this.prescriptionsFrame, "Patient ID:");
            if (var2 != null && !var2.trim().isEmpty()) {
               String var3 = JOptionPane.showInputDialog(this.prescriptionsFrame, "Medication:");
               if (var3 != null && !var3.trim().isEmpty()) {
                  String var4 = JOptionPane.showInputDialog(this.prescriptionsFrame, "Dosage:");
                  if (var4 == null) {
                     var4 = "";
                  }

                  String var5 = JOptionPane.showInputDialog(this.prescriptionsFrame, "Frequency:");
                  if (var5 == null) {
                     var5 = "";
                  }

                  String var6 = JOptionPane.showInputDialog(this.prescriptionsFrame, "Start Date (YYYY-MM-DD):");
                  if (var6 == null) {
                     var6 = "";
                  }

                  String var7 = JOptionPane.showInputDialog(this.prescriptionsFrame, "End Date (YYYY-MM-DD):");
                  if (var7 == null) {
                     var7 = "";
                  }

                  this.prescriptionsFrame.getTableModel().addRow(new Object[]{var1, var2, var3, var4, var5, var6, var7});
                  JOptionPane.showMessageDialog(this.prescriptionsFrame, "Prescription added. Click Save to persist.");
               }
            }
         }
      } catch (Exception var8) {
         JOptionPane.showMessageDialog(this.prescriptionsFrame, "Error: " + var8.getMessage());
      }
   }

   private void editPrescriptionDialog() {
      int var1 = this.prescriptionsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.prescriptionsFrame, "Please select a row to edit");
      } else {
         try {
            DefaultTableModel model = this.prescriptionsFrame.getTableModel();
            String[] columnNames = {"ID", "Patient ID", "Clinician ID", "Appointment ID", "Prescription Date", "Medication", "Dosage", "Frequency", "Duration Days", "Quantity", "Instructions", "Pharmacy", "Status", "Issue Date", "Collection Date"};
            
            // Edit each column
            for (int i = 0; i < model.getColumnCount(); i++) {
               String currentValue = String.valueOf(model.getValueAt(var1, i));
               String newValue = JOptionPane.showInputDialog(this.prescriptionsFrame, columnNames[i] + ":", currentValue);
               if (newValue != null) {
                  model.setValueAt(newValue, var1, i);
               }
            }
            
            JOptionPane.showMessageDialog(this.prescriptionsFrame, "Prescription updated. Click Save to persist changes.");
         } catch (Exception var3) {
            JOptionPane.showMessageDialog(this.prescriptionsFrame, "Error: " + var3.getMessage());
         }

      }
   }

   private void deletePrescriptionRecord() {
      int var1 = this.prescriptionsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.prescriptionsFrame, "Please select a row to delete");
      } else {
         this.prescriptionsFrame.getTableModel().removeRow(var1);
         JOptionPane.showMessageDialog(this.prescriptionsFrame, "Row deleted. Click Save to persist.");
      }
   }

   private void savePrescriptionsData() {
      try {
         String var1 = System.getProperty("user.dir");
         File var2 = !var1.endsWith("src") && !var1.endsWith("src\\") && !var1.endsWith("src/") ? new File("prescriptions.csv") : new File(var1 + "/../prescriptions.csv");
         PrintWriter var3 = new PrintWriter(new BufferedWriter(new FileWriter(var2, false)));

         try {
            var3.println(String.join(",", PRESCRIPTION_HEADERS));

            for(int var4 = 0; var4 < this.prescriptionsFrame.getTableModel().getRowCount(); ++var4) {
               String[] var5 = new String[PRESCRIPTION_HEADERS.length];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  Object var7 = this.prescriptionsFrame.getTableModel().getValueAt(var4, var6);
                  var5[var6] = this.csvEscape(var7 == null ? "" : var7.toString());
               }

               var3.println(String.join(",", var5));
            }
         } catch (Throwable var9) {
            try {
               var3.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var3.close();
         JOptionPane.showMessageDialog(this.prescriptionsFrame, "Prescriptions data saved to " + var2.getAbsolutePath());
      } catch (Exception var10) {
         JOptionPane.showMessageDialog(this.prescriptionsFrame, "Error saving: " + var10.getMessage());
      }

   }

   private void generatePrescriptionOutput() {
      int var1 = this.prescriptionsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.prescriptionsFrame, "Please select a prescription to generate output");
      } else {
         try {
            String var2 = System.getProperty("user.dir");
            File var3 = !var2.endsWith("src") && !var2.endsWith("src\\") && !var2.endsWith("src/") ? new File("prescriptions_output.txt") : new File(var2 + "/../prescriptions_output.txt");
            PrintWriter var4 = new PrintWriter(new BufferedWriter(new FileWriter(var3, true)));

            try {
               var4.println("-------------------------------------");
               var4.println("    PRESCRIPTION FORM");
               var4.println("--------------------------------------");
               var4.println();
               String var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 0));
               var4.println("PRESCRIPTION ID: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 1));
               var4.println("PATIENT ID: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 2));
               var4.println("CLINICIAN ID: " + var10001);
               var4.println();
               var4.println("....... PRESCRIPTION DETAILS.........");
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 4));
               var4.println("Prescription Date: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 5));
               var4.println("Medication: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 6));
               var4.println("Dosage: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 7));
               var4.println("Frequency: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 8));
               var4.println("Duration (Days): " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 9));
               var4.println("Quantity: " + var10001);
               var4.println();
               var4.println("--- INSTRUCTIONS ---");
               var4.println(this.prescriptionsFrame.getTableModel().getValueAt(var1, 10));
               var4.println();
               var4.println("--- PHARMACY INFORMATION ---");
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 11));
               var4.println("Pharmacy: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 12));
               var4.println("Status: " + var10001);
               var4.println();
               var4.println("--- DISPENSING DETAILS ---");
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 13));
               var4.println("Issue Date: " + var10001);
               var10001 = String.valueOf(this.prescriptionsFrame.getTableModel().getValueAt(var1, 14));
               var4.println("Collection Date: " + var10001);
               var4.println();
               var4.println("-------------------------------------");
               var4.println();
               var4.println();
            } catch (Throwable var8) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            var4.close();
            JOptionPane.showMessageDialog(this.prescriptionsFrame, "Prescription output generated successfully!\nFile: " + var3.getAbsolutePath());
         } catch (Exception var9) {
            JOptionPane.showMessageDialog(this.prescriptionsFrame, "Error generating output: " + var9.getMessage());
         }

      }
   }

   private void loadPrescriptions() {
      String var1 = System.getProperty("user.dir");
      ArrayList var2 = new ArrayList();
      if (var1.endsWith("src") || var1.endsWith("src\\") || var1.endsWith("src/")) {
         var2.add("../prescriptions.csv");
         var2.add(var1 + "/../prescriptions.csv");
      }

      var2.add("prescriptions.csv");
      var2.add("../prescriptions.csv");
      var2.add(var1 + "/prescriptions.csv");
      var2.add(var1 + "/../prescriptions.csv");
      Iterator var3 = var2.iterator();

      while(true) {
         File var5;
         do {
            do {
               if (!var3.hasNext()) {
                  System.out.println("prescriptions.csv not found");
                  return;
               }

               String var4 = (String)var3.next();
               var5 = new File(var4);
            } while(!var5.exists());
         } while(var5.length() <= 100L);

         try {
            BufferedReader var6 = new BufferedReader(new FileReader(var5));

            label64: {
               try {
                  String var7 = var6.readLine();
                  if (var7 != null && var7.toLowerCase().contains("prescription_id")) {
                     String var8;
                     while((var8 = var6.readLine()) != null) {
                        String[] var9 = var8.split(",");
                        if (var9.length >= 7) {
                           this.prescriptionsFrame.getTableModel().addRow(var9);
                        }
                     }

                     System.out.println("Prescriptions loaded from: " + var5.getAbsolutePath());
                     break label64;
                  }
               } catch (Throwable var11) {
                  try {
                     var6.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               var6.close();
               continue;
            }

            var6.close();
            return;
         } catch (Exception var12) {
            PrintStream var10000 = System.out;
            String var10001 = var5.getAbsolutePath();
            var10000.println("Error loading prescriptions from " + var10001 + ": " + var12.getMessage());
         }
      }
   }

   private void openStaffWindow() {
      if (this.staffFrame == null || !this.staffFrame.isVisible()) {
         this.staffFrame = new StaffFrame();
         this.loadStaff();
         this.setupStaffListeners();
         this.staffFrame.setVisible(true);
      }

   }

   private void setupStaffListeners() {
      this.staffFrame.getAddBtn().addActionListener(e -> {
         this.addStaffDialog();
      });
      this.staffFrame.getEditBtn().addActionListener(e -> {
         this.editStaffDialog();
      });
      this.staffFrame.getDeleteBtn().addActionListener(e -> {
         this.deleteStaffRecord();
      });
      this.staffFrame.getSaveBtn().addActionListener(e -> {
         this.saveStaffData();
      });
      this.staffFrame.getCloseBtn().addActionListener(e -> {
         this.staffFrame.dispose();
      });
   }

   private void addStaffDialog() {
      try {
         String var1 = JOptionPane.showInputDialog(this.staffFrame, "Staff ID:");
         if (var1 != null && !var1.trim().isEmpty()) {
            String var2 = JOptionPane.showInputDialog(this.staffFrame, "First Name:");
            if (var2 != null && !var2.trim().isEmpty()) {
               String var3 = JOptionPane.showInputDialog(this.staffFrame, "Last Name:");
               if (var3 != null && !var3.trim().isEmpty()) {
                  String var4 = JOptionPane.showInputDialog(this.staffFrame, "Position:");
                  if (var4 == null) {
                     var4 = "";
                  }

                  String var5 = JOptionPane.showInputDialog(this.staffFrame, "Phone:");
                  if (var5 == null) {
                     var5 = "";
                  }

                  String var6 = JOptionPane.showInputDialog(this.staffFrame, "Email:");
                  if (var6 == null) {
                     var6 = "";
                  }

                  this.staffFrame.getTableModel().addRow(new Object[]{var1, var2, var3, var4, var5, var6});
                  JOptionPane.showMessageDialog(this.staffFrame, "Staff added. Click Save to persist.");
               }
            }
         }
      } catch (Exception var7) {
         JOptionPane.showMessageDialog(this.staffFrame, "Error: " + var7.getMessage());
      }
   }

   private void editStaffDialog() {
      int var1 = this.staffFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.staffFrame, "Please select a row to edit");
      } else {
         try {
            DefaultTableModel model = this.staffFrame.getTableModel();
            String[] columnNames = {"ID", "First Name", "Last Name", "Role", "Department", "Facility ID", "Phone", "Email", "Employment Status", "Start Date", "Line Manager", "Access Level"};
            
            // Edit each column
            for (int i = 0; i < model.getColumnCount(); i++) {
               String currentValue = String.valueOf(model.getValueAt(var1, i));
               String newValue = JOptionPane.showInputDialog(this.staffFrame, columnNames[i] + ":", currentValue);
               if (newValue != null) {
                  model.setValueAt(newValue, var1, i);
               }
            }
            
            JOptionPane.showMessageDialog(this.staffFrame, "Staff updated. Click Save to persist changes.");
         } catch (Exception var4) {
            JOptionPane.showMessageDialog(this.staffFrame, "Error: " + var4.getMessage());
         }

      }
   }

   private void deleteStaffRecord() {
      int var1 = this.staffFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.staffFrame, "Please select a row to delete");
      } else {
         this.staffFrame.getTableModel().removeRow(var1);
         JOptionPane.showMessageDialog(this.staffFrame, "Row deleted. Click Save to persist.");
      }
   }

   private void saveStaffData() {
      try {
         String var1 = System.getProperty("user.dir");
         File var2 = !var1.endsWith("src") && !var1.endsWith("src\\") && !var1.endsWith("src/") ? new File("staff.csv") : new File(var1 + "/../staff.csv");
         PrintWriter var3 = new PrintWriter(new BufferedWriter(new FileWriter(var2, false)));

         try {
            var3.println(String.join(",", STAFF_HEADERS));

            for(int var4 = 0; var4 < this.staffFrame.getTableModel().getRowCount(); ++var4) {
               String[] var5 = new String[STAFF_HEADERS.length];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  Object var7 = this.staffFrame.getTableModel().getValueAt(var4, var6);
                  var5[var6] = this.csvEscape(var7 == null ? "" : var7.toString());
               }

               var3.println(String.join(",", var5));
            }
         } catch (Throwable var9) {
            try {
               var3.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var3.close();
         JOptionPane.showMessageDialog(this.staffFrame, "Staff data saved to " + var2.getAbsolutePath());
      } catch (Exception var10) {
         JOptionPane.showMessageDialog(this.staffFrame, "Error saving: " + var10.getMessage());
      }

   }

   private void loadStaff() {
      String var1 = System.getProperty("user.dir");
      ArrayList var2 = new ArrayList();
      if (var1.endsWith("src") || var1.endsWith("src\\") || var1.endsWith("src/")) {
         var2.add("../staff.csv");
         var2.add(var1 + "/../staff.csv");
      }

      var2.add("staff.csv");
      var2.add("../staff.csv");
      var2.add(var1 + "/staff.csv");
      var2.add(var1 + "/../staff.csv");
      Iterator var3 = var2.iterator();

      while(true) {
         File var5;
         do {
            do {
               if (!var3.hasNext()) {
                  System.out.println("staff.csv not found");
                  return;
               }

               String var4 = (String)var3.next();
               var5 = new File(var4);
            } while(!var5.exists());
         } while(var5.length() <= 100L);

         try {
            BufferedReader var6 = new BufferedReader(new FileReader(var5));

            label64: {
               try {
                  String var7 = var6.readLine();
                  if (var7 != null && var7.toLowerCase().contains("staff_id")) {
                     String var8;
                     while((var8 = var6.readLine()) != null) {
                        String[] var9 = var8.split(",");
                        if (var9.length >= 6) {
                           this.staffFrame.getTableModel().addRow(var9);
                        }
                     }

                     System.out.println("Staff loaded from: " + var5.getAbsolutePath());
                     break label64;
                  }
               } catch (Throwable var11) {
                  try {
                     var6.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               var6.close();
               continue;
            }

            var6.close();
            return;
         } catch (Exception var12) {
            PrintStream var10000 = System.out;
            String var10001 = var5.getAbsolutePath();
            var10000.println("Error loading staff from " + var10001 + ": " + var12.getMessage());
         }
      }
   }

   private void openReferralsWindow() {
      if (this.referralsFrame == null || !this.referralsFrame.isVisible()) {
         this.referralsFrame = new ReferralsFrame();
         this.loadReferrals();
         this.setupReferralsListeners();
         this.referralsFrame.setVisible(true);
      }

   }

   private void setupReferralsListeners() {
      this.referralsFrame.getAddBtn().addActionListener(e -> {
         this.addReferralDialog();
      });
      this.referralsFrame.getEditBtn().addActionListener(e -> {
         this.editReferralDialog();
      });
      this.referralsFrame.getDeleteBtn().addActionListener(e -> {
         this.deleteReferralRecord();
      });
      this.referralsFrame.getGenerateOutputBtn().addActionListener(e -> {
         this.generateReferralOutput();
      });
      this.referralsFrame.getSaveBtn().addActionListener(e -> {
         this.saveReferralsData();
      });
      this.referralsFrame.getCloseBtn().addActionListener(e -> {
         this.referralsFrame.dispose();
      });
   }

   private void addReferralDialog() {
      try {
         String var1 = JOptionPane.showInputDialog(this.referralsFrame, "Patient ID:");
         if (var1 != null && !var1.trim().isEmpty()) {
            String var2 = JOptionPane.showInputDialog(this.referralsFrame, "Referring Clinician ID:");
            if (var2 != null && !var2.trim().isEmpty()) {
               String var3 = JOptionPane.showInputDialog(this.referralsFrame, "Referred To Clinician ID:");
               if (var3 != null && !var3.trim().isEmpty()) {
                  String var4 = JOptionPane.showInputDialog(this.referralsFrame, "Referring Facility ID:");
                  if (var4 == null) {
                     var4 = "";
                  }

                  String var5 = JOptionPane.showInputDialog(this.referralsFrame, "Referred To Facility ID:");
                  if (var5 == null) {
                     var5 = "";
                  }

                  String var6 = JOptionPane.showInputDialog(this.referralsFrame, "Urgency Level (Low/Medium/High/Urgent):");
                  if (var6 == null) {
                     var6 = "Medium";
                  }

                  String var7 = JOptionPane.showInputDialog(this.referralsFrame, "Referral Reason:");
                  if (var7 == null) {
                     var7 = "";
                  }

                  String var8 = JOptionPane.showInputDialog(this.referralsFrame, "Clinical Summary:");
                  if (var8 == null) {
                     var8 = "";
                  }

                  String var9 = JOptionPane.showInputDialog(this.referralsFrame, "Requested Investigations:");
                  if (var9 == null) {
                     var9 = "";
                  }

                  String var10 = JOptionPane.showInputDialog(this.referralsFrame, "Notes:");
                  if (var10 == null) {
                     var10 = "";
                  }

                  String var11 = this.getNextReferralId();
                  String var12 = LocalDate.now().toString();
                  String var13 = LocalDateTime.now().toString();
                  this.referralsFrame.getTableModel().addRow(new Object[]{var11, var1, var2, var3, var4, var5, var12, var6, var7, var8, var9, "Pending", "", var10, var13, var13});
                  JOptionPane.showMessageDialog(this.referralsFrame, "Referral added. Click Save to persist.");
               }
            }
         }
      } catch (Exception var14) {
         JOptionPane.showMessageDialog(this.referralsFrame, "Error: " + var14.getMessage());
      }
   }

   private void editReferralDialog() {
      int var1 = this.referralsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.referralsFrame, "Please select a referral to edit");
      } else {
         try {
            DefaultTableModel model = this.referralsFrame.getTableModel();
            String[] columnNames = {"Referral ID", "Patient ID", "Referring Clinician", "Referred To", "Referring Facility", "Referred To Facility", "Date", "Urgency", "Reason", "Clinical Summary", "Investigations", "Status", "Appointment ID", "Notes", "Created", "Last Updated"};
            
            // Edit each column
            for (int i = 0; i < model.getColumnCount(); i++) {
               String currentValue = String.valueOf(model.getValueAt(var1, i));
               String newValue = JOptionPane.showInputDialog(this.referralsFrame, columnNames[i] + ":", currentValue);
               if (newValue != null) {
                  model.setValueAt(newValue, var1, i);
               }
            }
            
            // Update last modified timestamp
            model.setValueAt(LocalDateTime.now().toString(), var1, 15);
            JOptionPane.showMessageDialog(this.referralsFrame, "Referral updated. Click Save to persist changes.");
         } catch (Exception var5) {
            JOptionPane.showMessageDialog(this.referralsFrame, "Error: " + var5.getMessage());
         }

      }
   }

   private void deleteReferralRecord() {
      int var1 = this.referralsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.referralsFrame, "Please select a referral to delete");
      } else {
         int var2 = JOptionPane.showConfirmDialog(this.referralsFrame, "Are you sure you want to delete this referral?");
         if (var2 == 0) {
            this.referralsFrame.getTableModel().removeRow(var1);
            JOptionPane.showMessageDialog(this.referralsFrame, "Referral deleted. Click Save to persist.");
         }

      }
   }

   private void saveReferralsData() {
      try {
         String var1 = System.getProperty("user.dir");
         File var2 = !var1.endsWith("src") && !var1.endsWith("src\\") && !var1.endsWith("src/") ? new File("referrals.csv") : new File(var1 + "/../referrals.csv");
         PrintWriter var3 = new PrintWriter(new BufferedWriter(new FileWriter(var2, false)));

         try {
            var3.println(String.join(",", REFERRAL_HEADERS));

            for(int var4 = 0; var4 < this.referralsFrame.getTableModel().getRowCount(); ++var4) {
               String[] var5 = new String[REFERRAL_HEADERS.length];

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  Object var7 = this.referralsFrame.getTableModel().getValueAt(var4, var6);
                  var5[var6] = this.csvEscape(var7 == null ? "" : var7.toString());
               }

               var3.println(String.join(",", var5));
            }
         } catch (Throwable var9) {
            try {
               var3.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var3.close();
         JOptionPane.showMessageDialog(this.referralsFrame, "Referrals data saved to:\n" + var2.getAbsolutePath());
      } catch (Exception var10) {
         JOptionPane.showMessageDialog(this.referralsFrame, "Error saving: " + var10.getMessage());
      }

   }

   private void loadReferrals() {
      String var1 = System.getProperty("user.dir");
      ArrayList var2 = new ArrayList();
      if (var1.endsWith("src") || var1.endsWith("src\\") || var1.endsWith("src/")) {
         var2.add("../referrals.csv");
         var2.add(var1 + "/../referrals.csv");
      }

      var2.add("referrals.csv");
      var2.add("../referrals.csv");
      var2.add(var1 + "/referrals.csv");
      var2.add(var1 + "/../referrals.csv");
      Iterator var3 = var2.iterator();

      while(true) {
         File var5;
         do {
            do {
               if (!var3.hasNext()) {
                  System.out.println("referrals.csv not found in known locations");
                  return;
               }

               String var4 = (String)var3.next();
               var5 = new File(var4);
            } while(!var5.exists());
         } while(var5.length() <= 100L);

         try {
            BufferedReader var6 = new BufferedReader(new FileReader(var5));

            label83: {
               try {
                  String var7 = var6.readLine();
                  if (var7 == null) {
                     break label83;
                  }

                  this.referralsFrame.getTableModel().setRowCount(0);
                  int var11 = 0;

                  while(true) {
                     String var10;
                     if ((var10 = var6.readLine()) == null) {
                        System.out.println("Referrals loaded successfully - " + var11 + " records");
                        break;
                     }

                     List var12 = this.parseCsvLine(var10);
                     if (!var12.isEmpty()) {
                        Object[] var13 = new Object[REFERRAL_HEADERS.length];

                        for(int var14 = 0; var14 < REFERRAL_HEADERS.length; ++var14) {
                           var13[var14] = var14 < var12.size() ? ((String)var12.get(var14)).trim() : "";
                        }

                        this.referralsFrame.getTableModel().addRow(var13);
                        ++var11;
                     }
                  }
               } catch (Throwable var16) {
                  try {
                     var6.close();
                  } catch (Throwable var15) {
                     var16.addSuppressed(var15);
                  }

                  throw var16;
               }

               var6.close();
               return;
            }

            var6.close();
         } catch (Exception var17) {
            PrintStream var10000 = System.out;
            String var10001 = var5.getAbsolutePath();
            var10000.println("Error reading " + var10001 + ": " + var17.getMessage());
         }
      }
   }

   private void generateReferralOutput() {
      int var1 = this.referralsFrame.getSelectedRow();
      if (var1 == -1) {
         JOptionPane.showMessageDialog(this.referralsFrame, "Please select a referral to generate output");
      } else {
         try {
            String var2 = System.getProperty("user.dir");
            File var3 = !var2.endsWith("src") && !var2.endsWith("src\\") && !var2.endsWith("src/") ? new File("referrals_output.txt") : new File(var2 + "/../referrals_output.txt");
            PrintWriter var4 = new PrintWriter(new BufferedWriter(new FileWriter(var3, true)));

            try {
               var4.println("=====================================");
               var4.println("    NHS REFERRAL FORM");
               var4.println("=====================================");
               var4.println();
               String var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 0));
               var4.println("REFERRAL ID: " + var10001);
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 1));
               var4.println("PATIENT ID: " + var10001);
               var4.println();
               var4.println("--- CLINICAL PATHWAY INFORMATION ---");
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 6));
               var4.println("Referral Date: " + var10001);
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 7));
               var4.println("Urgency Level: " + var10001);
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 11));
               var4.println("Status: " + var10001);
               var4.println();
               var4.println("--- REFERRING PROVIDER ---");
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 2));
               var4.println("Clinician ID: " + var10001);
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 4));
               var4.println("Facility ID: " + var10001);
               var4.println();
               var4.println("--- REFERRED TO PROVIDER ---");
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 3));
               var4.println("Clinician ID: " + var10001);
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 5));
               var4.println("Facility ID: " + var10001);
               var4.println();
               var4.println("--- CLINICAL INFORMATION ---");
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 8));
               var4.println("Reason for Referral: " + var10001);
               var4.println();
               var4.println("Clinical Summary: ");
               var4.println(this.referralsFrame.getTableModel().getValueAt(var1, 9));
               var4.println();
               var4.println("Requested Investigations: ");
               var4.println(this.referralsFrame.getTableModel().getValueAt(var1, 10));
               var4.println();
               var4.println("--- APPOINTMENT DETAILS ---");
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 12));
               var4.println("Appointment ID: " + var10001);
               var4.println();
               var4.println("--- ADDITIONAL NOTES ---");
               var4.println(this.referralsFrame.getTableModel().getValueAt(var1, 13));
               var4.println();
               var4.println("--- RECORD DETAILS ---");
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 14));
               var4.println("Created Date: " + var10001);
               var10001 = String.valueOf(this.referralsFrame.getTableModel().getValueAt(var1, 15));
               var4.println("Last Updated: " + var10001);
               var4.println();
               var4.println("=====================================");
               var4.println();
               var4.println();
            } catch (Throwable var8) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            var4.close();
            JOptionPane.showMessageDialog(this.referralsFrame, "Referral output generated successfully to:\n" + var3.getAbsolutePath());
         } catch (Exception var9) {
            JOptionPane.showMessageDialog(this.referralsFrame, "Error generating output: " + var9.getMessage());
         }

      }
   }

   private String getNextReferralId() {
      String[] var1 = new String[]{"referrals.csv", "src/referrals.csv", "../referrals.csv"};
      int var2 = 0;
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         File var7 = new File(var6);
         if (var7.exists()) {
            try {
               BufferedReader var8 = new BufferedReader(new FileReader(var7));

               try {
                  var8.readLine();

                  String var9;
                  while((var9 = var8.readLine()) != null) {
                     String[] var10 = var9.split(",");
                     if (var10.length > 0) {
                        String var11 = var10[0].trim();
                        if (var11.startsWith("REF")) {
                           try {
                              int var12 = Integer.parseInt(var11.substring(3));
                              if (var12 > var2) {
                                 var2 = var12;
                              }
                           } catch (Exception var13) {
                           }
                        }
                     }
                  }
               } catch (Throwable var15) {
                  try {
                     var8.close();
                  } catch (Throwable var14) {
                     var15.addSuppressed(var14);
                  }

                  throw var15;
               }

               var8.close();
            } catch (Exception var16) {
            }
         }
      }

      int var17 = var2 + 1;
      return String.format("REF%03d", var17);
   }

   private List<String> parseCsvLine(String var1) {
      ArrayList var2 = new ArrayList();
      StringBuilder var3 = new StringBuilder();
      boolean var4 = false;

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         char var6 = var1.charAt(var5);
         if (var6 == '"') {
            if (var4 && var5 + 1 < var1.length() && var1.charAt(var5 + 1) == '"') {
               var3.append('"');
               ++var5;
            } else {
               var4 = !var4;
            }
         } else if (var6 == ',' && !var4) {
            var2.add(var3.toString().trim());
            var3.setLength(0);
         } else {
            var3.append(var6);
         }
      }

      var2.add(var3.toString().trim());
      return var2;
   }
}

package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.PatientFrame;

public class PatientController {
    
    private final PatientFrame patientFrame;
    
    public PatientController(PatientFrame frame) {
        this.patientFrame = frame;
        setupListeners();
        loadPatients();
    }
    
    // Setup button listeners
    private void setupListeners() {
        patientFrame.getAddBtn().addActionListener(e -> addPatient());
        patientFrame.getEditBtn().addActionListener(e -> editPatient());
        patientFrame.getDeleteBtn().addActionListener(e -> deletePatient());
        patientFrame.getSaveBtn().addActionListener(e -> savePatients());
        patientFrame.getCloseBtn().addActionListener(e -> patientFrame.dispose());
    }
    
    // Load patients from CSV file
    private void loadPatients() {
        File file = new File("patients.csv");
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Skip header
            
            patientFrame.getTableModel().setRowCount(0);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                patientFrame.getTableModel().addRow(data);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(patientFrame, "Cannot load patients");
        }
    }
    
    // Add new patient
    private void addPatient() {
        String firstName = JOptionPane.showInputDialog(patientFrame, "First name:");
        if (firstName == null || firstName.trim().isEmpty()) return;
        
        String lastName = JOptionPane.showInputDialog(patientFrame, "Last name:");
        if (lastName == null || lastName.trim().isEmpty()) return;
        
        String dob = JOptionPane.showInputDialog(patientFrame, "Date of Birth (YYYY-MM-DD):");
        String nhsNumber = JOptionPane.showInputDialog(patientFrame, "NHS Number:");
        String gender = JOptionPane.showInputDialog(patientFrame, "Gender:");
        String phone = JOptionPane.showInputDialog(patientFrame, "Phone:");
        String email = JOptionPane.showInputDialog(patientFrame, "Email:");
        String address = JOptionPane.showInputDialog(patientFrame, "Address:");
        String postcode = JOptionPane.showInputDialog(patientFrame, "Postcode:");
        String emergencyContact = JOptionPane.showInputDialog(patientFrame, "Emergency Contact:");
        String emergencyPhone = JOptionPane.showInputDialog(patientFrame, "Emergency Phone:");
        String gpSurgeryId = JOptionPane.showInputDialog(patientFrame, "GP Surgery ID:");
        
        String patientId = "P" + (patientFrame.getTableModel().getRowCount() + 1);
        String regDate = LocalDate.now().toString();
        
        Object[] row = {patientId, firstName, lastName, dob, nhsNumber, gender, 
                       phone, email, address, postcode, emergencyContact, 
                       emergencyPhone, regDate, gpSurgeryId};
        
        patientFrame.getTableModel().addRow(row);
        JOptionPane.showMessageDialog(patientFrame, "Patient added! Click Save to save.");
    }
    
    // Edit selected patient
    private void editPatient() {
        int row = patientFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(patientFrame, "Please select a patient");
            return;
        }
        
        DefaultTableModel model = patientFrame.getTableModel();
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            String current = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(patientFrame, 
                "Edit " + model.getColumnName(i) + ":", current);
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }
        
        JOptionPane.showMessageDialog(patientFrame, "Patient updated! Click Save to save.");
    }
    
    // Delete selected patient
    private void deletePatient() {
        int row = patientFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(patientFrame, "Please select a patient");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(patientFrame, "Delete this patient?");
        if (confirm == 0) {
            patientFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(patientFrame, "Patient deleted! Click Save to save.");
        }
    }
    
    // Save patients to CSV file
    private void savePatients() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("patients.csv"))) {
            
            // Write header
            writer.println("patient_id,first_name,last_name,date_of_birth,nhs_number,gender,phone_number,email,address,postcode,emergency_contact_name,emergency_contact_phone,registration_date,gp_surgery_id");
            
            // Write data
            DefaultTableModel model = patientFrame.getTableModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String[] data = new String[14];
                for (int col = 0; col < 14; col++) {
                    Object value = model.getValueAt(row, col);
                    data[col] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }
            
            JOptionPane.showMessageDialog(patientFrame, "Patients saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(patientFrame, "Cannot save patients");
        }
    }
}

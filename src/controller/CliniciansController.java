package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.CliniciansFrame;

public class CliniciansController {
    
    private CliniciansFrame cliniciansFrame;
    
    public CliniciansController(CliniciansFrame frame) {
        this.cliniciansFrame = frame;
        setupListeners();
        loadClinicians();
    }
    
    // Setup button listeners
    private void setupListeners() {
        cliniciansFrame.getAddBtn().addActionListener(e -> addClinician());
        cliniciansFrame.getEditBtn().addActionListener(e -> editClinician());
        cliniciansFrame.getDeleteBtn().addActionListener(e -> deleteClinician());
        cliniciansFrame.getSaveBtn().addActionListener(e -> saveClinicians());
        cliniciansFrame.getCloseBtn().addActionListener(e -> cliniciansFrame.dispose());
    }
    
    // Load clinicians from CSV file
    private void loadClinicians() {
        File file = new File("clinicians.csv");
        if (!file.exists()) {
            return;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine(); // Skip header
            
            cliniciansFrame.getTableModel().setRowCount(0);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                cliniciansFrame.getTableModel().addRow(data);
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Cannot load clinicians");
        }
    }
    
    // Add new clinician
    private void addClinician() {
        String firstName = JOptionPane.showInputDialog(cliniciansFrame, "First name:");
        if (firstName == null || firstName.trim().isEmpty()) return;
        
        String lastName = JOptionPane.showInputDialog(cliniciansFrame, "Last name:");
        if (lastName == null || lastName.trim().isEmpty()) return;
        
        String title = JOptionPane.showInputDialog(cliniciansFrame, "Title (Dr/Mr/Mrs/Ms):");
        String speciality = JOptionPane.showInputDialog(cliniciansFrame, "Speciality:");
        String gmcNumber = JOptionPane.showInputDialog(cliniciansFrame, "GMC Number:");
        String phone = JOptionPane.showInputDialog(cliniciansFrame, "Phone:");
        String email = JOptionPane.showInputDialog(cliniciansFrame, "Email:");
        String workplaceId = JOptionPane.showInputDialog(cliniciansFrame, "Workplace ID:");
        String workplaceType = JOptionPane.showInputDialog(cliniciansFrame, "Workplace Type:");
        String employmentStatus = JOptionPane.showInputDialog(cliniciansFrame, "Employment Status:");
        
        String clinicianId = "C" + (cliniciansFrame.getTableModel().getRowCount() + 1);
        String startDate = LocalDate.now().toString();
        
        Object[] row = {clinicianId, firstName, lastName, title, speciality, gmcNumber,
                       phone, email, workplaceId, workplaceType, employmentStatus, startDate};
        
        cliniciansFrame.getTableModel().addRow(row);
        JOptionPane.showMessageDialog(cliniciansFrame, "Clinician added! Click Save to save.");
    }
    
    // Edit selected clinician
    private void editClinician() {
        int row = cliniciansFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Please select a clinician");
            return;
        }
        
        DefaultTableModel model = cliniciansFrame.getTableModel();
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            String current = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(cliniciansFrame, 
                "Edit " + model.getColumnName(i) + ":", current);
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }
        
        JOptionPane.showMessageDialog(cliniciansFrame, "Clinician updated! Click Save to save.");
    }
    
    // Delete selected clinician
    private void deleteClinician() {
        int row = cliniciansFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Please select a clinician");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(cliniciansFrame, "Delete this clinician?");
        if (confirm == 0) {
            cliniciansFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(cliniciansFrame, "Clinician deleted! Click Save to save.");
        }
    }
    
    // Save clinicians to CSV file
    private void saveClinicians() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("clinicians.csv"));
            
            // Write header
            writer.println("clinician_id,first_name,last_name,title,speciality,gmc_number,phone_number,email,workplace_id,workplace_type,employment_status,start_date");
            
            // Write data
            DefaultTableModel model = cliniciansFrame.getTableModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String[] data = new String[12];
                for (int col = 0; col < 12; col++) {
                    Object value = model.getValueAt(row, col);
                    data[col] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }
            
            writer.close();
            JOptionPane.showMessageDialog(cliniciansFrame, "Clinicians saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(cliniciansFrame, "Cannot save clinicians");
        }
    }
}

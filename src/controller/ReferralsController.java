package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.ReferralsFrame;

public class ReferralsController {
    
    private ReferralsFrame referralsFrame;
    
    public ReferralsController(ReferralsFrame frame) {
        this.referralsFrame = frame;
        setupListeners();
        loadReferrals();
    }
    
    // Setup button listeners
    private void setupListeners() {
        referralsFrame.getAddBtn().addActionListener(e -> addReferral());
        referralsFrame.getEditBtn().addActionListener(e -> editReferral());
        referralsFrame.getDeleteBtn().addActionListener(e -> deleteReferral());
        referralsFrame.getSaveBtn().addActionListener(e -> saveReferrals());
        referralsFrame.getCloseBtn().addActionListener(e -> referralsFrame.dispose());
    }
    
    // Load referrals from CSV file
    private void loadReferrals() {
        File file = new File("referrals.csv");
        if (!file.exists()) {
            return;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine(); // Skip header
            
            referralsFrame.getTableModel().setRowCount(0);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                referralsFrame.getTableModel().addRow(data);
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(referralsFrame, "Cannot load referrals");
        }
    }
    
    // Add new referral
    private void addReferral() {
        String patientId = JOptionPane.showInputDialog(referralsFrame, "Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;
        
        String referringClinicianId = JOptionPane.showInputDialog(referralsFrame, "Referring Clinician ID:");
        if (referringClinicianId == null || referringClinicianId.trim().isEmpty()) return;
        
        String referredToClinicianId = JOptionPane.showInputDialog(referralsFrame, "Referred To Clinician ID:");
        String referringFacilityId = JOptionPane.showInputDialog(referralsFrame, "Referring Facility ID:");
        String referredToFacilityId = JOptionPane.showInputDialog(referralsFrame, "Referred To Facility ID:");
        String urgencyLevel = JOptionPane.showInputDialog(referralsFrame, "Urgency Level:");
        String referralReason = JOptionPane.showInputDialog(referralsFrame, "Referral Reason:");
        String clinicalSummary = JOptionPane.showInputDialog(referralsFrame, "Clinical Summary:");
        String requestedInvestigations = JOptionPane.showInputDialog(referralsFrame, "Requested Investigations:");
        String status = JOptionPane.showInputDialog(referralsFrame, "Status:");
        String appointmentId = JOptionPane.showInputDialog(referralsFrame, "Appointment ID:");
        String notes = JOptionPane.showInputDialog(referralsFrame, "Notes:");
        
        String referralId = "R" + (referralsFrame.getTableModel().getRowCount() + 1);
        String referralDate = LocalDate.now().toString();
        String createdDate = referralDate;
        String lastUpdated = referralDate;
        
        Object[] row = {referralId, patientId, referringClinicianId, referredToClinicianId,
                       referringFacilityId, referredToFacilityId, referralDate, urgencyLevel,
                       referralReason, clinicalSummary, requestedInvestigations, status,
                       appointmentId, notes, createdDate, lastUpdated};
        
        referralsFrame.getTableModel().addRow(row);
        JOptionPane.showMessageDialog(referralsFrame, "Referral added! Click Save to save.");
    }
    
    // Edit selected referral
    private void editReferral() {
        int row = referralsFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(referralsFrame, "Please select a referral");
            return;
        }
        
        DefaultTableModel model = referralsFrame.getTableModel();
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            String current = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(referralsFrame, 
                "Edit " + model.getColumnName(i) + ":", current);
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }
        
        JOptionPane.showMessageDialog(referralsFrame, "Referral updated! Click Save to save.");
    }
    
    // Delete selected referral
    private void deleteReferral() {
        int row = referralsFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(referralsFrame, "Please select a referral");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(referralsFrame, "Delete this referral?");
        if (confirm == 0) {
            referralsFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(referralsFrame, "Referral deleted! Click Save to save.");
        }
    }
    
    // Save referrals to CSV file
    private void saveReferrals() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("referrals.csv"));
            
            // Write header
            writer.println("referral_id,patient_id,referring_clinician_id,referred_to_clinician_id,referring_facility_id,referred_to_facility_id,referral_date,urgency_level,referral_reason,clinical_summary,requested_investigations,status,appointment_id,notes,created_date,last_updated");
            
            // Write data
            DefaultTableModel model = referralsFrame.getTableModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String[] data = new String[16];
                for (int col = 0; col < 16; col++) {
                    Object value = model.getValueAt(row, col);
                    data[col] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }
            
            writer.close();
            JOptionPane.showMessageDialog(referralsFrame, "Referrals saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(referralsFrame, "Cannot save referrals");
        }
    }
}

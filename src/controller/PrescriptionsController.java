package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.PrescriptionsFrame;

public class PrescriptionsController {
    
    private PrescriptionsFrame prescriptionsFrame;
    
    public PrescriptionsController(PrescriptionsFrame frame) {
        this.prescriptionsFrame = frame;
        setupListeners();
        loadPrescriptions();
    }
    
    // Setup button listeners
    private void setupListeners() {
        prescriptionsFrame.getAddBtn().addActionListener(e -> addPrescription());
        prescriptionsFrame.getEditBtn().addActionListener(e -> editPrescription());
        prescriptionsFrame.getDeleteBtn().addActionListener(e -> deletePrescription());
        prescriptionsFrame.getSaveBtn().addActionListener(e -> savePrescriptions());
        prescriptionsFrame.getCloseBtn().addActionListener(e -> prescriptionsFrame.dispose());
    }
    
    // Load prescriptions from CSV file
    private void loadPrescriptions() {
        File file = new File("prescriptions.csv");
        if (!file.exists()) {
            return;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine(); // Skip header
            
            prescriptionsFrame.getTableModel().setRowCount(0);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                prescriptionsFrame.getTableModel().addRow(data);
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(prescriptionsFrame, "Cannot load prescriptions");
        }
    }
    
    // Add new prescription
    private void addPrescription() {
        String patientId = JOptionPane.showInputDialog(prescriptionsFrame, "Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;
        
        String clinicianId = JOptionPane.showInputDialog(prescriptionsFrame, "Clinician ID:");
        if (clinicianId == null || clinicianId.trim().isEmpty()) return;
        
        String appointmentId = JOptionPane.showInputDialog(prescriptionsFrame, "Appointment ID:");
        String medicationName = JOptionPane.showInputDialog(prescriptionsFrame, "Medication Name:");
        String dosage = JOptionPane.showInputDialog(prescriptionsFrame, "Dosage:");
        String frequency = JOptionPane.showInputDialog(prescriptionsFrame, "Frequency:");
        String durationDays = JOptionPane.showInputDialog(prescriptionsFrame, "Duration (days):");
        String quantity = JOptionPane.showInputDialog(prescriptionsFrame, "Quantity:");
        String instructions = JOptionPane.showInputDialog(prescriptionsFrame, "Instructions:");
        String pharmacyName = JOptionPane.showInputDialog(prescriptionsFrame, "Pharmacy Name:");
        String status = JOptionPane.showInputDialog(prescriptionsFrame, "Status:");
        
        String prescriptionId = "RX" + (prescriptionsFrame.getTableModel().getRowCount() + 1);
        String prescriptionDate = LocalDate.now().toString();
        String issueDate = prescriptionDate;
        String collectionDate = "";
        
        Object[] row = {prescriptionId, patientId, clinicianId, appointmentId, prescriptionDate,
                       medicationName, dosage, frequency, durationDays, quantity, instructions,
                       pharmacyName, status, issueDate, collectionDate};
        
        prescriptionsFrame.getTableModel().addRow(row);
        JOptionPane.showMessageDialog(prescriptionsFrame, "Prescription added! Click Save to save.");
    }
    
    // Edit selected prescription
    private void editPrescription() {
        int row = prescriptionsFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(prescriptionsFrame, "Please select a prescription");
            return;
        }
        
        DefaultTableModel model = prescriptionsFrame.getTableModel();
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            String current = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(prescriptionsFrame, 
                "Edit " + model.getColumnName(i) + ":", current);
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }
        
        JOptionPane.showMessageDialog(prescriptionsFrame, "Prescription updated! Click Save to save.");
    }
    
    // Delete selected prescription
    private void deletePrescription() {
        int row = prescriptionsFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(prescriptionsFrame, "Please select a prescription");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(prescriptionsFrame, "Delete this prescription?");
        if (confirm == 0) {
            prescriptionsFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(prescriptionsFrame, "Prescription deleted! Click Save to save.");
        }
    }
    
    // Save prescriptions to CSV file
    private void savePrescriptions() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("prescriptions.csv"));
            
            // Write header
            writer.println("prescription_id,patient_id,clinician_id,appointment_id,prescription_date,medication_name,dosage,frequency,duration_days,quantity,instructions,pharmacy_name,status,issue_date,collection_date");
            
            // Write data
            DefaultTableModel model = prescriptionsFrame.getTableModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String[] data = new String[15];
                for (int col = 0; col < 15; col++) {
                    Object value = model.getValueAt(row, col);
                    data[col] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }
            
            writer.close();
            JOptionPane.showMessageDialog(prescriptionsFrame, "Prescriptions saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(prescriptionsFrame, "Cannot save prescriptions");
        }
    }
}

package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.AppointmentsFrame;

public class AppointmentsController {
    
    private AppointmentsFrame appointmentsFrame;
    
    public AppointmentsController(AppointmentsFrame frame) {
        this.appointmentsFrame = frame;
        setupListeners();
        loadAppointments();
    }
    
    // Setup button listeners
    private void setupListeners() {
        appointmentsFrame.getAddBtn().addActionListener(e -> addAppointment());
        appointmentsFrame.getEditBtn().addActionListener(e -> editAppointment());
        appointmentsFrame.getDeleteBtn().addActionListener(e -> deleteAppointment());
        appointmentsFrame.getSaveBtn().addActionListener(e -> saveAppointments());
        appointmentsFrame.getCloseBtn().addActionListener(e -> appointmentsFrame.dispose());
    }
    
    // Load appointments from CSV file
    private void loadAppointments() {
        File file = new File("appointments.csv");
        if (!file.exists()) {
            return;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine(); // Skip header
            
            appointmentsFrame.getTableModel().setRowCount(0);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                appointmentsFrame.getTableModel().addRow(data);
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(appointmentsFrame, "Cannot load appointments");
        }
    }
    
    // Add new appointment
    private void addAppointment() {
        String patientId = JOptionPane.showInputDialog(appointmentsFrame, "Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;
        
        String clinicianId = JOptionPane.showInputDialog(appointmentsFrame, "Clinician ID:");
        if (clinicianId == null || clinicianId.trim().isEmpty()) return;
        
        String facilityId = JOptionPane.showInputDialog(appointmentsFrame, "Facility ID:");
        String appointmentDate = JOptionPane.showInputDialog(appointmentsFrame, "Date (YYYY-MM-DD):");
        String appointmentTime = JOptionPane.showInputDialog(appointmentsFrame, "Time (HH:MM):");
        String duration = JOptionPane.showInputDialog(appointmentsFrame, "Duration (minutes):");
        String appointmentType = JOptionPane.showInputDialog(appointmentsFrame, "Type:");
        String status = JOptionPane.showInputDialog(appointmentsFrame, "Status:");
        String reason = JOptionPane.showInputDialog(appointmentsFrame, "Reason:");
        String notes = JOptionPane.showInputDialog(appointmentsFrame, "Notes:");
        
        String appointmentId = "A" + (appointmentsFrame.getTableModel().getRowCount() + 1);
        String createdDate = LocalDate.now().toString();
        String lastModified = createdDate;
        
        Object[] row = {appointmentId, patientId, clinicianId, facilityId, appointmentDate, 
                       appointmentTime, duration, appointmentType, status, reason, notes, 
                       createdDate, lastModified};
        
        appointmentsFrame.getTableModel().addRow(row);
        JOptionPane.showMessageDialog(appointmentsFrame, "Appointment added! Click Save to save.");
    }
    
    // Edit selected appointment
    private void editAppointment() {
        int row = appointmentsFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(appointmentsFrame, "Please select an appointment");
            return;
        }
        
        DefaultTableModel model = appointmentsFrame.getTableModel();
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            String current = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(appointmentsFrame, 
                "Edit " + model.getColumnName(i) + ":", current);
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }
        
        JOptionPane.showMessageDialog(appointmentsFrame, "Appointment updated! Click Save to save.");
    }
    
    // Delete selected appointment
    private void deleteAppointment() {
        int row = appointmentsFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(appointmentsFrame, "Please select an appointment");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(appointmentsFrame, "Delete this appointment?");
        if (confirm == 0) {
            appointmentsFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(appointmentsFrame, "Appointment deleted! Click Save to save.");
        }
    }
    
    // Save appointments to CSV file
    private void saveAppointments() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("appointments.csv"));
            
            // Write header
            writer.println("appointment_id,patient_id,clinician_id,facility_id,appointment_date,appointment_time,duration_minutes,appointment_type,status,reason_for_visit,notes,created_date,last_modified");
            
            // Write data
            DefaultTableModel model = appointmentsFrame.getTableModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String[] data = new String[13];
                for (int col = 0; col < 13; col++) {
                    Object value = model.getValueAt(row, col);
                    data[col] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }
            
            writer.close();
            JOptionPane.showMessageDialog(appointmentsFrame, "Appointments saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(appointmentsFrame, "Cannot save appointments");
        }
    }
}

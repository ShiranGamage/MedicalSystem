package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.StaffFrame;

public class StaffController {
    
    private StaffFrame staffFrame;
    
    public StaffController(StaffFrame frame) {
        this.staffFrame = frame;
        setupListeners();
        loadStaff();
    }
    
    // Setup button listeners
    private void setupListeners() {
        staffFrame.getAddBtn().addActionListener(e -> addStaff());
        staffFrame.getEditBtn().addActionListener(e -> editStaff());
        staffFrame.getDeleteBtn().addActionListener(e -> deleteStaff());
        staffFrame.getSaveBtn().addActionListener(e -> saveStaff());
        staffFrame.getCloseBtn().addActionListener(e -> staffFrame.dispose());
    }
    
    // Load staff from CSV file
    private void loadStaff() {
        File file = new File("staff.csv");
        if (!file.exists()) {
            return;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine(); // Skip header
            
            staffFrame.getTableModel().setRowCount(0);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                staffFrame.getTableModel().addRow(data);
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(staffFrame, "Cannot load staff");
        }
    }
    
    // Add new staff
    private void addStaff() {
        String firstName = JOptionPane.showInputDialog(staffFrame, "First name:");
        if (firstName == null || firstName.trim().isEmpty()) return;
        
        String lastName = JOptionPane.showInputDialog(staffFrame, "Last name:");
        if (lastName == null || lastName.trim().isEmpty()) return;
        
        String role = JOptionPane.showInputDialog(staffFrame, "Role:");
        String department = JOptionPane.showInputDialog(staffFrame, "Department:");
        String facilityId = JOptionPane.showInputDialog(staffFrame, "Facility ID:");
        String phone = JOptionPane.showInputDialog(staffFrame, "Phone:");
        String email = JOptionPane.showInputDialog(staffFrame, "Email:");
        String employmentStatus = JOptionPane.showInputDialog(staffFrame, "Employment Status:");
        String lineManager = JOptionPane.showInputDialog(staffFrame, "Line Manager:");
        String accessLevel = JOptionPane.showInputDialog(staffFrame, "Access Level:");
        
        String staffId = "S" + (staffFrame.getTableModel().getRowCount() + 1);
        String startDate = LocalDate.now().toString();
        
        Object[] row = {staffId, firstName, lastName, role, department, facilityId,
                       phone, email, employmentStatus, startDate, lineManager, accessLevel};
        
        staffFrame.getTableModel().addRow(row);
        JOptionPane.showMessageDialog(staffFrame, "Staff added! Click Save to save.");
    }
    
    // Edit selected staff
    private void editStaff() {
        int row = staffFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(staffFrame, "Please select a staff member");
            return;
        }
        
        DefaultTableModel model = staffFrame.getTableModel();
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            String current = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(staffFrame, 
                "Edit " + model.getColumnName(i) + ":", current);
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }
        
        JOptionPane.showMessageDialog(staffFrame, "Staff updated! Click Save to save.");
    }
    
    // Delete selected staff
    private void deleteStaff() {
        int row = staffFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(staffFrame, "Please select a staff member");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(staffFrame, "Delete this staff member?");
        if (confirm == 0) {
            staffFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(staffFrame, "Staff deleted! Click Save to save.");
        }
    }
    
    // Save staff to CSV file
    private void saveStaff() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("staff.csv"));
            
            // Write header
            writer.println("staff_id,first_name,last_name,role,department,facility_id,phone_number,email,employment_status,start_date,line_manager,access_level");
            
            // Write data
            DefaultTableModel model = staffFrame.getTableModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String[] data = new String[12];
                for (int col = 0; col < 12; col++) {
                    Object value = model.getValueAt(row, col);
                    data[col] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }
            
            writer.close();
            JOptionPane.showMessageDialog(staffFrame, "Staff saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(staffFrame, "Cannot save staff");
        }
    }
}

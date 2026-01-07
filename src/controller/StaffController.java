package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.StaffFrame;

public class StaffController {

    private StaffFrame frame;

    public StaffController(StaffFrame frame) {
        this.frame = frame;

        frame.getAddBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStaff();
            }
        });

        frame.getEditBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editStaff();
            }
        });

        frame.getDeleteBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStaff();
            }
        });

        frame.getSaveBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveStaff();
            }
        });

        frame.getCloseBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        loadStaff();
    }

    // ---------------- Load Staff ----------------

    private void loadStaff() {
        File file = new File("staff.csv");
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();

            DefaultTableModel model = frame.getTableModel();
            model.setRowCount(0);

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                model.addRow(data);
            }

            br.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Cannot load staff");
        }
    }

    // ---------------- Add Staff ----------------

    private void addStaff() {
        String firstName = JOptionPane.showInputDialog(frame, "First name:");
        if (firstName == null || firstName.trim().isEmpty()) return;

        String lastName = JOptionPane.showInputDialog(frame, "Last name:");
        if (lastName == null || lastName.trim().isEmpty()) return;

        String role = JOptionPane.showInputDialog(frame, "Role:");
        String department = JOptionPane.showInputDialog(frame, "Department:");
        String facilityId = JOptionPane.showInputDialog(frame, "Facility ID:");
        String phone = JOptionPane.showInputDialog(frame, "Phone:");
        String email = JOptionPane.showInputDialog(frame, "Email:");
        String employmentStatus = JOptionPane.showInputDialog(frame, "Employment Status:");
        String lineManager = JOptionPane.showInputDialog(frame, "Line Manager:");
        String accessLevel = JOptionPane.showInputDialog(frame, "Access Level:");

        DefaultTableModel model = frame.getTableModel();
        String staffId = "ST" + (model.getRowCount() + 1);
        String startDate = LocalDate.now().toString();

        Object[] row = {
            staffId, firstName, lastName, role, department, facilityId,
            phone, email, employmentStatus, startDate, lineManager, accessLevel
        };

        model.addRow(row);
        JOptionPane.showMessageDialog(frame, "Staff added! Click Save.");
    }

    // ---------------- Edit Staff ----------------

    private void editStaff() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a staff member");
            return;
        }

        DefaultTableModel model = frame.getTableModel();

        for (int i = 0; i < model.getColumnCount(); i++) {
            String oldValue = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(frame, "Edit " + model.getColumnName(i), oldValue);
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }

        JOptionPane.showMessageDialog(frame, "Staff updated! Click Save.");
    }

    // ---------------- Delete Staff ----------------

    private void deleteStaff() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a staff member");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this staff member?");
        if (confirm == JOptionPane.YES_OPTION) {
            frame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(frame, "Staff deleted! Click Save.");
        }
    }

    // ---------------- Save Staff ----------------

    private void saveStaff() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("staff.csv"));

            writer.println(
                "staff_id,first_name,last_name,role,department,facility_id,phone_number," +
                "email,employment_status,start_date,line_manager,access_level"
            );

            DefaultTableModel model = frame.getTableModel();

            for (int r = 0; r < model.getRowCount(); r++) {
                String[] data = new String[12];
                for (int c = 0; c < 12; c++) {
                    Object value = model.getValueAt(r, c);
                    data[c] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }

            writer.close();
            JOptionPane.showMessageDialog(frame, "Staff saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Cannot save staff");
        }
    }
}

package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.AppointmentsFrame;

public class AppointmentsController {

    private AppointmentsFrame myFrame;
    private MedicalController medicalController;

    public AppointmentsController(AppointmentsFrame myFrame) {
        this(myFrame, null);
    }

    public AppointmentsController(AppointmentsFrame myFrame, MedicalController medicalController) {
        
        this.myFrame = myFrame;
        this.medicalController = medicalController;
        myFrame.getAddBtn().addActionListener(e -> addAppointment());
        myFrame.getEditBtn().addActionListener(e -> editAppointment());
        myFrame.getDeleteBtn().addActionListener(e -> deleteAppointment());
        myFrame.getSaveBtn().addActionListener(e -> saveAppointments());
        myFrame.getCloseBtn().addActionListener(e -> myFrame.dispose());

        loadAppointmentsInfo();
    }

    // ---------------- Load ----------------

    private void loadAppointmentsInfo() {
        
        File file = new File("appointments.csv");
        
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            DefaultTableModel model = myFrame.getTableModel();
            model.setRowCount(0);

            String txtLine;
            while ((txtLine = reader.readLine()) != null) {
                model.addRow(txtLine.split(",", -1));
            }
            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(myFrame, "Cannot load appointments");
        }
    }

    // ---------------- Add ----------------

    private void addAppointment() {
        String patientId = JOptionPane.showInputDialog(myFrame, "Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;

        String clinicianId = JOptionPane.showInputDialog(myFrame, "Clinician ID:");
        if (clinicianId == null || clinicianId.trim().isEmpty()) return;

        String facilityId = JOptionPane.showInputDialog(myFrame, "Facility ID:");
        String date = JOptionPane.showInputDialog(myFrame, "Date (YYYY-MM-DD):");
        String time = JOptionPane.showInputDialog(myFrame, "Time (HH:MM):");
        String duration = JOptionPane.showInputDialog(myFrame, "Duration (minutes):");
        String type = JOptionPane.showInputDialog(myFrame, "Type:");
        String status = JOptionPane.showInputDialog(myFrame, "Status:");
        String reason = JOptionPane.showInputDialog(myFrame, "Reason:");
        String notes = JOptionPane.showInputDialog(myFrame, "Notes:");

        DefaultTableModel model = myFrame.getTableModel();
        String today = LocalDate.now().toString();

        // Auto-generate appointment ID by comparing with last ID
        int newIdNumber = 1;
        if (model.getRowCount() > 0) {
            String lastId = String.valueOf(model.getValueAt(model.getRowCount() - 1, 0));
            if (lastId.startsWith("A")) {
                try {
                    newIdNumber = Integer.parseInt(lastId.substring(1)) + 1;
                } catch (NumberFormatException e) {
                    newIdNumber = model.getRowCount() + 1;
                }
            } else {
                newIdNumber = model.getRowCount() + 1;
            }
        }

        Object[] row = {
            "A0" + newIdNumber,
            patientId, clinicianId, facilityId,
            date, time, duration,
            type, status, reason, notes,
            today, today
        };

        model.addRow(row);
        JOptionPane.showMessageDialog(myFrame, "Appointment added! Click Save.");
    }

    // ---------------- Edit ----------------

    private void editAppointment() {
        int row = myFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(myFrame, "Please select an appointment");
            return;
        }

        DefaultTableModel model = myFrame.getTableModel();

        for (int col = 0; col < model.getColumnCount(); col++) {
            String oldValue = String.valueOf(model.getValueAt(row, col));
            String newValue = JOptionPane.showInputDialog(
                    myFrame,
                    "Edit " + model.getColumnName(col) + ":",
                    oldValue
            );

            if (newValue != null) {
                model.setValueAt(newValue, row, col);
            }
        }

        JOptionPane.showMessageDialog(myFrame, "Appointment updated! Click Save.");
    }

    // ---------------- Delete ----------------

    private void deleteAppointment() {
        int row = myFrame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(myFrame, "Please select an appointment");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(myFrame, "Delete this appointment?");
        if (confirm == JOptionPane.YES_OPTION) {
            myFrame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(myFrame, "Appointment deleted! Click Save.");
        }
    }

    // ---------------- Save ----------------

    private void saveAppointments() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("appointments.csv"));

            writer.println("appointment_id,patient_id,clinician_id,facility_id,"
                    + "appointment_date,appointment_time,duration_minutes,"
                    + "appointment_type,status,reason_for_visit,notes,"
                    + "created_date,last_modified");

            DefaultTableModel model = myFrame.getTableModel();

            for (int r = 0; r < model.getRowCount(); r++) {
                String[] data = new String[13];

                for (int c = 0; c < 13; c++) {
                    Object val = model.getValueAt(r, c);
                    data[c] = val == null ? "" : val.toString();
                }

                writer.println(String.join(",", data));
            }

            writer.close();
            JOptionPane.showMessageDialog(myFrame, "Appointments saved successfully!");
            
            // Refresh main window appointments table
            if (medicalController != null) {
                medicalController.refreshMainAppointments();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(myFrame, "Cannot save appointments");
        }
    }
}

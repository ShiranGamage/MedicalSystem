package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.PrescriptionsFrame;

public class PrescriptionsController {

    private PrescriptionsFrame frame;
    private java.util.Set<String> newlyAddedIds;

    public PrescriptionsController(PrescriptionsFrame frame) {
        this.frame = frame;
        this.newlyAddedIds = new java.util.HashSet<>();

        frame.getAddBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPrescription();
            }
        });

        frame.getEditBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editPrescription();
            }
        });

        frame.getDeleteBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePrescription();
            }
        });

        frame.getSaveBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePrescriptions();
            }
        });

        frame.getCloseBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        loadPrescriptions();
    }

    // ---------------- Load Prescriptions ----------------

    private void loadPrescriptions() {
        File file = new File("prescriptions.csv");
        if (!file.exists()) {
            return;
        }

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
            JOptionPane.showMessageDialog(frame, "Cannot load prescriptions");
        }
    }

    // ---------------- Add Prescription ----------------

    private void addPrescription() {
        String patientId = JOptionPane.showInputDialog(frame, "Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;

        String clinicianId = JOptionPane.showInputDialog(frame, "Clinician ID:");
        if (clinicianId == null || clinicianId.trim().isEmpty()) return;

        String appointmentId = JOptionPane.showInputDialog(frame, "Appointment ID:");
        String medicationName = JOptionPane.showInputDialog(frame, "Medication Name:");
        String dosage = JOptionPane.showInputDialog(frame, "Dosage:");
        String frequency = JOptionPane.showInputDialog(frame, "Frequency:");
        String durationDays = JOptionPane.showInputDialog(frame, "Duration (days):");
        String quantity = JOptionPane.showInputDialog(frame, "Quantity:");
        String instructions = JOptionPane.showInputDialog(frame, "Instructions:");
        String pharmacyName = JOptionPane.showInputDialog(frame, "Pharmacy Name:");
        String status = JOptionPane.showInputDialog(frame, "Status:");

        DefaultTableModel model = frame.getTableModel();
        String prescriptionId = "RX0" + (model.getRowCount() + 1);
        String prescriptionDate = LocalDate.now().toString();
        String issueDate = prescriptionDate;
        String collectionDate = "";

        Object[] row = {
            prescriptionId, patientId, clinicianId, appointmentId, prescriptionDate,
            medicationName, dosage, frequency, durationDays, quantity, instructions,
            pharmacyName, status, issueDate, collectionDate
        };

        model.addRow(row);
        newlyAddedIds.add(prescriptionId);
        
        // Auto-save after adding
        savePrescriptions();
    }

    // ---------------- Edit Prescription ----------------

    private void editPrescription() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a prescription");
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

        JOptionPane.showMessageDialog(frame, "Prescription updated! Click Save.");
    }

    // ---------------- Delete Prescription ----------------

    private void deletePrescription() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a prescription");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this prescription?");
        if (confirm == JOptionPane.YES_OPTION) {
            frame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(frame, "Prescription deleted! Click Save.");
        }
    }

    // ---------------- Save Prescriptions ----------------

    private void savePrescriptions() {
        try {
            // Save to CSV file
            PrintWriter writer = new PrintWriter(new FileWriter("prescriptions.csv"));

            writer.println(
                "prescription_id,patient_id,clinician_id,appointment_id,prescription_date," +
                "medication_name,dosage,frequency,duration_days,quantity,instructions," +
                "pharmacy_name,status,issue_date,collection_date"
            );

            DefaultTableModel model = frame.getTableModel();

            for (int r = 0; r < model.getRowCount(); r++) {
                String[] data = new String[15];
                for (int c = 0; c < 15; c++) {
                    Object value = model.getValueAt(r, c);
                    data[c] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }

            writer.close();

            // Append only newly added prescriptions to formatted text file
            PrintWriter outputWriter = new PrintWriter(new FileWriter("prescriptions_output.txt", true));

            for (int r = 0; r < model.getRowCount(); r++) {
                String prescriptionId = String.valueOf(model.getValueAt(r, 0));
                
                // Only write if this prescription was newly added in this session
                if (!newlyAddedIds.contains(prescriptionId)) {
                    continue;
                }

                outputWriter.println(".....................................");
                outputWriter.println("    PRESCRIPTION RECORD");
                outputWriter.println(".....................................");
                outputWriter.println();
                outputWriter.println("PRESCRIPTION ID: " + model.getValueAt(r, 0));
                outputWriter.println("PATIENT ID: " + model.getValueAt(r, 1));
                outputWriter.println();
                outputWriter.println("--- PRESCRIBER INFORMATION ---");
                outputWriter.println("Clinician ID: " + model.getValueAt(r, 2));
                outputWriter.println("Appointment ID: " + model.getValueAt(r, 3));
                outputWriter.println();
                outputWriter.println("--- PRESCRIPTION DETAILS ---");
                outputWriter.println("Prescription Date: " + model.getValueAt(r, 4));
                outputWriter.println("Medication: " + model.getValueAt(r, 5));
                outputWriter.println("Dosage: " + model.getValueAt(r, 6));
                outputWriter.println("Frequency: " + model.getValueAt(r, 7));
                outputWriter.println("Duration (Days): " + model.getValueAt(r, 8));
                outputWriter.println("Quantity: " + model.getValueAt(r, 9));
                outputWriter.println();
                outputWriter.println("--- INSTRUCTIONS ---");
                outputWriter.println(model.getValueAt(r, 10));
                outputWriter.println();
                outputWriter.println("--- PHARMACY & DISPENSING ---");
                outputWriter.println("Pharmacy: " + model.getValueAt(r, 11));
                outputWriter.println("Status: " + model.getValueAt(r, 12));
                outputWriter.println("Issue Date: " + model.getValueAt(r, 13));
                outputWriter.println("Collection Date: " + model.getValueAt(r, 14));
                outputWriter.println();
                outputWriter.println("-------------------------------------");
                outputWriter.println();
                outputWriter.println();
            }

            outputWriter.close();

            // Clear the newly added IDs after successful save
            newlyAddedIds.clear();

            JOptionPane.showMessageDialog(frame, "Prescriptions saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Cannot save prescriptions");
        }
    }
}

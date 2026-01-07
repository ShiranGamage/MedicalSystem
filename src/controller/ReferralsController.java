package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.ReferralsFrame;

public class ReferralsController {

    private ReferralsFrame frame;
    private java.util.Set<String> newlyAddedIds;

    public ReferralsController(ReferralsFrame frame) {
        this.frame = frame;
        this.newlyAddedIds = new java.util.HashSet<>();

        frame.getAddBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addReferral();
            }
        });

        frame.getEditBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editReferral();
            }
        });

        frame.getDeleteBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteReferral();
            }
        });

        frame.getSaveBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveReferrals();
            }
        });

        frame.getCloseBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        loadReferrals();
    }

    // ---------------- Load Referrals ----------------

    private void loadReferrals() {
        File file = new File("referrals.csv");
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine(); // skip header

            DefaultTableModel model = frame.getTableModel();
            model.setRowCount(0);

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                model.addRow(data);
            }

            br.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Cannot load referrals");
        }
    }

    // ---------------- Add Referral ----------------

    private void addReferral() {
        String patientId = JOptionPane.showInputDialog(frame, "Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) return;

        String referringClinicianId = JOptionPane.showInputDialog(frame, "Referring Clinician ID:");
        if (referringClinicianId == null || referringClinicianId.trim().isEmpty()) return;

        String referredToClinicianId = JOptionPane.showInputDialog(frame, "Referred To Clinician ID:");
        String referringFacilityId = JOptionPane.showInputDialog(frame, "Referring Facility ID:");
        String referredToFacilityId = JOptionPane.showInputDialog(frame, "Referred To Facility ID:");
        String urgencyLevel = JOptionPane.showInputDialog(frame, "Urgency Level:");
        String referralReason = JOptionPane.showInputDialog(frame, "Referral Reason:");
        String clinicalSummary = JOptionPane.showInputDialog(frame, "Clinical Summary:");
        String requestedInvestigations = JOptionPane.showInputDialog(frame, "Requested Investigations:");
        String status = JOptionPane.showInputDialog(frame, "Status:");
        String appointmentId = JOptionPane.showInputDialog(frame, "Appointment ID:");
        String notes = JOptionPane.showInputDialog(frame, "Notes:");

        DefaultTableModel model = frame.getTableModel();
        String referralId = "R0" + (model.getRowCount() + 1);
        String today = LocalDate.now().toString();

        Object[] row = {
            referralId, patientId, referringClinicianId, referredToClinicianId,
            referringFacilityId, referredToFacilityId, today, urgencyLevel,
            referralReason, clinicalSummary, requestedInvestigations, status,
            appointmentId, notes, today, today
        };

        model.addRow(row);
        newlyAddedIds.add(referralId);
        
        // Auto-save after adding
        saveReferrals();
    }

    // ---------------- Edit Referral ----------------

    private void editReferral() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a referral");
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

        JOptionPane.showMessageDialog(frame, "Referral updated! Click Save.");
    }

    // ---------------- Delete Referral ----------------

    private void deleteReferral() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a referral");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this referral?");
        if (confirm == JOptionPane.YES_OPTION) {
            frame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(frame, "Referral deleted! Click Save.");
        }
    }

    // ---------------- Save Referrals ----------------

    private void saveReferrals() {
        try {
            // Save to CSV file
            PrintWriter writer = new PrintWriter(new FileWriter("referrals.csv"));

            writer.println(
                "referral_id,patient_id,referring_clinician_id,referred_to_clinician_id," +
                "referring_facility_id,referred_to_facility_id,referral_date,urgency_level," +
                "referral_reason,clinical_summary,requested_investigations,status," +
                "appointment_id,notes,created_date,last_updated"
            );

            DefaultTableModel model = frame.getTableModel();

            for (int r = 0; r < model.getRowCount(); r++) {
                String[] data = new String[16];
                for (int c = 0; c < 16; c++) {
                    Object value = model.getValueAt(r, c);
                    data[c] = value == null ? "" : value.toString();
                }
                writer.println(String.join(",", data));
            }

            writer.close();

            // Append only newly added referrals to formatted text file
            PrintWriter outputWriter = new PrintWriter(new FileWriter("referrals_output.txt", true));

            for (int r = 0; r < model.getRowCount(); r++) {
                String referralId = String.valueOf(model.getValueAt(r, 0));
                
                // Only write if this referral was newly added in this session
                if (!newlyAddedIds.contains(referralId)) {
                    continue;
                }

                outputWriter.println(".....................................");
                outputWriter.println("    NHS REFERRAL FORM");
                outputWriter.println(".....................................");
                outputWriter.println();
                outputWriter.println("REFERRAL ID: " + model.getValueAt(r, 0));
                outputWriter.println("PATIENT ID: " + model.getValueAt(r, 1));
                outputWriter.println();
                outputWriter.println("--- CLINICAL PATHWAY INFORMATION ---");
                outputWriter.println("Referral Date: " + model.getValueAt(r, 6));
                outputWriter.println("Urgency Level: " + model.getValueAt(r, 7));
                outputWriter.println("Status: " + model.getValueAt(r, 11));
                outputWriter.println();
                outputWriter.println("--- REFERRING PROVIDER ---");
                outputWriter.println("Clinician ID: " + model.getValueAt(r, 2));
                outputWriter.println("Facility ID: " + model.getValueAt(r, 4));
                outputWriter.println();
                outputWriter.println("--- REFERRED TO PROVIDER ---");
                outputWriter.println("Clinician ID: " + model.getValueAt(r, 3));
                outputWriter.println("Facility ID: " + model.getValueAt(r, 5));
                outputWriter.println();
                outputWriter.println("--- CLINICAL INFORMATION ---");
                outputWriter.println("Reason for Referral: " + model.getValueAt(r, 8));
                outputWriter.println();
                outputWriter.println("Clinical Summary: ");
                outputWriter.println(model.getValueAt(r, 9));
                outputWriter.println();
                outputWriter.println("Requested Investigations: ");
                outputWriter.println(model.getValueAt(r, 10));
                outputWriter.println();
                outputWriter.println("--- APPOINTMENT DETAILS ---");
                outputWriter.println("Appointment ID: " + model.getValueAt(r, 12));
                outputWriter.println();
                outputWriter.println("--- ADDITIONAL NOTES ---");
                outputWriter.println(model.getValueAt(r, 13));
                outputWriter.println();
                outputWriter.println("--- RECORD DETAILS ---");
                outputWriter.println("Created Date: " + model.getValueAt(r, 14));
                outputWriter.println("Last Updated: " + model.getValueAt(r, 15));
                outputWriter.println();
                outputWriter.println("=====================================");
                outputWriter.println();
                outputWriter.println();
                outputWriter.println();
            }

            outputWriter.close();

            // Clear the newly added IDs after successful save
            newlyAddedIds.clear();

            JOptionPane.showMessageDialog(frame, "Referrals saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Cannot save referrals");
        }
    }
}

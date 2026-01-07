
package controller;

import java.io.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.CliniciansFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CliniciansController {

    private CliniciansFrame frame;

    public CliniciansController(CliniciansFrame frame) {
        this.frame = frame;

        frame.getAddBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addClinician();
            }
        });

        frame.getEditBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editClinician();
            }
        });

        frame.getDeleteBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteClinician();
            }
        });

        frame.getSaveBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveClinicians();
            }
        });

        frame.getCloseBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        loadClinicians();
    }

    // ---------------- Load Clinicians ----------------

    private void loadClinicians() {
        File file = new File("clinicians.csv");
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
                model.addRow(line.split(",", -1));
            }

            br.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Cannot load clinicians");
        }
    }

    // ---------------- Add Clinician ----------------

    private void addClinician() {
        String firstName = JOptionPane.showInputDialog(frame, "First name:");
        if (firstName == null || firstName.trim().isEmpty()) return;

        String lastName = JOptionPane.showInputDialog(frame, "Last name:");
        if (lastName == null || lastName.trim().isEmpty()) return;

        String title = JOptionPane.showInputDialog(frame, "Title:");
         String speciality = JOptionPane.showInputDialog(frame, "Speciality:");
          String gmcNumber = JOptionPane.showInputDialog(frame, "GMC Number:");
            String phone = JOptionPane.showInputDialog(frame, "Phone:");
           String email = JOptionPane.showInputDialog(frame, "Email:");
           String workplaceId = JOptionPane.showInputDialog(frame, "Workplace ID:");
         String workplaceType = JOptionPane.showInputDialog(frame, "Workplace Type:");
        String employmentStatus = JOptionPane.showInputDialog(frame, "Employment Status:");

        DefaultTableModel model = frame.getTableModel();

        String clinicianId = "C0" + (model.getRowCount() + 1);
        String startDate = LocalDate.now().toString();

        Object[] row = {
            clinicianId,
            firstName,
            lastName,
            title,
            speciality,
            gmcNumber,
            phone,
            email,
            workplaceId,
            workplaceType,
            employmentStatus,
            startDate
        };

        model.addRow(row);
        JOptionPane.showMessageDialog(frame, "Clinician added. Click Save.");
    }

    // ---------------- Edit Clinician ----------------

    private void editClinician() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a clinician");
            return;
        }

        DefaultTableModel model = frame.getTableModel();

        for (int i = 0; i < model.getColumnCount(); i++) {
            String oldValue = String.valueOf(model.getValueAt(row, i));
            String newValue = JOptionPane.showInputDialog(
                frame, 
                "Edit " + model.getColumnName(i) + ":", 
                oldValue
            );
            
            if (newValue != null) {
                model.setValueAt(newValue, row, i);
            }
        }

        JOptionPane.showMessageDialog(frame, "Clinician updated. Click Save.");
    }

    // ---------------- Delete Clinician ----------------

    private void deleteClinician() {
        int row = frame.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a clinician");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this clinician?");
        if (confirm == JOptionPane.YES_OPTION) {
            frame.getTableModel().removeRow(row);
            JOptionPane.showMessageDialog(frame, "Clinician deleted. Click Save.");
        }
    }

    // ---------------- Save Clinicians ----------------

    private void saveClinicians() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("clinicians.csv"));

            pw.println("clinician_id,first_name,last_name,title,speciality,gmc_number," +
                      "phone_number,email,workplace_id,workplace_type,employment_status,start_date");

            DefaultTableModel model = frame.getTableModel();

            for (int row = 0; row < model.getRowCount(); row++) {
                String[] data = new String[12];
                for (int col = 0; col < 12; col++) {
                    Object value = model.getValueAt(row, col);
                    data[col] = value == null ? "" : value.toString();
                }
                pw.println(String.join(",", data));
            }

            pw.close();
            JOptionPane.showMessageDialog(frame, "Clinicians saved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Cannot save clinicians");
        }
    }
}



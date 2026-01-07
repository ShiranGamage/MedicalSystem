package controller;

import java.io.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.*;

public class MedicalController {

    private MainFrame mainFrame;

    private PatientFrame patientFrame;
    private CliniciansFrame cliniciansFrame;
    private AppointmentsFrame appointmentsFrame;
    private PrescriptionsFrame prescriptionsFrame;
    private StaffFrame staffFrame;
    private ReferralsFrame referralsFrame;

    public MedicalController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        loadAppointmentsToMainTable();
        setupButtons();
    }

    // ---------------- Load Appointments ----------------

    private void loadAppointmentsToMainTable() {
        File file = new File("appointments.csv");
        if (!file.exists()) {
            System.out.println("appointments.csv not found");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.readLine();

            DefaultTableModel model = mainFrame.getAppointmentsTableModel();
            model.setRowCount(0);

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                model.addRow(data);
            }

            br.close();
            System.out.println("Appointments loaded to main window");

        } catch (Exception e) {
            System.out.println("Error loading appointments");
        }
    }

    //  method to refresh appointments table
    public void refreshMainAppointments() {
        loadAppointmentsToMainTable();
    }

    // ---------------- Buttons ----------------

    private void setupButtons() {

        mainFrame.getPatientsBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPatients();
            }
        });

        mainFrame.getCliniciansBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openClinicians();
            }
        });

        mainFrame.getAppointmentsBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAppointments();
            }
        });

        mainFrame.getPrescriptionsBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPrescriptions();
            }
        });

        mainFrame.getStaffBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openStaff();
            }
        });

        mainFrame.getReferralsBtn().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openReferrals();
            }
        });
    }

    // ---------------- Open Windows ----------------

    private void openPatients() {
        if (patientFrame == null || !patientFrame.isVisible()) {
            patientFrame = new PatientFrame();
            new PatientController(patientFrame);
            patientFrame.setVisible(true);
        } else {
            patientFrame.toFront();
        }
    }

    private void openClinicians() {
        if (cliniciansFrame == null || !cliniciansFrame.isVisible()) {
            cliniciansFrame = new CliniciansFrame();
            new CliniciansController(cliniciansFrame);
            cliniciansFrame.setVisible(true);
        } else {
            cliniciansFrame.toFront();
        }
    }

    private void openAppointments() {
        if (appointmentsFrame == null || !appointmentsFrame.isVisible()) {
            appointmentsFrame = new AppointmentsFrame();
            @SuppressWarnings("unused")
            AppointmentsController ac = new AppointmentsController(appointmentsFrame, this);
            appointmentsFrame.setVisible(true);
        } else {
            appointmentsFrame.toFront();
        }
    }

    private void openPrescriptions() {
        if (prescriptionsFrame == null || !prescriptionsFrame.isVisible()) {
            prescriptionsFrame = new PrescriptionsFrame();
            new PrescriptionsController(prescriptionsFrame);
            prescriptionsFrame.setVisible(true);
        } else {
            prescriptionsFrame.toFront();
        }
    }

    private void openStaff() {
        if (staffFrame == null || !staffFrame.isVisible()) {
            staffFrame = new StaffFrame();
            new StaffController(staffFrame);
            staffFrame.setVisible(true);
        } else {
            staffFrame.toFront();
        }
    }

    private void openReferrals() {
        if (referralsFrame == null || !referralsFrame.isVisible()) {
            referralsFrame = new ReferralsFrame();
            new ReferralsController(referralsFrame);
            referralsFrame.setVisible(true);
        } else {
            referralsFrame.toFront();
        }
    }
}

package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {
    
    // Buttons for the main frame
private JButton patientsBtn;
private JButton cliniciansBtn;
private JButton appointmentsBtn;
private JButton prescriptionsBtn;
private JButton staffBtn;
private JButton referralsBtn;
    
 
private JTable appointmentsTable;
private DefaultTableModel appointmentsTableModel;

public MainFrame() {
       
        
    setTitle("Medical Management System");
    setSize(1250, 650);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(9, 9));

        // Create buttons for window
    patientsBtn = new JButton("Patients");
    cliniciansBtn = new JButton("Clinicians");
    appointmentsBtn = new JButton("Appointments");
    prescriptionsBtn = new JButton("Prescriptions");
    staffBtn = new JButton("Staff");
    referralsBtn = new JButton("Referrals");

        // Top panel with buttons
        JPanel topPanel = new JPanel();

    topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 9, 9));
    topPanel.add(new JLabel("Modules: "));
    topPanel.add(patientsBtn);
    topPanel.add(cliniciansBtn);
    topPanel.add(appointmentsBtn);
    topPanel.add(prescriptionsBtn);
    topPanel.add(staffBtn);
    topPanel.add(referralsBtn);
    add(topPanel, BorderLayout.NORTH);

        // Center panel with appointments table
    String[] col_Headers = {"ID", "Patient", "Clinician", "Facility", "Date", "Time", "Duration", "Type", "Status", "Reason", "Notes", "Created", "Modified"};
    appointmentsTableModel = new DefaultTableModel(col_Headers, 0);
    appointmentsTable = new JTable(appointmentsTableModel);
        
        JPanel center_Panel = new JPanel(new BorderLayout());
        center_Panel.add(new JLabel("Appointments Pool:"), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        center_Panel.add(scrollPane, BorderLayout.CENTER);
        add(center_Panel, BorderLayout.CENTER);
    }
    
    // Get methods for buttons
    public JButton getPatientsBtn() { 
        return patientsBtn; 
    }
    
    public JButton getCliniciansBtn() { 
        return cliniciansBtn; 
    }
    
    public JButton getAppointmentsBtn() { 
        return appointmentsBtn; 
    }
    
    public JButton getPrescriptionsBtn() { 
        return prescriptionsBtn; 
    }
    
    public JButton getStaffBtn() { 
        return staffBtn; 
    }
    
    public JButton getReferralsBtn() { 
        return referralsBtn; 
    }
    
    // Getters for table
    public DefaultTableModel getAppointmentsTableModel() { 
        return appointmentsTableModel; 
    }
    
    public JTable getAppointmentsTable() { 
        return appointmentsTable; 
    }

    // Update table columns
    public void updateAppointmentColumns(String[] headers) {
        appointmentsTableModel.setColumnIdentifiers(headers);
    }
}
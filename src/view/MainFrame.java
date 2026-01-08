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
    setSize(1000, 500);
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
    
    // Apply modern styling to buttons
    styleButton(patientsBtn);
    styleButton(cliniciansBtn);
    styleButton(appointmentsBtn);
    styleButton(prescriptionsBtn);
    styleButton(staffBtn);
    styleButton(referralsBtn);

        // Top panel with buttons
        JPanel topPanel = new JPanel();

    topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));
    topPanel.setBackground(new Color(240, 240, 240));
    JLabel modulesLabel = new JLabel("Modules: ");
    modulesLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
    topPanel.add(modulesLabel);
    topPanel.add(patientsBtn);
    topPanel.add(cliniciansBtn);
    topPanel.add(appointmentsBtn);
    topPanel.add(prescriptionsBtn);
    
    topPanel.add(referralsBtn);
    topPanel.add(staffBtn);
    add(topPanel, BorderLayout.NORTH);

        // Center panel with appointments table
    String[] col_Headers = {"ID", "Patient", "Clinician", "Facility", "Date", "Time", "Duration", "Type", "Status", "Reason", "Notes", "Created", "Modified"};
    appointmentsTableModel = new DefaultTableModel(col_Headers, 0);
    appointmentsTable = new JTable(appointmentsTableModel);
        
        JPanel center_Panel = new JPanel(new BorderLayout());
        center_Panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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
    
    // Modern button styling
    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(135, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }
}
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Main window for appointments
public class AppointmentsFrame extends JFrame {
    

    // Buttons for the menu
    
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton saveBtn;
    private JButton exitBtn;

    private JTable appoint_Table;
    private DefaultTableModel modelNew;

    String[] col_Headers = {"ID", "Patient ID", "Clinician ID", "Facility ID", "Date", "Time", "Duration", "Type", "Status", "Reason", "Notes", "Created Date", "Last Modified"};
    

    public AppointmentsFrame() {
        // Basic Window Setup
    setTitle("Hospital Appointments Management");
    setSize(1200, 650); 
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    
    setLayout(new BorderLayout(8, 8));

     // Set up the column names for the table
    modelNew = new DefaultTableModel(col_Headers, 0);
    appoint_Table = new JTable(modelNew);
        
        // scroll panel
    
    JScrollPane scrollPane = new JScrollPane(appoint_Table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel for buttons
        
    JPanel buttonPanel = new JPanel();
    addBtn = new JButton("Add Appointment");
    editBtn = new JButton("Edit Selected");
    deleteBtn = new JButton("Remove");
    saveBtn = new JButton("Save All");
    exitBtn = new JButton("Close");

    buttonPanel.add(addBtn);
    buttonPanel.add(editBtn);
    buttonPanel.add(deleteBtn);
    buttonPanel.add(saveBtn);
    buttonPanel.add(exitBtn);

    add(buttonPanel, BorderLayout.SOUTH);
    
}

// Getters to listen for clicks
public JButton getAddBtn() { 
    return addBtn; 
}
public JButton getEditBtn() {
     return editBtn; 
}
public JButton getDeleteBtn() { 
    return deleteBtn; 
}
public JButton getSaveBtn() { 
    return saveBtn; 
}
public JButton getCloseBtn() { 
    return exitBtn; }
    
    public DefaultTableModel getModel() { 
        return modelNew; 
    }
    
    public DefaultTableModel getTableModel() { 
        return modelNew; 
    }
    
    // to get the selected row index
    public int getSelectedRow() { 
        return appoint_Table.getSelectedRow(); 
    }
}
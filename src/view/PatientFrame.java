



package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Main window for patients
public class PatientFrame extends JFrame {
    
    
    private JTable patientsTable;
    private DefaultTableModel modelNew;
    
    // Buttons for the menu
    
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton saveBtn;
    private JButton exitBtn;

    public PatientFrame() {
        // Basic Window Setup
    setTitle("Patient Management");
    setSize(1250, 650); 
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    
    setLayout(new BorderLayout(7, 7));

     // Set up the column names for the table
    String[] col_Headers = {"ID", "First Name", "Last Name", "DOB", "NHS No", "Gender", "Phone", "Email", "Address", "Postcode", "Emergency Contact", "Emergency Phone", "Registration Date", "GP Surgery ID"};
    modelNew = new DefaultTableModel(col_Headers, 0);
    patientsTable = new JTable(modelNew);
        
        // scroll panel
    
    JScrollPane scrollPane = new JScrollPane(patientsTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel for buttons
        
    JPanel buttonPanel = new JPanel();
    addBtn = new JButton("Add Patient");
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
    
    // get the table model 
    public DefaultTableModel getTableModel() {
         return modelNew; 
        }
    
    // to get the selected row index
    public int getSelectedRow() { 
        return patientsTable.getSelectedRow(); 
    }
}







package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Main window for clinicians
public class PrescriptionsFrame extends JFrame {
    
    
    private JTable PrescriptionsTable;
    private DefaultTableModel modelNew;
    
    // Buttons for the menu
    
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton saveBtn;
    private JButton exitBtn;

    public PrescriptionsFrame() {
        // Basic Window Setup
    setTitle("Prescription Management");
    setSize(1250, 650); 
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    
    setLayout(new BorderLayout(7, 7));

     // Set up the column names for the table
    String[] col_Headers = {"ID", "Patient ID", "Clinician ID", "Appointment ID", "Prescription Date", "Medication", "Dosage", "Frequency", "Duration Days", "Quantity", "Instructions", "Pharmacy", "Status", "Issue Date", "Collection Date"};
    modelNew = new DefaultTableModel(col_Headers, 0);
    PrescriptionsTable = new JTable(modelNew);
        
        // scroll panel
    
    JScrollPane scrollPane = new JScrollPane(PrescriptionsTable);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel for buttons
        
    JPanel buttonPanel = new JPanel();
    addBtn = new JButton("Add Prescription");
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
        return PrescriptionsTable.getSelectedRow(); 
    }
}















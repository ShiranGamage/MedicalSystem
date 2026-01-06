
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Main window for clinicians
public class ReferralsFrame extends JFrame {
    
    
    private JTable referralsTable;
    private DefaultTableModel modelNew;
    private DefaultTableModel tableModel;
    
    // Buttons for the menu
    
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton saveBtn;
    private JButton exitBtn;

    public ReferralsFrame() {
        // Basic Window Setup
    setTitle("Referral Management");
    setSize(1250, 650); 
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    
    setLayout(new BorderLayout(7, 7));

     // Set up the column names for the table
    String[] col_Headers = {"Referral ID", "Patient ID", "Referring Clinician", "Referred To", "Referring Facility", 
                         "Referred To Facility", "Date", "Urgency", "Reason", "Clinical Summary", 
                         "Investigations", "Status", "Appointment ID", "Notes", "Created", "Last Updated"};
    modelNew = new DefaultTableModel(col_Headers, 0);
    referralsTable = new JTable(modelNew);
        
        // scroll panel
    
    JScrollPane scrollPane = new JScrollPane(referralsTable);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel for buttons
        
    JPanel buttonPanel = new JPanel();
    addBtn = new JButton("Add Referral");
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
    return exitBtn; 
}
    
public DefaultTableModel getModel() { 
    return modelNew; 
}
    
// get the table model 
public DefaultTableModel getTableModel() {
    return modelNew; 
}
    
// to get the selected row index
public int getSelectedRow() { 
    return referralsTable.getSelectedRow(); 
}




}


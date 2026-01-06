


package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Main window for clinicians
public class StaffFrame extends JFrame {
    
    
    private JTable staffTable;
    private DefaultTableModel modelNew;
    
    // Buttons for the menu
    
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton saveBtn;
    private JButton exitBtn;

    public StaffFrame() {
        // Basic Window Setup
    setTitle("Staff Management");
    setSize(1250, 650); 
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    
    setLayout(new BorderLayout(7, 7));

     // Set up the column names for the table
    String[] col_Headers = {"ID", "First Name", "Last Name", "Role", "Department", "Facility ID", "Phone", "Email", "Employment Status", "Start Date", "Line Manager", "Access Level"};
    modelNew = new DefaultTableModel(col_Headers, 0);
    staffTable = new JTable(modelNew);
        
        // scroll panel
    
    JScrollPane scrollPane = new JScrollPane(staffTable);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel for buttons
        
    JPanel buttonPanel = new JPanel();
    addBtn = new JButton("Add Staff");
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
        return staffTable.getSelectedRow(); 
    }
}


















/* 
package view;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class StaffFrame extends JFrame {
    private final JTable staffTable;
    private final DefaultTableModel tableModel;
    private final JButton addBtn = new JButton("Add Staff");
    private final JButton editBtn = new JButton("Edit");
    private final JButton deleteBtn = new JButton("Delete");
    private final JButton saveBtn = new JButton("Save");
    private final JButton closeBtn = new JButton("Close");

    public StaffFrame() {
        setTitle("Staff Management");
        setSize(1400, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(new LineBorder(Color.WHITE, 3));

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "First Name", "Last Name", "Role", "Department", "Facility ID", "Phone", "Email", "Employment Status", "Start Date", "Line Manager", "Access Level"}, 0);
        staffTable = new JTable(tableModel);
        
        // Align headers to the left
        ((javax.swing.table.DefaultTableCellRenderer)staffTable.getTableHeader().getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new LineBorder(Color.WHITE, 2));
        centerPanel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(Color.WHITE, 2));
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(saveBtn);
        panel.add(closeBtn);
        add(panel, BorderLayout.SOUTH);
    }

    public JButton getAddBtn() { return addBtn; }
    public JButton getEditBtn() { return editBtn; }
    public JButton getDeleteBtn() { return deleteBtn; }
    public JButton getSaveBtn() { return saveBtn; }
    public JButton getCloseBtn() { return closeBtn; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getStaffTable() { return staffTable; }
    public int getSelectedRow() { return staffTable.getSelectedRow(); }
} */

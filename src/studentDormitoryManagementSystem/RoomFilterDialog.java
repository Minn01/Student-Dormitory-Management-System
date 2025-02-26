/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;

/**
 *
 * @author min
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class RoomFilterDialog extends JDialog {
    private JComboBox<String> statusCombo;
    private JComboBox<String> typeCombo;
    private JButton okBtn, cancelBtn;
    
    // This filter is built based on selections.
    private RowFilter<DefaultTableModel, Object> filter;
    
    public RoomFilterDialog(Window owner) {
        super(owner, "Filter Rooms", ModalityType.APPLICATION_MODAL);
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        panel.add(new JLabel("Status:"));
        // "All" means no filtering on status.
        statusCombo = new JComboBox<>(new String[] {"All", "Available", "Occupied", "Unavailable"});
        panel.add(statusCombo);
        
        panel.add(new JLabel("Room Type:"));
        // Replace these values with your actual room types.
        typeCombo = new JComboBox<>(new String[] {"All", "Standard Single", "Deluxe Single", "Executive Single", "Studio Single", "Premium Single"});
        panel.add(typeCombo);
        
        add(panel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okBtn = new JButton("OK");
        cancelBtn = new JButton("Cancel");
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
        
        okBtn.addActionListener(e -> {
            String status = (String) statusCombo.getSelectedItem();
            String type = (String) typeCombo.getSelectedItem();
            List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
            
            if (!"All".equalsIgnoreCase(status)) {
                // Column index 2 is "Status" in RoomsPanel.
                filters.add(RowFilter.regexFilter("^" + status + "$", 2));
            }
            if (!"All".equalsIgnoreCase(type)) {
                // Column index 3 is "Type" in RoomsPanel.
                filters.add(RowFilter.regexFilter("^" + type + "$", 3));
            }
            if (filters.isEmpty()) {
                filter = null;
            } else {
                filter = RowFilter.andFilter(filters);
            }
            setVisible(false);
        });
        
        cancelBtn.addActionListener(e -> {
            filter = null;
            setVisible(false);
        });
    }
    
    public RowFilter<DefaultTableModel, Object> getFilter() {
        return filter;
    }
}

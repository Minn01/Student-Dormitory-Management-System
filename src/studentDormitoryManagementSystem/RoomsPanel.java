/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
/**
 *
 * @author min
 */


public class RoomsPanel extends JPanel {
    private DBConnect dbc;
    private RoomManager roomManager;
    private RoomAllocationManager roomAllocationManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JButton assignRoomBtn; // Assign Room button
    private JButton refreshBtn;    // Refresh button
    private JButton filterBtn;     // New Filter button
    private JButton checkOutBtn;
    private int studentId;

    public RoomsPanel(DBConnect dbc) {
        this.dbc = dbc;
        roomManager = new RoomManager(dbc);
        setLayout(new BorderLayout(10, 10));

        // Top panel with search field on left and buttons on right.
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // Left: Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.WEST);

        // Right: Buttons panel (Assign, Filter, Refresh)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        assignRoomBtn = new JButton("Assign Room");
        filterBtn = new JButton("Filter");
        refreshBtn = new JButton("Refresh");
        checkOutBtn = new JButton("Check Out");
        
        buttonPanel.add(assignRoomBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(checkOutBtn);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
     

        // Table model and table setup.
        tableModel = new DefaultTableModel(new String[]{"Room ID", "Room No", "Status", "Type"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load room data from the database.
        loadRoomData();

        // Filter as user types.
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterText(); }
            public void removeUpdate(DocumentEvent e) { filterText(); }
            public void changedUpdate(DocumentEvent e) { filterText(); }
            private void filterText(){
                String text = searchField.getText();
                if(text.trim().length() == 0){
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Double-click row for details.
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    int selectedRow = table.getSelectedRow();
                    if(selectedRow != -1){
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        int roomId = (int)tableModel.getValueAt(modelRow, 0);
                        RoomDetailDialog detail = new RoomDetailDialog(SwingUtilities.getWindowAncestor(RoomsPanel.this), roomId, dbc);
                        detail.setVisible(true);
                    }
                }
            }
        });

        // "Assign Room" button action.
        assignRoomBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(RoomsPanel.this, "Please select a room to assign.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int roomId = (int)tableModel.getValueAt(modelRow, 0);
            String status = (String)tableModel.getValueAt(modelRow, 2);
            if (!"Available".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(RoomsPanel.this, "The selected room is not available.", "Room Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Open the combined assignment dialog, passing the selected room ID.
            CombinedAssignRoomDialog assignDialog = new CombinedAssignRoomDialog(SwingUtilities.getWindowAncestor(RoomsPanel.this), roomId, dbc);
            assignDialog.setVisible(true);
            // Refresh table after assignment.
            loadRoomData();
        });

        // "Filter" button action.
        filterBtn.addActionListener(e -> {
            RoomFilterDialog filterDialog = new RoomFilterDialog(SwingUtilities.getWindowAncestor(RoomsPanel.this));
            filterDialog.setVisible(true);
            RowFilter<DefaultTableModel, Object> rf = filterDialog.getFilter();
            sorter.setRowFilter(rf);
        });
        
        // "Refresh" button action.
        refreshBtn.addActionListener(e -> loadRoomData());
        
        checkOutBtn.addActionListener(e -> {
            CheckoutDialog checkoutDialog = new CheckoutDialog(this, studentId, roomAllocationManager);
        });
    }

    // Loads room data from the database into the table.
    private void loadRoomData(){
        tableModel.setRowCount(0);
        String sql = "SELECT r.room_id, r.room_no, r.status, rt.type_name " +
                     "FROM ROOMS r JOIN ROOM_TYPE rt ON r.type_id = rt.type_id " +
                     "ORDER BY r.room_id";
        try (Statement stmt = dbc.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while(rs.next()){
                int roomId = rs.getInt("room_id");
                String roomNo = rs.getString("room_no");
                String status = rs.getString("status");
                String type = rs.getString("type_name");
                tableModel.addRow(new Object[]{roomId, roomNo, status, type});
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + ex.getMessage());
        }
    }
}


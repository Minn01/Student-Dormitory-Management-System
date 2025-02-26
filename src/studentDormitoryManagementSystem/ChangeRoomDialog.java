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
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ChangeRoomDialog extends JDialog {
    private int studentId;
    private DBConnect dbc;
    private RoomAllocationManager roomAllocationManager;
    private RoomManager roomManager;
    
    // UI Components
    private JLabel currentRoomLabel;
    private JComboBox<RoomItem> newRoomComboBox;
    private JButton changeRoomButton, cancelButton;
    
    // To store the student's current room_id
    private int currentRoomId;
    
    public ChangeRoomDialog(Window owner, int studentId, DBConnect dbc) {
        super(owner, "Change Room Assignment", ModalityType.APPLICATION_MODAL);
        this.studentId = studentId;
        this.dbc = dbc;
        this.roomAllocationManager = new RoomAllocationManager(dbc);
        this.roomManager = new RoomManager(dbc);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        initComponents();
        loadCurrentRoom();
        loadAvailableRooms();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        
        // Panel to display current room assignment.
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentPanel.setBorder(BorderFactory.createTitledBorder("Current Room"));
        currentRoomLabel = new JLabel("Loading current room...");
        currentPanel.add(currentRoomLabel);
        
        // Panel for selecting a new room.
        JPanel newPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newPanel.setBorder(BorderFactory.createTitledBorder("New Room Selection"));
        newPanel.add(new JLabel("Select New Room:"));
        newRoomComboBox = new JComboBox<>();
        newPanel.add(newRoomComboBox);
        
        // Combine the two panels.
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.add(currentPanel);
        centerPanel.add(newPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Buttons panel.
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        changeRoomButton = new JButton("Change Room");
        cancelButton = new JButton("Cancel");
        buttonsPanel.add(changeRoomButton);
        buttonsPanel.add(cancelButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        changeRoomButton.addActionListener(e -> changeRoom());
        cancelButton.addActionListener(e -> dispose());
        
        add(panel);
    }
    
    // Load the current active room assignment for the student.
    private void loadCurrentRoom() {
        String sql = "SELECT room_id FROM Room_Allocation WHERE student_id = ? AND check_out_date IS NULL";
        try (PreparedStatement pstmt = dbc.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentRoomId = rs.getInt("room_id");
                // Get room_no for display.
                String roomSql = "SELECT room_no FROM ROOMS WHERE room_id = ?";
                try (PreparedStatement pstmt2 = dbc.getConnection().prepareStatement(roomSql)) {
                    pstmt2.setInt(1, currentRoomId);
                    ResultSet rs2 = pstmt2.executeQuery();
                    if (rs2.next()) {
                        String roomNo = rs2.getString("room_no");
                        currentRoomLabel.setText("Current Room: " + roomNo);
                    } else {
                        currentRoomLabel.setText("Current Room: Not Found");
                    }
                }
            } else {
                currentRoomLabel.setText("No current room assigned.");
                changeRoomButton.setEnabled(false);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading current room: " + ex.getMessage());
            System.out.println("DEBUG: Error loading current room: " + ex.getMessage());
        }
    }
    
    // Load available rooms (rooms with status "Available") excluding the current room.
    private void loadAvailableRooms() {
        Vector<RoomItem> availableRooms = new Vector<>();
        String sql = "SELECT room_id, room_no FROM ROOMS WHERE status = 'Available'";
        try (Statement stmt = dbc.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("room_id");
                String roomNo = rs.getString("room_no");
                if (id != currentRoomId) {
                    availableRooms.add(new RoomItem(id, roomNo));
                }
            }
            System.out.println("DEBUG: Loaded " + availableRooms.size() + " available new rooms.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading available rooms: " + ex.getMessage());
            System.out.println("DEBUG: Error loading available rooms: " + ex.getMessage());
        }
        newRoomComboBox.setModel(new DefaultComboBoxModel<>(availableRooms));
    }
    
    // Perform the room change operation.
    private void changeRoom() {
        // Ensure a new room is selected.
        RoomItem selected = (RoomItem) newRoomComboBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a new room.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int newRoomId = selected.getRoomId();
        
        // 1. Close the current room allocation by setting check_out_date.
        String updateAllocationSql = "UPDATE Room_Allocation SET check_out_date = SYSDATE WHERE student_id = ? AND check_out_date IS NULL";
        try (PreparedStatement pstmt = dbc.getConnection().prepareStatement(updateAllocationSql)) {
            pstmt.setInt(1, studentId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("DEBUG: Marked " + rowsAffected + " allocations as closed for student_id " + studentId);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating current room allocation: " + ex.getMessage());
            return;
        }
        
        // 2. Insert new room allocation record for the new room.
        int allocResult = roomAllocationManager.assignRoom(newRoomId, studentId);
        if (allocResult <= 0) {
            JOptionPane.showMessageDialog(this, "Error assigning new room. Please try again.");
            return;
        }
        
        // 3. Update the new room's status to "Occupied" (or "Unavailable" if you prefer).
        int updateNewRoom = roomManager.updateRoomStatus(newRoomId, "Occupied");
        if (updateNewRoom <= 0) {
            JOptionPane.showMessageDialog(this, "Error updating new room status.");
            return;
        }
        
        // 4. Update the previous room's status to "Available."
        int updateOldRoom = roomManager.updateRoomStatus(currentRoomId, "Available");
        if (updateOldRoom <= 0) {
            JOptionPane.showMessageDialog(this, "Error updating previous room status.");
            return;
        }
        
        JOptionPane.showMessageDialog(this, "Room changed successfully.");
        dispose();
    }
    
    // Helper class to represent room items.
    private class RoomItem {
        private int roomId;
        private String roomNo;
        
        public RoomItem(int roomId, String roomNo) {
            this.roomId = roomId;
            this.roomNo = roomNo;
        }
        
        public int getRoomId() { return roomId; }
        public String getRoomNo() { return roomNo; }
        
        @Override
        public String toString() {
            return roomNo;
        }
    }
}

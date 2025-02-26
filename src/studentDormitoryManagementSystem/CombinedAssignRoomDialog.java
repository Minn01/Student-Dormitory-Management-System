/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
/**
 *
 * @author min
 */
public class CombinedAssignRoomDialog extends JDialog {
    private int preselectedRoomId; // Room selected from RoomsPanel; 0 if none.
    private DBConnect dbc;
    private RoomAllocationManager roomAllocationManager;
    private RoomManager roomManager;
    private StudentManager studentManager;
    
    // Student fields
    private JTextField studentIdField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField emergencyContactField;
    
    // Room selection
    private JComboBox<RoomItem> roomComboBox;
    
    private JButton assignButton, cancelButton;
    
    // Constructor accepts the preselected room id and the DB connection.
    public CombinedAssignRoomDialog(Window owner, int preselectedRoomId, DBConnect dbc) {
        super(owner, "Assign Room & Register Student", ModalityType.APPLICATION_MODAL);
        this.preselectedRoomId = preselectedRoomId;
        this.dbc = dbc;
        this.roomAllocationManager = new RoomAllocationManager(dbc);
        this.roomManager = new RoomManager(dbc);
        this.studentManager = new StudentManager(dbc);
        setSize(500, 350);
        setLocationRelativeTo(owner);
        initComponents();
        
        // Preselect the room if provided.
        if(preselectedRoomId > 0) {
            for (int i = 0; i < roomComboBox.getItemCount(); i++) {
                RoomItem item = roomComboBox.getItemAt(i);
                if(item.getRoomId() == preselectedRoomId) {
                    roomComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        
        // Panel for Student Information
        JPanel studentPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        studentPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        
        studentPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        studentPanel.add(studentIdField);
        
        studentPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        studentPanel.add(nameField);
        
        studentPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        studentPanel.add(emailField);
        
        studentPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        studentPanel.add(phoneField);
        
        studentPanel.add(new JLabel("Emergency Contact:"));
        emergencyContactField = new JTextField();
        studentPanel.add(emergencyContactField);
        
        // Panel for Room Selection
        JPanel roomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roomPanel.setBorder(BorderFactory.createTitledBorder("Room Selection"));
        roomPanel.add(new JLabel("Select Room:"));
        
        Vector<RoomItem> availableRooms = new Vector<>();
        String sql = "SELECT room_id, room_no FROM Rooms WHERE status = 'Available'";
        try (Statement stmt = dbc.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
                int id = rs.getInt("room_id");
                String roomNo = rs.getString("room_no");
                availableRooms.add(new RoomItem(id, roomNo));
            }
            System.out.println("DEBUG: Loaded " + availableRooms.size() + " available rooms.");
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading available rooms: " + ex.getMessage());
            System.out.println("DEBUG: Error loading available rooms: " + ex.getMessage());
        }
        roomComboBox = new JComboBox<>(availableRooms);
        roomPanel.add(roomComboBox);
        
        // Combine studentPanel and roomPanel into a centerPanel.
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(studentPanel, BorderLayout.CENTER);
        centerPanel.add(roomPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel for buttons.
        assignButton = new JButton("Assign Room");
        cancelButton = new JButton("Cancel");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(assignButton);
        bottomPanel.add(cancelButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        assignButton.addActionListener(e -> assignRoom());
        cancelButton.addActionListener(e -> dispose());
        
        add(panel);
    }
    
    // Process the room assignment and student registration.
    private void assignRoom(){
        System.out.println("DEBUG: Starting room assignment process.");
        
        // Validate student fields.
        String studentIdStr = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String emergencyContact = emergencyContactField.getText().trim();
        if(studentIdStr.isEmpty() || name.isEmpty() || email.isEmpty() ||
           phone.isEmpty() || emergencyContact.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in all student information fields.", 
                                          "Input Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("DEBUG: Student fields validation failed.");
            return;
        }
        int studentIdValue;
        try {
            studentIdValue = Integer.parseInt(studentIdStr);
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", 
                                          "Input Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("DEBUG: Student ID parsing failed.");
            return;
        }
        
        // Check if the student already exists; update if so, or insert a new record.
        boolean exists = studentManager.studentExists(studentIdValue);
        int studentResult = 0;
        if(exists) {
            System.out.println("DEBUG: Student exists. Updating record for student_id " + studentIdValue);
            studentResult = studentManager.updateStudent(studentIdValue, name, email, phone, emergencyContact);
        } else {
            System.out.println("DEBUG: Inserting new student record for student_id " + studentIdValue);
            studentResult = studentManager.addStudent(studentIdValue, name, email, phone, emergencyContact);
        }
        if(studentResult <= 0) {
            JOptionPane.showMessageDialog(this, "Failed to save student record. Cannot assign room.", 
                                          "Student Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("DEBUG: Student record save failed for student_id " + studentIdValue);
            return;
        }
        
        // Commit if auto-commit is disabled.
        try {
            Connection conn = dbc.getConnection();
            if(!conn.getAutoCommit()){
                conn.commit();
                System.out.println("DEBUG: Student record committed.");
            } else {
                System.out.println("DEBUG: Auto-commit is enabled; student record is already committed.");
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error committing student record: " + ex.getMessage(), 
                                          "Commit Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("DEBUG: Commit error: " + ex.getMessage());
            return;
        }
        
        // Check room selection.
        RoomItem selected = (RoomItem) roomComboBox.getSelectedItem();
        if(selected == null){
            JOptionPane.showMessageDialog(this, "No available room selected.", 
                                          "Selection Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("DEBUG: No room selected.");
            return;
        }
        System.out.println("DEBUG: Selected room " + selected.getRoomNo() + " (room_id: " + selected.getRoomId() + ")");
        
        // Create the room allocation record.
        System.out.println("DEBUG: Creating room allocation record for student_id " + studentIdValue);
        int result = roomAllocationManager.assignRoom(selected.getRoomId(), studentIdValue);
        if(result > 0) {
            // Update room status to "Unavailable" now that we've updated the check constraint.
            roomManager.updateRoomStatus(selected.getRoomId(), "Unavailable");
            JOptionPane.showMessageDialog(this, "Room " + selected.getRoomNo() + " assigned to student " + studentIdValue + " successfully.");
            System.out.println("DEBUG: Room assigned successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign room. Please try again.", 
                                          "Assignment Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("DEBUG: Room assignment failed.");
        }
    }
    
    // Helper class for room items in the combo box.
    private class RoomItem {
        private int roomId;
        private String roomNo;
        
        public RoomItem(int roomId, String roomNo){
            this.roomId = roomId;
            this.roomNo = roomNo;
        }
        
        public int getRoomId() { return roomId; }
        public String getRoomNo() { return roomNo; }
        
        @Override
        public String toString(){
            return roomNo;
        }
    }
}

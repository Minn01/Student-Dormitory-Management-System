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
public class AssignRoomDialog extends JDialog {
    private int studentId;
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
    
    public AssignRoomDialog(Window owner, int studentId, DBConnect dbc) {
        super(owner, "Assign Room & Update Student", ModalityType.APPLICATION_MODAL);
        this.studentId = studentId;
        this.dbc = dbc;
        this.roomAllocationManager = new RoomAllocationManager(dbc);
        this.roomManager = new RoomManager(dbc);
        this.studentManager = new StudentManager(dbc);
        setSize(500, 350);
        setLocationRelativeTo(owner);
        initComponents();
        // If a valid studentId is provided, pre-populate the student info.
        if(studentId > 0) {
            loadStudentDetails();
            studentIdField.setEditable(false);
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
        try(Statement stmt = dbc.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
                int id = rs.getInt("room_id");
                String roomNo = rs.getString("room_no");
                availableRooms.add(new RoomItem(id, roomNo));
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading available rooms: " + ex.getMessage());
        }
        roomComboBox = new JComboBox<>(availableRooms);
        roomPanel.add(roomComboBox);
        
        // Combine studentPanel and roomPanel into a centerPanel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(studentPanel, BorderLayout.CENTER);
        centerPanel.add(roomPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel for buttons
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
    
    // Load student details from the database and pre-populate fields.
    private void loadStudentDetails() {
        String sql = "SELECT student_id, name, email, phone, emergency_contact FROM Student WHERE student_id = ?";
        try (PreparedStatement pstmt = dbc.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                studentIdField.setText(String.valueOf(rs.getInt("student_id")));
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
                emergencyContactField.setText(rs.getString("emergency_contact"));
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading student details: " + ex.getMessage());
        }
    }
    
    // Process the room assignment and student registration/update
    private void assignRoom(){
        // Validate student fields
        String studentIdStr = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String emergencyContact = emergencyContactField.getText().trim();
        
        if(studentIdStr.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || emergencyContact.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in all student information fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int studentIdValue;
        try {
            studentIdValue = Integer.parseInt(studentIdStr);
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Insert or update the student record.
        // (Here we call addStudent; you may wish to modify StudentManager to update existing records.)
        int studentResult = studentManager.addStudent(studentIdValue, name, email, phone, emergencyContact);
        // If insertion fails, we assume the student already exists.
        
        // Check room selection
        RoomItem selected = (RoomItem) roomComboBox.getSelectedItem();
        if(selected == null){
            JOptionPane.showMessageDialog(this, "No available room selected.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Assign the room to the student.
        int result = roomAllocationManager.assignRoom(selected.getRoomId(), studentIdValue);
        if(result > 0) {
            // Update room status to 'Occupied'
            roomManager.updateRoomStatus(selected.getRoomId(), "Occupied");
            JOptionPane.showMessageDialog(this, "Room " + selected.getRoomNo() + " assigned to student " + studentIdValue + " successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign room. Please try again.", "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper class for combo box items
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

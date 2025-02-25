/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
/**
 *
 * @author min
 */
public class AssignRoomToStudentDialog extends JDialog {
    private int roomId;
    private DBConnect dbc;
    private RoomAllocationManager roomAllocationManager;
    private RoomManager roomManager;
    
    private JTextField studentIdField;
    private JButton assignButton, cancelButton;
    
    public AssignRoomToStudentDialog(Window owner, int roomId, DBConnect dbc) {
        super(owner, "Assign Room to Student", ModalityType.APPLICATION_MODAL);
        this.roomId = roomId;
        this.dbc = dbc;
        this.roomAllocationManager = new RoomAllocationManager(dbc);
        this.roomManager = new RoomManager(dbc);
        setSize(400, 200);
        setLocationRelativeTo(owner);
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        
        // Center panel: ask for Student ID
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(new JLabel("Enter Student ID:"));
        studentIdField = new JTextField(15);
        centerPanel.add(studentIdField);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        
        // Bottom panel: assign and cancel buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        assignButton = new JButton("Assign Room");
        cancelButton = new JButton("Cancel");
        bottomPanel.add(assignButton);
        bottomPanel.add(cancelButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        assignButton.addActionListener(e -> assignRoom());
        cancelButton.addActionListener(e -> dispose());
        
        add(panel);
    }
    
    // Process the room assignment
    private void assignRoom(){
        String studentIdStr = studentIdField.getText().trim();
        if (studentIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid student ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Business Rule: Check if the student already has a room assignment if needed.
        // (This sample code assumes the student can be assigned if they exist.)
        
        // Assign room: call RoomAllocationManager.assignRoom(room_id, student_id)
        int result = roomAllocationManager.assignRoom(roomId, studentId);
        if (result > 0) {
            // Update the room status to "Occupied"
            roomManager.updateRoomStatus(roomId, "Occupied");
            JOptionPane.showMessageDialog(this, "Room assigned successfully to student " + studentId + ".");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign room. Please check the details and try again.", "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

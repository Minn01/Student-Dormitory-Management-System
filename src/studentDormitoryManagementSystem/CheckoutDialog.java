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
import java.awt.event.*;

public class CheckoutDialog extends JDialog {
    private JTextField studentIdField;
    private JButton confirmButton, cancelButton;
    private RoomAllocationManager allocationManager;

    // New constructor that accepts student ID
    public CheckoutDialog(JFrame parent, int studentId, RoomAllocationManager allocationManager) {
        super(parent, "Student Check-Out", true);
        this.allocationManager = allocationManager;

        setLayout(new GridLayout(3, 2, 10, 10));
        add(new JLabel("Enter Student ID:"));
        studentIdField = new JTextField(String.valueOf(studentId)); // Pre-fill field
        add(studentIdField);

        confirmButton = new JButton("Confirm Check-Out");
        cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> handleCheckout());
        cancelButton.addActionListener(e -> dispose());

        add(confirmButton);
        add(cancelButton);

        setSize(300, 150);
        setLocationRelativeTo(parent);
    }

    private void handleCheckout() {
        String studentIdText = studentIdField.getText().trim();

        if (studentIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Student ID.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdText);
            int result = allocationManager.checkoutStudent(studentId);

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Student checked out successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Check-out failed. Ensure student is assigned to a room.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

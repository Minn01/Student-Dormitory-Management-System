/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author min
 */
public class StudentDormitoryApp extends JFrame {
    private DBConnect dbc;
    
    // Panels for each tab/view
    private RoomsPanel roomsPanel;
    private StudentsPanel studentsPanel;
    private PaymentsPanel paymentsPanel;
    
    // Left navigation panel components
    private JPanel navPanel;
    private JButton btnRooms;
    private JButton btnStudents;
    private JButton btnPayments;
    
    // Right content panel with CardLayout
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    public StudentDormitoryApp() {
        // Initialize DB connection
        dbc = new DBConnect();
        
        // Set up main frame
        setTitle("Student Dormitory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Create navigation panel (left side)
        navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(3, 1, 5, 5));
        btnRooms = new JButton("Rooms");
        btnStudents = new JButton("Students");
        btnPayments = new JButton("Payments");
        
        navPanel.add(btnRooms);
        navPanel.add(btnStudents);
        navPanel.add(btnPayments);
        
        // Create content panel (right side) with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        roomsPanel = new RoomsPanel(dbc);
        studentsPanel = new StudentsPanel(dbc);
        paymentsPanel = new PaymentsPanel(dbc);
        
        contentPanel.add(roomsPanel, "Rooms");
        contentPanel.add(studentsPanel, "Students");
        contentPanel.add(paymentsPanel, "Payments");
        
        // Layout: Left nav panel and right content panel
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        // Button listeners to switch panels
        btnRooms.addActionListener(e -> cardLayout.show(contentPanel, "Rooms"));
        btnStudents.addActionListener(e -> cardLayout.show(contentPanel, "Students"));
        btnPayments.addActionListener(e -> cardLayout.show(contentPanel, "Payments"));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentDormitoryApp app = new StudentDormitoryApp();
            app.setVisible(true);
        });
    }
}
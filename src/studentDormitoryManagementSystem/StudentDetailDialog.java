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
import java.sql.*;

public class StudentDetailDialog extends JDialog {
    private int studentId;
    private DBConnect dbc;
    
    public StudentDetailDialog(Window owner, int studentId, DBConnect dbc) {
        super(owner, "Student Details", ModalityType.APPLICATION_MODAL);
        this.studentId = studentId;
        this.dbc = dbc;
        setSize(500, 400);
        setLocationRelativeTo(owner);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new BorderLayout(10,10));
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        
        String sql = "SELECT student_id, name, email, phone, emergency_contact FROM Student WHERE student_id = ?";
        try(PreparedStatement pstmt = dbc.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                StringBuilder sb = new StringBuilder();
                sb.append("Student ID: ").append(rs.getInt("student_id")).append("\n");
                sb.append("Name: ").append(rs.getString("name")).append("\n");
                sb.append("Email: ").append(rs.getString("email")).append("\n");
                sb.append("Phone: ").append(rs.getString("phone")).append("\n");
                sb.append("Emergency Contact: ").append(rs.getString("emergency_contact")).append("\n");
                detailsArea.setText(sb.toString());
            }
        } catch(Exception ex) {
            detailsArea.setText("Error retrieving details: " + ex.getMessage());
        }
        
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton assignRoomBtn = new JButton("Assign Room");
        JButton closeBtn = new JButton("Close");
        bottomPanel.add(assignRoomBtn);
        bottomPanel.add(closeBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // When the button is pressed, open the updated assign dialog.
        assignRoomBtn.addActionListener(e -> {
            // Pass the studentId so that the assign dialog can pre-populate the student data.
            AssignRoomDialog assignDialog = new AssignRoomDialog(this, studentId, dbc);
            assignDialog.setVisible(true);
        });
        
        closeBtn.addActionListener(e -> dispose());
        
        add(panel);
    }
}


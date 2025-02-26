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

class StudentDetailDialog extends JDialog {
    private int studentId;
    private DBConnect dbc;
    private JTextArea detailsArea;

    public StudentDetailDialog(Window owner, int studentId, DBConnect dbc) {
        super(owner, "Student Details", ModalityType.APPLICATION_MODAL);
        this.studentId = studentId;
        this.dbc = dbc;
        setSize(500, 400);
        setLocationRelativeTo(owner);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateBtn = new JButton("Update");
        JButton changeRoomButton = new JButton("Change Room");
        
        topPanel.add(changeRoomButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        topPanel.add(updateBtn);
        panel.add(topPanel, BorderLayout.NORTH);
        
        updateBtn.addActionListener(e -> {
            UpdateStudentDialog updateDialog = new UpdateStudentDialog(this, studentId, dbc);
            updateDialog.setVisible(true);
            reloadStudentDetails();
        });

        changeRoomButton.addActionListener(e -> {
            ChangeRoomDialog changeRoomDialog = new ChangeRoomDialog(this, studentId, dbc);
            changeRoomDialog.setVisible(true);
            reloadStudentDetails();
        });
       
        add(panel);
        reloadStudentDetails();
    }
    
    private void reloadStudentDetails() {
        String sql = "SELECT student_id, name, email, phone, emergency_contact FROM Student WHERE student_id = ?";
        try (PreparedStatement pstmt = dbc.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            if(rs.next()){
                sb.append("Student ID: ").append(rs.getInt("student_id")).append("\n");
                sb.append("Name: ").append(rs.getString("name")).append("\n");
                sb.append("Email: ").append(rs.getString("email")).append("\n");
                sb.append("Phone: ").append(rs.getString("phone")).append("\n");
                sb.append("Emergency Contact: ").append(rs.getString("emergency_contact")).append("\n");
            }
            detailsArea.setText(sb.toString());
        } catch(Exception ex) {
            detailsArea.setText("Error retrieving details: " + ex.getMessage());
        }
    }
}

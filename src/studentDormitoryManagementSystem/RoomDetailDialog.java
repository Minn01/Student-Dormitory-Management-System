package studentDormitoryManagementSystem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author min
 */

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RoomDetailDialog extends JDialog {
    private int roomId;
    private DBConnect dbc;
    
    public RoomDetailDialog(Window owner, int roomId, DBConnect dbc) {
        super(owner, "Room Details", ModalityType.APPLICATION_MODAL);
        this.roomId = roomId;
        this.dbc = dbc;
        setSize(400,300);
        setLocationRelativeTo(owner);
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        
        // Query the room details from the database
        String sql = "SELECT r.room_id, r.room_no, r.status, rt.type_name, rt.monthly_rate " +
                     "FROM Rooms r JOIN Room_Type rt ON r.type_id = rt.type_id " +
                     "WHERE r.room_id = ?";
        try(PreparedStatement pstmt = dbc.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                StringBuilder sb = new StringBuilder();
                sb.append("Room ID: ").append(rs.getInt("room_id")).append("\n");
                sb.append("Room No: ").append(rs.getString("room_no")).append("\n");
                sb.append("Status: ").append(rs.getString("status")).append("\n");
                sb.append("Type: ").append(rs.getString("type_name")).append("\n");
                sb.append("Monthly Rate: ").append(rs.getDouble("monthly_rate")).append("\n");
                detailsArea.setText(sb.toString());
            }
        } catch(Exception ex){
            detailsArea.setText("Error retrieving details: " + ex.getMessage());
        }
        
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        panel.add(closeBtn, BorderLayout.SOUTH);
        
        add(panel);
    }
}
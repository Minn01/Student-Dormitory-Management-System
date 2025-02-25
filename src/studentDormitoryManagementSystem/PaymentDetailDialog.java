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

public class PaymentDetailDialog extends JDialog {
    private int paymentId;
    private DBConnect dbc;
    
    public PaymentDetailDialog(Window owner, int paymentId, DBConnect dbc) {
        super(owner, "Payment Details", ModalityType.APPLICATION_MODAL);
        this.paymentId = paymentId;
        this.dbc = dbc;
        setSize(400,300);
        setLocationRelativeTo(owner);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new BorderLayout(10,10));
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        
        String sql = "SELECT payment_id, amount, payment_date, receipt_number, student_id " +
                     "FROM Payment WHERE payment_id = ?";
        try(PreparedStatement pstmt = dbc.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                StringBuilder sb = new StringBuilder();
                sb.append("Payment ID: ").append(rs.getInt("payment_id")).append("\n");
                sb.append("Amount: ").append(rs.getDouble("amount")).append("\n");
                sb.append("Date: ").append(rs.getDate("payment_date")).append("\n");
                sb.append("Receipt: ").append(rs.getString("receipt_number")).append("\n");
                sb.append("Student ID: ").append(rs.getInt("student_id")).append("\n");
                detailsArea.setText(sb.toString());
            }
        } catch(Exception ex) {
            detailsArea.setText("Error retrieving details: " + ex.getMessage());
        }
        
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        panel.add(closeBtn, BorderLayout.SOUTH);
        add(panel);
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author min
 */
public class PaymentManager {
    private Connection connection;
    private DBConnect dbc;
    private IdGenerator idGen;

    public PaymentManager(DBConnect dbc) {
        this.dbc = dbc;
        this.connection = dbc.getConnection();
        this.idGen = new IdGenerator(connection);
    }

    // Process a payment, auto-generating payment_id
    public int processPayment(double amount, String receipt_number, int student_id, int type_id) {
        int result = 0;
        int newPaymentId = idGen.getNextId("Payment", "payment_id");
        String sql = "INSERT INTO Payment (payment_id, amount, payment_date, receipt_number, student_id, type_id) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, newPaymentId);
            stmt.setDouble(2, amount);
            stmt.setDate(3, new Date(System.currentTimeMillis()));
            stmt.setString(4, receipt_number);
            stmt.setInt(5, student_id);
            stmt.setInt(6, type_id);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PaymentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}

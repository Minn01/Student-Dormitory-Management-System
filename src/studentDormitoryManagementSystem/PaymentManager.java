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

    // Insert a new payment
    public int addPayment(double amount, String receiptNumber, int studentId, String paymentType, Date paymentDate) {
        int newPaymentId = idGen.getNextId("Payment", "payment_id");
        String sql = "INSERT INTO Payment (payment_id, amount, payment_date, receipt_number, student_id, payment_type) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newPaymentId);
            stmt.setDouble(2, amount);
            stmt.setDate(3, paymentDate != null ? paymentDate : new Date(System.currentTimeMillis()));
            stmt.setString(4, receiptNumber);
            stmt.setInt(5, studentId);
            stmt.setString(6, paymentType);

            return stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PaymentManager.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    // Update an existing payment
    public int updatePayment(int paymentId, double amount, String receiptNumber, int studentId, String paymentType, Date paymentDate) {
        String sql = "UPDATE Payment SET amount = ?, payment_date = ?, receipt_number = ?, student_id = ?, payment_type = ? WHERE payment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setDate(2, paymentDate);
            stmt.setString(3, receiptNumber);
            stmt.setInt(4, studentId);
            stmt.setString(5, paymentType);
            stmt.setInt(6, paymentId);

            return stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PaymentManager.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    // Delete a payment
    public int deletePayment(int paymentId) {
        String sql = "DELETE FROM Payment WHERE payment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            return stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PaymentManager.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
}
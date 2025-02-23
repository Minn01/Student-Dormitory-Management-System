/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author min
 */

public class StudentManager {
    Connection connection;
    DBConnect dbc;

    public StudentManager(DBConnect dbc) {
        this.dbc = dbc;
        this.connection = dbc.getConnection();
    }

    // Add a new student
    public int addStudent(int student_id, String name, String email, String phone, String emergency_contact) {
        int result = 0;
        String sql = "INSERT INTO Student (student_id, name, email, phone, emergency_contact) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, student_id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, emergency_contact);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    // Retrieve student details as a String
    public String getStudentDetails(int student_id) {
        String details = "";
        String sql = "SELECT student_id, name, contact_info, current_balance FROM Student WHERE student_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, student_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                details = "ID: " + rs.getInt("student_id") + ", Name: " + rs.getString("name")
                        + ", Contact: " + rs.getString("contact_info") + ", Balance: " + rs.getDouble("current_balance");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return details;
    }

    // Update student's contact information
    public int updateStudentContact(int student_id, String newContact) {
        int result = 0;
        String sql = "UPDATE Student SET contact_info = ? WHERE student_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, newContact);
            stmt.setInt(2, student_id);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    // Additional methods (deleteStudent, etc.) can be added as needed.
}
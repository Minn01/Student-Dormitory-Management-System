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

    public int addStudent(int student_id, String name, String email, String phone, String emergency_contact) {
        int result = 0;
        String sql = "INSERT INTO Student (student_id, name, email, phone, emergency_contact) VALUES (?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    public int updateStudent(int studentId, String name, String email, String phone, String emergencyContact) {
        int result = 0;
        String sql = "UPDATE Student SET name = ?, email = ?, phone = ?, emergency_contact = ? WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, emergencyContact);
            stmt.setInt(5, studentId);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int deleteStudent(int studentId) {
        int result = 0;
        String sql = "DELETE FROM Student WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public boolean studentExists(int studentId) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM Student WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exists;
    }
}
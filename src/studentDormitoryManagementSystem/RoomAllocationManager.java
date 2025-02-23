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

public class RoomAllocationManager {
    private Connection connection;
    private DBConnect dbc;
    private IdGenerator idGen;

    public RoomAllocationManager(DBConnect dbc) {
        this.dbc = dbc;
        this.connection = dbc.getConnection();
        this.idGen = new IdGenerator(connection);
    }

    // Assign a room to a student (auto-generates allocation_id)
    public int assignRoom(int room_id, int student_id) {
        int result = 0;
        int newAllocationId = idGen.getNextId("Room_allocation", "allocation_id");
        String sql = "INSERT INTO Room_allocation (allocation_id, start_date, status, room_id, student_id) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, newAllocationId);
            stmt.setDate(2, new Date(System.currentTimeMillis()));
            stmt.setString(3, "Active");
            stmt.setInt(4, room_id);
            stmt.setInt(5, student_id);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(RoomAllocationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    // Change room assignment for a student
    public int changeRoom(int student_id, int new_room_id) {
        int result = 0;
        // Mark current allocation as Inactive
        String sqlUpdate = "UPDATE Room_allocation SET status = 'Inactive', end_date = ? WHERE student_id = ? AND status = 'Active'";
        // Insert new allocation record
        String sqlInsert = "INSERT INTO Room_allocation (allocation_id, start_date, status, room_id, student_id) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate);
            stmtUpdate.setDate(1, new Date(System.currentTimeMillis()));
            stmtUpdate.setInt(2, student_id);
            stmtUpdate.executeUpdate();

            int newAllocationId = idGen.getNextId("Room_allocation", "allocation_id");
            PreparedStatement stmtInsert = connection.prepareStatement(sqlInsert);
            stmtInsert.setInt(1, newAllocationId);
            stmtInsert.setDate(2, new Date(System.currentTimeMillis()));
            stmtInsert.setString(3, "Active");
            stmtInsert.setInt(4, new_room_id);
            stmtInsert.setInt(5, student_id);
            result = stmtInsert.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(RoomAllocationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}

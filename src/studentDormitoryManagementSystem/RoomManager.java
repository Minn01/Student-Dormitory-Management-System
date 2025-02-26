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
public class RoomManager {
    private Connection connection;
    private DBConnect dbc;

    public RoomManager(DBConnect dbc) {
        this.dbc = dbc;
        this.connection = dbc.getConnection();
    }

    // Check available rooms and return a formatted string with details
    public String checkRoomAvailability() {
        String sql = "SELECT room_id, room_no, status FROM Room WHERE status = 'Available'";
        StringBuilder availableRooms = new StringBuilder();

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availableRooms.append("Room ID: ")
                              .append(rs.getInt("room_id"))
                              .append(", Room No: ")
                              .append(rs.getString("room_no"))
                              .append("\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return availableRooms.toString();
    }

    // Update room status (e.g., mark as 'Occupied' or 'Available')
    public int updateRoomStatus(int roomId, String status) {
        int result = 0;
        // Use the table name "ROOMS" as defined in your DDL.
        String sql = "UPDATE ROOMS SET status = ? WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            result = stmt.executeUpdate();
            System.out.println("DEBUG: Room status updated to " + status + " for room_id " + roomId);
        } catch (SQLException ex) {
            Logger.getLogger(RoomManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("DEBUG: Error updating room status: " + ex.getMessage());
        }
        return result;
    }
}

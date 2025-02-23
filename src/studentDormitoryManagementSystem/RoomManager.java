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
    public int updateRoomStatus(int room_id, String status) {
        int result = 0;
        String sql = "UPDATE Room SET status = ? WHERE room_id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, room_id);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(RoomManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}

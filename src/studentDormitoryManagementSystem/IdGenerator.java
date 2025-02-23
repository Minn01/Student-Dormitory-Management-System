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
public class IdGenerator {
    private Connection connection;

    public IdGenerator(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns the next available ID for the specified table and column.
     * If the table is empty, returns 1.
     */
    public int getNextId(String tableName, String columnName) {
        int nextId = 0;
        String sql = "SELECT NVL(MAX(" + columnName + "), 0) FROM " + tableName;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nextId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(IdGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nextId + 1;
    }
}

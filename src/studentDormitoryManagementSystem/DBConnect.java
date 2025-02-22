/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author min
 */
public class DBConnect {

    private Connection connection = null;
    private String db_server = "";
    private String username = "";
    private String password = "";
    private boolean connected = false;
    DatabaseMetaData meta = null;
    String schema = null;

    OracleDataSource ods = null;

    public DBConnect() {

        db_server = "tetraserver.thddns.net";
        username = "DBMS153";
        password = "takamatsu";

        try {
            ods = new OracleDataSource();
            ods.setURL(
                    "jdbc:oracle:thin:" + username + "/" + password + "@"
                    + db_server + ":4421/orcl"); // :<port>/<sid>

            connection = ods.getConnection();

            // Create Oracle DatabaseMetaData object
            meta = connection.getMetaData();

            // gets driver info:
            System.out.println("JDBC driver version is " + meta.getDriverVersion());
            System.out.println("Your JDBC installation is correct.");
            connected = true;

        } catch (SQLException ex) {
            System.err.print("DBConnect SQLException: " + ex.getMessage());
            connected = false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}

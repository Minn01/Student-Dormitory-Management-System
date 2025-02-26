package studentDormitoryManagementSystem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
/**
 *
 * @author min
 */
public class StudentsPanel extends JPanel {
    private DBConnect dbc;
    private StudentManager studentManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JButton refreshBtn; // New refresh button

    public StudentsPanel(DBConnect dbc) {
        this.dbc = dbc;
        studentManager = new StudentManager(dbc);
        setLayout(new BorderLayout(10,10));
        
        // Top panel with search field on left and refresh button on right.
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Search bar panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Refresh button on the right.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshBtn = new JButton("Refresh");
        buttonPanel.add(refreshBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Table to show students, including a "Late Payment" column.
        tableModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Phone", "Late Payment"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load student data from DB.
        loadStudentData();
        
        // Filter as text is entered.
        searchField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter(){
                String text = searchField.getText();
                if(text.trim().length() == 0){
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        // Refresh button action: reload student data.
        refreshBtn.addActionListener(e -> loadStudentData());
        
        // Double-click row for details.
        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if(selectedRow != -1){
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        int studentId = (int)tableModel.getValueAt(modelRow, 0);
                        StudentDetailDialog detail = new StudentDetailDialog(SwingUtilities.getWindowAncestor(StudentsPanel.this), studentId, dbc);
                        detail.setVisible(true);
                    }
                }
            }
        });
    }
    
    // Loads student data from the database.
    private void loadStudentData(){
        tableModel.setRowCount(0);
        String sql = "SELECT s.student_id, s.name, s.phone, " +
                     "       CASE " +
                     "         WHEN (SELECT MAX(r.check_in_date) FROM Room_Allocation r WHERE r.student_id = s.student_id) IS NULL THEN 'No' " +
                     "         WHEN SYSDATE < (SELECT MAX(r.check_in_date) FROM Room_Allocation r WHERE r.student_id = s.student_id) + 10 THEN 'No' " +
                     "         WHEN (SELECT COUNT(*) FROM Payment p WHERE p.student_id = s.student_id " +
                     "                AND TRUNC(p.payment_date, 'MM') = TRUNC(SYSDATE, 'MM')) = 0 THEN 'Yes' " +
                     "         ELSE 'No' " +
                     "       END AS late_payment " +
                     "FROM Student s " +
                     "ORDER BY s.student_id";
        try(Statement stmt = dbc.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
             while(rs.next()){
                 int id = rs.getInt("student_id");
                 String name = rs.getString("name");
                 String phone = rs.getString("phone");
                 String late = rs.getString("late_payment");
                 tableModel.addRow(new Object[]{id, name, phone, late});
             }
        } catch(SQLException ex){
             JOptionPane.showMessageDialog(this, "Error loading student data: " + ex.getMessage());
        }
    }
    
}

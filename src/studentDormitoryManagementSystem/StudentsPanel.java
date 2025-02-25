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
    
    public StudentsPanel(DBConnect dbc) {
        this.dbc = dbc;
        studentManager = new StudentManager(dbc);
        setLayout(new BorderLayout(10,10));
        
        // Search bar panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        add(topPanel, BorderLayout.NORTH);
        
        // Table to show students, including a new "Late Payment" column
        tableModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Phone", "Late Payment"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load student data from DB
        loadStudentData();
        
        // Filter as text is entered
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
        
        // Open detail view on double-click
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
    
    // Loads student data from the database. Here we join Payment to show if any payment is late.
    private void loadStudentData(){
        tableModel.setRowCount(0);
        // Calculate late payment: if the student has no payment in the current month, mark as "Yes"
        String sql = "SELECT s.student_id, s.name, s.phone, " +
                     "       CASE WHEN (SELECT COUNT(*) FROM Payment p " +
                     "                  WHERE p.student_id = s.student_id " +
                     "                    AND TRUNC(p.payment_date, 'MM') = TRUNC(SYSDATE, 'MM')) = 0 " +
                     "            THEN 'Yes' ELSE 'No' END AS late_payment " +
                     "FROM Student s " +
                     "ORDER BY s.student_id";
        try(Statement stmt = dbc.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
                int id = rs.getInt("student_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String late = rs.getString("late_payment");
                tableModel.addRow(new Object[]{id, name, phone, late});
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
        }
    }
}
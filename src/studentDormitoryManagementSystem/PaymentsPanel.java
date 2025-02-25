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
public class PaymentsPanel extends JPanel {
    private DBConnect dbc;
    private PaymentManager paymentManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    
    public PaymentsPanel(DBConnect dbc) {
        this.dbc = dbc;
        paymentManager = new PaymentManager(dbc);
        setLayout(new BorderLayout(10,10));
        
        // Search bar panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        add(topPanel, BorderLayout.NORTH);
        
        // Table model and table
        tableModel = new DefaultTableModel(new String[]{"Payment ID", "Amount", "Date", "Student ID"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load payment data from DB
        loadPaymentData();
        
        // Filter rows as text changes
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
        
        // Row double-click to show details
        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    int selectedRow = table.getSelectedRow();
                    if(selectedRow != -1){
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        int paymentId = (int)tableModel.getValueAt(modelRow, 0);
                        PaymentDetailDialog detail = new PaymentDetailDialog(SwingUtilities.getWindowAncestor(PaymentsPanel.this), paymentId, dbc);
                        detail.setVisible(true);
                    }
                }
            }
        });
    }
    
    private void loadPaymentData(){
        tableModel.setRowCount(0);
        // Assuming Payment table has columns: payment_id, amount, payment_date, student_id
        String sql = "SELECT payment_id, amount, payment_date, student_id FROM Payment ORDER BY payment_date DESC";
        try(Statement stmt = dbc.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
                int pid = rs.getInt("payment_id");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("payment_date");
                int studentId = rs.getInt("student_id");
                tableModel.addRow(new Object[]{pid, amount, date, studentId});
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + ex.getMessage());
        }
    }
}
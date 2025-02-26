package studentDormitoryManagementSystem;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author min
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import com.toedter.calendar.JDateChooser;

public class PaymentEntryDialog extends JDialog {
    private DBConnect dbc;
    private PaymentManager paymentManager;
    private JTextField amountField, studentIdField, receiptField, paymentTypeField;
    private JButton submitButton, cancelButton;
    private JDateChooser dateChooser;

    public PaymentEntryDialog(Window owner, DBConnect dbc) {
        super(owner, "Add Payment", ModalityType.APPLICATION_MODAL);
        this.dbc = dbc;
        this.paymentManager = new PaymentManager(dbc);
        setSize(350, 300);
        setLocationRelativeTo(owner);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        panel.add(amountField);

        panel.add(new JLabel("Payment Date:"));
        dateChooser = new JDateChooser();
        panel.add(dateChooser);

        panel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        panel.add(studentIdField);

        panel.add(new JLabel("Receipt Number:"));
        receiptField = new JTextField();
        panel.add(receiptField);

        panel.add(new JLabel("Payment Type:"));
        paymentTypeField = new JTextField();
        panel.add(paymentTypeField);

        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitPayment();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void submitPayment() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            java.util.Date utilDate = dateChooser.getDate();
            if (utilDate == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid date.");
                return;
            }
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            int studentId = Integer.parseInt(studentIdField.getText());
            String receipt = receiptField.getText();
            String paymentType = paymentTypeField.getText();

            int result = paymentManager.addPayment(amount, receipt, studentId, paymentType, sqlDate);

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Payment added successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add payment.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }
}
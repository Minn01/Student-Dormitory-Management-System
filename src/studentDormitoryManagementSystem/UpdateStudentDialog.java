/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentDormitoryManagementSystem;

/**
 *
 * @author min
 */
import javax.swing.*;
import java.awt.*;
class UpdateStudentDialog extends JDialog {
    public UpdateStudentDialog(Window owner, int studentId, DBConnect dbc) {
        super(owner, "Update Student", ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emergencyField = new JTextField();
        JButton updateButton = new JButton("Update");
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Emergency Contact:")); panel.add(emergencyField);
        panel.add(updateButton);
        add(panel);
        
        updateButton.addActionListener(e -> {
            StudentManager manager = new StudentManager(dbc);
            int result = manager.updateStudent(studentId, nameField.getText(), emailField.getText(), phoneField.getText(), emergencyField.getText());
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}


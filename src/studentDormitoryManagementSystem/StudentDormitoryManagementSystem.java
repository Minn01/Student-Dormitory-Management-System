/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package studentDormitoryManagementSystem;

/**
 *
 * @author min
 */
public class StudentDormitoryManagementSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello World!!");
        DBConnect dbc = new DBConnect();
        
        StudentManager studentManager = new StudentManager(dbc);
        studentManager.addStudent(6612012, "Min Thant", "u6612012@au.edu", "082-363-1352", "082-363-1352");
    }
    
}

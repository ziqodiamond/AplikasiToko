/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package toko.views;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
/**
 *
 * @author user
 */
class koneksi {
     private static Connection mysqlconfig;
    public static Connection configDB() throws SQLException{
        try{
            String url ="jdbc:mysql://localhost:3306/login1";
            String user ="root";
            String password = "";
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            mysqlconfig=DriverManager.getConnection(url, user, password);
        }catch (Exception e) {
            System.err.println("koneksi gagal"+e.getMessage());
        }
        return mysqlconfig;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}

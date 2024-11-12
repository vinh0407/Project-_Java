package baoCaoTK;

import connectDatabase.ConnectDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class bc2 extends javax.swing.JFrame {

    // Declare connection variables
    ConnectDB con = new ConnectDB();
    Connection conn;
    DefaultTableModel tableModel;

    public bc2() {
        initComponents();
        tableModel = (DefaultTableModel) tblTable.getModel();
        showTable();
    }

    // Method to fetch data and display in table
    public void showTable() {
        tableModel.setRowCount(0);
        conn = con.getConnection();
        try {
            String sql = "{call phongDatMax}";
            CallableStatement statement = conn.prepareCall(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("maPhong"),
                    rs.getString("tenPhong"),
                    rs.getString("dem")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Đóng kết nối thành công");
                } catch (SQLException ex) {
                    Logger.getLogger(bc2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // Initialize the components of the form
    @SuppressWarnings("unchecked")
    private void initComponents() {

        // Set up the JFrame properties
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TOP 3 PHÒNG ĐƯỢC ĐẶT NHIỀU NHẤT");
        setResizable(false);
        setLocationRelativeTo(null); // Center the window

        // Set up the panel and layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Table setup
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTable = new javax.swing.JTable();

        tblTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Mã phòng", "Tên phòng", "Số lần đặt/hẹn"
            }
        ));

        // Customize the table appearance
        tblTable.setRowHeight(30); // Set row height for better readability
        tblTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        tblTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        // Customize column widths
        tblTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Mã phòng
        tblTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên phòng
        tblTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Số lần đặt/hẹn

        // Add table to scroll pane
        jScrollPane1.setViewportView(tblTable);
        panel.add(Box.createVerticalStrut(10)); // Add some spacing between the title and the table
        panel.add(jScrollPane1);

        // Add panel to the content pane
        getContentPane().add(panel);

        pack(); // Adjust window size based on content
    }

    // Main method to run the form
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(bc2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new bc2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTable;
    // End of variables declaration
}

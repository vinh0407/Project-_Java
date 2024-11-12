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

public class bc1 extends javax.swing.JFrame {

    ConnectDB con = new ConnectDB();
    Connection conn;
    DefaultTableModel tableModel;

    /**
     * Creates new form bc1
     */
    public bc1() {
        initComponents();
        tableModel = (DefaultTableModel) tblTable.getModel();
        showTable();
    }

    // Fetch data from the database and populate the table
    public void showTable() {
        tableModel.setRowCount(0);
        conn = con.getConnection();
        try {
            String sql = "{call khachHangDatPhongMax}";
            CallableStatement statement = conn.prepareCall(sql);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("maKH"),
                    rs.getString("tenKH"),
                    rs.getString("sdt"),
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
                    Logger.getLogger(bc1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        // Set up the JFrame and its properties
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TOP 3 KHÁCH HÀNG ĐẶT/HẸN PHÒNG NHIỀU NHẤT");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        setLocationRelativeTo(null); // Center the window

        // Initialize components
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Set up the table model and table
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTable = new javax.swing.JTable();

        tblTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Số lần đặt/hẹn"
            }
        ));
        
        // Set table properties for better readability
        tblTable.setRowHeight(30);
        tblTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        tblTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        // Customize column widths
        tblTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Mã khách hàng
        tblTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên khách hàng
        tblTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Số điện thoại
        tblTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Số lần đặt/hẹn

        jScrollPane1.setViewportView(tblTable);
        panel.add(Box.createVerticalStrut(10)); // Add some vertical spacing
        panel.add(jScrollPane1);

        // Add panel to JFrame content
        getContentPane().add(panel);

        pack(); // Adjust the size based on content
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        // Set the Nimbus look and feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(bc1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new bc1().setVisible(true);
            }
        });
    }

    // Variables declaration
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTable;
}

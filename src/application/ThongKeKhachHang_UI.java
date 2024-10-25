package application;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.table.*;

import DAO.HoaDonPhongDAO;
import connectDB.ConnectDB;
import entity.*;

public class ThongKeKhachHang_UI extends JFrame implements ActionListener, KeyListener {
    JPanel pnMain;
    private JTextField txtMaKH, txtTenKH, txtThanhTien;
    private kDatePicker dpTuNgay, dpDenNgay;
    private DefaultTableModel modelTable;
    private JButton btnThongKe;
    private JTable table;
    private JLabel lbShowMessages;
    private final int SUCCESS = 1, ERROR = 0, NORMAL = 2;
    ImageIcon analyticsIcon = new ImageIcon("data/images/analytics_16.png");
    ImageIcon checkIcon = new ImageIcon("data/images/check2_16.png");
    ImageIcon errorIcon = new ImageIcon("data/images/cancel_16.png");
    private HoaDonPhongDAO hdPhongDAO = new HoaDonPhongDAO();

    public ThongKeKhachHang_UI() {
        try {
            ConnectDB.getInstance().connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setTitle("Thống Kê Tổng Hợp Khách Hàng");
        setSize(1000, 670);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pnMain = new JPanel();
        pnMain.setLayout(null);
        pnMain.setBounds(0, 0, 1000, 670);

        getContentPane().add(pnMain);

        JLabel lbTitle = new JLabel("Báo Cáo Tổng Hợp Khách Hàng");
        lbTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lbTitle.setFont(new Font("Dialog", Font.BOLD, 20));
        lbTitle.setBounds(10, 11, 972, 30);
        pnMain.add(lbTitle);

        JLabel lbMaKH = new JLabel("Mã khách hàng: ");
        lbMaKH.setBounds(10, 52, 110, 16);
        pnMain.add(lbMaKH);

        txtMaKH = new JTextField();
        txtMaKH.setBounds(120, 50, 170, 20);
        pnMain.add(txtMaKH);
        txtMaKH.setColumns(10);

        JLabel lbTuNgay = new JLabel("Từ ngày");
        lbTuNgay.setBounds(10, 80, 55, 16);

        dpTuNgay = new kDatePicker(170);
        dpTuNgay.setBounds(120, 76, 170, 20);
        pnMain.add(dpTuNgay);

        pnMain.add(lbTuNgay);

        JLabel lbTenKH = new JLabel("Tên Khách Hàng:");
        lbTenKH.setBounds(348, 52, 104, 16);
        pnMain.add(lbTenKH);

        JLabel lbDenNgay = new JLabel("Đến ngày:");
        lbDenNgay.setBounds(348, 80, 70, 16);
        pnMain.add(lbDenNgay);

        dpDenNgay = new kDatePicker(170);
        dpDenNgay.setBounds(466, 76, 170, 20);
        pnMain.add(dpDenNgay);

        txtTenKH = new JTextField();
        txtTenKH.setBounds(466, 50, 170, 20);
        pnMain.add(txtTenKH);
        txtTenKH.setColumns(10);

        JPanel pnTable = new JPanel();
        pnTable.setBounds(10, 128, 972, 458);
        pnMain.add(pnTable);
        pnTable.setLayout(new BorderLayout(0, 0));

        // mã hóa đơn phòng
        String[] cols = { "Mã HD", "Mã phòng", "Loại phòng", "Giá phòng", "Ngày CheckIn", "Ngày CheckOut", "Số Ngày",
                "Thành tiền", "Mã KH", "Tên KH" };
        modelTable = new DefaultTableModel(cols, 0) {
            // khóa sửa dữ liệu trực tiếp trên table
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };

        table = new JTable(modelTable);
        JScrollPane scpTableDV = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pnTable.add(scpTableDV, BorderLayout.CENTER);

        JPanel pnThongKe = new JPanel();
        pnThongKe.setBounds(10, 589, 972, 40);
        pnMain.add(pnThongKe);
        pnThongKe.setLayout(null);

        JLabel lbTongDoanhThu = new JLabel("Tổng doanh thu:");
        lbTongDoanhThu.setBounds(0, 12, 105, 16);
        pnThongKe.add(lbTongDoanhThu);

        txtThanhTien = new JTextField();
        txtThanhTien.setBounds(100, 10, 205, 20);
        txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
        txtThanhTien.setText("0.0");
        txtThanhTien.setEditable(false);
        txtThanhTien.setColumns(10);
        txtThanhTien.setBackground(new Color(127, 255, 212));
        pnThongKe.add(txtThanhTien);

        // JLabel lbA = new JLabel("345678", blueAddIcon, JLabel.LEFT);
        JLabel lbVND = new JLabel("VND");
        lbVND.setBounds(309, 12, 35, 16);
        pnThongKe.add(lbVND);

        btnThongKe = new JButton("Thống kê", analyticsIcon);
        btnThongKe.setBounds(793, 0, 179, 40);
        pnThongKe.add(btnThongKe);

        lbShowMessages = new JLabel("");
        lbShowMessages.setBounds(10, 107, 626, 14);
        pnMain.add(lbShowMessages);

        btnThongKe.addActionListener(this);
        try {
            getListSearchByDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        reSizeColumnTable();
    }

    public static void main(String[] args) {
        new ThongKeKhachHang_UI().setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThongKe)) {
            showMessage("", NORMAL);
            modelTable.getDataVector().removeAllElements();
            modelTable.fireTableDataChanged();
            String maKH = txtMaKH.getText().trim();
            String tenKH = txtTenKH.getText().trim();
            ArrayList<HoaDonPhong> dataList = null;
            try {
                Date tuNgay = dpTuNgay.getFullDate();
                Date denNgay = dpDenNgay.getFullDate();

                if (validData()) {
                    if (!maKH.isEmpty() && tenKH.isEmpty()) {
                        dataList = getListSearchByMaKH();
                    } else if (!tenKH.isEmpty() && maKH.isEmpty()) {
                        dataList = getListSearchByTenKH();
                    } else if (maKH.isEmpty() && tenKH.isEmpty()) {
                        dataList = getListSearchByDate();
                    } else if (!(maKH.isEmpty() && tenKH.isEmpty())) {
                        dataList = getListSearchByMaKHAndTenKH();
                    }
                    if (dataList == null || dataList.isEmpty() || dataList.size() <= 0) {
                        showMessage("Không tìm thấy danh sách thống kê theo yêu cầu", ERROR);
                    } else
                        DocDuLieuVaoTable(dataList);
                } else {
                    if (denNgay.before(tuNgay)) {
                        dpDenNgay.setValueToDay();
                        JOptionPane.showMessageDialog(this, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu",
                                "Cảnh báo", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Object o = e.getSource();
        Object key = e.getKeyCode();
        // bắt sự kiện nhấn phím enter tự nhấn btnLogin
        if (o.equals(txtMaKH) | o.equals(txtTenKH)) {
            if (key.equals(KeyEvent.VK_ENTER)) {
                btnThongKe.doClick();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private ArrayList<HoaDonPhong> getListSearchByDate() throws ParseException {
        Date tuNgay = dpTuNgay.getFullDate();
        Date denNgay = dpDenNgay.getFullDate();
        ArrayList<HoaDonPhong> dataList = hdPhongDAO.getListHDPhongByDate(tuNgay, denNgay);
        return dataList;
    }

    private ArrayList<HoaDonPhong> getListSearchByMaKH() throws ParseException {
        Date tuNgay = dpTuNgay.getFullDate();
        Date denNgay = dpDenNgay.getFullDate();
        int maKH = Integer.parseInt(txtMaKH.getText().trim());
        ArrayList<HoaDonPhong> dataList = hdPhongDAO.getListHDPhongByMaKH(maKH, tuNgay, denNgay);
        return dataList;
    }

    private ArrayList<HoaDonPhong> getListSearchByTenKH() throws ParseException {
        Date tuNgay = dpTuNgay.getFullDate();
        Date denNgay = dpDenNgay.getFullDate();
        String tenKH = txtTenKH.getText().trim();
        ArrayList<HoaDonPhong> dataList = hdPhongDAO.getListHDPhongByTenKH(tenKH, tuNgay, denNgay);
        return dataList;
    }

    private ArrayList<HoaDonPhong> getListSearchByMaKHAndTenKH() throws ParseException {
        Date tuNgay = dpTuNgay.getFullDate();
        Date denNgay = dpDenNgay.getFullDate();
        int maKH = Integer.parseInt(txtMaKH.getText().trim());
        String tenKH = txtTenKH.getText().trim();
        ArrayList<HoaDonPhong> dataList = hdPhongDAO.getListHDPhongByMaKHAndTenKH(maKH, tenKH, tuNgay, denNgay);
        return dataList;
    }

    private void DocDuLieuVaoTable(ArrayList<HoaDonPhong> dataList) {
        Double sum = 0.0;
        DecimalFormat df = new DecimalFormat("#,###.##");
        for (HoaDonPhong item : dataList) {
            Phong phong = item.getPhong();
            LoaiPhong lPhong = item.getPhong().getLoaiPhong();
            KhachHang kh = item.getKhachHang();
            String ngayGioNhan = formatDate(item.getNgayGioNhan());
            String ngayGioTra = formatDate(item.getNgayGioTra());
            int soNgay = (int) tinhSoNgay(item.getNgayGioNhan(), item.getNgayGioTra());
            Double thanhTien = lPhong.getDonGia() * soNgay;
            String tien = df.format(thanhTien);
            sum += thanhTien;

            String donGia = df.format(lPhong.getDonGia());
            modelTable.addRow(new Object[] { item.getMaHD(), phong.getMaPhong(), lPhong.getTenLoaiPhong(), donGia,
                    ngayGioNhan, ngayGioTra, soNgay, tien, kh.getMaKH(), kh.getTenKH() });
        }
        String thanhTien = df.format(sum);
        txtThanhTien.setText(thanhTien);
    }

    private String formatDate(Date date) {
        if (date == null)
            return "Chưa cập nhật";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private void showMessage(String message, int type) {
        if (type == SUCCESS) {
            lbShowMessages.setForeground(Color.GREEN);
            lbShowMessages.setIcon(checkIcon);
        } else if (type == ERROR) {
            lbShowMessages.setForeground(Color.RED);
            lbShowMessages.setIcon(errorIcon);
        } else {
            lbShowMessages.setForeground(Color.BLACK);
            lbShowMessages.setIcon(null);
        }
        lbShowMessages.setText(message);
    }

    private long tinhSoNgay(Date tuNgay, Date denNgay) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        long millis = System.currentTimeMillis();
        if (tuNgay == null) {
            tuNgay = new Date(millis);
        }
        if (denNgay == null) {
            denNgay = new Date(millis);
        }
        cal1.setTime(tuNgay);
        cal2.setTime(denNgay);
        long result = (cal2.getTime().getTime() - cal1.getTime().getTime()) / (24 * 3600 * 1000);
        return result <= 0 ? 1 : result;
    }

    private boolean validData() throws ParseException {
        Date tuNgay = dpTuNgay.getFullDate();
        Date denNgay = dpDenNgay.getFullDate();
        if (denNgay.before(tuNgay)) {
            return false;
        }
        return true;
    }

    private void reSizeColumnTable() {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(65);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(95);
        table.getColumnModel().getColumn(6).setPreferredWidth(65);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);
        table.getColumnModel().getColumn(8).setPreferredWidth(70);
        table.getColumnModel().getColumn(9).setPreferredWidth(204);
    }
}

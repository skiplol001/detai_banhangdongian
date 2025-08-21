package model;

import model.KhachHang;
import javax.swing.*;
import java.awt.*;
public class GiaoDienBanHang extends JDialog {

    private boolean daBanHang = false;
    private String vatPhamDuocChon;

    public GiaoDienBanHang(JFrame parent, KhachHang khachHang) {
        super(parent, "Bán vật phẩm cho " + khachHang.getTen(), true);

        setLayout(new BorderLayout());
        setSize(400, 300);

        // Panel thông tin khách hàng
        JPanel pnlThongTin = new JPanel(new GridLayout(0, 1));
        pnlThongTin.add(new JLabel("Khách hàng: " + khachHang.getTen()));
        pnlThongTin.add(new JLabel("Mã KH: " + khachHang.getMaKH()));
        add(pnlThongTin, BorderLayout.NORTH);

        // Panel vật phẩm
        JPanel pnlVatPham = new JPanel(new GridLayout(0, 1));
        String[] dsVatPham = {"Snack", "Thuốc", "Nước suối", "Bánh mì"};

        ButtonGroup group = new ButtonGroup();
        for (String vp : dsVatPham) {
            JRadioButton rb = new JRadioButton(vp);
            rb.addActionListener(e -> vatPhamDuocChon = vp);
            group.add(rb);
            pnlVatPham.add(rb);
        }

        add(new JScrollPane(pnlVatPham), BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnlNut = new JPanel();
        JButton btnBan = new JButton("Bán");
        btnBan.addActionListener(e -> {
            if (vatPhamDuocChon != null) {
                daBanHang = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn vật phẩm!");
            }
        });
        pnlNut.add(btnBan);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        pnlNut.add(btnHuy);

        add(pnlNut, BorderLayout.SOUTH);
    }

    public boolean isDaBanHang() {
        return daBanHang;
    }

    public String getVatPhamDuocChon() {
        return vatPhamDuocChon;
    }
}

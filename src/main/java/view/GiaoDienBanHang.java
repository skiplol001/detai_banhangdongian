package view;

import model.KhachHang;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GiaoDienBanHang extends JDialog {

    private boolean daBanHang = false;
    private String vatPhamDuocChon;
    private List<String> dsVatPham;

    public GiaoDienBanHang(JFrame parent, KhachHang khachHang) {
        super(parent, "Bán vật phẩm cho " + khachHang.getTen(), true);

        // Đọc danh sách vật phẩm từ file
        dsVatPham = docDanhSachVatPham();

        setLayout(new BorderLayout());
        setSize(400, 300);

        // Panel thông tin khách hàng
        JPanel pnlThongTin = new JPanel(new GridLayout(0, 1));
        pnlThongTin.add(new JLabel("Khách hàng: " + khachHang.getTen()));
        pnlThongTin.add(new JLabel("Mã KH: " + khachHang.getMaKH()));
        add(pnlThongTin, BorderLayout.NORTH);

        // Panel vật phẩm
        JPanel pnlVatPham = new JPanel(new GridLayout(0, 1));

        if (dsVatPham.isEmpty()) {
            pnlVatPham.add(new JLabel("Không có vật phẩm nào để bán!"));
        } else {
            ButtonGroup group = new ButtonGroup();
            for (String vp : dsVatPham) {
                JRadioButton rb = new JRadioButton(vp);
                rb.addActionListener(e -> vatPhamDuocChon = vp);
                group.add(rb);
                pnlVatPham.add(rb);
            }
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

    private List<String> docDanhSachVatPham() {
        List<String> danhSach = new ArrayList<>();

        // Sử dụng đường dẫn tương đối
        String duongDan = "database/vatpham.txt";
        File file = new File(duongDan);

        // Nếu file không tồn tại, tạo file mặc định
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có
                FileWriter writer = new FileWriter(file);
                writer.write("Bánh mì:50\n");
                writer.write("Nước suối:30\n");
                writer.write("Thuốc:100\n");
                writer.write("Snack:20\n");
                writer.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo file mặc định: " + e.getMessage());
            }
        }

        // Đọc file bằng vòng lặp
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length > 0) {
                    danhSach.add(parts[0].trim()); // Chỉ lấy tên vật phẩm, bỏ khoảng trắng
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đọc file: " + e.getMessage());
            // Trả về danh sách mặc định nếu có lỗi
            danhSach.add("Bánh mì");
            danhSach.add("Nước suối");
            danhSach.add("Thuốc");
            danhSach.add("Snack");
        }

        return danhSach;
    }

    public boolean isDaBanHang() {
        return daBanHang;
    }

    public String getVatPhamDuocChon() {
        return vatPhamDuocChon;
    }

    public List<String> getDanhSachVatPham() {
        return dsVatPham;
    }
}

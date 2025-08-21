package model;

import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import util.TextFileStorage;

public class QuanLyKhachHangService {

    private KhachHang khachHangHienTai;
    private final QuanLyKhachHang quanLyKH;
    private final TextFileStorage.PlayerData playerData;
    private final Random random;
    private final Map<String, String> khachHangYeuCauMap; // Thêm Map mới

    private final String[] danhSachVatPham = {
      "Snack", "Thuốc", "Nước suối", "Bánh mì"
    };

    public QuanLyKhachHangService(TextFileStorage.PlayerData playerData) {
        this.quanLyKH = new QuanLyKhachHang();
        this.playerData = playerData;
        this.random = new Random();
        this.khachHangYeuCauMap = new HashMap<>(); // Khởi tạo Map
    }

    /**
     * Tạo một khách hàng mới ngẫu nhiên và lưu yêu cầu của họ.
     *
     * @return Đối tượng KhachHang mới được tạo.
     */
    public KhachHang taoKhachHangMoi() {
        List<KhachHang> danhSach = QuanLyKhachHang.taiDanhSachKhachHang();
        if (!danhSach.isEmpty()) {
            KhachHang khachMoi = danhSach.get(random.nextInt(danhSach.size()));
            
            // Bước quan trọng: Gán khách hàng mới cho thuộc tính hiện tại của service.
            this.khachHangHienTai = khachMoi; 
            
            // Lưu yêu cầu ngẫu nhiên vào Map, sử dụng mã KH để liên kết.
            khachHangYeuCauMap.put(khachMoi.getMaKH(), generateRandomYeuCau());
            
            return khachMoi;
        }
        return null;
    }

    /**
     * Tạo một chuỗi yêu cầu ngẫu nhiên với các vật phẩm không trùng lặp.
     *
     * @return Chuỗi yêu cầu.
     */
    private String generateRandomYeuCau() {
        StringBuilder yeuCau = new StringBuilder("Tôi muốn mua: ");
        
        // Sử dụng Collections.shuffle để chọn ngẫu nhiên các vật phẩm không trùng lặp
        List<String> vatPhamList = Arrays.asList(danhSachVatPham);
        Collections.shuffle(vatPhamList);
        
        // Chọn ngẫu nhiên từ 1 đến 4 vật phẩm
        int soLuongVatPham = random.nextInt(4) + 1;

        for (int i = 0; i < soLuongVatPham; i++) {
            yeuCau.append(vatPhamList.get(i));
            if (i < soLuongVatPham - 1) {
                yeuCau.append(", ");
            }
        }
        return yeuCau.toString();
    }

    /**
     * Lấy yêu cầu của khách hàng hiện tại.
     *
     * @return Chuỗi yêu cầu hoặc null nếu không có khách hàng.
     */
    public String layYeuCauKhachHangHienTai() {
        if (khachHangHienTai == null) {
            return null;
        }
        return khachHangYeuCauMap.get(khachHangHienTai.getMaKH());
    }

    public void setKhachHangHienTai(KhachHang khachHang) {
        this.khachHangHienTai = khachHang;
    }

    /**
     * Lấy thông tin chi tiết của khách hàng hiện tại.
     *
     * @return Chuỗi HTML chứa thông tin khách hàng.
     */
    public String layThongTinKhachHang() {
        if (khachHangHienTai == null) {
            return null;
        }

        String yeuCauHienTai = layYeuCauKhachHangHienTai();
        
        return String.format("<html><b>THÔNG TIN KIỂM TRA</b><br><br>"
                + "<b>Tên:</b> %s<br>"
                + "<b>Tuổi:</b> %d<br>"
                + "<b>Mã KH:</b> %s<br>"
                + "<b>Loại:</b> %s<br>"
                + "<b>Yêu cầu:</b> %s",
                khachHangHienTai.getTen(),
                khachHangHienTai.getTuoi(),
                khachHangHienTai.getMaKH(),
                khachHangHienTai.isLaVong() ? "Vong" : "Người thường",
                yeuCauHienTai != null ? yeuCauHienTai : "Chưa có yêu cầu");
    }

    public KhachHang getKhachHangHienTai() {
        return this.khachHangHienTai;
    }

    public boolean xuLyBanHang(boolean quyetDinhBan) {
        if (khachHangHienTai == null) {
            return false;
        }

        if (quyetDinhBan) {
            handleSuccessfulSale();
        } else {
            handleRejectedSale();
        }
        
        // Sau khi giao dịch hoàn tất, reset trạng thái
        khachHangYeuCauMap.remove(khachHangHienTai.getMaKH());
        this.khachHangHienTai = null;

        return true;
    }

    private void handleSuccessfulSale() {
        if (khachHangHienTai.isLaVong()) {
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 10);
        } else {
            playerData.money += 50;
            playerData.mentalPoints = Math.min(100, playerData.mentalPoints + 5);
        }
    }

    private void handleRejectedSale() {
        if (!khachHangHienTai.isLaVong()) {
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 5);
        }
    }
}
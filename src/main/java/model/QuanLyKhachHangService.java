package model;

import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import util.TextFileStorage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class QuanLyKhachHangService {

    private KhachHang khachHangHienTai;
    private final QuanLyKhachHang quanLyKH;
    private final TextFileStorage.PlayerData playerData;
    private final Random random;

    private List<String> danhSachVatPham;
    private Map<String, Integer> giaVatPham;

    public QuanLyKhachHangService(TextFileStorage.PlayerData playerData) {
        this.quanLyKH = new QuanLyKhachHang();
        this.playerData = playerData;
        this.random = new Random();
        this.danhSachVatPham = new ArrayList<>();
        this.giaVatPham = new HashMap<>();
        taiDanhSachVatPhamTuFile();
    }

    private void taiDanhSachVatPhamTuFile() {
        try {
            // Sử dụng đường dẫn tương đối
            File file = new File("database/vatpham.txt");
            if (!file.exists()) {
                file = new File("../database/vatpham.txt"); // Thử đường dẫn khác nếu cần
            }

            List<String> lines = Files.readAllLines(file.toPath());

            for (String line : lines) {
                // Bỏ qua dòng trống và dòng comment
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("#")) {
                    // Tách tên vật phẩm và giá bằng dấu :
                    String[] parts = trimmedLine.split(":");
                    if (parts.length == 2) {
                        String tenVatPham = parts[0].trim();
                        try {
                            int gia = Integer.parseInt(parts[1].trim());
                            danhSachVatPham.add(tenVatPham);
                            giaVatPham.put(tenVatPham, gia);
                        } catch (NumberFormatException e) {
                            System.err.println("Lỗi định dạng giá trong file: " + trimmedLine);
                        }
                    }
                }
            }

            // Nếu file rỗng, sử dụng danh sách mặc định
            if (danhSachVatPham.isEmpty()) {
                danhSachVatPham = Arrays.asList("Snack", "Thuốc", "Nước suối", "Bánh mì");
                giaVatPham.put("Snack", 20);
                giaVatPham.put("Thuốc", 100);
                giaVatPham.put("Nước suối", 30);
                giaVatPham.put("Bánh mì", 50);
                System.out.println("Sử dụng danh sách vật phẩm mặc định");
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file vatpham.txt: " + e.getMessage());
            // Fallback: sử dụng danh sách mặc định nếu không đọc được file
            danhSachVatPham = Arrays.asList("Snack", "Thuốc", "Nước suối", "Bánh mì");
            giaVatPham.put("Snack", 20);
            giaVatPham.put("Thuốc", 100);
            giaVatPham.put("Nước suối", 30);
            giaVatPham.put("Bánh mì", 50);
            System.out.println("Sử dụng danh sách vật phẩm mặc định do lỗi đọc file");
        }
    }

    private HashMap<String, Integer> generateRandomYeuCauMap() {
        HashMap<String, Integer> requiredItems = new HashMap<>();

        // Kiểm tra nếu danh sách vật phẩm rỗng
        if (danhSachVatPham.isEmpty()) {
            System.out.println("Danh sách vật phẩm trống, không thể tạo yêu cầu");
            return requiredItems;
        }

        // Tạo danh sách tạm để xáo trộn
        List<String> vatPhamList = new ArrayList<>(danhSachVatPham);
        Collections.shuffle(vatPhamList);

        int soLuongVatPham = random.nextInt(Math.min(4, vatPhamList.size())) + 1;

        for (int i = 0; i < soLuongVatPham; i++) {
            String vatPham = vatPhamList.get(i);
            requiredItems.put(vatPham, 1);
        }

        return requiredItems;
    }

    public KhachHang taoKhachHangMoi() {
        List<KhachHang> danhSach = QuanLyKhachHang.layDanhSachKhachHangHomNay();
        if (danhSach != null && !danhSach.isEmpty()) {
            KhachHang khachMoi = danhSach.get(random.nextInt(danhSach.size()));
            khachMoi.setVatPhamYeuCau(generateRandomYeuCauMap());
            this.khachHangHienTai = khachMoi;
            return khachMoi;
        }
        return null;
    }

    public String layYeuCauKhachHangHienTai() {
        if (khachHangHienTai == null) {
            return "Chưa có yêu cầu";
        }
        StringBuilder yeuCau = new StringBuilder("Tôi muốn mua: ");
        HashMap<String, Integer> vatPham = khachHangHienTai.getVatPhamYeuCau();
        int count = 0;
        for (Map.Entry<String, Integer> entry : vatPham.entrySet()) {
            yeuCau.append(entry.getKey());
            if (count < vatPham.size() - 1) {
                yeuCau.append(", ");
            }
            count++;
        }
        return yeuCau.toString();
    }

    public void setKhachHangHienTai(KhachHang khachHang) {
        this.khachHangHienTai = khachHang;
    }

    public KhachHang getKhachHangHienTai() {
        return this.khachHangHienTai;
    }

    public boolean xuLyBanHang(boolean quyetDinhBan, Map<String, Integer> inventory) {
        if (khachHangHienTai == null) {
            return false;
        }

        if (quyetDinhBan) {
            if (!handleSuccessfulSale(inventory)) {
                return false;
            }
        } else {
            handleRejectedSale();
        }

        this.khachHangHienTai = null;
        TextFileStorage.savePlayerData(playerData);
        return true;
    }

    private boolean handleSuccessfulSale(Map<String, Integer> inventory) {
        HashMap<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.getOrDefault(itemName, 0);

            if (currentQuantity < requiredQuantity) {
                return false;
            }
        }

        // Tính tổng tiền bán được
        int totalMoney = 0;
        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.get(itemName);
            int itemPrice = giaVatPham.getOrDefault(itemName, 50); // Mặc định 50 nếu không tìm thấy giá

            totalMoney += itemPrice * requiredQuantity;

            if (currentQuantity == requiredQuantity) {
                inventory.remove(itemName);
            } else {
                inventory.put(itemName, currentQuantity - requiredQuantity);
            }
        }

        if (khachHangHienTai.isLaVong()) {
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 10);
        } else {
            playerData.money += totalMoney;
            playerData.mentalPoints = Math.min(100, playerData.mentalPoints + 5);
        }

        return true;
    }

    private void handleRejectedSale() {
        if (!khachHangHienTai.isLaVong()) {
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 5);
        }
    }

    public String kiemTraDuVatPham(Map<String, Integer> inventory) {
        if (khachHangHienTai == null) {
            return "Chưa có khách hàng";
        }

        StringBuilder missingItems = new StringBuilder();
        HashMap<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.getOrDefault(itemName, 0);

            if (currentQuantity < requiredQuantity) {
                int missing = requiredQuantity - currentQuantity;
                missingItems.append("Thiếu ").append(missing).append(" ").append(itemName).append("\n");
            }
        }

        if (missingItems.length() == 0) {
            return "Bạn có đủ vật phẩm! Có thể bán.";
        } else {
            return "Vật phẩm thiếu:\n" + missingItems.toString();
        }
    }

    public boolean kiemTraCoTheBan(Map<String, Integer> inventory) {
        if (khachHangHienTai == null) {
            return false;
        }

        HashMap<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.getOrDefault(itemName, 0);

            if (currentQuantity < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    // Thêm phương thức để lấy giá vật phẩm
    public int getGiaVatPham(String tenVatPham) {
        return giaVatPham.getOrDefault(tenVatPham, 50); // Mặc định 50 nếu không tìm thấy
    }

    // Thêm phương thức để lấy danh sách vật phẩm (nếu cần)
    public List<String> getDanhSachVatPham() {
        return new ArrayList<>(danhSachVatPham);
    }

    // Thêm phương thức để lấy map giá vật phẩm (nếu cần)
    public Map<String, Integer> getGiaVatPhamMap() {
        return new HashMap<>(giaVatPham);
    }
}

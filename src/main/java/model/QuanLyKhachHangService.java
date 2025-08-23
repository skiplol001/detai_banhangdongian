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

    private final String[] danhSachVatPham = {
        "Snack", "Thuốc", "Nước suối", "Bánh mì"
    };

    public QuanLyKhachHangService(TextFileStorage.PlayerData playerData) {
        this.quanLyKH = new QuanLyKhachHang();
        this.playerData = playerData;
        this.random = new Random();
    }

    private HashMap<String, Integer> generateRandomYeuCauMap() {
        HashMap<String, Integer> requiredItems = new HashMap<>();
        List<String> vatPhamList = Arrays.asList(danhSachVatPham);
        Collections.shuffle(vatPhamList);
        int soLuongVatPham = random.nextInt(4) + 1;
        for (int i = 0; i < soLuongVatPham; i++) {
            requiredItems.put(vatPhamList.get(i), 1);
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

    // QUAN TRỌNG: Đổi tất cả HashMap thành Map
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

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.get(itemName);

            if (currentQuantity == requiredQuantity) {
                inventory.remove(itemName);
            } else {
                inventory.put(itemName, currentQuantity - requiredQuantity);
            }
        }

        if (khachHangHienTai.isLaVong()) {
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 10);
        } else {
            playerData.money += 50;
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
}

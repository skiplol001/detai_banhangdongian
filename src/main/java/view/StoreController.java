package view;

import model.Player;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JOptionPane;

public class StoreController {

    // ========== CẤU HÌNH THÔNG SỐ MẶC CẢ ==========
    private static final int MAX_SUCCESS_RATE = 50;           // Tỉ lệ thành công tối đa (%)
    private static final int SUCCESS_RATE_REDUCTION = 1;      // Mỗi 1% giảm giá giảm bao nhiêu % tỉ lệ thành công
    private static final int MAX_DISCOUNT_PERCENT = 50;       // Giảm giá tối đa (%)
    // ==============================================

    private final Player player;
    private final Random random;
    private List<String> dsVatPham;
    private Map<String, Integer> giaVatPham;

    public StoreController(Player player) {
        this.player = player;
        this.random = new Random();
        this.dsVatPham = new ArrayList<>();
        this.giaVatPham = new HashMap<>();
        docDanhSachVatPhamVaGia();
    }

    public final void docDanhSachVatPhamVaGia() {
        dsVatPham.clear();
        giaVatPham.clear();

        // Sử dụng đường dẫn tương đối
        String duongDan = "database/vatpham.txt";
        File file = new File(duongDan);

        // Nếu file không tồn tại, tạo file mặc định
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                FileWriter writer = new FileWriter(file);
                writer.write("Bánh mì:50\n");
                writer.write("Nước suối:30\n");
                writer.write("Thuốc:100\n");
                writer.write("Snack:20\n");
                writer.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi tạo file mặc định: " + e.getMessage());
            }
        }

        // Đọc file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String tenVatPham = parts[0];
                    int gia = Integer.parseInt(parts[1]);
                    dsVatPham.add(tenVatPham);
                    giaVatPham.put(tenVatPham, gia);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi đọc file: " + e.getMessage());
            // Thiết lập giá trị mặc định nếu có lỗi
            dsVatPham.add("Bánh mì");
            dsVatPham.add("Nước suối");
            dsVatPham.add("Thuốc");
            dsVatPham.add("Snack");
            giaVatPham.put("Bánh mì", 50);
            giaVatPham.put("Nước suối", 30);
            giaVatPham.put("Thuốc", 100);
            giaVatPham.put("Snack", 20);
        }
    }

    public List<String> getDsVatPham() {
        return dsVatPham;
    }

    public Map<String, Integer> getGiaVatPham() {
        return giaVatPham;
    }

    public Player getPlayer() {
        return player;
    }

    public void handleBuy(String itemName, int price) {
        if (player.getMoney() >= price) {
            player.addItem(itemName, 1);
            player.setMoney(player.getMoney() - price);
            JOptionPane.showMessageDialog(null, "Đã mua " + itemName + "!");
        } else {
            JOptionPane.showMessageDialog(null, "Không đủ tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sellItem(String itemName) {
        int price = giaVatPham.getOrDefault(itemName, 0);

        if (player.getInventory().getOrDefault(itemName, 0) > 0) {
            player.removeItem(itemName, 1);
            player.setMoney(player.getMoney() + price);
            JOptionPane.showMessageDialog(null, "Đã bán " + itemName + " với giá " + price + "$!");
        } else {
            JOptionPane.showMessageDialog(null, "Không có vật phẩm để bán", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean attemptBargain(String itemName, int discount) {
        int originalPrice = giaVatPham.get(itemName);
        int successRate = calculateSuccessRate(itemName, discount);
        int newPrice = originalPrice - discount;

        if (successRate <= 0) {
            JOptionPane.showMessageDialog(null, "Không thể mặc cả giảm nhiều như vậy!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Random xem có thành công không
        boolean success = random.nextInt(100) < successRate;

        if (success) {
            // Mua với giá mới
            if (player.getMoney() >= newPrice) {
                player.addItem(itemName, 1);
                player.setMoney(player.getMoney() - newPrice);

                // Hiển thị thông tin chi tiết
                double discountPercent = (double) discount / originalPrice * 100;
                String message = String.format(
                        "Mặc cả thành công!\n"
                        + "Mua %s với giá %d$ (giảm %d$ - %.1f%%)\n"
                        + "Tỉ lệ thành công: %d%%",
                        itemName, newPrice, discount, discountPercent, successRate
                );
                JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Mặc cả thành công nhưng không đủ tiền để mua!", "Thất bại", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            // MẶC CẢ THẤT BẠI - TRỪ TIỀN NGƯỜI CHƠI DỰA VÀO GIÁ MỚI
            if (player.getMoney() >= newPrice) {
                player.setMoney(player.getMoney() - newPrice);

                double discountPercent = (double) discount / originalPrice * 100;
                String message = String.format(
                        "Mặc cả thất bại!\n"
                        + "Chủ cửa hàng không đồng ý giảm %d$ (%.1f%%)\n"
                        + "Tỉ lệ thành công chỉ có %d%%\n\n"
                        + "Bạn đã bị trừ %d$ tiền phạt!",
                        discount, discountPercent, successRate, newPrice
                );
                JOptionPane.showMessageDialog(null, message, "Thất bại", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Mặc cả thất bại và bạn không đủ tiền để trả phạt!\nBạn đã bị phá sản!",
                        "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    public int getMaxDiscount(String itemName) {
        int originalPrice = giaVatPham.get(itemName);
        return (originalPrice * MAX_DISCOUNT_PERCENT) / 100; // Giảm tối đa theo %
    }

    public int calculateSuccessRate(String itemName, int discount) {
        int originalPrice = giaVatPham.get(itemName);

        if (originalPrice == 0) {
            return 0; // Tránh chia cho 0
        }
        // Tính phần trăm giảm giá
        double discountPercent = (double) discount / originalPrice * 100;

        // Tỉ lệ thành công = MAX_SUCCESS_RATE - (discountPercent * SUCCESS_RATE_REDUCTION)
        int successRate = MAX_SUCCESS_RATE - (int) Math.round(discountPercent * SUCCESS_RATE_REDUCTION);

        return Math.max(0, Math.min(MAX_SUCCESS_RATE, successRate)); // Đảm bảo trong khoảng 0-50
    }

    public int getNewPriceAfterDiscount(String itemName, int discount) {
        int originalPrice = giaVatPham.get(itemName);
        return originalPrice - discount;
    }

    public String getBargainInfo(String itemName, int discount) {
        int originalPrice = giaVatPham.get(itemName);
        int newPrice = originalPrice - discount;
        int successRate = calculateSuccessRate(itemName, discount);

        return String.format("Giá gốc: %d$ → Giá mới: %d$ (Giảm %d$)\nTỉ lệ thành công: %d%%",
                originalPrice, newPrice, discount, successRate);
    }

    // Phương thức để xem thông tin cấu hình (tuỳ chọn)
    public String getBargainConfigInfo() {
        return String.format(
                "Cấu hình mặc cả hiện tại:\n"
                + "- Tỉ lệ thành công tối đa: %d%%\n"
                + "- Mỗi 1%% giảm giá giảm %d%% tỉ lệ thành công\n"
                + "- Giảm giá tối đa: %d%%",
                MAX_SUCCESS_RATE, SUCCESS_RATE_REDUCTION, MAX_DISCOUNT_PERCENT
        );
    }

    // Các phương thức để điều chỉnh cấu hình (nếu muốn cho phép thay đổi động)
    public static int getMaxSuccessRate() {
        return MAX_SUCCESS_RATE;
    }

    public static int getSuccessRateReduction() {
        return SUCCESS_RATE_REDUCTION;
    }

    public static int getMaxDiscountPercent() {
        return MAX_DISCOUNT_PERCENT;
    }
}

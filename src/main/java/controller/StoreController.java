package controller;

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
    private static final int MAX_SUCCESS_RATE = 50;
    private static final int SUCCESS_RATE_REDUCTION = 1;
    private static final int MAX_DISCOUNT_PERCENT = 50;
    private static final int MAX_BARGAIN_ATTEMPTS_PER_DAY = 5;
    // ==============================================

    private final Random random;
    private List<String> dsVatPham;
    private Map<String, Integer> giaVatPham;
    private Map<String, Integer> unlockCosts;
    private int bargainAttemptsToday;
    private Player player;

    // Hệ thống mở khóa và quản lý kho
    private Map<String, Boolean> unlockedItems;
    private Map<String, Integer> stock;

    // Đường dẫn file
    private static final String ITEMS_FILE_PATH = "database/vatpham.txt";
    private static final String UNLOCKED_ITEMS_FILE_PATH = "database/unlockvatpham.txt";

    public StoreController(Player player) {
        this.player = player;
        this.random = new Random();
        this.dsVatPham = new ArrayList<>();
        this.giaVatPham = new HashMap<>();
        this.unlockCosts = new HashMap<>();
        this.bargainAttemptsToday = 0;
        this.unlockedItems = new HashMap<>();
        this.stock = new HashMap<>();

        // Tạo thư mục database nếu chưa tồn tại
        createDatabaseDirectory();

        docDanhSachVatPhamVaGia();
        loadUnlockedItems();
        initializeUnlockSystem();

        // Debug: hiển thị trạng thái mở khóa
        debugUnlockedItems();
    }

    private void createDatabaseDirectory() {
        File databaseDir = new File("database");
        if (!databaseDir.exists()) {
            boolean created = databaseDir.mkdirs();
            if (created) {
                System.out.println("Đã tạo thư mục database");
            }
        }
    }

    public final void docDanhSachVatPhamVaGia() {
        dsVatPham.clear();
        giaVatPham.clear();
        unlockCosts.clear();
        unlockedItems.clear(); // Xóa dữ liệu cũ

        File file = new File(ITEMS_FILE_PATH);

        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write("Bánh mì:50:0\n");
                writer.write("Nước suối:30:0\n");
                writer.write("Thuốc:100:500\n");
                writer.write("Snack:20:300\n");
                writer.write("Cà phê:80:400\n");
                writer.write("Bánh ngọt:60:350\n");
                writer.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi tạo file mặc định: " + e.getMessage());
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String tenVatPham = parts[0].trim();
                    int gia = Integer.parseInt(parts[1].trim());
                    dsVatPham.add(tenVatPham);
                    giaVatPham.put(tenVatPham, gia);

                    if (parts.length >= 3) {
                        int unlockCost = Integer.parseInt(parts[2].trim());
                        unlockCosts.put(tenVatPham, unlockCost);
                    } else {
                        unlockCosts.put(tenVatPham, 0);
                    }

                    // KHỞI TẠO MẶC ĐỊNH TẤT CẢ VẬT PHẨM ĐỀU CHƯA MỞ KHÓA
                    unlockedItems.put(tenVatPham, false);
                }
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi đọc file: " + e.getMessage());
            setupDefaultItems();
        }
    }

    private void setupDefaultItems() {
        dsVatPham.add("Bánh mì");
        dsVatPham.add("Nước suối");
        dsVatPham.add("Thuốc");
        dsVatPham.add("Snack");
        dsVatPham.add("Cà phê");
        dsVatPham.add("Bánh ngọt");

        giaVatPham.put("Bánh mì", 50);
        giaVatPham.put("Nước suối", 30);
        giaVatPham.put("Thuốc", 100);
        giaVatPham.put("Snack", 20);
        giaVatPham.put("Cà phê", 80);
        giaVatPham.put("Bánh ngọt", 60);

        unlockCosts.put("Bánh mì", 0);
        unlockCosts.put("Nước suối", 0);
        unlockCosts.put("Thuốc", 500);
        unlockCosts.put("Snack", 300);
        unlockCosts.put("Cà phê", 400);
        unlockCosts.put("Bánh ngọt", 350);

        // Mặc định tất cả đều chưa mở khóa
        for (String item : dsVatPham) {
            unlockedItems.put(item, false);
        }
    }

    public void initializeUnlockSystem() {
        // Khởi tạo kho hàng cho tất cả vật phẩm
        for (String item : dsVatPham) {
            stock.put(item, 0);
        }

        // Thêm số lượng ban đầu cho vật phẩm đã mở khóa
        for (String item : unlockedItems.keySet()) {
            if (unlockedItems.get(item)) {
                stock.put(item, 10);
            }
        }
    }

    private void loadUnlockedItems() {
        File file = new File(UNLOCKED_ITEMS_FILE_PATH);

        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write("Bánh mì\n");
                writer.write("Nước suối\n");
                writer.close();

                // CHỈ mở khóa các vật phẩm mặc định
                unlockedItems.put("Bánh mì", true);
                unlockedItems.put("Nước suối", true);

                // Đảm bảo các vật phẩm khác vẫn bị khóa
                for (String item : dsVatPham) {
                    if (!item.equals("Bánh mì") && !item.equals("Nước suối")) {
                        unlockedItems.put(item, false);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi tạo file unlock: " + e.getMessage());
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                // Đầu tiên, đặt tất cả vật phẩm về trạng thái chưa mở khóa
                for (String item : dsVatPham) {
                    unlockedItems.put(item, false);
                }

                // Sau đó, chỉ mở khóa những vật phẩm có trong file
                while ((line = reader.readLine()) != null) {
                    String itemName = line.trim();
                    if (dsVatPham.contains(itemName)) {
                        unlockedItems.put(itemName, true);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi đọc file unlock: " + e.getMessage());
            }
        }
    }

    private void saveUnlockedItem(String itemName) {
        // CHỈ lưu vật phẩm đã tồn tại trong danh sách
        if (dsVatPham.contains(itemName)) {
            try (FileWriter writer = new FileWriter(UNLOCKED_ITEMS_FILE_PATH, true)) {
                writer.write(itemName + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi lưu vật phẩm đã mở khóa: " + e.getMessage());
            }
        }
    }

    public int getBalance() {
        return player.getMoney();
    }

    public void setBalance(int amount) {
        player.setMoney(amount);
    }

    public List<String> getDsVatPham() {
        return new ArrayList<>(dsVatPham);
    }

    public Map<String, Integer> getGiaVatPham() {
        return new HashMap<>(giaVatPham);
    }

    public Player getPlayer() {
        return player;
    }

    public int getBargainAttemptsToday() {
        return bargainAttemptsToday;
    }

    public int getRemainingBargainAttempts() {
        return MAX_BARGAIN_ATTEMPTS_PER_DAY - bargainAttemptsToday;
    }

    public void resetBargainAttempts() {
        bargainAttemptsToday = 0;
    }

    public void handleBuy(String itemName, int price) {
        if (!isItemUnlocked(itemName)) {
            JOptionPane.showMessageDialog(null, "Vật phẩm này chưa được mở khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentStock = stock.getOrDefault(itemName, 0);
        if (currentStock <= 0) {
            JOptionPane.showMessageDialog(null, "Hết hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (getBalance() >= price) {
            player.addItem(itemName, 1);
            setBalance(getBalance() - price);
            stock.put(itemName, currentStock - 1);
            JOptionPane.showMessageDialog(null, "Đã mua " + itemName + "!");
        } else {
            JOptionPane.showMessageDialog(null, "Không đủ tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sellItem(String itemName) {
        int price = giaVatPham.getOrDefault(itemName, 0);

        if (player.getInventory().getOrDefault(itemName, 0) > 0) {
            player.removeItem(itemName, 1);
            setBalance(getBalance() + price);
            JOptionPane.showMessageDialog(null, "Đã bán " + itemName + " với giá " + price + "$!");
        } else {
            JOptionPane.showMessageDialog(null, "Không có vật phẩm để bán", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean attemptBargain(String itemName, int discount) {
        if (!isItemUnlocked(itemName)) {
            JOptionPane.showMessageDialog(null, "Vật phẩm này chưa được mở khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (bargainAttemptsToday >= MAX_BARGAIN_ATTEMPTS_PER_DAY) {
            JOptionPane.showMessageDialog(null,
                    "Bạn đã hết lượt mặc cả hôm nay! Hãy quay lại vào ngày mai.",
                    "Hết lượt mặc cả", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int originalPrice = giaVatPham.get(itemName);
        int successRate = calculateSuccessRate(itemName, discount);
        int newPrice = originalPrice - discount;

        if (successRate <= 0) {
            JOptionPane.showMessageDialog(null, "Không thể mặc cả giảm nhiều như vậy!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        bargainAttemptsToday++;
        boolean success = random.nextInt(100) < successRate;

        if (success) {
            if (getBalance() >= newPrice) {
                player.addItem(itemName, 1);
                setBalance(getBalance() - newPrice);

                double discountPercent = (double) discount / originalPrice * 100;
                String message = String.format(
                        "Mặc cả thành công!\nMua %s với giá %d$ (giảm %d$ - %.1f%%)\nTỉ lệ thành công: %d%%\nBạn còn %d lượt mặc cả hôm nay.",
                        itemName, newPrice, discount, discountPercent, successRate, getRemainingBargainAttempts()
                );
                JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Mặc cả thành công nhưng không đủ tiền để mua!", "Thất bại", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            int suggestedPrice = originalPrice - (discount / 2);
            String message = String.format(
                    "Mặc cả thất bại!\nChủ cửa hàng không đồng ý giảm %d$.\nTuy nhiên, ông ấy đề xuất bán với giá %d$ (giảm %d$ so với giá gốc).\n\nBạn có đồng ý mua với giá này không?\nBạn còn %d lượt mặc cả hôm nay.",
                    discount, suggestedPrice, originalPrice - suggestedPrice, getRemainingBargainAttempts()
            );

            int option = JOptionPane.showConfirmDialog(null, message, "Đề xuất giá mới",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                if (getBalance() >= suggestedPrice) {
                    player.addItem(itemName, 1);
                    setBalance(getBalance() - suggestedPrice);
                    JOptionPane.showMessageDialog(null, "Đã mua " + itemName + " với giá " + suggestedPrice + "$!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Bạn không đủ tiền để mua với giá đề xuất!", "Thất bại", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Bạn đã từ chối đề xuất giá.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
    }

    public int getMaxDiscount(String itemName) {
        int originalPrice = giaVatPham.get(itemName);
        return (originalPrice * MAX_DISCOUNT_PERCENT) / 100;
    }

    public int calculateSuccessRate(String itemName, int discount) {
        int originalPrice = giaVatPham.get(itemName);
        if (originalPrice == 0) {
            return 0;
        }

        double discountPercent = (double) discount / originalPrice * 100;
        int successRate = MAX_SUCCESS_RATE - (int) Math.round(discountPercent * SUCCESS_RATE_REDUCTION);
        return Math.max(0, Math.min(MAX_SUCCESS_RATE, successRate));
    }

    public int getNewPriceAfterDiscount(String itemName, int discount) {
        return giaVatPham.get(itemName) - discount;
    }

    public String getBargainInfo(String itemName, int discount) {
        int originalPrice = giaVatPham.get(itemName);
        int newPrice = originalPrice - discount;
        int successRate = calculateSuccessRate(itemName, discount);

        return String.format("Giá gốc: %d$ → Giá mới: %d$ (Giảm %d$)\nTỉ lệ thành công: %d%%\nLượt mặc cả còn lại: %d/%d",
                originalPrice, newPrice, discount, successRate, getRemainingBargainAttempts(), MAX_BARGAIN_ATTEMPTS_PER_DAY);
    }

    public String getBargainConfigInfo() {
        return String.format(
                "Cấu hình mặc cả hiện tại:\n- Tỉ lệ thành công tối đa: %d%%\n- Mỗi 1%% giảm giá giảm %d%% tỉ lệ thành công\n- Giảm giá tối đa: %d%%\n- Số lần mặc cả mỗi ngày: %d",
                MAX_SUCCESS_RATE, SUCCESS_RATE_REDUCTION, MAX_DISCOUNT_PERCENT, MAX_BARGAIN_ATTEMPTS_PER_DAY
        );
    }

    // ========== HỆ THỐNG MỞ KHÓA VÀ QUẢN LÝ KHO ==========
    public boolean isItemUnlocked(String itemName) {
        return unlockedItems.getOrDefault(itemName, false);
    }

    public int getUnlockCost(String itemName) {
        return unlockCosts.getOrDefault(itemName, 0);
    }

    public List<String> getLockableItems() {
        List<String> lockable = new ArrayList<>();
        for (String item : dsVatPham) {
            // CHỈ thêm vật phẩm chưa mở khóa VÀ có chi phí mở khóa > 0
            if (!isItemUnlocked(item) && getUnlockCost(item) > 0) {
                lockable.add(item);
            }
        }
        return lockable;
    }

    public boolean unlockItem(String itemName) {
        // KIỂM TRA vật phẩm có tồn tại trong danh sách không
        if (!dsVatPham.contains(itemName)) {
            JOptionPane.showMessageDialog(null, "Vật phẩm không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // KIỂM TRA vật phẩm đã được mở khóa chưa
        if (isItemUnlocked(itemName)) {
            JOptionPane.showMessageDialog(null, "Vật phẩm này đã được mở khóa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        int cost = getUnlockCost(itemName);
        if (getBalance() >= cost) {
            setBalance(getBalance() - cost);
            unlockedItems.put(itemName, true);
            saveUnlockedItem(itemName);
            stock.put(itemName, 10); // Thêm hàng khi mở khóa

            return true;
        }
        return false;
    }

    public Map<String, Integer> getSoLuongHangHoa() {
        return new HashMap<>(stock);
    }

    public void nhapHang(String itemName, int quantity) {
        if (!isItemUnlocked(itemName)) {
            JOptionPane.showMessageDialog(null, "Vật phẩm này chưa được mở khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentStock = stock.getOrDefault(itemName, 0);
        int cost = giaVatPham.get(itemName) * quantity * 80 / 100;

        if (getBalance() >= cost) {
            stock.put(itemName, currentStock + quantity);
            setBalance(getBalance() - cost);
            JOptionPane.showMessageDialog(null, "Đã nhập " + quantity + " " + itemName + " với giá " + cost + "$");
        } else {
            JOptionPane.showMessageDialog(null, "Không đủ tiền để nhập hàng! Cần " + cost + "$", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void traHang(String itemName, int quantity) {
        if (!isItemUnlocked(itemName)) {
            JOptionPane.showMessageDialog(null, "Vật phẩm này chưa được mở khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentStock = stock.getOrDefault(itemName, 0);
        if (quantity <= currentStock) {
            int refund = giaVatPham.get(itemName) * quantity * 70 / 100;
            stock.put(itemName, currentStock - quantity);
            setBalance(getBalance() + refund);
            JOptionPane.showMessageDialog(null, "Đã trả " + quantity + " " + itemName + " và nhận lại " + refund + "$");
        } else {
            JOptionPane.showMessageDialog(null, "Không đủ hàng để trả! Chỉ còn " + currentStock + " trong kho", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static int getMaxSuccessRate() {
        return MAX_SUCCESS_RATE;
    }

    public static int getSuccessRateReduction() {
        return SUCCESS_RATE_REDUCTION;
    }

    public static int getMaxDiscountPercent() {
        return MAX_DISCOUNT_PERCENT;
    }

    public static int getMaxBargainAttemptsPerDay() {
        return MAX_BARGAIN_ATTEMPTS_PER_DAY;
    }

    // Phương thức debug để kiểm tra trạng thái mở khóa
    public void debugUnlockedItems() {
        System.out.println("=== DEBUG UNLOCKED ITEMS ===");
        for (String item : dsVatPham) {
            System.out.println(item + ": " + (isItemUnlocked(item) ? "UNLOCKED" : "LOCKED"));
        }
        System.out.println("===========================");
    }
}

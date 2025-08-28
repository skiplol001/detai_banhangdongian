package util;

import model.Player;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class TextFileStorage {

    static final String DATA_DIR = "database";
    private static final String PLAYER_FILE = DATA_DIR + File.separator + "player_data.txt";
    private static final String INVENTORY_FILE = DATA_DIR + File.separator + "inventory.txt";

    static {
        createDataDirectory();
    }

    private static void createDataDirectory() {
        try {
            Path path = Paths.get(DATA_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Không thể tạo thư mục dữ liệu: " + e.getMessage());
        }
    }

    public static class PlayerData {

        public int money;
        public int mentalPoints;
        public Map<String, Integer> inventory;

        public PlayerData(int money, int mentalPoints, Map<String, Integer> inventory) {
            this.money = money;
            this.mentalPoints = mentalPoints;
            this.inventory = inventory;
        }
    }

    public static PlayerData loadPlayerData() {
        int money = 1000;
        int mentalPoints = 100;
        Map<String, Integer> inventory = new HashMap<>();

        try {
            // Tự động sửa file inventory trước khi đọc
            validateAndFixInventoryFile();

            // Đọc file player_data.txt
            if (Files.exists(Paths.get(PLAYER_FILE))) {
                List<String> playerLines = Files.readAllLines(Paths.get(PLAYER_FILE));

                if (playerLines.size() >= 1 && !playerLines.get(0).trim().isEmpty()) {
                    try {
                        money = Integer.parseInt(playerLines.get(0).trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi parse money, sử dụng mặc định: 1000");
                    }
                }

                if (playerLines.size() >= 2 && !playerLines.get(1).trim().isEmpty()) {
                    try {
                        mentalPoints = Integer.parseInt(playerLines.get(1).trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi parse mentalPoints, sử dụng mặc định: 100");
                    }
                }
            }

            // Đọc file inventory.txt (đã được validate)
            if (Files.exists(Paths.get(INVENTORY_FILE))) {
                List<String> inventoryLines = Files.readAllLines(Paths.get(INVENTORY_FILE));

                for (String line : inventoryLines) {
                    if (line != null && !line.trim().isEmpty()) {
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            try {
                                String itemName = parts[0].trim();
                                int quantity = Integer.parseInt(parts[1].trim());
                                inventory.put(itemName, quantity);
                            } catch (NumberFormatException e) {
                                System.err.println("Lỗi parse inventory: " + line);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc dữ liệu: " + e.getMessage());
        }

        return new PlayerData(money, mentalPoints, inventory);
    }

    // THÊM PHƯƠNG THỨC MỚI ĐỂ LƯU PlayerData
    public static void savePlayerData(PlayerData playerData) {
        try {
            Files.write(Paths.get(PLAYER_FILE),
                    (playerData.money + "\n" + playerData.mentalPoints).getBytes(StandardCharsets.UTF_8), // Thêm StandardCharsets.UTF_8
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            StringBuilder inventoryContent = new StringBuilder();
            for (Map.Entry<String, Integer> entry : playerData.inventory.entrySet()) {
                inventoryContent.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            }
            Files.write(Paths.get(INVENTORY_FILE),
                    inventoryContent.toString().getBytes(StandardCharsets.UTF_8), // Thêm StandardCharsets.UTF_8
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Lỗi lưu dữ liệu: " + e.getMessage());
        }
    }

    // GIỮ NGUYÊN PHƯƠNG THỨC CŨ (nếu cần cho các phần khác)
    public static void savePlayerData(Player player) {
        try {
            Files.write(Paths.get(PLAYER_FILE),
                    (player.getMoney() + "\n" + player.getMentalPoints()).getBytes(StandardCharsets.UTF_8), // Thêm StandardCharsets.UTF_8
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            StringBuilder inventoryContent = new StringBuilder();
            for (Map.Entry<String, Integer> entry : player.getInventory().entrySet()) {
                inventoryContent.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            }
            Files.write(Paths.get(INVENTORY_FILE),
                    inventoryContent.toString().getBytes(StandardCharsets.UTF_8), // Thêm StandardCharsets.UTF_8
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Lỗi lưu dữ liệu: " + e.getMessage());
        }
    }

    public static boolean validateAndFixInventoryFile() {
        try {
            Path inventoryPath = Paths.get(INVENTORY_FILE);

            // Nếu file không tồn tại, tạo file mới
            if (!Files.exists(inventoryPath)) {
                Files.write(inventoryPath, "".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                return true;
            }

            // Đọc và kiểm tra từng dòng
            List<String> lines = Files.readAllLines(inventoryPath, StandardCharsets.UTF_8);
            List<String> validLines = new ArrayList<>();

            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        try {
                            // Kiểm tra nếu số lượng là số hợp lệ
                            Integer.parseInt(parts[1].trim());
                            validLines.add(line);
                        } catch (NumberFormatException e) {
                            System.err.println("Bỏ qua dòng không hợp lệ: " + line);
                        }
                    } else {
                        System.err.println("Bỏ qua dòng không đúng định dạng: " + line);
                    }
                }
            }

            // Ghi lại chỉ các dòng hợp lệ
            Files.write(inventoryPath, validLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi kiểm tra file inventory: " + e.getMessage());
            return false;
        }
    }

    public static void resetInventoryFile() {
        try {
            Path inventoryPath = Paths.get(INVENTORY_FILE);
            Files.deleteIfExists(inventoryPath);
            Files.write(inventoryPath, "".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE); // Ghi với UTF-8
            System.out.println("Đã reset file inventory");
        } catch (IOException e) {
            System.err.println("Lỗi reset inventory: " + e.getMessage());
        }
    }

    public static void resetAllData() {
        try {
            Files.deleteIfExists(Paths.get(PLAYER_FILE));
            Files.deleteIfExists(Paths.get(INVENTORY_FILE));
            System.out.println("Đã reset tất cả dữ liệu");
        } catch (IOException e) {
            System.err.println("Lỗi reset dữ liệu: " + e.getMessage());
        }
    }
}

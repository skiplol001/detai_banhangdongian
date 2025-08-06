package UIvaThuatToan;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TextFileStorage {
    private static final String DATA_DIR = "database"; // Thư mục cùng cấp với ứng dụng
    private static final String PLAYER_FILE = DATA_DIR + File.separator + "player_data.txt";
    private static final String INVENTORY_FILE = DATA_DIR + File.separator + "inventory.txt";

    static {
        createDataDirectory();
    }

    private static void createDataDirectory() {
        try {
            // Tạo thư mục database nếu chưa tồn tại
            Path path = Paths.get(DATA_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Đã tạo thư mục lưu trữ tại: " + path.toAbsolutePath());
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
        int money = 0;
        int mentalPoints = 100; // Giá trị mặc định
        Map<String, Integer> inventory = new HashMap<>();

        try {
            // Đọc file player_data.txt
            if (Files.exists(Paths.get(PLAYER_FILE))) {
                List<String> playerLines = Files.readAllLines(Paths.get(PLAYER_FILE));
                if (playerLines.size() >= 2) {
                    money = Integer.parseInt(playerLines.get(0));
                    mentalPoints = Integer.parseInt(playerLines.get(1));
                }
            }

            // Đọc file inventory.txt
            if (Files.exists(Paths.get(INVENTORY_FILE))) {
                List<String> inventoryLines = Files.readAllLines(Paths.get(INVENTORY_FILE));
                for (String line : inventoryLines) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        inventory.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi đọc dữ liệu: " + e.getMessage());
        }

        return new PlayerData(money, mentalPoints, inventory);
    }

    public static void savePlayerData(Player player) {
        try {
            // Lưu thông tin người chơi
            Files.write(Paths.get(PLAYER_FILE), 
                       (player.getMoney() + "\n" + player.getMentalPoints()).getBytes(),
                       StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Lưu inventory
            StringBuilder inventoryContent = new StringBuilder();
            for (Map.Entry<String, Integer> entry : player.getInventory().entrySet()) {
                inventoryContent.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            }
            Files.write(Paths.get(INVENTORY_FILE), 
                       inventoryContent.toString().getBytes(),
                       StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            System.out.println("Đã lưu dữ liệu vào: " + Paths.get(DATA_DIR).toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Lỗi lưu dữ liệu: " + e.getMessage());
        }
    }
}
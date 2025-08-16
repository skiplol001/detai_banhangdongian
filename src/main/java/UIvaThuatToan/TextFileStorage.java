package UIvaThuatToan;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TextFileStorage {
    private static final String DATA_DIR = "database";
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
        int money = 0;
        int mentalPoints = 100;
        Map<String, Integer> inventory = new HashMap<>();

        try {
            if (Files.exists(Paths.get(PLAYER_FILE))) {
                List<String> playerLines = Files.readAllLines(Paths.get(PLAYER_FILE));
                if (playerLines.size() >= 2) {
                    money = Integer.parseInt(playerLines.get(0));
                    mentalPoints = Integer.parseInt(playerLines.get(1));
                }
            }

            if (Files.exists(Paths.get(INVENTORY_FILE))) {
                List<String> inventoryLines = Files.readAllLines(Paths.get(INVENTORY_FILE));
                for (String line : inventoryLines) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        inventory.put(parts[0], Integer.valueOf(parts[1]));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi đọc dữ liệu: " + e.getMessage());
        }

        return new PlayerData(money, mentalPoints, inventory);
    }

    public static void savePlayerData(Player player) {
        try {
            Files.write(Paths.get(PLAYER_FILE), 
                       (player.getMoney() + "\n" + player.getMentalPoints()).getBytes(),
                       StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            StringBuilder inventoryContent = new StringBuilder();
            for (Map.Entry<String, Integer> entry : player.getInventory().entrySet()) {
                inventoryContent.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            }
            Files.write(Paths.get(INVENTORY_FILE), 
                       inventoryContent.toString().getBytes(),
                       StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Lỗi lưu dữ liệu: " + e.getMessage());
        }
    }
}
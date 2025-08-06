package UIvaThuatToan;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private int money;
    private int mentalPoints;
    private Map<String, Integer> inventory;

    public Player(int initialMoney) {
        TextFileStorage.PlayerData data = TextFileStorage.loadPlayerData();
        this.money = data.money > 0 ? data.money : initialMoney;
        this.mentalPoints = data.mentalPoints;
        this.inventory = data.inventory != null ? data.inventory : new HashMap<>();
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMentalPoints() {
        return mentalPoints;
    }

    public void setMentalPoints(int mentalPoints) {
        this.mentalPoints = mentalPoints;
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void addItem(String itemName, int quantity) {
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + quantity);
    }

    public void removeItem(String itemName, int quantity) {
        int current = inventory.getOrDefault(itemName, 0);
        if (current <= quantity) {
            inventory.remove(itemName);
        } else {
            inventory.put(itemName, current - quantity);
        }
    }

    public void saveToDB() {
        TextFileStorage.savePlayerData(this);
    }
}

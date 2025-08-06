package UIvaThuatToan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class GameUI extends JFrame {

    private final Player player;
    private final HashMap<String, Item> shopItems = new HashMap<>();
    private final JLabel moneyLabel;
    private final JPanel itemPanel = new JPanel(new GridLayout(0, 2, 10, 10));

    public GameUI() {
        player = new Player(1000);
        loadShopItems();

        setTitle("Cửa hàng ma");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        moneyLabel = new JLabel("Tiền: " + player.getMoney());
        JButton sellButton = new JButton("Bán vật phẩm");
        sellButton.addActionListener(this::openSellDialog);

        add(new JLabel("Danh sách vật phẩm (Mua):", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(itemPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(moneyLabel);
        bottomPanel.add(sellButton);
        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                player.saveToDB();
                super.windowClosing(e);
            }
        });

        refreshItemPanel();
    }

    private void loadShopItems() {
        // Thêm vật phẩm mặc định vào cửa hàng
        shopItems.put("Bánh mì", new Item("Bánh mì", 50));
        shopItems.put("Nước", new Item("Nước", 30));
        shopItems.put("Thuốc", new Item("Thuốc", 100));
    }

    private void refreshItemPanel() {
        itemPanel.removeAll();
        shopItems.forEach((name, item) -> {
            JButton buyButton = new JButton(String.format("Mua %s (%d)", name, item.getPrice()));
            buyButton.addActionListener(e -> handleBuy(item));
            itemPanel.add(new JLabel(name + " - Giá: " + item.getPrice()));
            itemPanel.add(buyButton);
        });
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    private void handleBuy(Item item) {
        if (player.getMoney() >= item.getPrice()) {
            player.addItem(item.getName(), 1);
            player.setMoney(player.getMoney() - item.getPrice());
            updateUI();
            JOptionPane.showMessageDialog(this, "Đã mua " + item.getName() + "!");
        } else {
            JOptionPane.showMessageDialog(this, "Không đủ tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSellDialog(ActionEvent e) {
        JDialog dialog = new JDialog(this, "Bán vật phẩm", true);
        dialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        player.getInventory().forEach((name, quantity) -> {
            JButton sellButton = new JButton("Bán " + name);
            sellButton.addActionListener(ev -> sellItem(name, dialog));
            panel.add(new JLabel(name + " x" + quantity));
            panel.add(sellButton);
        });

        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void sellItem(String itemName, JDialog dialog) {
        if (!shopItems.containsKey(itemName)) {
            JOptionPane.showMessageDialog(this, "Không thể bán vật phẩm này", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (player.getInventory().getOrDefault(itemName, 0) > 0) {
            player.removeItem(itemName, 1);
            player.setMoney(player.getMoney() + shopItems.get(itemName).getPrice());
            updateUI();
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không có vật phẩm để bán", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUI() {
        moneyLabel.setText("Tiền: " + player.getMoney());
        refreshItemPanel();
    }
}

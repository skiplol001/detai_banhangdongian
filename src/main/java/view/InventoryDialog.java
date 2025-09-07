package view;

import controller.StoreController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class InventoryDialog extends JDialog {

    private final StoreController controller;
    private JSpinner[] quantitySpinners;

    public InventoryDialog(JFrame parent, StoreController controller) {
        super(parent, "Quản lý Hàng Hóa Cửa Hàng", true);
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(700, 550);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("QUẢN LÝ HÀNG HÓA CỬA HÀNG - NHẬP/TRẢ/MỞ KHÓA HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel chính với tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab quản lý kho hàng
        JPanel inventoryPanel = createInventoryPanel();
        tabbedPane.addTab("Kho hàng", inventoryPanel);

        // Tab mở khóa vật phẩm
        JPanel unlockPanel = createUnlockPanel();
        tabbedPane.addTab("Mở khóa vật phẩm", unlockPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<String> allItems = controller.getDsVatPham();
        Map<String, Integer> currentQuantities = controller.getSoLuongHangHoa();

        quantitySpinners = new JSpinner[allItems.size()];

        // Tiêu đề
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        headerPanel.add(new JLabel("Tên hàng hóa"));
        headerPanel.add(new JLabel("Giá bán"));
        headerPanel.add(new JLabel("Số lượng hiện có"));
        headerPanel.add(new JLabel("Số lượng nhập/trả"));
        itemsPanel.add(headerPanel);
        itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (int i = 0; i < allItems.size(); i++) {
            String item = allItems.get(i);

            // CHỈ hiển thị vật phẩm đã mở khóa
            if (controller.isItemUnlocked(item)) {
                int price = controller.getGiaVatPham().get(item);
                int currentQty = currentQuantities.getOrDefault(item, 0);

                JPanel itemPanel = new JPanel(new GridLayout(1, 4, 5, 5));

                JLabel nameLabel = new JLabel(item);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
                itemPanel.add(nameLabel);

                JLabel priceLabel = new JLabel(price + "$");
                itemPanel.add(priceLabel);

                JLabel stockLabel = new JLabel(String.valueOf(currentQty));
                stockLabel.setForeground(currentQty < 5 ? Color.RED : Color.BLACK);
                itemPanel.add(stockLabel);

                SpinnerNumberModel model = new SpinnerNumberModel(0, -currentQty, 100, 1);
                JSpinner spinner = new JSpinner(model);
                JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner);
                spinner.setEditor(editor);
                quantitySpinners[i] = spinner;

                spinner.addChangeListener(e -> {
                    int value = (Integer) spinner.getValue();
                    if (value < 0) {
                        spinner.setBackground(new Color(255, 200, 200));
                    } else if (value > 0) {
                        spinner.setBackground(new Color(200, 255, 200));
                    } else {
                        spinner.setBackground(Color.WHITE);
                    }
                });

                itemPanel.add(spinner);
                itemsPanel.add(itemPanel);
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            } else {
                // Đối với vật phẩm chưa mở khóa, đặt spinner null
                quantitySpinners[i] = null;
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUnlockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Danh sách vật phẩm có thể mở khóa
        JPanel unlockItemsPanel = new JPanel();
        unlockItemsPanel.setLayout(new BoxLayout(unlockItemsPanel, BoxLayout.Y_AXIS));

        List<String> lockableItems = controller.getLockableItems();

        if (lockableItems.isEmpty()) {
            JLabel noItemsLabel = new JLabel("Tất cả vật phẩm đã được mở khóa!");
            noItemsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noItemsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            unlockItemsPanel.add(noItemsLabel);
        } else {
            for (String item : lockableItems) {
                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                JLabel itemLabel = new JLabel(item + " - Giá mở khóa: " + controller.getUnlockCost(item) + "$");
                itemPanel.add(itemLabel);

                JButton unlockItemButton = new JButton("Mở khóa");
                unlockItemButton.addActionListener(e -> {
                    if (controller.unlockItem(item)) {
                        JOptionPane.showMessageDialog(this,
                                "Đã mở khóa thành công " + item + "!");
                        dispose();
                        new InventoryDialog((JFrame) getParent(), controller).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Không đủ tiền để mở khóa " + item + "!",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
                itemPanel.add(unlockItemButton);

                unlockItemsPanel.add(itemPanel);
                unlockItemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(unlockItemsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel thông tin
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin mở khóa"));

        JLabel infoLabel = new JLabel("<html>• Mở khóa vật phẩm mới để bán trong cửa hàng<br>"
                + "• Mỗi vật phẩm có chi phí mở khóa khác nhau<br>"
                + "• Sau khi mở khóa, bạn có thể nhập hàng để bán</html>");
        infoPanel.add(infoLabel);

        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton applyButton = new JButton("Áp dụng thay đổi");
        applyButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyButton.setBackground(new Color(50, 150, 50));
        applyButton.setForeground(Color.WHITE);
        applyButton.addActionListener(this::handleApplyChanges);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void handleApplyChanges(ActionEvent ev) {
        List<String> allItems = controller.getDsVatPham();
        boolean hasChanges = false;

        for (int i = 0; i < allItems.size(); i++) {
            String item = allItems.get(i);

            // Chỉ xử lý nếu spinner không null (vật phẩm đã mở khóa)
            if (quantitySpinners[i] != null) {
                int change = (Integer) quantitySpinners[i].getValue();

                if (change != 0 && controller.isItemUnlocked(item)) {
                    if (change > 0) {
                        // Nhập hàng
                        controller.nhapHang(item, change);
                        JOptionPane.showMessageDialog(this,
                                "Đã nhập " + change + " " + item + " vào kho");
                    } else {
                        // Trả hàng
                        controller.traHang(item, -change);
                        JOptionPane.showMessageDialog(this,
                                "Đã trả " + (-change) + " " + item + " về nhà cung cấp");
                    }
                    hasChanges = true;
                }
            }
        }

        if (hasChanges) {
            JOptionPane.showMessageDialog(this,
                    "Đã cập nhật kho hàng thành công!");
            dispose();

            // Cập nhật giao diện chính nếu cần
            if (getParent() instanceof UiPhuBanHang) {
                ((UiPhuBanHang) getParent()).customizeUI();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không có thay đổi nào được thực hiện");
        }
    }
}

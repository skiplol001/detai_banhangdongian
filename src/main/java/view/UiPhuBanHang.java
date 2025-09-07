/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/Application.java to edit this template
 */
package view;

import controller.StoreController;
import model.Player;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import javax.swing.event.ChangeListener;

/**
 *
 * @author lap top
 */
public class UiPhuBanHang extends javax.swing.JFrame {

    private final StoreController controller;
    private final Runnable onCloseCallback;

    public UiPhuBanHang(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        this.controller = new StoreController(new Player(1000));
        initComponents();
        customizeUI();
        setupEventHandlers();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.getPlayer().saveToDB();
                super.windowClosing(e);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            }
        });
    }

    void customizeUI() {
        JLabel[] itemLabels = {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6};
        JButton[] normalButtons = {btnBanhmi, btnNuoc, btnthuoc, btnSnack, btnItem5, btnItem6};

        // Ẩn tất cả các component trước
        for (JLabel label : itemLabels) {
            label.setVisible(false);
            label.setText("");
        }
        for (JButton button : normalButtons) {
            button.setVisible(false);
            button.setText("");
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
        }

        // Hiển thị các vật phẩm theo dữ liệu từ file - TỐI ĐA 6 VẬT PHẨM
        int itemCount = Math.min(controller.getDsVatPham().size(), itemLabels.length);

        for (int i = 0; i < itemCount; i++) {
            String vatPham = controller.getDsVatPham().get(i);
            int gia = controller.getGiaVatPham().get(vatPham);
            int soLuong = controller.getSoLuongHangHoa().get(vatPham); // Lấy số lượng từ kho

            // Cập nhật label - hiển thị cả số lượng
            itemLabels[i].setText(vatPham + " - Giá: " + gia + "$ - Tồn kho: " + soLuong);
            itemLabels[i].setVisible(true);

            // Đổi màu nếu số lượng ít
            if (soLuong < 5) {
                itemLabels[i].setForeground(Color.RED);
            } else {
                itemLabels[i].setForeground(Color.BLACK);
            }

            // Cập nhật nút - disable nếu hết hàng
            normalButtons[i].setText("Mua - " + vatPham + " x1");
            normalButtons[i].setVisible(true);
            normalButtons[i].setEnabled(soLuong > 0);

            if (soLuong == 0) {
                normalButtons[i].setToolTipText("Hết hàng");
                normalButtons[i].setBackground(Color.LIGHT_GRAY);
            }

            // Gán action listener động
            final String item = vatPham;
            final int price = gia;
            normalButtons[i].addActionListener(e -> {
                if (controller.getSoLuongHangHoa().get(item) > 0) {
                    controller.handleBuy(item, price);
                    customizeUI(); // Refresh UI sau khi mua
                } else {
                    JOptionPane.showMessageDialog(this, "Mặt hàng này đã hết!");
                }
            });
        }

        // Áp dụng theme
        JLabel[] visibleLabels = Arrays.stream(itemLabels)
                .filter(JLabel::isVisible)
                .toArray(JLabel[]::new);

        JButton[] visibleButtons = Arrays.stream(normalButtons)
                .filter(JButton::isVisible)
                .toArray(JButton[]::new);

        StoreTheme.applyTheme(
                this,
                label1,
                jPanel2,
                visibleLabels,
                visibleButtons,
                btnBan,
                btnMacca,
                btnHangHoa
        );
    }

    private void setupEventHandlers() {
        btnBan.addActionListener(this::openSellDialog);
        btnMacca.addActionListener(this::openBargainDialog);
        btnHangHoa.addActionListener(this::openInventoryDialog);
    }

    private void openInventoryDialog(ActionEvent e) {
        InventoryDialog inventoryDialog = new InventoryDialog(this, controller);
        inventoryDialog.setVisible(true);
    }

    private void openSellDialog(ActionEvent e) {
        JDialog dialog = new JDialog(this, "Bán vật phẩm", true);
        dialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        controller.getPlayer().getInventory().forEach((name, quantity) -> {
            JButton sellButton = new JButton("Bán " + name);
            sellButton.addActionListener(ev -> {
                controller.sellItem(name);
                dialog.dispose();
            });
            panel.add(new JLabel(name + " x" + quantity));
            panel.add(sellButton);
        });

        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openBargainDialog(ActionEvent e) {
        JDialog dialog = new JDialog(this, "Mặc cả với chủ cửa hàng", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 250);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chọn vật phẩm để mặc cả:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        for (String item : controller.getDsVatPham()) {
            JButton itemButton = new JButton(item + " - Giá: " + controller.getGiaVatPham().get(item) + "$");
            itemButton.addActionListener(ev -> openBargainForItem(item, dialog));
            itemsPanel.add(itemButton);
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(ev -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openBargainForItem(String itemName, JDialog parentDialog) {
        parentDialog.dispose();

        int originalPrice = controller.getGiaVatPham().get(itemName);
        int maxDiscount = controller.getMaxDiscount(itemName);

        JDialog dialog = new JDialog(this, "Mặc cả - " + itemName, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300); // Tăng kích thước để chứa thêm thông tin

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));

        // Hiển thị thông tin vật phẩm
        JLabel itemInfoLabel = new JLabel("<html><b>" + itemName + "</b><br>Giá gốc: " + originalPrice + "$<br>Giảm tối đa: " + maxDiscount + "$ (50%)</html>");
        itemInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(itemInfoLabel, BorderLayout.NORTH);

        // Thay đổi GridLayout thành 4x2 để hiển thị thêm thông tin
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        // Giá gốc (chỉ hiển thị, không chỉnh sửa)
        controlPanel.add(new JLabel("Giá gốc:"));
        JTextField originalPriceField = new JTextField(originalPrice + "$");
        originalPriceField.setEditable(false);
        controlPanel.add(originalPriceField);

        // Số tiền muốn giảm (dùng spinner với nút mũi tên)
        controlPanel.add(new JLabel("Số tiền giảm:"));
        JPanel discountPanel = new JPanel(new BorderLayout());
        JSpinner discountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, maxDiscount, 1));
        discountPanel.add(discountSpinner, BorderLayout.CENTER);
        controlPanel.add(discountPanel);

        // Phần trăm giảm giá (thêm mới)
        controlPanel.add(new JLabel("Phần trăm giảm:"));
        JLabel discountPercentLabel = new JLabel("0%"); // Khai báo biến ở đây
        discountPercentLabel.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(discountPercentLabel);

        // Tỉ lệ thành công (hiển thị động)
        controlPanel.add(new JLabel("Tỉ lệ thành công:"));
        JLabel chanceLabel = new JLabel("50%"); // Bắt đầu từ 50%
        chanceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        chanceLabel.setForeground(new Color(0, 100, 0));
        controlPanel.add(chanceLabel);

        // Giá mới sau khi giảm
        controlPanel.add(new JLabel("Giá mới:"));
        JLabel newPriceLabel = new JLabel(originalPrice + "$");
        newPriceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(newPriceLabel);

        mainPanel.add(controlPanel, BorderLayout.CENTER);

        // Cập nhật tỉ lệ thành công và giá mới khi thay đổi giá trị
        discountSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int discount = (Integer) discountSpinner.getValue();
                int successRate = controller.calculateSuccessRate(itemName, discount);
                int newPrice = originalPrice - discount;

                // Tính phần trăm giảm giá
                double discountPercent = (double) discount / originalPrice * 100;

                // Cập nhật thông tin
                chanceLabel.setText(successRate + "%");
                newPriceLabel.setText(newPrice + "$");
                discountPercentLabel.setText(String.format("%.1f%%", discountPercent));

                // Đổi màu tỉ lệ thành công theo mức độ
                if (successRate >= 40) {
                    chanceLabel.setForeground(new Color(0, 100, 0)); // Xanh lá đậm
                } else if (successRate >= 20) {
                    chanceLabel.setForeground(new Color(200, 150, 0)); // Vàng
                } else {
                    chanceLabel.setForeground(new Color(200, 0, 0)); // Đỏ
                }

                // Đổi màu phần trăm giảm giá
                if (discountPercent >= 40) {
                    discountPercentLabel.setForeground(new Color(0, 100, 0)); // Xanh lá
                } else if (discountPercent >= 20) {
                    discountPercentLabel.setForeground(new Color(200, 150, 0)); // Vàng
                } else {
                    discountPercentLabel.setForeground(new Color(200, 0, 0)); // Đỏ
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton bargainButton = new JButton("Mặc cả");
        bargainButton.setFont(new Font("Arial", Font.BOLD, 12));
        bargainButton.addActionListener(ev -> {
            int discount = (Integer) discountSpinner.getValue();
            boolean success = controller.attemptBargain(itemName, discount);
            if (success) {
                dialog.dispose();
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(ev -> dialog.dispose());

        buttonPanel.add(bargainButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnNuoc = new javax.swing.JButton();
        btnthuoc = new javax.swing.JButton();
        btnBanhmi = new javax.swing.JButton();
        btnSnack = new javax.swing.JButton();
        btnBan = new javax.swing.JButton();
        btnMacca = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnItem5 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnItem6 = new javax.swing.JButton();
        btnHangHoa = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cửa Hàng");

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setForeground(new java.awt.Color(102, 255, 255));

        label1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label1.setText("CỬA HÀNG TIỆN LỢI");
        label1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel2.setBackground(new java.awt.Color(153, 255, 255));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Bánh mì - Giá: 50$");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Nước suối - Giá: 100$");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Thuốc - Giá: 250$");
        jLabel3.setAlignmentY(getAlignmentY());

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Snack - Giá: 20$");

        btnNuoc.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        btnNuoc.setText("Mua - Nước suối x1");

        btnthuoc.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        btnthuoc.setText("Mua - Thuốc x1");

        btnBanhmi.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        btnBanhmi.setText("Mua - Bánh mì x1");

        btnSnack.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        btnSnack.setText("Mua - Snack x1");

        btnBan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnBan.setText("BÁN VẬT PHẨM");

        btnMacca.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnMacca.setText("MẶC CẢ");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Item5 - Giá:0$");

        btnItem5.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Item6 - Giá:0$");

        btnItem6.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N

        btnHangHoa.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnHangHoa.setText("Hàng Hóa");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnItem6, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnItem5, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnNuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnthuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBanhmi, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSnack, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnBan, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMacca, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHangHoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnBanhmi, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnthuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSnack, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnItem5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnItem6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBan, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnMacca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHangHoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addComponent(label1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBan;
    private javax.swing.JButton btnBanhmi;
    private javax.swing.JButton btnHangHoa;
    private javax.swing.JButton btnItem5;
    private javax.swing.JButton btnItem6;
    private javax.swing.JButton btnMacca;
    private javax.swing.JButton btnNuoc;
    private javax.swing.JButton btnSnack;
    private javax.swing.JButton btnthuoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel label1;
    private javax.swing.JMenuBar menuBar;
    // End of variables declaration//GEN-END:variables

}

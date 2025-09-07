package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class StoreTheme {

    // Màu sắc theme mới - phối màu hiện đại và chuyên nghiệp hơn
    private static final Color MAIN_BG = new Color(245, 248, 250);
    private static final Color PANEL_BG = new Color(255, 255, 255);
    private static final Color TITLE_COLOR = new Color(41, 128, 185);
    private static final Color ITEM_TEXT_COLOR = new Color(60, 60, 60);

    // Màu cho các nút thường (mua hàng)
    private static final Color NORMAL_BTN_COLOR = new Color(52, 152, 219);
    private static final Color NORMAL_HOVER_COLOR = new Color(41, 128, 185);
    private static final Color NORMAL_PRESSED_COLOR = new Color(32, 102, 148);

    // Màu cho nút bán hàng
    private static final Color SELL_BTN_COLOR = new Color(231, 76, 60);
    private static final Color SELL_HOVER_COLOR = new Color(192, 57, 43);
    private static final Color SELL_PRESSED_COLOR = new Color(153, 45, 34);

    // Màu cho nút mặc cả
    private static final Color BARGAIN_BTN_COLOR = new Color(46, 204, 113);
    private static final Color BARGAIN_HOVER_COLOR = new Color(39, 174, 96);
    private static final Color BARGAIN_PRESSED_COLOR = new Color(30, 132, 73);

    // Màu cho nút hàng hóa
    private static final Color INVENTORY_BTN_COLOR = new Color(155, 89, 182);
    private static final Color INVENTORY_HOVER_COLOR = new Color(142, 68, 173);
    private static final Color INVENTORY_PRESSED_COLOR = new Color(115, 55, 140);

    public static void applyTheme(JFrame frame, JLabel titleLabel, JPanel contentPanel,
            JLabel[] itemLabels, JButton[] normalButtons,
            JButton sellButton, JButton bargainButton, JButton inventoryButton) {

        // Thiết lập frame
        frame.getContentPane().setBackground(MAIN_BG);

        // Thiết lập tiêu đề
        setupTitleLabel(titleLabel);

        // Thiết lập panel nội dung
        setupContentPanel(contentPanel);

        // Thiết lập nhãn sản phẩm
        setupItemLabels(itemLabels);

        // Thiết lập nút mua hàng thường
        for (JButton btn : normalButtons) {
            setupNormalButton(btn);
        }

        // Thiết lập nút bán hàng
        setupSellButton(sellButton);

        // Thiết lập nút mặc cả
        setupBargainButton(bargainButton);

        // Thiết lập nút hàng hóa
        setupInventoryButton(inventoryButton);
    }

    private static void setupTitleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(TITLE_COLOR);
        label.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, TITLE_COLOR),
                new EmptyBorder(8, 0, 12, 0)
        ));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private static void setupContentPanel(JPanel panel) {
        panel.setBackground(PANEL_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
    }

    private static void setupItemLabels(JLabel[] labels) {
        for (JLabel lbl : labels) {
            lbl.setForeground(ITEM_TEXT_COLOR);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }

    private static void setupNormalButton(JButton btn) {
        customizeButton(btn, NORMAL_BTN_COLOR, NORMAL_HOVER_COLOR, NORMAL_PRESSED_COLOR, Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private static void setupSellButton(JButton btn) {
        customizeButton(btn, SELL_BTN_COLOR, SELL_HOVER_COLOR, SELL_PRESSED_COLOR, Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private static void setupBargainButton(JButton btn) {
        customizeButton(btn, BARGAIN_BTN_COLOR, BARGAIN_HOVER_COLOR, BARGAIN_PRESSED_COLOR, Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private static void setupInventoryButton(JButton btn) {
        customizeButton(btn, INVENTORY_BTN_COLOR, INVENTORY_HOVER_COLOR, INVENTORY_PRESSED_COLOR, Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private static void customizeButton(JButton btn, Color normalBg, Color hoverBg, Color pressedBg, Color textColor) {
        btn.setBackground(normalBg);
        btn.setForeground(textColor);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 30), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Hiệu ứng hover và press
        btn.addMouseListener(new ButtonHoverListener(normalBg, hoverBg, pressedBg, textColor));

        // Hiệu ứng khi nhấn
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(pressedBg);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(hoverBg);
            }
        });
    }

    // Lớp riêng xử lý hover để tránh bug
    private static class ButtonHoverListener extends MouseAdapter {

        private final Color normalBg;
        private final Color hoverBg;
        private final Color pressedBg;
        private final Color textColor;

        public ButtonHoverListener(Color normalBg, Color hoverBg, Color pressedBg, Color textColor) {
            this.normalBg = normalBg;
            this.hoverBg = hoverBg;
            this.pressedBg = pressedBg;
            this.textColor = textColor;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            JButton btn = (JButton) e.getSource();
            if (!btn.getModel().isPressed()) {
                btn.setBackground(hoverBg);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton btn = (JButton) e.getSource();
            if (!btn.getModel().isPressed()) {
                btn.setBackground(normalBg);
            }
        }
    }

    // Phương thức để lấy màu sắc cho các component khác nếu cần
    public static Color getMainBackgroundColor() {
        return MAIN_BG;
    }

    public static Color getPanelBackgroundColor() {
        return PANEL_BG;
    }

    public static Color getTitleColor() {
        return TITLE_COLOR;
    }
}

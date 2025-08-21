package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class StoreTheme {
    // Màu sắc theme
    private static final Color MAIN_BG = new Color(240, 240, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(0, 120, 215);
    private static final Color ITEM_TEXT_COLOR = new Color(70, 70, 70);
    private static final Color NORMAL_BTN_COLOR = new Color(0, 120, 215);
    private static final Color HOVER_BTN_COLOR = new Color(0, 140, 235);
    private static final Color SPECIAL_BTN_COLOR = new Color(215, 60, 60);
    private static final Color SPECIAL_HOVER_COLOR = new Color(235, 80, 80);

    public static void applyTheme(JFrame frame, JLabel titleLabel, JPanel contentPanel,
                                JLabel[] itemLabels, JButton[] normalButtons, JButton specialButton) {
        // Thiết lập frame
        frame.getContentPane().setBackground(MAIN_BG);

        // Thiết lập tiêu đề
        setupTitleLabel(titleLabel);

        // Thiết lập panel nội dung
        setupContentPanel(contentPanel);

        // Thiết lập nhãn sản phẩm
        setupItemLabels(itemLabels);

        // Thiết lập nút thường
        for (JButton btn : normalButtons) {
            setupNormalButton(btn);
        }

        // Thiết lập nút đặc biệt (nút bán)
        setupSpecialButton(specialButton);
    }

    private static void setupTitleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TITLE_COLOR);
        label.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, TITLE_COLOR),
                new EmptyBorder(5, 0, 10, 0)
        ));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private static void setupContentPanel(JPanel panel) {
        panel.setBackground(PANEL_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    private static void setupItemLabels(JLabel[] labels) {
        for (JLabel lbl : labels) {
            lbl.setForeground(ITEM_TEXT_COLOR);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }

    private static void setupNormalButton(JButton btn) {
        btn.setBackground(NORMAL_BTN_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 100, 190), 1),
                new EmptyBorder(8, 15, 8, 15)
        ));

        // Hiệu ứng hover với MouseAdapter riêng
        btn.addMouseListener(new ButtonHoverListener(
            NORMAL_BTN_COLOR,
            HOVER_BTN_COLOR,
            Color.WHITE,
            Color.WHITE
        ));
    }

    private static void setupSpecialButton(JButton btn) {
        btn.setBackground(SPECIAL_BTN_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 50, 50), 1),
                new EmptyBorder(8, 15, 8, 15)
        ));

        // Hiệu ứng hover cho nút đặc biệt
        btn.addMouseListener(new ButtonHoverListener(
            SPECIAL_BTN_COLOR,
            SPECIAL_HOVER_COLOR,
            Color.WHITE,
            Color.WHITE
        ));
    }

    // Lớp riêng xử lý hover để tránh bug
    private static class ButtonHoverListener extends MouseAdapter {
        private final Color normalBg;
        private final Color hoverBg;
        private final Color normalFg;
        private final Color hoverFg;

        public ButtonHoverListener(Color normalBg, Color hoverBg, Color normalFg, Color hoverFg) {
            this.normalBg = normalBg;
            this.hoverBg = hoverBg;
            this.normalFg = normalFg;
            this.hoverFg = hoverFg;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            JButton btn = (JButton) e.getSource();
            btn.setBackground(hoverBg);
            btn.setForeground(hoverFg);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton btn = (JButton) e.getSource();
            btn.setBackground(normalBg);
            btn.setForeground(normalFg);
        }
    }
}
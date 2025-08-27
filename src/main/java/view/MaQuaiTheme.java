package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MaQuaiTheme {

    // Màu sắc tối cho cửa hàng hoang
    private static final Color BACKGROUND_COLOR = new Color(25, 20, 15);
    private static final Color PANEL_BACKGROUND = new Color(40, 35, 30);
    private static final Color BORDER_COLOR = new Color(80, 70, 60);
    private static final Color TEXT_COLOR = new Color(180, 170, 160);
    private static final Color SPECIAL_BUTTON_BG = new Color(120, 30, 20);
    private static final Color SPECIAL_BUTTON_FG = new Color(220, 200, 190);
    private static final Color NORMAL_BUTTON_BG = new Color(60, 70, 50);
    private static final Color NORMAL_BUTTON_FG = new Color(200, 210, 190);
    private static final Color HIGHLIGHT_COLOR = new Color(150, 120, 80);

    // Phương thức chính để áp dụng theme đơn giản
    public static void applySimpleTheme(JFrame frame, JPanel[] panels, JLabel[] labels,
            JButton[] specialButtons, JButton[] normalButtons) {

        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        for (JPanel p : panels) {
            if (p != null) {
                p.setBackground(PANEL_BACKGROUND);
                p.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_COLOR, 2),
                        new EmptyBorder(8, 8, 8, 8)
                ));
            }
        }

        for (JLabel lbl : labels) {
            if (lbl != null) {
                lbl.setForeground(TEXT_COLOR);
            }
        }

        for (JButton btn : specialButtons) {
            if (btn != null) {
                setupButton(btn, SPECIAL_BUTTON_BG, SPECIAL_BUTTON_FG);
            }
        }

        for (JButton btn : normalButtons) {
            if (btn != null) {
                setupButton(btn, NORMAL_BUTTON_BG, NORMAL_BUTTON_FG);
            }
        }
    }

    private static void setupButton(JButton btn, Color bgColor, Color fgColor) {
        Color originalBg = bgColor;
        Color originalFg = fgColor;

        btn.setBackground(originalBg);
        btn.setForeground(originalFg);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        btn.addMouseListener(new MouseAdapter() {
            private final Color hoverBg = new Color(
                    Math.min(originalBg.getRed() + 20, 255),
                    Math.min(originalBg.getGreen() + 15, 255),
                    Math.min(originalBg.getBlue() + 10, 255)
            );
            private final Color hoverFg = Color.WHITE;

            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.setForeground(hoverFg);
                btn.setBorder(new CompoundBorder(
                        new LineBorder(HIGHLIGHT_COLOR, 2),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(originalBg);
                btn.setForeground(originalFg);
                btn.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_COLOR, 1),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    // PHƯƠNG THỨC ĐÃ ĐƯỢC SỬA - Sử dụng đường dẫn đúng
    public static void setupImageButtonForUI(JButton btn, String buttonType) {
        try {
            String imageName;
            String fallbackText;

            // Xác định tên file ảnh và văn bản fallback
            if ("btnKHTN".equals(buttonType)) {
                imageName = "btnKHTN";
                fallbackText = "KH Trong Ngày";
            } else if ("btnTTKH".equals(buttonType)) {
                imageName = "btnTTKH";
                fallbackText = "Thông Tin KH";
            } else {
                imageName = buttonType;
                fallbackText = buttonType;
            }

            // CHỈ sử dụng đường dẫn resources-button
            String imagePath = "/resources-button/" + imageName + ".png";
            java.net.URL imgURL = MaQuaiTheme.class.getResource(imagePath);

            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                int width = btn.getPreferredSize().width > 0 ? btn.getPreferredSize().width : 240;
                int height = btn.getPreferredSize().height > 0 ? btn.getPreferredSize().height : 120;

                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        width, height, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaledImage));
                btn.setText("");
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        btn.setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 2));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        btn.setBorder(BorderFactory.createEmptyBorder());
                    }
                });

                System.out.println("✓ Đã tải ảnh thành công: " + imgURL);
            } else {
                System.err.println("Không tìm thấy hình ảnh: " + imagePath);
                // Fallback với màu sắc phù hợp
                if ("btnKHTN".equals(buttonType)) {
                    setupButton(btn, SPECIAL_BUTTON_BG, SPECIAL_BUTTON_FG);
                    btn.setText(fallbackText);
                } else {
                    setupButton(btn, NORMAL_BUTTON_BG, NORMAL_BUTTON_FG);
                    btn.setText(fallbackText);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hình ảnh: " + e.getMessage());
            // Fallback với màu sắc phù hợp
            if ("btnKHTN".equals(buttonType)) {
                setupButton(btn, SPECIAL_BUTTON_BG, SPECIAL_BUTTON_FG);
                btn.setText("KH Trong Ngày");
            } else {
                setupButton(btn, NORMAL_BUTTON_BG, NORMAL_BUTTON_FG);
                btn.setText("Thông Tin KH");
            }
        }

    }

}

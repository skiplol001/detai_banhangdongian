package view;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MaQuaiTheme {

    public static void applyTheme(JFrame frame, JPanel[] panels, JLabel[] labels,
            JButton[] specialButtons, JButton[] normalButtons) {
        // Màu nền chính
        frame.getContentPane().setBackground(new Color(40, 45, 50));

        // Customize panels
        for (JPanel p : panels) {
            p.setBackground(new Color(60, 65, 70));
            p.setBorder(new CompoundBorder(
                    new LineBorder(new Color(80, 100, 80), 1),
                    new EmptyBorder(10, 10, 10, 10)
            ));
        }

        // Customize labels
        Font customFont = new Font("Dialog", Font.PLAIN, 12);
        Font boldFont = new Font("Dialog", Font.BOLD, 14);
        for (JLabel lbl : labels) {
            lbl.setForeground(new Color(200, 220, 200));
            lbl.setFont(customFont);
        }

        // Customize buttons - màu đỏ máu
        for (JButton btn : specialButtons) {
            setupButton(btn, new Color(150, 40, 40), new Color(255, 220, 220));
        }

        // Customize buttons - màu xanh rêu
        for (JButton btn : normalButtons) {
            setupButton(btn, new Color(70, 100, 80), new Color(220, 240, 220));
        }
    }

    private static void setupButton(JButton btn, Color bgColor, Color fgColor) {
        // Lưu màu gốc
        Color originalBg = bgColor;
        Color originalFg = fgColor;
        Border originalBorder = new CompoundBorder(
                new LineBorder(new Color(100, 120, 100), 1),
                new EmptyBorder(8, 15, 8, 15)
        );

        btn.setBackground(originalBg);
        btn.setForeground(originalFg);
        btn.setFocusPainted(false);
        btn.setBorder(originalBorder);
        btn.setFont(new Font("Dialog", Font.BOLD, 12));

        // Hiệu ứng hover
        btn.addMouseListener(new MouseAdapter() {
            private final Color hoverBg = new Color(
                    Math.min(originalBg.getRed() + 30, 255),
                    Math.min(originalBg.getGreen() + 30, 255),
                    Math.min(originalBg.getBlue() + 30, 255)
            );
            private final Color hoverFg = Color.WHITE;
            private final Border hoverBorder = new CompoundBorder(
                    new LineBorder(new Color(120, 140, 120), 1),
                    new EmptyBorder(8, 15, 8, 15)
            );

            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.setForeground(hoverFg);
                btn.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(originalBg);
                btn.setForeground(originalFg);
                btn.setBorder(originalBorder);
            }
        });
    }
}

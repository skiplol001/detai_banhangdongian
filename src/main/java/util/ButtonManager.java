package util;

import javax.swing.JButton;
import java.awt.Dimension;

public class ButtonManager {
    
    public static void fixButtonSize(JButton button, int width, int height) {
        if (button != null) {
            button.setPreferredSize(new Dimension(width, height));
            button.setMinimumSize(new Dimension(width, height));
            button.setMaximumSize(new Dimension(width, height));
            button.setSize(new Dimension(width, height));
        }
    }
    
    public static void setupImageButton(JButton button, String imagePath, int width, int height) {
        if (button != null) {
            fixButtonSize(button, width, height);
            button.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            
            // Code để tải và đặt ảnh vào button
            // Có thể thêm xử lý tải ảnh từ đường dẫn ở đây
        }
    }
    
    public static void enableButton(JButton button, boolean enabled) {
        if (button != null) {
            button.setEnabled(enabled);
        }
    }
    
    public static void setButtonText(JButton button, String text) {
        if (button != null) {
            button.setText(text);
        }
    }
    
    public static void setButtonVisibility(JButton button, boolean visible) {
        if (button != null) {
            button.setVisible(visible);
        }
    }
}
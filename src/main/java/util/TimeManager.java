package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class TimeManager {
    
    public static void updateTimeDisplay(JLabel timeLabel, String timeString, int currentHour, boolean isNight) {
        if (timeLabel != null) {
            SwingUtilities.invokeLater(() -> {
                timeLabel.setText(timeString);
                // Có thể thêm hiệu ứng đêm nếu cần
                if (isNight) {
                    // Áp dụng hiệu ứng đêm cho UI
                    timeLabel.setForeground(java.awt.Color.YELLOW);
                } else {
                    timeLabel.setForeground(java.awt.Color.BLACK);
                }
            });
        }
    }
    
    public static String getCurrentTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return formatter.format(date);
    }
    
    public static String formatGameTime(int hours, int minutes) {
        return String.format("%02d:%02d", hours, minutes);
    }
    
}
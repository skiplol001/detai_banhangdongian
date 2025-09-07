package util;

import view.UIChinh;
import javax.swing.JOptionPane;

public class DaySummaryManager {
    
    public static void showDaySummary(UIChinh mainUI, int dailyRevenue, int mentalPoints) {
        if (mainUI != null) {
            JOptionPane.showMessageDialog(mainUI,
                    "Tổng kết ngày:\n"
                    + "Doanh thu: " + dailyRevenue + "\n"
                    + "Điểm tinh thần: " + mentalPoints,
                    "Tổng kết ngày",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static int calculateDailyRevenue(int startMoney, int endMoney) {
        return Math.max(0, endMoney - startMoney);
    }
}
package controller;

import model.GameTimeManager;
import view.UIChinh;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import util.ButtonManager;
import util.GameStateManager;
import util.SoundManager;

public class KichBanNgay6 {

    private UIChinh mainUI;
    private GameTimeManager gameTimeManager;
    private Timer messageTimer;
    private int currentMessageIndex = 0;
    private boolean isKichBanActive = false;
    
    // Thông báo cho kịch bản ngày 6
    private final String[] MESSAGES_NGAY6 = {
        "Thời gian qua lẫn trốn... cuối cùng tôi cũng đã...",
        "Có thể gặp lại cô ấy...",
        "Có lẽ ngày mai là lần cuối gặp nhau, tôi chỉ là npc kết cục định sẵn là phải tuân theo dòng lệnh",
        "Tạm biệt cậu"
    };

    public KichBanNgay6(UIChinh mainUI, GameTimeManager gameTimeManager) {
        this.mainUI = mainUI;
        this.gameTimeManager = gameTimeManager;
        SoundManager.initialize();
    }

    public void kiemTraVaKichHoat() {
        int currentDay = getCurrentDayCount();

        if (currentDay == 6 && !isKichBanActive) {
            batDauKichBan();
        }
    }

    private int getCurrentDayCount() {
        try {
            String projectPath = System.getProperty("user.dir");
            String countFilePath = Paths.get(projectPath, "database", "dialog", "count.txt").toString();

            BufferedReader reader = new BufferedReader(new FileReader(countFilePath));
            String line = reader.readLine();
            reader.close();
            return Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi đọc file count.txt: " + e.getMessage());
            return 0;
        }
    }

    private String getImagePath(String imageName) {
        String projectPath = System.getProperty("user.dir");
        return Paths.get(projectPath, "database", "dialog", "level6", imageName).toString();
    }

    private void batDauKichBan() {
        isKichBanActive = true;

        // Dừng thời gian game
        if (gameTimeManager != null) {
            gameTimeManager.stopTimer();
        }

        // Tạm dừng BGM chính
        SoundManager.pauseBGM();

        // Hiển thị thông báo tiền kết
        hienThiMessageTheoThuTu(MESSAGES_NGAY6, this::ketThucKichBan6);
    }

    private void hienThiMessageTheoThuTu(String[] messages, Runnable onComplete) {
        currentMessageIndex = 0;

        messageTimer = new Timer(3000, new ActionListener() {
            @Override
    public void actionPerformed(ActionEvent e) {
        if (currentMessageIndex < messages.length) {
            JOptionPane.showMessageDialog(mainUI, messages[currentMessageIndex], 
                                        "Lục Thanh Tịnh", JOptionPane.INFORMATION_MESSAGE);
            currentMessageIndex++;
        } else {
            messageTimer.stop();
            onComplete.run();
        }
    }
});
messageTimer.start();
    }

    private void ketThucKichBan6() {
        // Thông báo chuyển sang ngày cuối
        JOptionPane.showMessageDialog(mainUI, 
            "Ngày mai sẽ là ngày cuối cùng...", 
            "Lục Thanh Tịnh", JOptionPane.WARNING_MESSAGE);

        // Tiếp tục thời gian game
        if (gameTimeManager != null && !gameTimeManager.isTimerRunning()) {
            gameTimeManager.startTimer();
        }

        // Tiếp tục BGM chính
        SoundManager.resumeBGM();

        isKichBanActive = false;
    }

    public void huyKichBan() {
        if (messageTimer != null && messageTimer.isRunning()) {
            messageTimer.stop();
        }
        isKichBanActive = false;
    }

    public boolean isKichBanActive() {
        return isKichBanActive;
    }
}
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

public class KichBanNgay4 {

    private UIChinh mainUI;
    private GameTimeManager gameTimeManager;
    private Timer messageTimer;
    private int currentMessageIndex = 0;
    private boolean isKichBanActive = false;
    private SoundKichBan4 soundManager;
    private String originalPlayerName;
    
    // Các message cho kịch bản
    private final String[] GIAI_DOAN_1_MESSAGES = {
        "cậu nhân viên à có thể bán cho tôi một thứ ko",
        "tôi sợ lắm"
    };

    private final String[] GIAI_DOAN_2_MESSAGES = {
        "lạnh lắm",
        "sợ lắm",
        "cô đơn lắm",
        "đây là đâu",
        "tối qua",
        "cứu tôi",
        "cứu tôi"
    };

    private final String[] GIAI_DOAN_3_MESSAGES = {
        "cậu ơi mắt tôi đâu!?",
        "cậu có thể bán mắt cho tôi không!?",
        "cậu ơi tôi không thấy gì"
    };

    public KichBanNgay4(UIChinh mainUI, GameTimeManager gameTimeManager) {
        this.mainUI = mainUI;
        this.gameTimeManager = gameTimeManager;
        this.soundManager = new SoundKichBan4();

        // Lưu tên gốc của người chơi
        this.originalPlayerName = GameStateManager.getPlayerName();
        // Đảm bảo SoundManager đã được khởi tạo
        SoundManager.initialize();
    }

    public void kiemTraVaKichHoat() {
        int currentDay = getCurrentDayCount();

        if (currentDay == 4 && !isKichBanActive) {
            batDauKichBan();
        }
    }

    private void tangNgayLen5() {
        try {
            String projectPath = System.getProperty("user.dir");
            String countFilePath = Paths.get(projectPath, "database", "dialog", "count.txt").toString();

            java.io.FileWriter writer = new java.io.FileWriter(countFilePath);
            writer.write("5");
            writer.close();

        } catch (IOException e) {
            System.err.println("Lỗi ghi file count.txt: " + e.getMessage());
        }
    }

    private int getCurrentMentalPoints() {
        try {
            String projectPath = System.getProperty("user.dir");
            String filePath = Paths.get(projectPath, "database", "player", "player_data.txt").toString();

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("mentalPoints:")) {
                    return Integer.parseInt(line.split(":")[1].trim());
                }
            }
            reader.close();
            return 100; // Giá trị mặc định nếu không tìm thấy
        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi khi đọc điểm tinh thần: " + e.getMessage());
            return 100;
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
        return Paths.get(projectPath, "database", "dialog", "level4", imageName).toString();
    }

    private void batDauKichBan() {
        isKichBanActive = true;

        // Dừng thời gian game
        if (gameTimeManager != null) {
            gameTimeManager.stopTimer();
        }

        // Tạm dừng BGM chính và phát âm thanh giai đoạn 1
        soundManager.pauseMainBGM();
        soundManager.playStageSound(1);

        // Giai đoạn 1: Hiển thị ảnh 1.png và message đầu tiên
        hienThiAnh("1.png");
        hienThiMessageTheoThuTu(GIAI_DOAN_1_MESSAGES, this::batDauGiaiDoan2);
    }

    private void batDauGiaiDoan2() {
        // Phát âm thanh giai đoạn 2
        soundManager.playStageSound(2);

        // Áp dụng filter đỏ
        applyRedFilter();

        // Làm rối thông tin người chơi
        lamRoiThongTinNguoiChoi();

        // Hiển thị ảnh 2.png
        hienThiAnh("2.png");

        // Hiển thị các message liên tiếp
        hienThiMessageLienTuc(GIAI_DOAN_2_MESSAGES, this::batDauGiaiDoan3);
    }

    private void batDauGiaiDoan3() {
        // Phát âm thanh giai đoạn 3
        soundManager.playStageSound(3);

        // Áp dụng filter xám
        applyGrayFilter();

        // Hiển thị ảnh 3.png
        hienThiAnh("3.png");

        // Hiển thị messages giai đoạn 3
        hienThiMessageTheoThuTu(GIAI_DOAN_3_MESSAGES, this::ketThucKichBan);
    }

    private void ketThucKichBan() {
        // Phát âm thanh giai đoạn 4
        soundManager.playStageSound(4);

        // Hiển thị ảnh 4.png
        hienThiAnh("4.png");

        // Thông báo nhận được vật phẩm
        JOptionPane.showMessageDialog(mainUI, "???", "???", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(mainUI, "cậu đang tình huống khó xử nhỉ? Cẩm lấy đoạn dữ liệu này có lẽ nó sẽ giúp cậu", "???", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(mainUI, "Bạn vừa nhận được đôi mắt của quỷ", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(mainUI, "có lẽ tôi chưa giới thiệu, tôi tên là Lục Thanh Tịnh (thở dài) tạm biệt tôi không thể ở lâu", "???", JOptionPane.INFORMATION_MESSAGE);

        // Thông báo lỗi
        JOptionPane.showMessageDialog(mainUI, "thứ này lẽ ra cậu không nên có", "Lỗi", JOptionPane.ERROR_MESSAGE);

        // PHỤC HỒI THÔNG TIN NGƯỜI CHƠI
        phucHoiThongTinNguoiChoi();

        // TĂNG NGÀY LÊN 5
        tangNgayLen5();

        // Khôi phục giao diện
        phucHoiThongTinNguoiChoi();

        // Tiếp tục BGM chính
        soundManager.resumeMainBGM();

        isKichBanActive = false;

        // Tiếp tục thời gian game
        if (gameTimeManager != null && !gameTimeManager.isTimerRunning()) {
            gameTimeManager.startTimer();
        }
    }

    // Các phương thức còn lại giữ nguyên...
    private void hienThiAnh(String imageName) {
        SwingUtilities.invokeLater(() -> {
            try {
                String imagePath = getImagePath(imageName);
                ImageIcon originalIcon = new ImageIcon(imagePath);

                int targetWidth = 230;
                int targetHeight = 270;

                Image scaledImage = originalIcon.getImage()
                        .getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                if (mainUI != null && mainUI.getBtnAnh() != null) {
                    mainUI.getBtnAnh().setIcon(scaledIcon);
                    ButtonManager.fixButtonSize(mainUI.getBtnAnh(), targetWidth, targetHeight);
                }
            } catch (Exception e) {
                System.err.println("Lỗi tải ảnh: " + e.getMessage());
                if (mainUI != null && mainUI.getBtnAnh() != null) {
                    ButtonManager.fixButtonSize(mainUI.getBtnAnh(), 200, 250);
                }
            }
        });
    }

    private void hienThiMessageTheoThuTu(String[] messages, Runnable onComplete) {
        currentMessageIndex = 0;

        messageTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentMessageIndex < messages.length) {
                    JOptionPane.showMessageDialog(mainUI, messages[currentMessageIndex], "Cô g@#$!?", JOptionPane.INFORMATION_MESSAGE);
                    currentMessageIndex++;
                } else {
                    messageTimer.stop();
                    onComplete.run();
                }
            }
        });
        messageTimer.start();
    }

    private void hienThiMessageLienTuc(String[] messages, Runnable onComplete) {
        currentMessageIndex = 0;

        messageTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentMessageIndex < messages.length) {
                    JOptionPane messageDialog = new JOptionPane(messages[currentMessageIndex], JOptionPane.INFORMATION_MESSAGE);
                    JDialog dialog = messageDialog.createDialog(mainUI, "Thông báo");
                    dialog.setVisible(true);

                    Timer closeTimer = new Timer(1000, evt -> dialog.dispose());
                    closeTimer.setRepeats(false);
                    closeTimer.start();

                    currentMessageIndex++;
                } else {
                    messageTimer.stop();
                    onComplete.run();
                }
            }
        });
        messageTimer.start();
    }

    private void applyRedFilter() {
        SwingUtilities.invokeLater(() -> {
            if (mainUI != null) {
                mainUI.getContentPane().setBackground(Color.RED);
            }
        });
    }

    private void applyGrayFilter() {
        SwingUtilities.invokeLater(() -> {
            if (mainUI != null) {
                mainUI.getContentPane().setBackground(Color.DARK_GRAY);
            }
        });
    }

    private void lamRoiThongTinNguoiChoi() {
        SwingUtilities.invokeLater(() -> {
            if (mainUI != null) {
                if (mainUI.getlblTen()!= null) {
                    mainUI.getlblTen().setText("!#@$@??");
                }
                if (mainUI.getJLabel8() != null) {
                    mainUI.getJLabel8().setText("####");
                }
            }
        });
    }

   private void phucHoiThongTinNguoiChoi() {
    SwingUtilities.invokeLater(() -> {
        if (mainUI != null) {
            if (mainUI.getlblTen()!= null) {
                mainUI.getlblTen().setText(originalPlayerName); // Phục hồi tên người chơi
            }
            if (mainUI.getJLabel8() != null) {
                // Lấy điểm tinh thần từ playerData của UI chính
                int mentalPoints = mainUI.getPlayerData().mentalPoints;
                mainUI.getJLabel8().setText(String.valueOf(mentalPoints));
            }
        }
    });
}

    public void huyKichBan() {
        if (messageTimer != null && messageTimer.isRunning()) {
            messageTimer.stop();
        }
        soundManager.cleanup();
        phucHoiThongTinNguoiChoi();
        isKichBanActive = false;
    }

    public boolean isKichBanActive() {
        return isKichBanActive;
    }
}

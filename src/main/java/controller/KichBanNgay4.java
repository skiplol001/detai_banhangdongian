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

public class KichBanNgay4 {

    private UIChinh mainUI;
    private GameTimeManager gameTimeManager;
    private Timer messageTimer;
    private int currentMessageIndex = 0;
    private boolean isKichBanActive = false;

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
    }

    public void kiemTraVaKichHoat() {
        int currentDay = getCurrentDayCount();
        System.out.println("DEBUG: Current day = " + currentDay);

        if (currentDay == 4 && !isKichBanActive) {
            System.out.println("DEBUG: Starting day 4 scenario at midnight!");
            batDauKichBan();
        }
    }

    private void ketThucKichBan() {
        // Hiển thị ảnh 4.png
        hienThiAnh("4.png");

        // Thông báo nhận được vật phẩm
        JOptionPane.showMessageDialog(mainUI, "???", "???", JOptionPane.INFORMATION_MESSAGE);
         JOptionPane.showMessageDialog(mainUI, "cậu đang tình huống khó xử nhỉ? Cẩm lấy đoạn dữ liệu này có lẽ nó sẽ giúp cậu", "???", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(mainUI, "Bạn vừa nhận được đôi mắt của quỷ", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
         JOptionPane.showMessageDialog(mainUI, "có lẽ tôi chưa giới thiệu, tôi tên là Lục Thanh Tịnh (thở dài) tạm biệt tôi không thể ở lâu", "???", JOptionPane.INFORMATION_MESSAGE);

        // Thông báo lỗi
        JOptionPane.showMessageDialog(mainUI, "thứ này lẽ ra cậu không nên có", "Lỗi", JOptionPane.ERROR_MESSAGE);

        // Khôi phục giao diện
        khoiPhucGiaoDien();

        // 🔥 QUAN TRỌNG: KHÔNG gọi endDaySequence() ở đây nữa
        // Vì kịch bản chạy giữa chừng, game vẫn tiếp tục đến 3h sáng
        isKichBanActive = false;

        // 🔥 TIẾP TỤC THỜI GIAN GAME (nếu đã dừng)
        if (gameTimeManager != null && !gameTimeManager.isTimerRunning()) {
            gameTimeManager.startTimer();
        }
    }

    private int getCurrentDayCount() {
        try {
            // Sử dụng đường dẫn tương đối
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
        // Sử dụng đường dẫn tương đối
        String projectPath = System.getProperty("user.dir");
        return Paths.get(projectPath, "database", "dialog", "level4", imageName).toString();
    }

    private void batDauKichBan() {
        isKichBanActive = true;

        // Dừng thời gian game
        if (gameTimeManager != null) {
            gameTimeManager.stopTimer();
        }

        // Giai đoạn 1: Hiển thị ảnh 1.png và message đầu tiên
        hienThiAnh("1.png");
        hienThiMessageTheoThuTu(GIAI_DOAN_1_MESSAGES, this::batDauGiaiDoan2);
    }

    private void batDauGiaiDoan2() {
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
        // Áp dụng filter xám
        applyGrayFilter();

        // Hiển thị ảnh 3.png
        hienThiAnh("3.png");

        // Hiển thị messages giai đoạn 3
        hienThiMessageTheoThuTu(GIAI_DOAN_3_MESSAGES, this::ketThucKichBan);
    }

    private void hienThiAnh(String imageName) {
        SwingUtilities.invokeLater(() -> {
            try {
                String imagePath = getImagePath(imageName);
                ImageIcon originalIcon = new ImageIcon(imagePath);

                // 🔥 CHỈNH KÍCH THƯỚC ẢNH NHỎ LẠI
                int targetWidth = 230;  // Giảm từ 252 xuống 200
                int targetHeight = 270; // Giảm từ 301 xuống 250

                Image scaledImage = originalIcon.getImage()
                        .getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                if (mainUI != null && mainUI.getBtnAnh() != null) {
                    mainUI.getBtnAnh().setIcon(scaledIcon);

                    // 🔥 CẬP NHẬT LẠI KÍCH THƯỚC BUTTON
                    ButtonManager.fixButtonSize(mainUI.getBtnAnh(), targetWidth, targetHeight);
                }
            } catch (Exception e) {
                System.err.println("Lỗi tải ảnh: " + e.getMessage());

                // 🔥 TẠO ẢNH MẶC ĐỊNH NHỎ HƠN NẾU LỖI
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

                    // Tự động đóng sau 1 giây
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
                // Làm rối tên người chơi
                if (mainUI.getJLabel2() != null) {
                    mainUI.getJLabel2().setText("!#@$@??");
                }

                // Làm rối điểm tinh thần
                if (mainUI.getJLabel8() != null) {
                    mainUI.getJLabel8().setText("####");
                }
            }
        });
    }

    private void khoiPhucGiaoDien() {
        SwingUtilities.invokeLater(() -> {
            if (mainUI != null) {
                // Khôi phục màu nền
                mainUI.getContentPane().setBackground(null);

                // Khôi phục thông tin người chơi
                mainUI.updateUI();
            }
        });
    }

    // Phương thức để hủy kịch bản nếu cần
    public void huyKichBan() {
        if (messageTimer != null && messageTimer.isRunning()) {
            messageTimer.stop();
        }
        khoiPhucGiaoDien();
        isKichBanActive = false;
    }

    public boolean isKichBanActive() {
        return isKichBanActive;
    }
}

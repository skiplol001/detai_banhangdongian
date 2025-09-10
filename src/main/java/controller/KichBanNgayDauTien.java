package controller;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 * @author lap top
 */
public class KichBanNgayDauTien {

    private final view.UIChinh mainUI;
    private final Timer dialogueTimer;
    private int dialogueStep = 0;
    private final String[] dialogues = {
        "Mọi việc không đơn giản như tôi nghĩ...",
        "(thở dài) Cậu đã làm tốt việc của mình...",
        "Nhưng những chuyện kì lạ vẫn còn đang..."
    };

    private static final String DB_PATH = "database/dialog";
    private static final String COUNT_FILE = DB_PATH + "/count.txt";

    public KichBanNgayDauTien(view.UIChinh mainUI) {
        this.mainUI = mainUI;
        this.dialogueTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextDialogue();
            }
        });
        this.dialogueTimer.setRepeats(true);

        // Đảm bảo thư mục database tồn tại
        ensureDatabaseDirectory();
    }

    private void ensureDatabaseDirectory() {
        File dbDir = new File(DB_PATH);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
    }

    public void startKichBan() {
        // Kiểm tra nếu đã là ngày đầu tiên chưa
        if (shouldRunFirstDayScript()) {
            dialogueStep = 0;
            dialogueTimer.start();
            showNextDialogue();
            markFirstDayCompleted();
        }
    }

    private boolean shouldRunFirstDayScript() {
        try {
            File countFile = new File(COUNT_FILE);
            if (!countFile.exists()) {
                return true; // Chưa có file -> ngày đầu tiên
            }

            BufferedReader reader = new BufferedReader(new FileReader(countFile));
            String line = reader.readLine();
            reader.close();

            if (line != null && line.matches("\\d+")) {
                int dayCount = Integer.parseInt(line);
                return dayCount == 1; // Chỉ chạy khi đúng ngày 1
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi đọc file count: " + e.getMessage());
        }
        return false;
    }

    private void markFirstDayCompleted() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(COUNT_FILE));
            writer.write("2"); // Đánh dấu đã qua ngày 1, chuyển sang ngày 2
            writer.close();
        } catch (IOException e) {
            System.err.println("Lỗi ghi file count: " + e.getMessage());
        }
    }

    private void showNextDialogue() {
        if (dialogueStep < dialogues.length) {
            // 🔥 TẠM DỪNG TIMER - CHỈ 1 DÒNG
            mainUI.tamDungTimer();

            // Hiển thị hội thoại
            JOptionPane.showMessageDialog(mainUI,
                    dialogues[dialogueStep],
                    "Một giọng nói bí ẩn",
                    JOptionPane.INFORMATION_MESSAGE);

            dialogueStep++;

            // 🔥 TIẾP TỤC TIMER - CHỈ 1 DÒNG
            mainUI.tiepTucTimer();
        } else {
            dialogueTimer.stop();
            showSystemError();
        }
    }

    private void showSystemError() {
        // 🔥 TẠM DỪNG TIMER
        mainUI.tamDungTimer();

        JOptionPane.showMessageDialog(mainUI,
                "⚠️ TRỤC TRẶC HỆ THỐNG ĐÃ ĐƯỢC KHẮC PHỤC\n\n"
                + "Thông báo kỹ thuật: Lỗi luồng dữ liệu #A7B9C đã được giải quyết.\n"
                + "Tất cả hoạt động đã trở lại bình thường.\n\n"
                + "(Thông báo cuối cùng từ hệ thống: 'Thời gian đến đây thôi... Hẹn gặp cậu ngay hôm sau.')",
                "Hệ Thống Đã Ổn Định",
                JOptionPane.WARNING_MESSAGE);

        // 🔥 TIẾP TỤC TIMER
        mainUI.tiepTucTimer();
    }

    // Phương thức static để các class khác có thể cập nhật số ngày
    public static void updateDayCount(int newDay) {
        try {
            String projectPath = System.getProperty("user.dir");
            String countFilePath = Paths.get(projectPath, "database", "dialog", "count.txt").toString();

            try (FileWriter writer = new FileWriter(countFilePath)) {
                writer.write(String.valueOf(newDay));
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi cập nhật số ngày: " + e.getMessage());
        }
    }

    // Phương thức static để đọc số ngày hiện tại
    public static int getCurrentDayCount() {
        try {
            File countFile = new File(COUNT_FILE);
            if (!countFile.exists()) {
                return 1; // Mặc định là ngày 1
            }

            BufferedReader reader = new BufferedReader(new FileReader(countFile));
            String line = reader.readLine();
            reader.close();

            if (line != null && line.matches("\\d+")) {
                return Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi đọc số ngày: " + e.getMessage());
        }
        return 1;
    }

    private static void ensureDirectory() {
        File dbDir = new File(DB_PATH);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
    }

}

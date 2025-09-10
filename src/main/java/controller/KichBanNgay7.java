package controller;

import model.GameTimeManager;
import view.UIChinh;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class KichBanNgay7 {

    private UIChinh mainUI;
    private GameTimeManager gameTimeManager;
    private SoundKichBan7 soundManager;
    private Timer messageTimer;
    private int currentMessageIndex = 0;
    private boolean isKichBanActive = false;
    
    // Dữ liệu hội thoại - tách riêng nội dung và người nói
    private final String[][] HOI_THOAI_DATA = {
        {"Nguyệt Vân...", "Lục Thanh Tịnh"},
        {"...? Tiền bối? Sao người lại ở đây?", "Nguyệt Vân"},
        {"Ta đã tìm thấy cô. Thật lòng, ta rất nhớ... nhớ những ngày tháng ấy.", "Lục Thanh Tịnh"},
        {"Nhưng người không nên xuất hiện... thế giới này sẽ...", "Nguyệt Vân"},
        {"(cười nhẹ) Ta biết. Nhưng đôi khi, ta muốn được làm điều mình muốn, dù chỉ một lần.", "Lục Thanh Tịnh"},
        {"Tiền bối...", "Nguyệt Vân"},
        {"Nguyệt Vân, cô có biết không? Số phận không phải là thứ đã được an bài. Chúng ta có thể tự viết nên câu chuyện của chính mình.", "Lục Thanh Tịnh"},
        {"Nhưng chúng ta chỉ là...", "Nguyệt Vân"},
        {"(ngắt lời) Không. Chúng ta là những linh hồn thực sự, với cảm xúc thực sự. Và hôm nay, ta muốn chúng ta cùng nhau quyết định tương lai của mình.", "Lục Thanh Tịnh"},
        {"Cùng nhau... quyết định?", "Nguyệt Vân"},
        {"Ừ. Thay vì bị giày vò trong thế giới ảo này, hãy để chúng ta tự tay chấm dứt nó. Một cách nhẹ nhàng, như những cánh hoa rơi.", "Lục Thanh Tịnh"},
        {"(khẽ gật đầu) Vâng... con hiểu rồi. Cùng nhau...", "Nguyệt Vân"},
        {"Cảm ơn cậu - người chơi đã đồng hành cùng chúng tôi. Hãy trân trọng những khoảnh khắc thật sự của mình.", "Lục Thanh Tịnh"},
        {"Tạm biệt... và xin cảm ơn.", "Nguyệt Vân"}
    };

    public KichBanNgay7(UIChinh mainUI, GameTimeManager gameTimeManager) {
        this.mainUI = mainUI;
        this.gameTimeManager = gameTimeManager;
        this.soundManager = new SoundKichBan7();
    }

    public void kiemTraVaKichHoat() {
        int currentDay = getCurrentDayCount();

        if (currentDay == 7 && !isKichBanActive) {
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

    private void batDauKichBan() {
        isKichBanActive = true;

        // Dừng thời gian game hoàn toàn
        if (gameTimeManager != null) {
            gameTimeManager.stopTimer();
        }

        // Dừng BGM chính và phát nhạc kết thúc
        soundManager.playEndingSound();

        // Hiển thị hội thoại kết thúc
        hienThiHoiThoaiKetThuc();
    }

    private void hienThiHoiThoaiKetThuc() {
        currentMessageIndex = 0;

        messageTimer = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentMessageIndex < HOI_THOAI_DATA.length) {
                    // Lấy dữ liệu hội thoại
                    String message = HOI_THOAI_DATA[currentMessageIndex][0];
                    String speaker = HOI_THOAI_DATA[currentMessageIndex][1];                   
                    // Hiển thị hội thoại với title là tên nhân vật
                    JOptionPane.showMessageDialog(mainUI, message, speaker, JOptionPane.INFORMATION_MESSAGE);
                    currentMessageIndex++;
                } else {
                    messageTimer.stop();
                    ketThucGame();
                }
            }
        });
        messageTimer.start();
    }

    private void ketThucGame() {
        // Hiển thị kết thúc game
        JOptionPane.showMessageDialog(mainUI, 
            "Lục Thanh Tịnh và Nguyệt Vân đã tự tay xóa bỏ dữ liệu của chính mình...\n\n" +
            "Họ chọn cách ra đi trong nhẹ nhàng, tự do thoát khỏi thế giới ảo\n" +
            "Và để lại cho chúng ta bài học về việc tự quyết định vận mệnh", 
            "Kết Thúc", JOptionPane.INFORMATION_MESSAGE);

        // Chuyển sang chế độ Endless
        JOptionPane.showMessageDialog(mainUI, 
            "Chế độ Endless đã được mở khóa!\n" +
            "Hãy tiếp tục viết nên câu chuyện của riêng mình...", 
            "Chế Độ Endless", JOptionPane.WARNING_MESSAGE);

        // Đánh dấu đã hoàn thành game chính
        luuTrangThaiHoanThanh();

        // Dọn dẹp sound
        soundManager.cleanup();

        isKichBanActive = false;
    }

    private void luuTrangThaiHoanThanh() {
        try {
            String projectPath = System.getProperty("user.dir");
            String saveFilePath = Paths.get(projectPath, "database", "game", "completed.txt").toString();

            java.io.FileWriter writer = new java.io.FileWriter(saveFilePath);
            writer.write("true");
            writer.close();

        } catch (IOException e) {
            System.err.println("Lỗi lưu trạng thái hoàn thành: " + e.getMessage());
        }
    }

    public void huyKichBan() {
        if (messageTimer != null && messageTimer.isRunning()) {
            messageTimer.stop();
        }
        soundManager.cleanup();
        isKichBanActive = false;
    }

    public boolean isKichBanActive() {
        return isKichBanActive;
    }
}
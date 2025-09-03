package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import util.GameStateManager;

/**
 * Lớp Opening hiển thị các thông báo giới thiệu gameplay và một chuỗi hội thoại
 * bí ẩn trước khi người chơi bắt đầu trò chơi chính. Chỉ chạy tutorial một lần
 * duy nhất cho mỗi người dùng.
 */
public class Opening {

    private UIChinh mainUI;
    private int currentMessageIndex = 0;

    // Danh sách các thông báo sẽ hiển thị
        private final String[] gameplayMessages = {
        "Chào mừng đến với Cửa Hàng Hoang!",
        "Đây là nơi bạn sẽ quản lý một cửa hàng kỳ lạ...",
        "Cậu có thấy?... chiếc lư hương góc trái màn hình không?",
        "Chiếc lư hương này sẽ bói mệnh giúp cậu xem được thông tin người trước mặt",
        "Quyển số tay góc phải màn hình...",
        "Quyển số tay ghi chép thông tin của người",
        "Cậu có thấy vị khách trước mặt không?Ấn vào cậu có thể xem yêu cầu của họ",
        "Nút 'bán': Bán vật phẩm cho khách hàng nếu bạn có đủ",
        "Nút 'không': Từ chối giao dịch với khách hàng",
        "Nút 'Cửa Hàng': Mở cửa hàng để mua vật phẩm mới",
        "Quản lý điểm tinh thần và tiền bạc khôn ngoan!",
        "Hãy cẩn thận, không phải tất cả khách hàng đều bình thường..."
    };

    // Chuỗi hội thoại bí ẩn
    private final String[] mysteriousDialogue = {
        "Cậu là ai? Sao lại ở đây?",
        "Tôi... cậu có lẽ có thể giúp được tôi..?",
        "Có thể cậu đang thắc mắc và hoang mang... đừng lo tôi không hại cậu",
        "Cậu chỉ việc chơi game như bình thường như tấm bình phong cho tôi"
    };

    public Opening(UIChinh mainUI) {
        this.mainUI = mainUI;
    }

    /**
     * Hiển thị tất cả các thông báo gameplay
     */
    public void showGameplayInstructions() {
        currentMessageIndex = 0;
        showNextGameplayMessage();
    }

    private void showNextGameplayMessage() {
        if (currentMessageIndex >= gameplayMessages.length) {
            // Kết thúc phần hướng dẫn, hiển thị báo lỗi giả
            showFakeError();
            return;
        }

        JOptionPane.showMessageDialog(mainUI,
                gameplayMessages[currentMessageIndex],
                "Hướng dẫn Gameplay",
                JOptionPane.INFORMATION_MESSAGE);

        currentMessageIndex++;

        // Tự động hiển thị message tiếp theo sau một khoảng thời gian
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextGameplayMessage();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Hiển thị báo lỗi giả trong 5 giây
     */
    private void showFakeError() {
        // Tạo một dialog KHÔNG modal để không chặn luồng thực thi
        JDialog errorDialog = new JDialog(mainUI, "Lỗi Hệ Thống", false);
        errorDialog.setSize(400, 200);
        errorDialog.setLayout(new BorderLayout());
        errorDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        errorDialog.setLocationRelativeTo(mainUI);

        // Tạo nội dung thông báo lỗi
        JLabel errorLabel = new JLabel("Lỗi kết nối hệ thống... Đang thử kết nối lại...", JLabel.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // Thanh tiến trình không xác định

        errorDialog.add(errorLabel, BorderLayout.CENTER);
        errorDialog.add(progressBar, BorderLayout.SOUTH);

        // Hiển thị dialog
        errorDialog.setVisible(true);

        // Tạo timer để đóng dialog sau 5 giây
        Timer errorTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorDialog.dispose();
                showMysteriousDialogue();
            }
        });
        errorTimer.setRepeats(false);
        errorTimer.start();
    }

    /**
     * Hiển thị chuỗi hội thoại bí ẩn
     */
    private void showMysteriousDialogue() {
        currentMessageIndex = 0;
        showNextDialogueMessage();
    }

    private void showNextDialogueMessage() {
        if (currentMessageIndex >= mysteriousDialogue.length) {
            // Hiển thị option cuối cùng với Yes/No
            showFinalQuestion();
            return;
        }

        JOptionPane.showMessageDialog(mainUI,
                mysteriousDialogue[currentMessageIndex],
                "???",
                JOptionPane.QUESTION_MESSAGE);

        currentMessageIndex++;

        // Tự động hiển thị message tiếp theo sau một khoảng thời gian
        Timer timer = new Timer(2500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextDialogueMessage();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Hiển thị câu hỏi cuối cùng với lựa chọn Yes/No
     */
    private void showFinalQuestion() {
        int response = JOptionPane.showOptionDialog(mainUI,
                "Cậu có nguyện giúp tôi không?",
                "Quyết định quan trọng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Có", "Không"}, // Hiển thị tiếng Việt
                "Có");

        if (response == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(mainUI,
                    "Thật tốt vì cậu đã ở lại... Hãy bắt đầu thôi!",
                    "Mừng rỡ",
                    JOptionPane.INFORMATION_MESSAGE);
            GameStateManager.saveUserChoice(false);
            GameStateManager.markTutorialCompleted();

            // Kích hoạt UI chính sau khi hoàn thành
            mainUI.setEnabled(true);
            mainUI.toFront();
        } else {
            JOptionPane.showMessageDialog(mainUI,
                    "Thật tốt vì cậu đã ở lại... Hãy bắt đầu thôi!",
                    "Mừng rỡ",
                    JOptionPane.INFORMATION_MESSAGE);
            GameStateManager.saveUserChoice(true);
            GameStateManager.markTutorialCompleted();

            // Đóng ứng dụng sau 2 giây
            Timer exitTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            exitTimer.setRepeats(false);
            exitTimer.start();
        }
    }

    /**
     * Phương thức chính để bắt đầu trình tự opening
     */
    private void showReturnChoice() {
        int response = JOptionPane.showOptionDialog(mainUI,
                "Cậu quay lại rồi à? Cậu muốn ở lại hay rời đi?",
                "Quyết định",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Ở lại", "Rời đi"}, // Hiển thị tiếng Việt
                "Ở lại");

        if (response == JOptionPane.YES_OPTION) {
            // Người chơi chọn ở lại
            JOptionPane.showMessageDialog(mainUI,
                    "Cảm ơn cậu đã quay lại... Hãy bắt đầu nào!",
                    "Lời cảm ơn",
                    JOptionPane.INFORMATION_MESSAGE);
            GameStateManager.saveUserChoice(false);

            // Kích hoạt UI chính sau khi hoàn thành
            mainUI.setEnabled(true);
            mainUI.toFront();
        } else {
            // Người chơi chọn rời đi
            JOptionPane.showMessageDialog(mainUI,
                    "Tôi tôn trọng quyết định của cậu... tạm biệt",
                    "Lời tạm biệt",
                    JOptionPane.INFORMATION_MESSAGE);
            GameStateManager.saveUserChoice(true);

            // Đóng ứng dụng sau 2 giây
            Timer exitTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            exitTimer.setRepeats(false);
            exitTimer.start();
        }
    }

    /**
     * Phương thức chính để bắt đầu trình tự opening
     */
    public void startOpeningSequence() {
        // Kiểm tra xem người dùng đã từ chối trong lần trước không
        if (GameStateManager.hasUserRefusedBefore()) {
            // Hiển thị lựa chọn cho người chơi đã từ chối trước đó
            showReturnChoice();
            return;
        }

        // Kiểm tra xem tutorial đã hoàn thành chưa
        if (GameStateManager.isTutorialCompleted()) {
            // Nếu đã hoàn thành tutorial, kích hoạt UI chính ngay lập tức
            mainUI.setEnabled(true);
            mainUI.toFront();
            return;
        }

        // Vô hiệu hóa UI chính trong khi hiển thị opening
        mainUI.setEnabled(false);

        // Tạo một timer để bắt đầu trình tự sau một khoảng thời gian ngắn
        Timer startTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameplayInstructions();
            }
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }
}

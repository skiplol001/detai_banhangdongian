package controller;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import util.SoundManager;

public class SoundKichBan4 {

    private Clip currentClip;
    private long mainBGMPosition = 0;

    public SoundKichBan4() {
        // Constructor
    }

    /**
     * Tạm dừng BGM chính và ghi nhớ vị trí
     */
    public void pauseMainBGM() {
        // Lưu vị trí hiện tại của BGM chính
        mainBGMPosition = SoundManager.getBGMPosition();
        // Tạm dừng BGM chính
        SoundManager.pauseBGM();
        System.out.println(" Đã tạm dừng BGM chính tại vị trí: " + mainBGMPosition);
    }

    /**
     * Phát âm thanh cho từng giai đoạn kịch bản (1.wav, 2.wav, 3.wav, 4.wav)
     */
    public void playStageSound(int stageNumber) {
        stopCurrentSound();

        String soundFileName = stageNumber + ".wav";
        String soundPath = getSoundPath(soundFileName);

        System.out.println("Phát âm thanh giai đoạn " + stageNumber + ": " + soundPath);

        try {
            File soundFile = new File(soundPath);
            if (soundFile.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                currentClip = AudioSystem.getClip();
                currentClip.open(audioInputStream);

                // Thiết lập volume
                setVolume(currentClip, 0.9f);

                currentClip.start();

                // Thêm listener để biết khi nào âm thanh kết thúc
                currentClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        System.out.println(" Âm thanh giai đoạn " + stageNumber + " kết thúc");
                    }
                });

            } else {
                System.err.println(" File âm thanh không tồn tại: " + soundPath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println(" Lỗi phát âm thanh giai đoạn " + stageNumber + ": " + e.getMessage());
        }
    }

    /**
     * Dừng âm thanh hiện tại
     */
    public void stopCurrentSound() {
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            currentClip.close();
            currentClip = null;
        }
    }

    /**
     * Tiếp tục BGM chính của game từ vị trí đã tạm dừng
     */
    public void resumeMainBGM() {
        stopCurrentSound();

        // Tiếp tục BGM chính từ vị trí đã lưu
        SoundManager.resumeBGMFromPosition(mainBGMPosition);
    }

    /**
     * Thiết lập volume cho clip
     */
    private void setVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }

    /**
     * Lấy đường dẫn tuyệt đối đến file âm thanh kịch bản
     */
    private String getSoundPath(String fileName) {
        String projectPath = System.getProperty("user.dir");
        return Paths.get(projectPath, "database", "dialog", "level4", fileName).toString();
    }

    /**
     * Dọn dẹp tài nguyên
     */
    public void cleanup() {
        stopCurrentSound();
    }
}

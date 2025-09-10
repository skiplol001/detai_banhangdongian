package controller;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import util.SoundManager;

public class SoundKichBan7 {

    private Clip endingSound;
    private long mainBGMPosition = 0;

    public SoundKichBan7() {
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
        System.out.println("Đã tạm dừng BGM chính tại vị trí: " + mainBGMPosition);
    }

    /**
     * Phát nhạc ending.wav trong suốt kịch bản 7
     */
    public void playEndingSound() {
        stopCurrentSound();

        // Thử phát file MP3 trước, nếu không có thì dùng WAV
        String[] extensions = {".mp3", ".wav"};
        String soundPath = null;

        for (String ext : extensions) {
            String testPath = getSoundPath("ending" + ext);
            if (new File(testPath).exists()) {
                soundPath = testPath;
                break;
            }
        }

        if (soundPath == null) {
            System.err.println("Không tìm thấy file ending.wav hoặc ending.mp3");
            return;
        }

        System.out.println("Phát nhạc ending: " + soundPath);

        try {
            AudioInputStream audioInputStream;

            if (soundPath.endsWith(".mp3")) {
                // Xử lý MP3
                audioInputStream = AudioSystem.getAudioInputStream(new File(soundPath));
                AudioFormat baseFormat = audioInputStream.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false
                );
                audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
            } else {
                // Xử lý WAV
                audioInputStream = AudioSystem.getAudioInputStream(new File(soundPath));
            }

            endingSound = AudioSystem.getClip();
            endingSound.open(audioInputStream);
            setVolume(endingSound, 0.9f);
            endingSound.loop(Clip.LOOP_CONTINUOUSLY);
            endingSound.start();

            System.out.println("Nhạc ending đã bắt đầu phát (loop)");

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Lỗi phát nhạc ending: " + e.getMessage());
        }
    }

    /**
     * Dừng âm thanh hiện tại
     */
    public void stopCurrentSound() {
        if (endingSound != null) {
            if (endingSound.isRunning()) {
                endingSound.stop();
            }
            endingSound.close();
            endingSound = null;
        }
    }

    /**
     * Tiếp tục BGM chính của game từ vị trí đã tạm dừng
     */
    public void resumeMainBGM() {
        stopCurrentSound();

        // Tiếp tục BGM chính từ vị trí đã lưu
        SoundManager.resumeBGMFromPosition(mainBGMPosition);
        System.out.println("Đã tiếp tục BGM chính từ vị trí: " + mainBGMPosition);
    }

    /**
     * Thiết lập volume cho clip
     */
    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }

    /**
     * Lấy đường dẫn tuyệt đối đến file âm thanh kịch bản 7
     */
    private String getSoundPath(String fileName) {
        String projectPath = System.getProperty("user.dir");
        return Paths.get(projectPath, "database", "dialog", "level7", fileName).toString();
    }

    /**
     * Dọn dẹp tài nguyên
     */
    public void cleanup() {
        stopCurrentSound();
    }
}

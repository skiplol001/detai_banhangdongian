package util;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class quản lý âm thanh - hỗ trợ cả sound effect và BGM Tích hợp với UI chính
 * và quản lý vòng đời ứng dụng
 */
public class SoundManager {

    private static boolean soundEnabled = true;
    private static boolean bgmEnabled = true;
    private static Clip successClip;
    private static Clip bgmClip;
    private static float volume = 0.8f;

    // Biến để theo dõi trạng thái
    private static boolean soundsLoaded = false;

    // Danh sách các listener để thông báo khi ứng dụng đóng
    private static List<Runnable> appCloseListeners = new ArrayList<>();

    // Tham chiếu đến UI chính để quản lý BGM
    private static Object mainUIReference;

    /**
     * Khởi tạo âm thanh (được gọi khi cần)
     */
    public static void initialize() {
        if (!soundsLoaded) {
            loadSounds();
        }
    }

    /**
     * Thiết lập tham chiếu đến UI chính
     */
    public static void setMainUIReference(Object mainUI) {
        mainUIReference = mainUI;
    }

    /**
     * Load tất cả âm thanh
     */
    private static void loadSounds() {
        try {
            loadSuccessSound();
            loadBGMSound();
            soundsLoaded = true;
            System.out.println("Âm thanh đã được load thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi load âm thanh: " + e.getMessage());
            soundsLoaded = false;
        }
    }

    /**
     * Load âm thanh success
     */
    private static void loadSuccessSound() {
        try {
            URL soundUrl = getSoundURL("success.wav");
            if (soundUrl != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundUrl);
                successClip = AudioSystem.getClip();
                successClip.open(audioIn);
            } else {
                System.err.println("Không thể tìm thấy file: sounds/success.wav");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi load success sound: " + e.getMessage());
        }
    }

    /**
     * Load nhạc nền BGM
     */
    private static void loadBGMSound() {
        try {
            URL bgmUrl = getSoundURL("bgm.wav");
            if (bgmUrl != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bgmUrl);
                bgmClip = AudioSystem.getClip();
                bgmClip.open(audioIn);
            } else {
                System.err.println("Không thể tìm thấy file: sounds/bgm.wav");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi load BGM: " + e.getMessage());
        }
    }

    /**
     * Helper method để lấy URL của file âm thanh
     */
    private static URL getSoundURL(String filename) {
        // Thử load từ classpath trước (khi đóng gói trong JAR)
        URL url = SoundManager.class.getClassLoader().getResource("sounds/" + filename);

        // Nếu không tìm thấy, thử load từ file system
        if (url == null) {
            try {
                File soundFile = new File("sounds/" + filename);
                if (soundFile.exists()) {
                    url = soundFile.toURI().toURL();
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tìm file âm thanh: " + e.getMessage());
            }
        }

        return url;
    }

    /**
     * Bật/tắt tất cả âm thanh
     */
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!enabled) {
            stopBGM();
        } else if (enabled && bgmEnabled) {
            playBGM();
        }
    }

    /**
     * Bật/tắt BGM
     */
    public static void setBGMEnabled(boolean enabled) {
        bgmEnabled = enabled;
        if (enabled) {
            playBGM();
        } else {
            stopBGM();
        }
    }

    /**
     * Kiểm tra âm thanh có được bật không
     */
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Kiểm tra BGM có được bật không
     */
    public static boolean isBGMEnabled() {
        return bgmEnabled;
    }

    /**
     * Phát âm thanh success - dùng khi mua hàng hoàn tất
     */
    public static void playSuccessSound() {
        if (!soundEnabled || !soundsLoaded) {
            return;
        }

        try {
            if (successClip != null) {
                if (successClip.isRunning()) {
                    successClip.stop();
                }
                successClip.setFramePosition(0);
                successClip.start();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi phát success sound: " + e.getMessage());
        }
    }

    /**
     * Phát nhạc nền BGM - chỉ tắt khi đóng ứng dụng hoàn toàn
     */
    public static void playBGM() {
        if (!soundEnabled || !bgmEnabled || !soundsLoaded) {
            return;
        }

        try {
            if (bgmClip != null && !bgmClip.isRunning()) {
                bgmClip.setFramePosition(0);
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                setVolume(volume);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi phát BGM: " + e.getMessage());
        }
    }

    /**
     * Dừng nhạc nền BGM - chỉ gọi khi đóng ứng dụng
     */
    public static void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    /**
     * Tạm dừng BGM (không giải phóng tài nguyên) - dùng khi mở form con
     */
    public static void pauseBGMForChildWindow() {
        // Không làm gì cả - BGM tiếp tục chạy ngay cả khi mở form con
        // Phương thức này giữ nguyên để tương thích nhưng không thực hiện hành động
    }

    /**
     * Tiếp tục BGM - dùng khi đóng form con
     */
    public static void resumeBGMAfterChildWindow() {
        // Không làm gì cả - BGM không bị tắt nên không cần resume
        // Phương thức này giữ nguyên để tương thích
    }

    /**
     * Thiết lập volume (0.0 - 1.0)
     */
    public static void setVolume(float vol) {
        volume = Math.max(0.0f, Math.min(1.0f, vol));

        if (bgmClip != null && bgmClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            try {
                FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Lỗi khi set volume: " + e.getMessage());
            }
        }
    }

    /**
     * Lấy volume hiện tại
     */
    public static float getVolume() {
        return volume;
    }

    /**
     * Thêm listener để xử lý khi ứng dụng đóng
     */
    public static void addAppCloseListener(Runnable listener) {
        appCloseListeners.add(listener);
    }

    /**
     * Giải phóng tài nguyên (chỉ gọi khi đóng ứng dụng hoàn toàn)
     */
    public static void cleanup() {
        // Thông báo cho tất cả listeners rằng ứng dụng đang đóng
        for (Runnable listener : appCloseListeners) {
            try {
                listener.run();
            } catch (Exception e) {
                System.err.println("Lỗi khi chạy app close listener: " + e.getMessage());
            }
        }

        // Dọn dẹp tài nguyên âm thanh
        stopBGM();
        if (successClip != null) {
            successClip.close();
        }
        if (bgmClip != null) {
            bgmClip.close();
        }
        soundsLoaded = false;

        System.out.println("SoundManager đã được dọn dẹp");
    }

    /**
     * Kiểm tra xem BGM có đang phát không
     */
    public static boolean isBGMPlaying() {
        return bgmClip != null && bgmClip.isRunning();
    }

    /**
     * Tạm dừng BGM (không giải phóng tài nguyên)
     */
    public static void pauseBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    /**
     * Tiếp tục BGM nếu đã được enabled
     */
    public static void resumeBGM() {
        if (soundEnabled && bgmEnabled && bgmClip != null && !bgmClip.isRunning()) {
            try {
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) {
                System.err.println("Lỗi khi resume BGM: " + e.getMessage());
            }
        }
    }

    /**
     * Kiểm tra xem âm thanh đã được load thành công
     */
    public static boolean isSoundsLoaded() {
        return soundsLoaded;
    }
}

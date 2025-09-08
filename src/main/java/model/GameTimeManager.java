package model;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

public class GameTimeManager {

    private Timer gameTimer;
    private long gameStartTime;
    private final float TIME_SCALE = 5.0f; // Tăng tốc độ lên 10 lần để test nhanh
    private int gameDayLength = 8 * 60 * 60 * 10; // 8 giờ game (tính bằng 1/100 giây)
    private int startHour = 19; // 7h tối
    private int endHour = 3; // 3h sáng
    private int currentDay = 1;
    private TimeUpdateListener updateListener;
    private DayEndListener dayEndListener;

    public interface TimeUpdateListener {

        void onTimeUpdate(String timeString, int currentHour, boolean isNight);
    }

    public interface DayEndListener {

        void onDayEnd();
    }

    public GameTimeManager() {
        initGameTimer();
    }

    public void setUpdateListener(TimeUpdateListener listener) {
        this.updateListener = listener;
    }

    public void setDayEndListener(DayEndListener listener) {
        this.dayEndListener = listener;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    private void initGameTimer() {
        gameStartTime = System.currentTimeMillis();
        gameTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGameTime();
            }
        });
    }

    public interface SpecialHourListener {

        void onSpecialHour(int hour);
    }

    private SpecialHourListener specialHourListener;

    public void setSpecialHourListener(SpecialHourListener listener) {
        this.specialHourListener = listener;
    }

    private void updateGameTime() {
        long realTimeElapsed = System.currentTimeMillis() - gameStartTime;
        long gameTimeElapsed = (long) (realTimeElapsed * TIME_SCALE);

        // Tính giờ trong game (bắt đầu từ 19:00)
        long totalTenthSeconds = gameTimeElapsed; // 1/10 giây
        long totalSeconds = totalTenthSeconds / 10;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (startHour + totalSeconds / 3600) % 24;

        String timeString = String.format("%02d:%02d", hours, minutes);
        boolean isNight = hours >= 19 || hours < 7;

        // 🔥 KIỂM TRA GIỜ ĐẶC BIỆT: 12h đêm (0 giờ)
        if (hours == 0 && minutes == 0 && specialHourListener != null) {
            specialHourListener.onSpecialHour(0); // 0 = 12h đêm
        }

        // Kiểm tra nếu đã đến 3h sáng (kết thúc ngày)
        if (hours == endHour && minutes == 0) {
            if (dayEndListener != null) {
                SwingUtilities.invokeLater(() -> {
                    dayEndListener.onDayEnd();
                    currentDay++;
                });
            }
            stopTimer();
            return;
        }

        if (updateListener != null) {
            updateListener.onTimeUpdate(timeString, (int) hours, isNight);
        }
    }

    public void startTimer() {
        gameStartTime = System.currentTimeMillis();
        gameTimer.start();
    }

    public void stopTimer() {
        gameTimer.stop();
    }

    public void restartTimer() {
        stopTimer();
        gameStartTime = System.currentTimeMillis();
        startTimer();
    }

    public boolean isTimerRunning() {
        return gameTimer.isRunning();
    }

    public float getTimeProgress() {
        long realTimeElapsed = System.currentTimeMillis() - gameStartTime;
        long gameTimeElapsed = (long) (realTimeElapsed * TIME_SCALE);
        return (float) gameTimeElapsed / gameDayLength;
    }

    public String getRemainingTime() {
        long realTimeElapsed = System.currentTimeMillis() - gameStartTime;
        long gameTimeElapsed = (long) (realTimeElapsed * TIME_SCALE);
        long remaining = gameDayLength - gameTimeElapsed;

        long remainingTenthSeconds = remaining;
        long remainingSeconds = remainingTenthSeconds / 10;
        long minutes = (remainingSeconds / 60) % 60;
        long hours = remainingSeconds / 3600;

        return String.format("%02d:%02d", hours, minutes);
    }

    // Phương thức để test nhanh - chạy thẳng đến gần 3h sáng
    public void fastForwardToNearEnd() {
        // Đặt thời gian bắt đầu sao cho chỉ còn 1 phút nữa là đến 3h sáng
        long almostFullDay = gameDayLength - (1 * 60 * 10); // 1 phút trước khi kết thúc
        gameStartTime = System.currentTimeMillis() - (long) (almostFullDay / TIME_SCALE);
    }

}

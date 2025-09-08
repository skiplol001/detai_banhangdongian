package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Class quản lý trạng thái game và lựa chọn của người dùng
 */
public class GameStateManager {

    private static final String CHOICE_FILE = "user_choice.txt";
    private static final String TUTORIAL_COMPLETED_FILE = "tutorial_completed.txt";

    /**
     * Kiểm tra xem người dùng đã từ chối giúp đỡ trong lần chơi trước không
     */
    public static boolean hasUserRefusedBefore() {
        File file = new File(CHOICE_FILE);
        if (!file.exists()) {
            return false;
        }

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                String choice = scanner.nextLine();
                return "refused".equals(choice);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Kiểm tra xem tutorial đã hoàn thành chưa
     */
    public static boolean isTutorialCompleted() {
        File file = new File(TUTORIAL_COMPLETED_FILE);
        return file.exists();
    }

    /**
     * Đánh dấu tutorial đã hoàn thành
     */
    public static void markTutorialCompleted() {
        try (FileWriter writer = new FileWriter(TUTORIAL_COMPLETED_FILE)) {
            writer.write("completed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lưu lựa chọn của người dùng vào file
     */
    public static void saveUserChoice(boolean refused) {
        try (FileWriter writer = new FileWriter(CHOICE_FILE)) {
            writer.write(refused ? "refused" : "accepted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Xóa tất cả trạng thái game (dùng cho testing hoặc reset game)
     */
    public static void resetGameState() {
        File choiceFile = new File(CHOICE_FILE);
        File tutorialFile = new File(TUTORIAL_COMPLETED_FILE);

        if (choiceFile.exists()) {
            choiceFile.delete();
        }

        if (tutorialFile.exists()) {
            tutorialFile.delete();
        }
    }
    // Thêm phương thức này vào lớp GameStateManager

    public static String getPlayerName() {
        try {
            String projectPath = System.getProperty("user.dir");
            String filePath = Paths.get(projectPath, "database", "player", "player_name.txt").toString();

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String name = reader.readLine();
            reader.close();

            return name != null ? name.trim() : "Người chơi";
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc tên người chơi: " + e.getMessage());
            return "Người chơi";
        }
    }
}

package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ROT13Decoder {

    // Sử dụng đường dẫn tương đối
    private static final String BASE_PATH = "database/dialog";
    private static final String COUNT_FILE = BASE_PATH + "/count.txt";
    private static final String ENCRYPTED_FILE_PREFIX = "xubvqnh";

    /**
     * Giải mã file xubvqnh.txt và GHI ĐÈ file gốc thành dạng đọc được
     * Trả về đường dẫn đến file đã được giải mã
     */
    public static String decodeAndOverwriteFile() {
        try {
            // Đọc số ngày từ file count.txt
            int dayCount = readDayCount();

            // Xác định thư mục tương ứng với ngày hiện tại (chỉ level1-6)
            String folderName = getFolderNameByDay(dayCount);
            String folderPath = BASE_PATH + "/" + folderName;

            // Kiểm tra xem thư mục có tồn tại không
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                return "Không tìm thấy thư mục cho ngày " + dayCount;
            }

            // Tìm file xubvqnh.txt trong thư mục
            File encryptedFile = findEncryptedFile(folder);
            if (encryptedFile == null) {
                return "Không tìm thấy file xubvqnh.txt trong thư mục " + folderName;
            }

            // Sao lưu file gốc trước khi ghi đè (tùy chọn)
            backupOriginalFile(encryptedFile);

            // Giải mã nội dung file
            String encryptedContent = readFileContent(encryptedFile);
            String decryptedContent = rot13Decode(encryptedContent);

            // GHI ĐÈ file gốc với nội dung đã giải mã
            writeToFile(encryptedFile.getAbsolutePath(), decryptedContent);

            return encryptedFile.getAbsolutePath();

        } catch (IOException e) {
            return "Lỗi khi xử lý file: " + e.getMessage();
        }
    }

    /**
     * Sao lưu file gốc trước khi ghi đè (tùy chọn)
     */
    private static void backupOriginalFile(File originalFile) throws IOException {
        String backupPath = originalFile.getAbsolutePath() + ".backup";
        Files.copy(originalFile.toPath(), Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Giải mã nội dung để hiển thị tạm thời (không ghi file)
     */
    public static String decodeForDisplay() {
        try {
            int dayCount = readDayCount();
            String folderName = getFolderNameByDay(dayCount);
            String folderPath = BASE_PATH + "/" + folderName;

            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                return "Không tìm thấy thư mục cho ngày này!";
            }

            File encryptedFile = findEncryptedFile(folder);
            if (encryptedFile == null) {
                return "Không tìm thấy file hội thoại!";
            }

            String encryptedContent = readFileContent(encryptedFile);
            return rot13Decode(encryptedContent);

        } catch (IOException e) {
            return "Lỗi khi đọc file!";
        }
    }

    /**
     * Đọc số ngày từ file count.txt
     */
    private static int readDayCount() throws IOException {
        Path countPath = Paths.get(COUNT_FILE);
        if (!Files.exists(countPath)) {
            Files.createDirectories(countPath.getParent());
            Files.write(countPath, "1".getBytes());
            return 1;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(COUNT_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                int day = Integer.parseInt(line.trim());
                return Math.max(1, Math.min(6, day));
            }
        }

        return 1;
    }

    /**
     * Xác định tên thư mục dựa trên số ngày (chỉ level1-6)
     */
    private static String getFolderNameByDay(int dayCount) {
        return "level" + dayCount;
    }

    /**
     * Tìm file mã hóa trong thư mục
     */
    private static File findEncryptedFile(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(ENCRYPTED_FILE_PREFIX)) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * Đọc nội dung file
     */
    private static String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Giải mã chuỗi sử dụng ROT13
     */
    public static String rot13Decode(String input) {
        StringBuilder output = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                if (c <= 'm') {
                    output.append((char) (c + 13));
                } else {
                    output.append((char) (c - 13));
                }
            } else if (c >= 'A' && c <= 'Z') {
                if (c <= 'M') {
                    output.append((char) (c + 13));
                } else {
                    output.append((char) (c - 13));
                }
            } else {
                output.append(c);
            }
        }
        return output.toString();
    }

    /**
     * Ghi nội dung vào file
     */
    private static void writeToFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    /**
     * Cập nhật số ngày trong count.txt (chỉ từ 1-6)
     */
    public static void incrementDayCount() throws IOException {
        int currentDay = readDayCount();
        int newDay = Math.min(6, currentDay + 1);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COUNT_FILE))) {
            writer.write(String.valueOf(newDay));
        }
        
        // TỰ ĐỘNG GIẢI MÃ FILE KHI CHUYỂN NGÀY
        decodeAndOverwriteFile();
    }

    /**
     * Lấy số ngày hiện tại từ count.txt
     */
    public static int getCurrentDay() {
        try {
            return readDayCount();
        } catch (IOException e) {
            return 1;
        }
    }

    /**
     * Kiểm tra xem file đã được giải mã chưa
     */
    public static boolean isFileDecoded(int day) {
        try {
            String folderName = getFolderNameByDay(day);
            String folderPath = BASE_PATH + "/" + folderName;
            File folder = new File(folderPath);
            
            if (!folder.exists()) return false;
            
            File encryptedFile = findEncryptedFile(folder);
            if (encryptedFile == null) return false;
            
            // Đọc thử nội dung file để kiểm tra
            String content = readFileContent(encryptedFile);
            // Nếu file chứa các ký tự ROT13 đặc trưng, coi như chưa giải mã
            return !content.matches(".*[a-zA-Z].*");
        } catch (IOException e) {
            return false;
        }
    }
}
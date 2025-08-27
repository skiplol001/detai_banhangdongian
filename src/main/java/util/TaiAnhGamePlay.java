package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaiAnhGamePlay {

    // Danh sách động chứa các đường dẫn ảnh
    private static List<String> imagePaths = new ArrayList<>();
    private static final String BACKGROUND_IMAGE = "/img/0_0.jpg"; // Ảnh nền mặc định

    // Khởi tạo danh sách ảnh
    static {
        // Thêm các ảnh vào danh sách
        imagePaths.add("/img/0_0.jpg"); // Ảnh nền
        imagePaths.add("/img/1_1.jpg"); // Ảnh 1 - nam
        imagePaths.add("/img/2_1.jpg"); // Ảnh 2 - nam
        imagePaths.add("/img/3_0.jpg"); // Ảnh 3 - nữ
        imagePaths.add("/img/4_0.jpg"); // Ảnh 4 - nữ
        imagePaths.add("/img/5_0.jpg"); // Ảnh 5 - nữ
        // Có thể thêm nhiều ảnh khác theo cùng định dạng
    }

    // Phương thức thêm ảnh mới vào danh sách
    public static void addImagePath(String imagePath) {
        if (!imagePaths.contains(imagePath)) {
            imagePaths.add(imagePath);
        }
    }

    // Phương thức tải ảnh ngẫu nhiên (không bao gồm ảnh nền)
    public static ImageIcon loadRandomImage(int width, int height) {
        try {
            if (imagePaths.size() <= 1) {
                throw new RuntimeException("Không có ảnh nào để tải (chỉ có ảnh nền)");
            }

            // Chọn ngẫu nhiên một ảnh (trừ ảnh nền đầu tiên)
            Random random = new Random();
            int randomIndex = random.nextInt(imagePaths.size() - 1) + 1;
            String imagePath = imagePaths.get(randomIndex);

            InputStream inputStream = TaiAnhGamePlay.class.getResourceAsStream(imagePath);

            if (inputStream == null) {
                // Thử lại nếu không tìm thấy (có thể do thiếu dấu / ở đầu)
                inputStream = TaiAnhGamePlay.class.getResourceAsStream(imagePath.startsWith("/") ? imagePath : "/" + imagePath);
            }

            if (inputStream == null) {
                throw new RuntimeException("Không tìm thấy ảnh: " + imagePath);
            }

            byte[] imageData = inputStream.readAllBytes();
            ImageIcon originalIcon = new ImageIcon(imageData);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);

        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh ngẫu nhiên: " + e.getMessage());
            return createDefaultImage(width, height, "Ảnh ngẫu nhiên");
        }
    }

    // Phương thức tải ảnh theo giới tính (Nam/Nữ) - tương thích với QuanLyKhachHang
    public static ImageIcon loadImageByGender(String gioiTinh, int width, int height) {
        try {
            List<String> genderImages = getImagesByGender(gioiTinh);

            if (genderImages.isEmpty()) {
                System.out.println("Không tìm thấy ảnh cho giới tính: " + gioiTinh + ", sử dụng ảnh ngẫu nhiên");
                return loadRandomImage(width, height);
            }

            // Chọn ngẫu nhiên một ảnh từ danh sách giới tính phù hợp
            Random random = new Random();
            String imagePath = genderImages.get(random.nextInt(genderImages.size()));

            InputStream inputStream = TaiAnhGamePlay.class.getResourceAsStream(imagePath);

            if (inputStream == null) {
                // Thử lại nếu không tìm thấy
                inputStream = TaiAnhGamePlay.class.getResourceAsStream(imagePath.startsWith("/") ? imagePath : "/" + imagePath);
            }

            if (inputStream == null) {
                System.err.println("Không tìm thấy ảnh: " + imagePath);
                return loadRandomImage(width, height);
            }

            byte[] imageData = inputStream.readAllBytes();
            ImageIcon originalIcon = new ImageIcon(imageData);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);

        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh theo giới tính '" + gioiTinh + "': " + e.getMessage());
            return loadRandomImage(width, height);
        }
    }

    // Phương thức tải ảnh nền
    public static ImageIcon loadBackgroundImage(int width, int height) {
        try {
            InputStream inputStream = TaiAnhGamePlay.class.getResourceAsStream(BACKGROUND_IMAGE);

            if (inputStream == null) {
                // Thử lại nếu không tìm thấy
                inputStream = TaiAnhGamePlay.class.getResourceAsStream(BACKGROUND_IMAGE.startsWith("/") ? BACKGROUND_IMAGE : "/" + BACKGROUND_IMAGE);
            }

            if (inputStream == null) {
                throw new RuntimeException("Không tìm thấy ảnh nền: " + BACKGROUND_IMAGE);
            }

            byte[] imageData = inputStream.readAllBytes();
            ImageIcon originalIcon = new ImageIcon(imageData);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);

        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh nền: " + e.getMessage());
            return createDefaultImage(width, height, "Nền");
        }
    }

    // Tạo ảnh mặc định khi không tải được ảnh
    private static ImageIcon createDefaultImage(int width, int height, String label) {
        Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // Tạo gradient background
        GradientPaint gradient = new GradientPaint(0, 0, Color.LIGHT_GRAY, width, height, Color.DARK_GRAY);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Vẽ chữ thông báo
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String message = "Không tải được " + label;
        FontMetrics metrics = g2d.getFontMetrics();
        int stringWidth = metrics.stringWidth(message);
        g2d.drawString(message, (width - stringWidth) / 2, height / 2);

        g2d.dispose();
        return new ImageIcon(image);
    }

    // Phương thức lấy số lượng ảnh hiện có (không tính ảnh nền)
    public static int getAvailableImageCount() {
        return imagePaths.size() - 1;
    }

    // Phương thức lấy danh sách ảnh theo giới tính
    public static List<String> getImagesByGender(String gioiTinh) {
        int genderCode = "Nữ".equalsIgnoreCase(gioiTinh) ? 0 : 1;
        List<String> result = new ArrayList<>();

        for (int i = 1; i < imagePaths.size(); i++) {
            String path = imagePaths.get(i);
            if (isImageForGender(path, genderCode)) {
                result.add(path);
            }
        }
        return result;
    }

    // Kiểm tra xem ảnh có phù hợp với giới tính không
    private static boolean isImageForGender(String imagePath, int genderCode) {
        try {
            String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String[] parts = fileName.split("_|\\.");

            if (parts.length >= 2) {
                int imageGender = Integer.parseInt(parts[1]);
                return imageGender == genderCode;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi phân tích tên file: " + imagePath + " - " + e.getMessage());
        }
        return false;
    }

    // Phương thức lấy số lượng ảnh theo giới tính
    public static int getImageCountByGender(String gioiTinh) {
        return getImagesByGender(gioiTinh).size();
    }

    private static Image scaleImagePreserveRatio(Image image, int targetWidth, int targetHeight) {
        int originalWidth = image.getWidth(null);
        int originalHeight = image.getHeight(null);

        // Tính toán kích thước mới giữ tỉ lệ
        double widthRatio = (double) targetWidth / originalWidth;
        double heightRatio = (double) targetHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        // Tạo ảnh mới với nền trong suốt
        BufferedImage newImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();

        // Thiết lập chất lượng rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền trong suốt
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, targetWidth, targetHeight);
        g2d.setComposite(AlphaComposite.SrcOver);

        // Tính vị trí để căn giữa
        int x = (targetWidth - newWidth) / 2;
        int y = (targetHeight - newHeight) / 2;

        // Vẽ ảnh đã scale
        g2d.drawImage(image, x, y, newWidth, newHeight, null);
        g2d.dispose();

        return newImage;
    }
    
}

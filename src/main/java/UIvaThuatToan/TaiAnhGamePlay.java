    package UIvaThuatToan;

    import javax.swing.*;
    import java.awt.*;
    import java.io.InputStream;
    import java.util.Random;

    public class TaiAnhGamePlay {

        private static final String[] IMAGE_PATHS = {
            "/img/1_0.jpg",
            "/img/2_0.jpg",
            "/img/3_0.jpg"
        };

        public static ImageIcon loadRandomImage(int width, int height) {
            try {
                // 1. Chọn đường dẫn ngẫu nhiên
                String imagePath = IMAGE_PATHS[new Random().nextInt(IMAGE_PATHS.length)];

                // 2. Sử dụng ClassLoader và đường dẫn ngẫu nhiên
                // Loại bỏ dấu '/' ở đầu để sử dụng với getClassLoader()
                InputStream inputStream = TaiAnhGamePlay.class.getClassLoader()
                        .getResourceAsStream(imagePath.substring(1));

                if (inputStream == null) {
                    // ... (phần code xử lý lỗi giữ nguyên để debug)
                    throw new RuntimeException("Không tìm thấy ảnh: " + imagePath
                            + "\nHãy kiểm tra:\n"
                            + "1. File phải nằm tại: src/main/resources/img/1_0.jpg\n"
                            + "2. Đã clean & rebuild project\n"
                            + "3. Trong target/classes/img có file ảnh không?");
                }

                byte[] imageData = inputStream.readAllBytes();
                return new ImageIcon(new ImageIcon(imageData).getImage()
                        .getScaledInstance(width, height, Image.SCALE_SMOOTH));

            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải ảnh", e);
            }
        }
    }

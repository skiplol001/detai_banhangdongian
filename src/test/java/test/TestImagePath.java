package test;

import java.net.URL;

public class TestImagePath {
    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA ĐƯỜNG DẪN ẢNH ===\n");
        
        String[] imageNames = {"btnKHTN", "btnTTKH"};
        
        for (String imageName : imageNames) {
            System.out.println("Kiểm tra hình: " + imageName + ".png");
            
            // Test nhiều đường dẫn khác nhau
            testPath("/resources-button/" + imageName + ".png", "Đường dẫn 1 (/resources-button/)");
            testPath("/" + imageName + ".png", "Đường dẫn 2 (/)");
            testPath("resources-button/" + imageName + ".png", "Đường dẫn 3 (resources-button/)");
            testPath("images/" + imageName + ".png", "Đường dẫn 4 (images/)");
            testPath("buttons/" + imageName + ".png", "Đường dẫn 5 (buttons/)");
            testPath("assets/" + imageName + ".png", "Đường dẫn 6 (assets/)");
            
            System.out.println("-----------------------------------");
        }
        
        // Kiểm tra thư mục gốc
        System.out.println("\n=== KIỂM TRA THƯ MỤC GỐC ===");
        URL rootURL = TestImagePath.class.getResource("/");
        if (rootURL != null) {
            System.out.println("Thư mục gốc: " + rootURL);
        }
        
        // Kiểm tra xem có thư mục resources-button không
        URL resourcesButtonURL = TestImagePath.class.getResource("/resources-button/");
        if (resourcesButtonURL != null) {
            System.out.println("✓ Tìm thấy thư mục /resources-button/");
        } else {
            System.out.println("✗ Không tìm thấy thư mục /resources-button/");
        }
    }
    
    private static void testPath(String path, String description) {
        URL imgURL = TestImagePath.class.getResource(path);
        if (imgURL != null) {
            System.out.println("✓ " + description + ": " + path);
            System.out.println("  → " + imgURL);
        } else {
            System.out.println("✗ " + description + ": " + path);
        }
    }
}
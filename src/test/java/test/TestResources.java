package test;

import java.io.File;
import java.net.URL;

public class TestResources {

    public static void main(String[] args) {
        System.out.println("Kiểm tra ảnh:");
        for (String path : new String[]{"/img/1_0.jpg", "/img/2_0.jpg", "/img/3_0.jpg"}) {
            URL url = TestResources.class.getResource(path);
            System.out.println(path + " → " + (url != null ? "OK" : "MISSING"));
        }

        System.out.println("Đường dẫn thực tế: "
                + new File("src/main/resources/img/1_0.jpg").getAbsolutePath());
        System.out.println("Tồn tại: "
                + new File("src/main/resources/img/1_0.jpg").exists());
    }
}

package UIvaThuatToan;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class QuanLyKhachHang {

    private static final String FILE_KHACH_HANG = "database" + File.separator + "danh_sach_khach_hang.txt";
    private static final List<KhachHang> DANH_SACH_KHACH_HANG_GOC = Arrays.asList(
            new KhachHang("Liễu Như Yên", 25, "Nữ", "KH001", false),
            new KhachHang("Tạ Minh Kha", 30, "Nam", "KH002", false),
            new KhachHang("Tiểu Lạc", 18, "Nữ", "KH003", false),
            new KhachHang("Shyn Mụi Mụi", 20, "Nữ", "KH004", false),
            new KhachHang("Tăng Quốc Cường", 28, "Nam", "KH005", false)
    );

    public static List<KhachHang> layDanhSachKhachHangHomNay() {
        List<KhachHang> danhSachKhachHangHomNay = new ArrayList<>();
        Random random = new Random();

        for (KhachHang baseCustomer : DANH_SACH_KHACH_HANG_GOC) {
            boolean laVong = random.nextDouble() < 0.3; // 30% là vong

            if (random.nextDouble() < 0.7) {
                // 70% giữ nguyên thông tin (có thể là vong hoặc không)
                danhSachKhachHangHomNay.add(new KhachHang(
                        baseCustomer.getTen(),
                        baseCustomer.getTuoi(),
                        baseCustomer.getGioiTinh(),
                        baseCustomer.getMaKH(),
                        laVong
                ));
            } else {
                // 30% thay đổi thông tin
                int tuoiMoi = baseCustomer.getTuoi() + random.nextInt(10) - 5;
                String maMoi = "MOD" + baseCustomer.getMaKH().substring(2);

                // Nếu là vong thì thêm dấu hiệu nhận biết
                if (laVong) {
                    maMoi = "V" + maMoi;
                    tuoiMoi += random.nextInt(20) + 10; // Vong thường có tuổi cao hơn
                }

                danhSachKhachHangHomNay.add(new KhachHang(
                        baseCustomer.getTen(),
                        tuoiMoi,
                        baseCustomer.getGioiTinh(),
                        maMoi,
                        laVong
                ));
            }
        }

        luuDanhSachKhachHang(danhSachKhachHangHomNay);
        return danhSachKhachHangHomNay;
    }

    private static void luuDanhSachKhachHang(List<KhachHang> danhSachKhachHang) {
        try {
            // Đảm bảo thư mục tồn tại
            Files.createDirectories(Paths.get("database"));

            StringBuilder noiDung = new StringBuilder();
            for (KhachHang khachHang : danhSachKhachHang) {
                noiDung.append(khachHang.getTen()).append(",")
                        .append(khachHang.getTuoi()).append(",")
                        .append(khachHang.getGioiTinh()).append(",")
                        .append(khachHang.getMaKH()).append(",")
                        .append(khachHang.isLaVong()).append("\n");
            }

            Files.write(Paths.get(FILE_KHACH_HANG),
                    noiDung.toString().getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu danh sách khách hàng: " + e.getMessage());
        }
    }

    public static List<KhachHang> taiDanhSachKhachHang() {
        List<KhachHang> danhSachKhachHang = new ArrayList<>();
        Path filePath = Paths.get(FILE_KHACH_HANG);

        try {
            // Kiểm tra file tồn tại và có thể đọc
            if (Files.exists(filePath))  {
                System.out.println("Đang đọc từ file: " + filePath.toAbsolutePath());

                List<String> cacDong = Files.readAllLines(filePath);
                if (cacDong.isEmpty()) {
                    System.out.println("File tồn tại nhưng trống, tạo dữ liệu mới");
                    return layDanhSachKhachHangHomNay();
                }

                for (String dong : cacDong) {
                    String[] cacPhan = dong.split(",");
                    if (cacPhan.length >= 4) {
                        boolean laVong = cacPhan.length > 4 && Boolean.parseBoolean(cacPhan[4]);
                        danhSachKhachHang.add(new KhachHang(
                                cacPhan[0],
                                Integer.parseInt(cacPhan[1]),
                                cacPhan[2],
                                cacPhan[3],
                                laVong
                        ));
                    }
                }
            } else {
                System.out.println("File không tồn tại, tạo dữ liệu mới");
                return layDanhSachKhachHangHomNay();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc danh sách khách hàng: " + e.getMessage());
            System.err.println("Tạo dữ liệu mới do có lỗi");
            return layDanhSachKhachHangHomNay();
        }

        return danhSachKhachHang;
    }
}
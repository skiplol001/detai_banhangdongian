package model;

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

            if (laVong) {
                // Nếu là vong: tuổi từ 100 trở lên và mã KH được random
                int tuoiVong = 100 + random.nextInt(100); // Tuổi từ 100-199
                String maVong = "V" + (1000 + random.nextInt(9000)); // Mã dạng V1000-V9999
                
                danhSachKhachHangHomNay.add(new KhachHang(
                        baseCustomer.getTen(),
                        tuoiVong,
                        baseCustomer.getGioiTinh(),
                        maVong,
                        true
                ));
            } else {
                // Nếu không phải vong: giữ nguyên thông tin gốc
                danhSachKhachHangHomNay.add(new KhachHang(
                        baseCustomer.getTen(),
                        baseCustomer.getTuoi(),
                        baseCustomer.getGioiTinh(),
                        baseCustomer.getMaKH(),
                        false
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
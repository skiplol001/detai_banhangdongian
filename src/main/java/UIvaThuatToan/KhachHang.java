package UIvaThuatToan;

public class KhachHang {

    private String ten;
    private int tuoi;
    private String gioiTinh;
    private String maKH;
    private boolean laVong; // true nếu là vong, false nếu là người thường

    public KhachHang(String ten, int tuoi, String gioiTinh, String maKH, boolean laVong) {
        this.ten = ten;
        this.tuoi = tuoi;
        this.gioiTinh = gioiTinh;
        this.maKH = maKH;
        this.laVong = laVong; // Đã sửa từ isVong thành laVong
    }

    // Getter methods
    public String getTen() {
        return ten;
    }

    public int getTuoi() {
        return tuoi;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public String getMaKH() {
        return maKH;
    }

    public boolean isLaVong() {
        return laVong;
    }

    @Override
    public String toString() {
        return "Tên: " + ten + ", Tuổi: " + tuoi + ", Giới tính: " + gioiTinh
                + ", Mã KH: " + maKH + ", Loại: " + (laVong ? "Vong" : "Người thường");
    }

    public String layThongTin() {
        return "Tên: " + ten
                + "\nTuổi: " + tuoi
                + "\nGiới tính: " + gioiTinh
                + "\nMã khách hàng: " + maKH
                + "\nLoại: " + (laVong ? "Vong" : "Người thường");

    }
}

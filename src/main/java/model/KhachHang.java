package model;

import java.util.HashMap;

public class KhachHang {

    private String ten;
    private int tuoi;
    private String gioiTinh;
    private String maKH;
    private boolean laVong;
    private HashMap<String, Integer> vatPhamYeuCau; // Thêm field này

    public KhachHang(String ten, int tuoi, String gioiTinh, String maKH, boolean laVong) {
        this.ten = ten;
        this.tuoi = tuoi;
        this.gioiTinh = gioiTinh;
        this.maKH = maKH;
        this.laVong = laVong;
        this.vatPhamYeuCau = new HashMap<>(); // Khởi tạo
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

    // Thêm các phương thức mới cho vatPhamYeuCau
    public HashMap<String, Integer> getVatPhamYeuCau() {
        return vatPhamYeuCau;
    }

    public void setVatPhamYeuCau(HashMap<String, Integer> vatPhamYeuCau) {
        this.vatPhamYeuCau = vatPhamYeuCau;
    }

    public void themVatPhamYeuCau(String tenVatPham, int soLuong) {
        this.vatPhamYeuCau.put(tenVatPham, soLuong);
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
                + "\nMã khách hàng: " + maKH;
    }
}

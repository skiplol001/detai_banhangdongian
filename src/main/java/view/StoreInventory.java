package model;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class StoreInventory {

    private Map<String, Integer> soLuongHangHoa;
    private List<String> dsVatPham;
    private Map<String, Integer> giaVatPham;

    public StoreInventory(List<String> dsVatPham, Map<String, Integer> giaVatPham) {
        this.dsVatPham = dsVatPham;
        this.giaVatPham = giaVatPham;
        this.soLuongHangHoa = new HashMap<>();

        // Khởi tạo số lượng mặc định
        for (String item : dsVatPham) {
            soLuongHangHoa.put(item, 10); // Số lượng mặc định là 10
        }
    }

    public Map<String, Integer> getSoLuongHangHoa() {
        return new HashMap<>(soLuongHangHoa);
    }

    public void nhapHang(String itemName, int quantity) {
        if (soLuongHangHoa.containsKey(itemName)) {
            int currentQty = soLuongHangHoa.get(itemName);
            soLuongHangHoa.put(itemName, currentQty + quantity);
        }
    }

    public void traHang(String itemName, int quantity) {
        if (soLuongHangHoa.containsKey(itemName)) {
            int currentQty = soLuongHangHoa.get(itemName);
            int newQty = Math.max(0, currentQty - quantity); // Đảm bảo không âm
            soLuongHangHoa.put(itemName, newQty);
        }
    }

    public boolean banHang(String itemName, int quantity) {
        if (soLuongHangHoa.containsKey(itemName) && soLuongHangHoa.get(itemName) >= quantity) {
            int currentQty = soLuongHangHoa.get(itemName);
            soLuongHangHoa.put(itemName, currentQty - quantity);
            return true;
        }
        return false;
    }

    public int getSoLuong(String itemName) {
        return soLuongHangHoa.getOrDefault(itemName, 0);
    }
}

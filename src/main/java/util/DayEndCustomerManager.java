package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import view.UiKhachHang;

public class DayEndCustomerManager {

    private static final String CUSTOMER_FILE_PATH = "database/danh_khach_hang_that.txt";
    private static final String DAY_COUNT_FILE_PATH = "database/dialog/count.txt";
    private static final int TARGET_CUSTOMER_COUNT = 10;
    private static UiKhachHang uiKhachHang; // Thêm tham chiếu

    // Thêm phương thức để thiết lập UI
    public static void setUiKhachHang(UiKhachHang ui) {
        uiKhachHang = ui;
    }

    public static void processEndOfDay() {
        try {
            int currentDay = readCurrentDay();

            if (currentDay == 2 || currentDay == 3 || currentDay == 5) {
                // Không cần reload UI ở đây vì sẽ reload sau khi write
                List<String> customers = readCustomers(false);

                if (shouldMigrationOccur()) {
                    List<String> remainingCustomers = removeOneRandomCustomer(customers);
                    List<String> newCustomers = addOneTouristCustomer(remainingCustomers);
                    writeCustomers(newCustomers);
                    // Reload UI sau khi thay đổi
                    reloadUI();
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi xử lý khách hàng cuối ngày: " + e.getMessage());
        }
    }

    private static int readCurrentDay() throws IOException {
        String dayCountPath = Paths.get(DAY_COUNT_FILE_PATH).toString();
        try (BufferedReader reader = new BufferedReader(new FileReader(dayCountPath))) {
            String line = reader.readLine();
            return line != null ? Integer.parseInt(line.trim()) : 1;
        }
    }

    private static List<String> readCustomers(boolean shouldReloadUI) throws IOException {
        List<String> customers = new ArrayList<>();
        String customerPath = Paths.get(CUSTOMER_FILE_PATH).toString();

        try (BufferedReader reader = new BufferedReader(new FileReader(customerPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    customers.add(line.trim());
                }
            }
        }

        // Chỉ reload UI khi được yêu cầu
        if (shouldReloadUI && uiKhachHang != null) {
            SwingUtilities.invokeLater(() -> {
                uiKhachHang.reloadKhachHang();
            });
        }

        return customers;
    }

    private static boolean shouldMigrationOccur() {
        Random random = new Random();
        return random.nextDouble() <= 0.7;
    }

    private static List<String> removeOneRandomCustomer(List<String> customers) {
        if (customers.isEmpty()) {
            return customers;
        }

        Random random = new Random();
        List<String> remaining = new ArrayList<>(customers);
        int indexToRemove = random.nextInt(remaining.size());
        remaining.remove(indexToRemove);

        return remaining;
    }

    private static List<String> addOneTouristCustomer(List<String> existingCustomers) {
        List<String> newCustomers = new ArrayList<>(existingCustomers);

        // Tạo hộp thoại nhập thông tin
        JTextField nameField = new JTextField();
        JCheckBox maleCheckBox = new JCheckBox("Nam");
        JCheckBox femaleCheckBox = new JCheckBox("Nữ");

        // Thiết lập sự kiện cho checkbox
        maleCheckBox.addActionListener(e -> {
            if (maleCheckBox.isSelected()) {
                femaleCheckBox.setSelected(false);
            }
        });

        femaleCheckBox.addActionListener(e -> {
            if (femaleCheckBox.isSelected()) {
                maleCheckBox.setSelected(false);
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên khách du lịch:"));
        panel.add(nameField);
        panel.add(maleCheckBox);
        panel.add(femaleCheckBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Thêm khách du lịch mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        String name;
        String gender;
        int age = 18 + new Random().nextInt(13); // 18-30 tuổi

        if (result == JOptionPane.OK_OPTION) {
            name = nameField.getText().trim();
            if (name.isEmpty()) {
                name = "Khách Du Lịch";
            }

            if (maleCheckBox.isSelected()) {
                gender = "Nam";
            } else if (femaleCheckBox.isSelected()) {
                gender = "Nữ";
            } else {
                gender = new Random().nextBoolean() ? "Nam" : "Nữ";
            }
        } else {
            // Nếu người chơi cancel, tạo random
            String[] touristNames = {"Du Khách Phương Nam", "Lữ Khách Phương Bắc", "Khách Tây"};
            name = touristNames[new Random().nextInt(touristNames.length)];
            gender = new Random().nextBoolean() ? "Nam" : "Nữ";
        }

        int maxCode = findMaxCustomerCode(existingCustomers);
        String newCustomer = String.format("%s,%d,%s,KH%03d", name, age, gender, maxCode + 1);
        newCustomers.add(newCustomer);

        return newCustomers;
    }

    private static int findMaxCustomerCode(List<String> customers) {
        int maxCode = 0;

        for (String customer : customers) {
            try {
                String[] parts = customer.split(",");
                if (parts.length >= 4) {
                    String code = parts[3].trim();
                    if (code.startsWith("KH")) {
                        // Xử lý cả KH06 và KH006
                        String numberPart = code.substring(2);
                        int customerCode = Integer.parseInt(numberPart);
                        if (customerCode > maxCode) {
                            maxCode = customerCode;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // Bỏ qua nếu không parse được số
            }
        }

        return maxCode;
    }

    private static void writeCustomers(List<String> customers) throws IOException {
        String customerPath = Paths.get(CUSTOMER_FILE_PATH).toString();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerPath))) {
            for (String customer : customers) {
                writer.write(customer);
                writer.newLine();
            }
        }
    }

    public static void ensureCustomerCount() {
        try {
            // Không cần reload UI ở đây
            List<String> customers = readCustomers(false);

            if (customers.size() < TARGET_CUSTOMER_COUNT) {
                int missingCount = TARGET_CUSTOMER_COUNT - customers.size();
                int maxCode = findMaxCustomerCode(customers);

                for (int i = 0; i < missingCount; i++) {
                    String[] touristNames = {"Du Khách Phương Nam", "Lữ Khách Phương Bắc", "Khách Tây"};
                    String name = touristNames[new Random().nextInt(touristNames.length)];
                    int age = 18 + new Random().nextInt(13);
                    String gender = new Random().nextBoolean() ? "Nam" : "Nữ";
                    String newCustomer = String.format("%s,%d,%s,KH%03d", name, age, gender, maxCode + i + 1);
                    customers.add(newCustomer);
                }

                writeCustomers(customers);
                // Reload UI sau khi thay đổi
                reloadUI();
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi đảm bảo số lượng khách hàng: " + e.getMessage());
        }
    }

    private static void reloadUI() {
        if (uiKhachHang != null) {
            SwingUtilities.invokeLater(() -> {
                uiKhachHang.reloadKhachHang();
            });
        }
    }
}

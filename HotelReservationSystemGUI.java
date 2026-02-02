import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class HotelReservationSystemGUI extends JFrame {

    JTextField customerField, nightsField;
    JComboBox<String> roomTypeBox;
    JTable table;
    DefaultTableModel model;

    public HotelReservationSystemGUI() {

        setTitle("Hotel Booking Management System");
        setSize(880, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Hotel Booking Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(85, 57, 130)); 
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(230, 230, 250)); 
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 210)),
                " Booking Details ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel customerLabel = new JLabel("Customer Name:");
        JLabel roomLabel = new JLabel("Room Type:");
        JLabel nightsLabel = new JLabel("No. of Nights:");

        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        nightsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        customerField = new JTextField(20);
        nightsField = new JTextField(10);
        customerField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        nightsField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        roomTypeBox = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});
        roomTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton bookBtn = new JButton("Book Room");
        JButton cancelBtn = new JButton("Cancel Booking");

        styleButton(bookBtn, new Color(150, 123, 182)); 
        styleButton(cancelBtn, new Color(200, 160, 220)); 

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(customerLabel, gbc);
        gbc.gridx = 1; formPanel.add(customerField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(roomLabel, gbc);
        gbc.gridx = 1; formPanel.add(roomTypeBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(nightsLabel, gbc);
        gbc.gridx = 1; formPanel.add(nightsField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(bookBtn, gbc);
        gbc.gridx = 1; formPanel.add(cancelBtn, gbc);

        model = new DefaultTableModel(
                new String[]{"Customer", "Room", "Nights", "Amount", "Status"}, 0
        );

        table = new JTable(model) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 240, 255) : new Color(230, 225, 250)); // purple shades
                } else {
                    c.setBackground(new Color(200, 180, 220)); // selected row
                }
                return c;
            }
        };
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(138, 105, 180)); 
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane tableScroll = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);

        bookBtn.addActionListener(e -> bookRoom());
        cancelBtn.addActionListener(e -> cancelBooking());
    }

    void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    void bookRoom() {
        String customer = customerField.getText().trim();
        String room = (String) roomTypeBox.getSelectedItem();
        int nights;

        if (customer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name");
            return;
        }

        try {
            nights = Integer.parseInt(nightsField.getText());
            if (nights <= 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid number of nights");
            return;
        }

        int rate = getRoomRate(room);
        int amount = rate * nights;

        model.addRow(new Object[]{customer, room, nights, "₹" + amount, "Booked"});

        saveBookingToFile(customer, room, nights, amount, "Booked");

        customerField.setText("");
        nightsField.setText("");
    }

    int getRoomRate(String room) {
        switch (room) {
            case "Deluxe": return 3000;
            case "Suite": return 5000;
            default: return 2000;
        }
    }

    void cancelBooking() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to cancel");
            return;
        }
        model.setValueAt("Cancelled", row, 4);

        String customer = (String) model.getValueAt(row, 0);
        String room = (String) model.getValueAt(row, 1);
        int nights = (int) model.getValueAt(row, 2);
        int amount = getRoomRate(room) * nights;
        saveBookingToFile(customer, room, nights, amount, "Cancelled");
    }

    void saveBookingToFile(String customer, String room, int nights, int amount, String status) {
        try (FileWriter fw = new FileWriter("bookings.txt", true)) {
            fw.write(customer + "," + room + "," + nights + "," + amount + "," + status + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new HotelReservationSystemGUI().setVisible(true)
        );
    }
}







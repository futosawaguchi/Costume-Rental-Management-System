// ReturnProcedurePanel.java
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.Box;
import java.util.List;

/**
 * Panel for members to return rented costumes
 */
public class ReturnProcedurePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private int        memberId;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    public ReturnProcedurePanel(CardLayout cardLayout, JPanel cardPanel, int memberId) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        this.memberId   = memberId;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // --- Title ---
        JLabel title = new JLabel("Return Procedure", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(20));
        add(title);

        // --- Currently rented costumes table ---
        List<String[]> current = loadCurrentRentals();
        if (current.isEmpty()) {
            add(Box.createVerticalStrut(20));
            add(new JLabel("You have no active rentals.", SwingConstants.CENTER));
        } else {
            String[] cols = {"Costume ID", "Rental Date", "Due Date", "Size", "Color"};
            String[][] data = new String[current.size()][cols.length];
            for (int i = 0; i < current.size(); i++) {
                data[i] = current.get(i);
            }
            JTable table = new JTable(data, cols);
            add(Box.createVerticalStrut(10));
            add(new JScrollPane(table));
        }

        add(Box.createVerticalStrut(20));

        // --- Return form ---
        JPanel form = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        form.add(new JLabel("Costume ID:"));
        JTextField idField = new JTextField(5);
        form.add(idField);
        form.add(new JLabel("Size:"));
        JTextField sizeField = new JTextField(5);
        form.add(sizeField);
        add(form);

        add(Box.createVerticalStrut(10));

        // --- Buttons ---
        JPanel btnPane = new JPanel();
        JButton returnBtn = new JButton("Return");
        JButton topBtn    = new JButton("Top");
        btnPane.add(returnBtn);
        btnPane.add(topBtn);
        add(btnPane);

        // --- Actions ---
        returnBtn.addActionListener(e -> {
            String idTxt   = idField.getText().trim();
            String sizeTxt = sizeField.getText().trim();
            if (idTxt.isEmpty() || sizeTxt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Both Costume ID and Size are required.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int[] result = processReturn(idTxt, sizeTxt);
                int lateDays     = result[0];
                int totalLateFee = result[1];

                String msg;
                if (lateDays > 0) {
                    msg = String.format(
                        "Return completed successfully.\n" +
                        "You are %d day%s late, so your late fee is ¥%d.",
                        lateDays, (lateDays == 1 ? "" : "s"), totalLateFee
                    );
                } else {
                    msg = "Return completed successfully on time.";
                }

                JOptionPane.showMessageDialog(this,
                    msg,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                cardLayout.show(cardPanel, "MEMBER_MENU");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Return failed:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        topBtn.addActionListener(e ->
            cardLayout.show(cardPanel, "MEMBER_MENU")
        );        
    }

    /** Load all currently unreturned rentals for this member */
    private List<String[]> loadCurrentRentals() {
        List<String[]> result = new ArrayList<>();
        try {
            Path rentalsPath = Paths.get("rentals.csv");
            if (!Files.exists(rentalsPath)) return result;

            // preload costume info
            Map<String,String[]> costumes = new HashMap<>();
            Path costumesPath = Paths.get("costume.csv");
            if (Files.exists(costumesPath)) {
                for (String line : Files.readAllLines(costumesPath)) {
                    String[] c = line.split(",", -1);
                    // { id, size, color, price, lateFee, period, status }
                    costumes.put(c[0], c);
                }
            }

            for (String line : Files.readAllLines(rentalsPath)) {
                String[] r = line.split(",", -1);
                // { memberId, costumeId, rentalDate, dueDate,
                //   actualReturnDate, isLate, totalLateFee }
                if (Integer.parseInt(r[0]) == memberId
                 && (r[4].isEmpty() || r[4].equals("NULL"))) {
                    String cid = r[1];
                    String[] cinfo = costumes.getOrDefault(cid, new String[]{"", "", "", "", "", ""});
                    result.add(new String[]{ cid, r[2], r[3], cinfo[1], cinfo[2] });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int[] processReturn(String costumeId, String inputSize) throws Exception {
        LocalDate today = LocalDate.now();

        Path rentalsPath = Paths.get("rentals.csv");
        List<String> allRentals = Files.readAllLines(rentalsPath);
        List<String> updatedRentals = new ArrayList<>();
        boolean found = false;

        long lateDays = 0;
        int totalLateFee = 0;

        Map<String, String> costumeIdToSize = new HashMap<>();
        for (String line : Files.readAllLines(Paths.get("costume.csv"))) {
            String[] c = line.split(",", -1);
            if (c.length >= 2) {
                costumeIdToSize.put(c[0], c[1]);
            }
        }

        for (String line : allRentals) {
            String[] r = line.split(",", -1);
            if (!found
            && Integer.parseInt(r[0]) == memberId
            && r[1].equals(costumeId)
            && (r[4].isEmpty() || r[4].equals("NULL"))
            ) {

                String actualSize = costumeIdToSize.getOrDefault(costumeId, "");
                if (!actualSize.equalsIgnoreCase(inputSize)) {
                    continue;
                }

                LocalDate dueDate = LocalDate.parse(r[3], DTF);
                lateDays = Math.max(0, ChronoUnit.DAYS.between(dueDate, today));
                boolean isLate = lateDays > 0;

                int feePerDay = 0;
                for (String cl : Files.readAllLines(Paths.get("costume.csv"))) {
                    String[] c = cl.split(",", -1);
                    if (c[0].equals(costumeId) && c[1].equalsIgnoreCase(inputSize)) {
                        feePerDay = Integer.parseInt(c[4]);
                        break;
                    }
                }
                totalLateFee = (int)(lateDays * feePerDay);

                r[4] = today.format(DTF);               // actualReturnDate
                r[5] = Boolean.toString(isLate);        // isLate
                r[6] = Integer.toString(totalLateFee);  // totalLateFee

                found = true;
            }
            updatedRentals.add(String.join(",", r));
        }

        if (!found) {
            throw new Exception("No matching rental record found for the given ID and size.");
        }


    
        Files.write(rentalsPath, updatedRentals,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Path costumesPath = Paths.get("costume.csv");
        List<String> allCostumes = Files.readAllLines(costumesPath);
        List<String> updatedCostumes = new ArrayList<>();

        for (String line : allCostumes) {
            String[] c = line.split(",", -1);
            if (c[0].equals(costumeId) && c[1].equals(inputSize)) {//size
                c[6] = "Available";
            }
            updatedCostumes.add(String.join(",", c));
        }
        Files.write(costumesPath, updatedCostumes,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Path resPath = Paths.get("reservations.csv");
        if (Files.exists(resPath)) {
            List<String> allRes     = Files.readAllLines(resPath);
            List<String> updatedRes = new ArrayList<>();
            for (String rl : allRes) {
                String[] r = rl.split(",", -1);
                // r = { reservation_id, member_id, costume_id, desired_start,
                //       actual_rent_date, actual_return, status }
                if (r.length >= 7
                && Integer.parseInt(r[1]) == memberId
                && r[2].equals(costumeId)
                && "completed".equalsIgnoreCase(r[6])
                ) {
                    r[5] = today.format(DTF);
                }
                updatedRes.add(String.join(",", r));
            }
            Files.write(resPath,
                        updatedRes,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
        }

        return new int[]{ (int)lateDays, totalLateFee };
    }
}

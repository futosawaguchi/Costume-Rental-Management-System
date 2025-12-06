import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.Box;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private int loggedInEmployeeId = -1;
    private int loggedInMemberId   = -1;
    private ReservationPanel reservationPanel;
    private JTextField memberIdField;
    private JTextField memberNameField;   
    private JTextField employeeIdField;
    private JTextField employeeNameField;
    private JPanel reservationActionPanel;


    public MainFrame() {
        setTitle("Costumer Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel topPanel = createTopPanel();
        JPanel registerPanel = createRegisterPanel();
        JPanel loginPanel = createLoginPanel();
        JPanel employeeLoginPanel = createEmployeeLoginPanel();
        JPanel empRegPanel = createEmployeeRegistrationPanel();
        // CostumeSearchPanel searchPanel = new CostumeSearchPanel(cardLayout, cardPanel);

        // CostumeRegistrationPanel costumeReg =
        //     new CostumeRegistrationPanel(cardLayout, cardPanel);
        // CostumeViewPanel costumeView =
        //     new CostumeViewPanel(cardLayout, cardPanel);
        CostumeRegistrationPanel costumeRegEmp =
            new CostumeRegistrationPanel(cardLayout, cardPanel);
        CostumeViewPanel costumeViewEmp =
            new CostumeViewPanel(cardLayout, cardPanel, "EMPLOYEE_MENU");
        CostumeSearchPanel searchPanelEmp =
            new CostumeSearchPanel(cardLayout, cardPanel, "EMPLOYEE_MENU");

        CostumeViewPanel costumeViewMem =
            new CostumeViewPanel(cardLayout, cardPanel, "MEMBER_MENU");
        CostumeSearchPanel searchPanelMem =
            new CostumeSearchPanel(cardLayout, cardPanel, "MEMBER_MENU");
        ReturnProcedurePanel returnPanel =
            new ReturnProcedurePanel(cardLayout, cardPanel, loggedInMemberId);
        // ReturnProcedurePanel returnPanel =
        // new ReturnProcedurePanel(cardLayout, cardPanel, loggedInMemberId, "MEMBER_MENU");
        // cardPanel.add(returnPanel, "RETURN_PROCEDURE");        
        reservationPanel = new ReservationPanel(cardLayout, cardPanel, loggedInMemberId);
        // reservationActionPanel = new ReservationActionPanel(cardLayout, cardPanel, loggedInMemberId);

        cardPanel.add(topPanel, "TOP");
        cardPanel.add(registerPanel, "REGISTER");
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(employeeLoginPanel, "EMPLOYEE_LOGIN");
        cardPanel.add(empRegPanel, "EMPLOYEE_REGISTER");

        cardPanel.add(costumeRegEmp, "COSTUME_REGISTER");
        cardPanel.add(costumeViewEmp, "VIEW_COSTUMES_EMP");
        cardPanel.add(searchPanelEmp,  "COSTUME_SEARCH_EMP");
        cardPanel.add(costumeViewMem, "VIEW_COSTUMES_MEM");
        cardPanel.add(searchPanelMem,  "COSTUME_SEARCH_MEM");
        cardPanel.add(returnPanel, "RETURN_PROCEDURE");
        cardPanel.add(reservationPanel, "RESERVATION");
        // cardPanel.add(reservationActionPanel, "MAKE_RESERVATION");
        cardPanel.add(new ReservationListPanel(cardLayout, cardPanel), "RESERVATION_LIST");
        cardPanel.add(new RentalHistoryPanel(cardLayout, cardPanel), "RENTAL_HISTORY");
        cardPanel.add(new DeleteMenuPanel(cardLayout, cardPanel),   "DELETE_MENU");        
        cardPanel.add(new DeleteMenuPanel(   cardLayout, cardPanel), "DELETE_MENU");
        cardPanel.add(new DeleteCostumePanel(cardLayout, cardPanel), "DELETE_COSTUME");
        cardPanel.add(new DeleteMemberPanel( cardLayout, cardPanel), "DELETE_MEMBER");
        cardPanel.add(new DeleteEmployeePanel(cardLayout, cardPanel), "DELETE_EMPLOYEE");
        add(cardPanel);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Costumer Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // For Members Section
        JLabel membersLabel = new JLabel("— For Members —", SwingConstants.CENTER);
        membersLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        membersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerButton = new JButton("Membership Registration");
        JButton viewMembersButton = new JButton("View Member Information");
        JButton loginButton = new JButton("Membership Login");

        // For Employees Section
        JLabel employeesLabel = new JLabel("— For Employees —", SwingConstants.CENTER);
        employeesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        employeesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
        JButton employeeLoginButton = new JButton("Employee Login");
        JButton viewEmployeesButton = new JButton("View Employee Information");

        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewMembersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        employeeLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewEmployeesButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton.addActionListener(e -> cardLayout.show(cardPanel, "REGISTER"));
        viewMembersButton.addActionListener(e -> {
            JPanel viewPanel = createMemberInfoPanel();
            cardPanel.add(viewPanel, "MEMBER_INFO");
            cardLayout.show(cardPanel, "MEMBER_INFO");
        });
        loginButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        // employeeLoginButton and viewEmployeesButton are not implemented
        employeeLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "EMPLOYEE_LOGIN"));
        viewEmployeesButton.addActionListener(e -> {
            JPanel empInfoPanel = createEmployeeInfoPanel();
            cardPanel.add(empInfoPanel, "EMP_INFO");
            cardLayout.show(cardPanel, "EMP_INFO");
        });
        

        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(membersLabel);        
        panel.add(Box.createVerticalStrut(20));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewMembersButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(employeesLabel);        
        panel.add(Box.createVerticalStrut(10));
        panel.add(employeeLoginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewEmployeesButton);

        return panel;
    }


    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(1500, 25));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(1500, 25));

        JButton submitButton = new JButton("Register");
        JLabel resultLabel = new JLabel(" ");

        JButton topButton = new JButton("Top");

        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Both name and email are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int newId = getNextMemberId();
                String date = java.time.LocalDate.now().toString();
                String line = newId + "," + name + "," + email + "," + date;

                java.nio.file.Files.write(
                    java.nio.file.Paths.get("members.csv"),
                    (line + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
                );

                JPanel completePanel = createRegistrationCompletePanel(newId);
                String panelName = "COMPLETE_" + newId;
                cardPanel.add(completePanel, panelName);
                cardLayout.show(cardPanel, panelName);

                nameField.setText("");
                emailField.setText("");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Failed to register.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        topButton.addActionListener(e -> cardLayout.show(cardPanel, "TOP"));

        panel.add(Box.createVerticalStrut(20));
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(submitButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(resultLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(topButton);

        return panel;
    }

    private int getNextMemberId() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("members.csv");
            if (!java.nio.file.Files.exists(path)) return 1;

            long lines = java.nio.file.Files.lines(path).count();
            return (int) lines + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private JPanel createRegistrationCompletePanel(int memberId) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("Membership registration complete");
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("Your Member ID is: " + memberId);
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton topButton = new JButton("Top");
        topButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        topButton.addActionListener(e -> cardLayout.show(cardPanel, "TOP"));

        panel.add(Box.createVerticalStrut(30));
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(idLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(topButton);

        return panel;
    }

    private JPanel createMemberInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Member ID", "Name", "Email", "Registration Date"};
        java.util.List<String[]> rowList = new java.util.ArrayList<>();

        try {
            java.nio.file.Path path = java.nio.file.Paths.get("members.csv");
            if (java.nio.file.Files.exists(path)) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                for (String line : lines) {
                    String[] data = line.split(",", -1);
                    if (data.length == 4) rowList.add(data);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Failed to load member data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        String[][] dataArray = rowList.toArray(new String[0][]);
        JTable table = new JTable(dataArray, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton topButton = new JButton("Top");
        topButton.addActionListener(e -> cardLayout.show(cardPanel, "TOP"));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(topButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel idLabel = new JLabel("Member ID:");
        memberIdField = new JTextField(10);
        memberIdField.setMaximumSize(new Dimension(1500, 25));

        JLabel nameLabel = new JLabel("Name:");
        memberNameField = new JTextField(20);
        memberNameField.setMaximumSize(new Dimension(1500, 25));

        JButton loginButton = new JButton("Login");
        JButton topButton = new JButton("Top");
        JLabel resultLabel = new JLabel(" ");

        loginButton.addActionListener(e -> {
            String idText = memberIdField.getText().trim();
            String nameInput = memberNameField.getText().trim();

            if (idText.isEmpty() || nameInput.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Both Member ID and Name are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int memberId = Integer.parseInt(idText);
                java.nio.file.Path path = java.nio.file.Paths.get("members.csv");
                boolean found = false;

                if (java.nio.file.Files.exists(path)) {
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                    for (String line : lines) {
                        String[] data = line.split(",", -1);
                        if (data.length >= 2) {
                            int fileId = Integer.parseInt(data[0]);
                            String fileName = data[1];
                            if (fileId == memberId && fileName.equals(nameInput)) {
                                found = true;
                                break;
                            }
                        }
                    }
                }

                if (found) {
                    JPanel memberMenuPanel = createMemberMenuPanel();
                    cardPanel.add(memberMenuPanel, "MEMBER_MENU");
                    cardLayout.show(cardPanel, "MEMBER_MENU");
                    loggedInMemberId = memberId;
                    memberIdField.setText("");
                    memberNameField.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel, "Login failed. ID or name is incorrect.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Member ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "An error occurred during login.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        topButton.addActionListener(e -> {
            memberIdField.setText("");
            memberNameField.setText("");
            resultLabel.setText(" ");
            cardLayout.show(cardPanel, "TOP");
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(idLabel);
        panel.add(memberIdField);
        panel.add(nameLabel);
        panel.add(memberNameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(resultLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(topButton);

        return panel;
    }

    private JPanel createMemberMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Member Menu");
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton searchButton = new JButton("Search");
        JButton myRentalsButton = new JButton("My Rentals");
        JButton viewCostumesButton = new JButton("View rental costumes");
        JButton rentButton = new JButton("Rental procedure");
        JButton returnButton = new JButton("Return procedure");
        JButton reservationBtn = new JButton("Reservation");
        JButton logoutButton = new JButton("Logout");

        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        myRentalsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewCostumesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reservationBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);


        searchButton.addActionListener(e -> cardLayout.show(cardPanel, "COSTUME_SEARCH_MEM"));

        viewCostumesButton.addActionListener(e -> {
            CostumeViewPanel fresh =
                new CostumeViewPanel(cardLayout, cardPanel, "MEMBER_MENU");
            cardPanel.add(fresh, "VIEW_COSTUMES_MEM");
            cardLayout.show(cardPanel, "VIEW_COSTUMES_MEM");
        });
        myRentalsButton.addActionListener(e -> {
            MyRentalsPanel fresh = new MyRentalsPanel(cardLayout, cardPanel, loggedInMemberId);
            cardPanel.add(fresh, "MY_RENTALS");
            cardLayout.show(cardPanel, "MY_RENTALS");
        });

        rentButton.addActionListener(e -> {
            RentalProcedurePanel rentP =
                new RentalProcedurePanel(cardLayout,
                                        cardPanel,
                                        loggedInMemberId,
                                        "MEMBER_MENU"); 
            cardPanel.add(rentP, "RENTAL_PROCEDURE");
            cardLayout.show(cardPanel, "RENTAL_PROCEDURE");
        });
        returnButton.addActionListener(e -> {
            ReturnProcedurePanel returnP =
                new ReturnProcedurePanel(cardLayout, cardPanel, loggedInMemberId);
            cardPanel.add(returnP, "RETURN_PROCEDURE");
            cardLayout.show(cardPanel, "RETURN_PROCEDURE");
        });

        reservationBtn.addActionListener(e -> {
            ReservationPanel rp = new ReservationPanel(cardLayout, cardPanel, loggedInMemberId);
            cardPanel.add(rp, "RESERVATION");
            cardLayout.show(cardPanel, "RESERVATION");
        });
        logoutButton.addActionListener(e -> cardLayout.show(cardPanel, "TOP"));

        panel.add(Box.createVerticalStrut(20));
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        panel.add(searchButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(myRentalsButton);        
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewCostumesButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(rentButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(returnButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(reservationBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(logoutButton);

        return panel;
    }

    private JPanel createEmployeeLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel idLabel = new JLabel("Employee ID:");
        employeeIdField = new JTextField(10);
        employeeIdField.setMaximumSize(new Dimension(1500, 25));

        JLabel nameLabel = new JLabel("Name:");
        employeeNameField = new JTextField(20);
        employeeNameField.setMaximumSize(new Dimension(1500, 25));

        JButton loginButton = new JButton("Login");
        JButton topButton = new JButton("Top");

        topButton.addActionListener(e -> {
            employeeIdField.setText("");
            employeeNameField.setText("");
            cardLayout.show(cardPanel, "TOP");
        });

        loginButton.addActionListener(e -> {
            String idText = employeeIdField.getText().trim();
            String name = employeeNameField.getText().trim();

            if (idText.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Both Employee ID and Name are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int empId = Integer.parseInt(idText);
                boolean matched = false;

                java.nio.file.Path path = java.nio.file.Paths.get("employees.csv");
                if (java.nio.file.Files.exists(path)) {
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                    for (String line : lines) {
                        String[] parts = line.split(",", -1);
                        if (parts.length >= 2) {
                            int fileId = Integer.parseInt(parts[0].trim());
                            String fileName = parts[1].trim();
                            if (empId == fileId && name.equals(fileName)) {
                                matched = true;
                                loggedInEmployeeId = empId;
                                break;
                            }
                        }
                    }
                }

                if (matched) {
                    JPanel empMenu = createEmployeeMenuPanel();
                    cardPanel.add(empMenu, "EMPLOYEE_MENU");
                    cardLayout.show(cardPanel, "EMPLOYEE_MENU");

                    employeeIdField.setText("");
                    employeeNameField.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel, "Login failed. ID or name is incorrect.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Employee ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "An error occurred during login.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(idLabel);
        panel.add(employeeIdField);
        panel.add(nameLabel);
        panel.add(employeeNameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(topButton);

        return panel;
    }

    private JPanel createEmployeeMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Employee Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerCostumeButton = new JButton("Register costume information");
        JButton searchButton = new JButton("Search");
        JButton viewButton = new JButton("View rental costumes");
        JButton reservationListButton   = new JButton("Reservation List");
        JButton rentalHistoryButton     = new JButton("Rental History");        
        JButton registerEmployeeButton = new JButton("Employee registration");
        JButton deleteMenuButton        = new JButton("Delete"); 
        JButton logoutButton = new JButton("Logout");


        for (JButton b : Arrays.asList(
                registerCostumeButton, searchButton, viewButton,
                reservationListButton, rentalHistoryButton,
                registerEmployeeButton, deleteMenuButton, logoutButton)) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        logoutButton.addActionListener(e -> {
            loggedInEmployeeId = -1;
            cardLayout.show(cardPanel, "TOP");
        });
        registerEmployeeButton.addActionListener(e ->
            cardLayout.show(cardPanel, "EMPLOYEE_REGISTER")
        );
        registerCostumeButton.addActionListener(e ->
            cardLayout.show(cardPanel, "COSTUME_REGISTER")
        );

        viewButton.addActionListener(e -> {
            CostumeViewPanel freshEmp =
                new CostumeViewPanel(cardLayout, cardPanel, "EMPLOYEE_MENU");
            cardPanel.add(freshEmp, "VIEW_COSTUMES_EMP");
            cardLayout.show(cardPanel, "VIEW_COSTUMES_EMP");
        });        
        searchButton.addActionListener(e -> cardLayout.show(cardPanel, "COSTUME_SEARCH_EMP"));
        // viewButton.addActionListener(e -> cardLayout.show(cardPanel, "VIEW_COSTUMES_EMP"));
        reservationListButton.addActionListener(e -> {
            ReservationListPanel p = new ReservationListPanel(cardLayout, cardPanel);
            cardPanel.add(p, "RESERVATION_LIST");
            cardLayout.show(cardPanel, "RESERVATION_LIST");
        });
        rentalHistoryButton.addActionListener(e -> {
            RentalHistoryPanel p = new RentalHistoryPanel(cardLayout, cardPanel);
            cardPanel.add(p, "RENTAL_HISTORY");
            cardLayout.show(cardPanel, "RENTAL_HISTORY");
        });
        registerEmployeeButton.addActionListener(e ->
            cardLayout.show(cardPanel, "EMPLOYEE_REGISTER"));
        deleteMenuButton.addActionListener(e ->
            cardLayout.show(cardPanel, "DELETE_MENU"));        

        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(registerCostumeButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(searchButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(reservationListButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(rentalHistoryButton);        
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerEmployeeButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(deleteMenuButton);    
        panel.add(Box.createVerticalStrut(10));
        panel.add(logoutButton);

        return panel;
    }

    private JPanel createEmployeeRegistrationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(1500, 25));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(1500, 25));

        JButton registerButton = new JButton("Register");
        JButton topButton      = new JButton("Top");

        registerButton.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                    "Both name and email are required.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int newId = getNextEmployeeId();
                String date = java.time.LocalDate.now().toString();
                String line = newId + "," + name + "," + email + "," + date;

                java.nio.file.Files.write(
                    java.nio.file.Paths.get("employees.csv"),
                    (line + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
                );

                JOptionPane.showMessageDialog(panel,
                    "Registration complete!\nYour Employee ID is: " + newId,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                nameField.setText("");
                emailField.setText("");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel,
                    "Failed to register employee.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        topButton.addActionListener(e -> cardLayout.show(cardPanel, "EMPLOYEE_MENU"));

        panel.add(Box.createVerticalStrut(20));
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(topButton);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private int getNextEmployeeId() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("employees.csv");
            if (!java.nio.file.Files.exists(path)) {
                return 1;
            }
            long lines = java.nio.file.Files.lines(path).count();
            return (int) lines + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
    private JPanel createEmployeeInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Employee ID", "Name", "Email", "Registration Date"};
        java.util.List<String[]> rowList = new java.util.ArrayList<>();

        try {
            java.nio.file.Path path = java.nio.file.Paths.get("employees.csv");
            if (java.nio.file.Files.exists(path)) {
                for (String line : java.nio.file.Files.readAllLines(path)) {
                    String[] parts = line.split(",", -1);
                    if (parts.length == 4) {
                        rowList.add(parts);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel,
                "Failed to load employee data.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        String[][] dataArray = rowList.toArray(new String[0][]);
        JTable table = new JTable(dataArray, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton topButton = new JButton("Top");
        topButton.addActionListener(e -> cardLayout.show(cardPanel, "TOP"));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(topButton, BorderLayout.SOUTH);

        return panel;
    }
}
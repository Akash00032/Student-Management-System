package SMS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentManagementSystem {
    private JFrame mainFrame;
    private JPanel panelTop, panelButtons, panelFooter, panelTable, panelBottom;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private Font headerFont, buttonFont;


    public StudentManagementSystem() {
        // Set up main frame
        mainFrame = new JFrame("Student Management System");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(900, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        // Set custom fonts
        headerFont = new Font("Arial", Font.BOLD, 30);
        buttonFont = new Font("Arial", Font.PLAIN, 14);

        // Create panels
        panelTop = new JPanel();
        panelTop.setBackground(new Color(51, 102, 255)); // Blue background
        panelTop.setPreferredSize(new Dimension(800, 50));

        panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center buttons
        panelButtons.setBackground(new Color(245, 245, 245));
        panelButtons.setPreferredSize(new Dimension(800, 80));

        panelFooter = new JPanel();
        panelFooter.setBackground(new Color(51, 51, 62)); // Dark background
        panelFooter.setPreferredSize(new Dimension(800, 40));

        // Header Label
        JLabel headerLabel = new JLabel("Student Management System");
        headerLabel.setFont(headerFont);
        headerLabel.setForeground(Color.WHITE);
        panelTop.add(headerLabel);

        // Table Panel
        panelTable = new JPanel(new BorderLayout());
        panelTable.setPreferredSize(new Dimension(800, 400));
        panelTable.setBackground(Color.WHITE);

        // Footer
        JLabel footerLabel = new JLabel("© 2024 Student Management System | Version 1.0");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panelFooter.add(footerLabel);

        // Table setup
        String[] columnNames = {"Student ID", "First Name", "Last Name", "Gender", "Email", "Phone", "Course"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        studentTable.setFillsViewportHeight(true);
        studentTable.setRowHeight(25);

        // Add a scroll pane to make the table scrollable with a border and title
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Student Records"));

        // Add the scroll pane to the panel
        panelTable.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        btnAdd = createButton("Add Student", new Color(50, 205, 50)); // Green
        btnUpdate = createButton("Update Student", new Color(255, 165, 0)); // Orange
        btnDelete = createButton("Delete Student", new Color(255, 69, 0)); // Red
        btnSearch = createButton("Search Student", new Color(70, 130, 180)); // Blue

        JButton btnRefresh = createButton("Refresh", new Color(0, 123, 255)); // Blue

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnSearch);
        panelButtons.add(btnRefresh);


        // Combine panelButtons and panelFooter into panelBottom
        panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(panelButtons, BorderLayout.CENTER);
        panelBottom.add(panelFooter, BorderLayout.SOUTH);

        // Add panels to frame
        mainFrame.add(panelTop, BorderLayout.NORTH); // Top section
        mainFrame.add(panelTable, BorderLayout.CENTER); // Table section
        mainFrame.add(panelBottom, BorderLayout.SOUTH); // Bottom section with buttons and footer

        // Event listeners
        btnAdd.addActionListener(e -> showAddDialog());
        btnUpdate.addActionListener(e -> showUpdateDialog());
        btnDelete.addActionListener(e -> deleteStudent());
        btnSearch.addActionListener(e -> showSearchDialog());
        btnRefresh.addActionListener(e -> refreshTable());


        // Load student data into table
        loadStudentData();

        // Make frame visible
        mainFrame.setVisible(true);
    }
    
    // Create buttons with hover effects
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // Load student data from database into the table
    private void loadStudentData() {
        tableModel.setRowCount(0); // Clear existing rows
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM students";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    Object[] row = {
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("gender"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("course")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error loading student data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Refresh the table by reloading data from the database
    private void refreshTable() {
        loadStudentData();
    }

    // Delete selected student
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a student to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentID = (int) tableModel.getValueAt(selectedRow, 0); // Get the selected student's ID

        int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to delete this student?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM students WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, studentID);
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(mainFrame, "Student deleted successfully!");
                        refreshTable(); // Refresh table after deletion
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Error: Student ID not found.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error while deleting student: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Show dialog to update student details
    private void showUpdateDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a student to update.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Fetch selected student data
        int studentID = (int) tableModel.getValueAt(selectedRow, 0);
        String firstName = (String) tableModel.getValueAt(selectedRow, 1);
        String lastName = (String) tableModel.getValueAt(selectedRow, 2);
        String gender = (String) tableModel.getValueAt(selectedRow, 3);
        String email = (String) tableModel.getValueAt(selectedRow, 4);
        String phone = (String) tableModel.getValueAt(selectedRow, 5);
        String course = (String) tableModel.getValueAt(selectedRow, 6);

        JDialog updateDialog = new JDialog(mainFrame, "Update Student", true);
        updateDialog.setSize(350, 350);
        updateDialog.setLocationRelativeTo(mainFrame);

        JPanel updatePanel = new JPanel(new GridLayout(7, 2, 10, 10));
        JTextField tfFirstName = new JTextField(firstName);
        JTextField tfLastName = new JTextField(lastName);
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Male", "Female", "Transgender", "Others"});
        cbGender.setSelectedItem(gender);
        JTextField tfEmail = new JTextField(email);
        JTextField tfPhone = new JTextField(phone);
        JComboBox<String> cbCourse = new JComboBox<>(new String[]{
                "BCA", "MCA", "B.Tech", "M.Tech", "B.Com", "M.Com",
                "BBA", "MBA", "BA", "MA", "B.Sc", "M.Sc", "PhD", "Diploma"
        });
        cbCourse.setSelectedItem(course);

        updatePanel.add(new JLabel("First Name:"));
        updatePanel.add(tfFirstName);
        updatePanel.add(new JLabel("Last Name:"));
        updatePanel.add(tfLastName);
        updatePanel.add(new JLabel("Gender:"));
        updatePanel.add(cbGender);
        updatePanel.add(new JLabel("Email:"));
        updatePanel.add(tfEmail);
        updatePanel.add(new JLabel("Phone:"));
        updatePanel.add(tfPhone);
        updatePanel.add(new JLabel("Course:"));
        updatePanel.add(cbCourse);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            String newFirstName = tfFirstName.getText();
            String newLastName = tfLastName.getText();
            String newGender = (String) cbGender.getSelectedItem();
            String newEmail = tfEmail.getText();
            String newPhone = tfPhone.getText();
            String newCourse = (String) cbCourse.getSelectedItem();

            // Validate inputs
            if (!validateInputs(newFirstName, newLastName, newGender, newEmail, newPhone, newCourse)) {
                showErrorDialog("Please ensure all fields are filled correctly.\n- Email should be valid.\n- Phone should be 10 digits.");
                return;
            }

            // Update database
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "UPDATE students SET first_name = ?, last_name = ?, gender = ?, email = ?, phone = ?, course = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, newFirstName);
                    statement.setString(2, newLastName);
                    statement.setString(3, newGender);
                    statement.setString(4, newEmail);
                    statement.setString(5, newPhone);
                    statement.setString(6, newCourse);
                    statement.setInt(7, studentID);

                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(mainFrame, "Student details updated successfully!");
                        refreshTable();
                        updateDialog.dispose();
                    } else {
                        showErrorDialog("Error: Student ID not found.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorDialog("Error while updating student: " + ex.getMessage());
            }
        });

        updatePanel.add(new JLabel()); // Empty cell
        updatePanel.add(btnSave);
        updateDialog.add(updatePanel);
        updateDialog.setVisible(true);
    }

    // Show dialog to add a new student
    private void showAddDialog() {
        // Create input fields
        JTextField txtFirstName = new JTextField(10);
        JTextField txtLastName = new JTextField(10);
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Male", "Female", "Transgender", "Others"});
        JTextField txtEmail = new JTextField(10);
        JTextField txtPhone = new JTextField(10);
        JComboBox<String> cbCourse = new JComboBox<>(new String[]{
                "BCA", "MCA", "B.Tech", "M.Tech", "B.Com", "M.Com",
                "BBA", "MBA", "BA", "MA", "B.Sc", "M.Sc", "PhD", "Diploma"
        });

        // Create a panel to hold the input fields
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("First Name:"));
        panel.add(txtFirstName);
        panel.add(new JLabel("Last Name:"));
        panel.add(txtLastName);
        panel.add(new JLabel("Gender:"));
        panel.add(cbGender); // Use JComboBox for gender
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Phone:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Course:"));
        panel.add(cbCourse); // Use JComboBox for course

        // Show dialog
        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            // Get input values
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String gender = (String) cbGender.getSelectedItem(); // Get selected gender
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String course = (String) cbCourse.getSelectedItem(); // Get selected course

            // Validate inputs
            if (!validateInputs(firstName, lastName, gender, email, phone, course)) {
                showErrorDialog("Please ensure all fields are filled correctly.\n- Email should be valid.\n- Phone should be 10 digits.");
                return; // Exit the method
            }

            // Insert the new student into the database
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO students (first_name, last_name, gender, email, phone, course) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, gender);
                    statement.setString(4, email);
                    statement.setString(5, phone);
                    statement.setString(6, course);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(mainFrame, "Student added successfully!");
                        refreshTable(); // Refresh table after adding a student
                    } else {
                        showErrorDialog("Error: Could not add student.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Error while adding student: " + e.getMessage());
            }
        }
    }

    // Validate input fields
    private boolean validateInputs(String firstName, String lastName, String gender, String email, String phone, String course) {
        return !firstName.isEmpty() && !lastName.isEmpty() && !gender.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !course.isEmpty()
                && email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                && phone.matches("\\d{10}");
    }

    // Show error dialog
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Show search dialog
    private void showSearchDialog() {
        JDialog searchDialog = new JDialog(mainFrame, "Search Student", true);
        searchDialog.setSize(250, 150);
        searchDialog.setLayout(new GridLayout(4, 2, 10, 10));
        searchDialog.setLocationRelativeTo(mainFrame);

        JTextField tfSearchValue = new JTextField();
        JComboBox<String> cbSearchCriteria = new JComboBox<>(new String[]{"ID", "Name", "Phone Number"});

        JButton btnSearchNow = new JButton("Search");
        btnSearchNow.addActionListener(e -> {
            String criteria = (String) cbSearchCriteria.getSelectedItem();
            String value = tfSearchValue.getText();
            searchStudent(criteria, value);
            searchDialog.dispose();
        });

        searchDialog.add(new JLabel("Search By:"));
        searchDialog.add(cbSearchCriteria);
        searchDialog.add(new JLabel("Value:"));
        searchDialog.add(tfSearchValue);
        searchDialog.add(new JLabel());
        searchDialog.add(btnSearchNow);

        searchDialog.setVisible(true);
    }

    // Search student in database
    private void searchStudent(String criteria, String value) {
        tableModel.setRowCount(0); // Clear existing rows

        String query = "";
        switch (criteria) {
            case "ID":
                query = "SELECT * FROM students WHERE id = ?";
                break;
            case "Name":
                query = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ?";
                break;
            case "Phone Number":
                query = "SELECT * FROM students WHERE phone = ?";
                break;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the parameters for the prepared statement
            if (criteria.equals("ID") || criteria.equals("Phone Number")) {
                preparedStatement.setString(1, value);
            } else if (criteria.equals("Name")) {
                preparedStatement.setString(1, "%" + value + "%");
                preparedStatement.setString(2, "%" + value + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Add the results to the table
                while (resultSet.next()) {
                    Object[] row = {
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("gender"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("course")
                           };
                    tableModel.addRow(row);
                }
            }

            // If no results are found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(mainFrame, "No matching records found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error while searching student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagementSystem::new);
    }
}

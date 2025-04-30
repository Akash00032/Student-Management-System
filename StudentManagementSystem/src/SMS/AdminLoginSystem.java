package SMS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLoginSystem extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public AdminLoginSystem() {
        // Set up the frame
        setTitle("Admin Login - Student Management System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(230, 240, 250)); // Light blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel lblTitle = new JLabel("Admin Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 51, 102)); // Dark blue
        add(lblTitle, BorderLayout.NORTH);

        // Username Label and Field
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 16));
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblUsername, gbc);

        gbc.gridx = 1;
        mainPanel.add(txtUsername, gbc);

        // Password Label and Field
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lblPassword, gbc);

        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(51, 153, 255)); // Blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(btnLogin, gbc);

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);

        // Button action listener
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateAdmin();
            }
        });

        // Make the frame visible
        setVisible(true);
    }

    // Admin authentication logic
    private void authenticateAdmin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        // Hardcoded admin credentials (can be replaced with DB check)
        if (username.equals("admin") && password.equals("admin123")) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            openStudentManagementSystem();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Open Student Management System on successful login
    private void openStudentManagementSystem() {
        SwingUtilities.invokeLater(() -> {
            new StudentManagementSystem(); // Open main system
            dispose(); // Close login window
        });
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLoginSystem::new);
    }
}

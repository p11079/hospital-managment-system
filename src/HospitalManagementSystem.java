import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
public class HospitalManagementSystem extends JFrame {
    private JTextField txtUsername, txtRoomNumber, txtRoomType, txtPrice;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;

    public HospitalManagementSystem() {
        connectDatabase();
        showRoleSelectionWindow();
    }
    private void connectDatabase() {
        try {
            String url = "jdbc:postgresql://ep-rough-glitter-a485m8ua-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require";
            String user = "neondb_owner";
            String password = "npg_mwinK78lFTZO";

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField txtName, txtPhone, txtAddress;
    private JComboBox<String> genderCombo;

    private void showRoleSelectionWindow() {
        JFrame roleFrame = new JFrame("Select Role");
        roleFrame.setSize(300, 150);
        roleFrame.setLayout(new GridLayout(2, 1, 10, 10));
        roleFrame.setLocationRelativeTo(null);
        roleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Login As:", SwingConstants.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnDoctor = new JButton("Doctor");
        JButton btnPatient = new JButton("Patient");

        btnDoctor.addActionListener(e -> {
            roleFrame.dispose();
            showDoctorLoginWindow();
        });

        btnPatient.addActionListener(e -> {
            roleFrame.dispose();
            showPatientLoginWindow();
        });

        buttonPanel.add(btnDoctor);
        buttonPanel.add(btnPatient);

        roleFrame.add(label);
        roleFrame.add(buttonPanel);
        roleFrame.setVisible(true);
    }

    private void showDoctorLoginWindow() {
        JFrame loginFrame = new JFrame("Doctor Login");
        loginFrame.setSize(350, 220);
        loginFrame.setLayout(new BorderLayout(10, 10));
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
    
        // Center Panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        inputPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        inputPanel.add(txtUsername);
    
        inputPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        inputPanel.add(txtPassword);
    
        loginFrame.add(inputPanel, BorderLayout.CENTER);
    
        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnLogin = new JButton("Login");
        JButton btnBack = new JButton("Back");
        JButton btnSignUp = new JButton("Sign Up");
    
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSignUp);
        buttonPanel.add(btnBack);
    
        loginFrame.add(buttonPanel, BorderLayout.SOUTH);
    
        // Action listeners
        btnLogin.addActionListener(e -> login(loginFrame, "doctor"));
        btnBack.addActionListener(e -> {
            loginFrame.dispose();
            showRoleSelectionWindow();
        });
        btnSignUp.addActionListener(e -> {
            loginFrame.dispose();
            showDoctorSignupWindow(); // You should define this method
        });
    
        loginFrame.setVisible(true);
    }
    private void showDoctorSignupWindow() {
        JFrame signupFrame = new JFrame("Doctor Sign Up");
        signupFrame.setSize(400, 250);
        signupFrame.setLayout(new BorderLayout(10, 10));
        signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signupFrame.setLocationRelativeTo(null);
    
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JTextField txtName = new JTextField();
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtSpecialization = new JTextField();
    
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(txtName);
    
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(txtUsername);
    
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(txtPassword);
    
        inputPanel.add(new JLabel("Specialization:"));
        inputPanel.add(txtSpecialization);
    
        signupFrame.add(inputPanel, BorderLayout.CENTER);
    
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSubmit = new JButton("Sign Up");
        JButton btnBack = new JButton("Back");
    
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnBack);
        signupFrame.add(buttonPanel, BorderLayout.SOUTH);
    
        // Action Listeners
        btnSubmit.addActionListener(e -> {
            String name = txtName.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String specialization = txtSpecialization.getText().trim();
    
            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || specialization.isEmpty()) {
                JOptionPane.showMessageDialog(signupFrame, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (conn == null) {
                JOptionPane.showMessageDialog(signupFrame, "Database not connected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                String query = "INSERT INTO doctors (name, username, password, specialization) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, username);
                pst.setString(3, password); // ⚠ Insecure: consider hashing
                pst.setString(4, specialization);
    
                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(signupFrame, "Doctor registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    signupFrame.dispose();
                    showDoctorLoginWindow();
                } else {
                    JOptionPane.showMessageDialog(signupFrame, "Failed to register doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(signupFrame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        btnBack.addActionListener(e -> {
            signupFrame.dispose();
            showDoctorLoginWindow();
        });
    
        signupFrame.setVisible(true);
    }

    private void showPatientLoginWindow() {
        JFrame loginFrame = new JFrame("Patient Login");
        loginFrame.setSize(400, 250);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Patient Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loginFrame.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);
        loginFrame.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Login");
        JButton btnBack = new JButton("Back");
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnBack);

        JPanel signupPanel = new JPanel(new FlowLayout());
        JButton btnSignup = new JButton("Sign Up as Patient");
        signupPanel.add(btnSignup);

        JPanel bottomWrapper = new JPanel();
        bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.Y_AXIS));
        bottomWrapper.add(buttonPanel);
        bottomWrapper.add(Box.createVerticalStrut(5));
        bottomWrapper.add(signupPanel);
        loginFrame.add(bottomWrapper, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> login(loginFrame, "patient"));
        btnSignup.addActionListener(e -> {
            loginFrame.dispose();
            showPatientSignupWindow();
        });
        btnBack.addActionListener(e -> {
            loginFrame.dispose();
            showRoleSelectionWindow();
        });
        loginFrame.setVisible(true);
    }

    private void login(JFrame frame, String role) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
    
        try {
            String sql = role.equals("doctor")
                ? "SELECT * FROM doctors WHERE username=? AND password=?"
                : "SELECT * FROM patients WHERE username=? AND password=?";
    
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
    
            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "Login Successful as " + role.toUpperCase() + "!");
                frame.dispose();
                if (role.equals("doctor")) {
                    String fetchedDoctorName = rs.getString("name"); // Fetch doctor's name
                    showDoctorDashboard(fetchedDoctorName);          // Pass name to dashboard
                } else {
                    String fetchedPatientName = rs.getString("name"); // Fetch patient's name
                    showPatientDashboard(fetchedPatientName);        // Pass name to main window
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPatientSignupWindow() {
        JFrame signupFrame = new JFrame("Patient Sign Up");
        signupFrame.setSize(400, 350);
        signupFrame.setLayout(new GridLayout(8, 2, 5, 5));
        signupFrame.setLocationRelativeTo(null);
        signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        signupFrame.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        signupFrame.add(txtUsername);

        signupFrame.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        signupFrame.add(txtPassword);

        signupFrame.add(new JLabel("Confirm Password:"));
        txtConfirmPassword = new JPasswordField();
        signupFrame.add(txtConfirmPassword);

        signupFrame.add(new JLabel("Name:"));
        txtName = new JTextField();
        signupFrame.add(txtName);

        signupFrame.add(new JLabel("Gender:"));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        signupFrame.add(genderCombo);

        signupFrame.add(new JLabel("Phone Number:"));
        txtPhone = new JTextField();
        signupFrame.add(txtPhone);

        signupFrame.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        signupFrame.add(txtAddress);

        JButton btnRegister = new JButton("Register");
        JButton btnBack = new JButton("Back");
        signupFrame.add(btnRegister);
        signupFrame.add(btnBack);

        btnRegister.addActionListener(e -> registerPatient(signupFrame));
        btnBack.addActionListener(e -> {
            signupFrame.dispose();
            showPatientLoginWindow();
        });

        signupFrame.setVisible(true);
    }

    private void registerPatient(JFrame signupFrame) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String name = txtName.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(signupFrame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(signupFrame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String checkQuery = "SELECT COUNT(*) FROM patients WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(signupFrame, "Username already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insertSql = "INSERT INTO patients (username, password, name, gender, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(insertSql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, name);
            pst.setString(4, gender);
            pst.setString(5, phone);
            pst.setString(6, address);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(signupFrame, "Patient Registered Successfully!");
            signupFrame.dispose();
            showPatientLoginWindow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDoctorDashboard(String doctorName) {
        JFrame dashboardFrame = new JFrame("Doctor Dashboard");
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        dashboardFrame.setUndecorated(false);
        dashboardFrame.setLayout(new BorderLayout());
    
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 130, 184));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
        JLabel lblDashboard = new JLabel("DASHBOARD");
        lblDashboard.setFont(new Font("Arial", Font.BOLD, 24));
        lblDashboard.setForeground(Color.WHITE);
    
        JLabel lblWelcome = new JLabel("Welcome Dr. " + doctorName);
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 18));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setHorizontalAlignment(SwingConstants.RIGHT);
    
        topPanel.add(lblDashboard, BorderLayout.WEST);
        topPanel.add(lblWelcome, BorderLayout.EAST);
        dashboardFrame.add(topPanel, BorderLayout.NORTH);
    
        // Left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        leftPanel.setBackground(Color.LIGHT_GRAY);
    
        JButton btnAppointments = new JButton("Appointments");
        JButton btnPrescriptions = new JButton("Prescriptions");
        btnAppointments.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPrescriptions.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        leftPanel.add(btnAppointments);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(btnPrescriptions);
        dashboardFrame.add(leftPanel, BorderLayout.WEST);
    
        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        dashboardFrame.add(centerPanel, BorderLayout.CENTER);
    
        // Appointments table setup
        String[] columnNames = {"Appointment ID", "Patient Name", "Symptoms", "Appointment Date", "Booked At"};
        DefaultTableModel appointmentModel = new DefaultTableModel(columnNames, 0);
        JTable appointmentTable = new JTable(appointmentModel);
        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        JButton btnDone = new JButton("Done");
        
        // Button action to show appointments
        btnAppointments.addActionListener(e -> {
            appointmentModel.setRowCount(0); // Clear table
            try {
                // Get doctor ID from doctorName
                String getIdSql = "SELECT id FROM doctors WHERE name = ?";
                PreparedStatement pst1 = conn.prepareStatement(getIdSql);
                pst1.setString(1, doctorName);
                ResultSet rs1 = pst1.executeQuery();
        
                if (rs1.next()) {
                    int docId = rs1.getInt("id");
        
                    // Now fetch appointments for this doctor (include appointment id)
                    String sql = "SELECT id, patient_name, symptoms, appointment_date, created_at FROM appointments WHERE doc_id = ?";
                    PreparedStatement pst2 = conn.prepareStatement(sql);
                    pst2.setInt(1, docId);
                    ResultSet rs2 = pst2.executeQuery();
        
                    while (rs2.next()) {
                        int appointmentId = rs2.getInt("id");
                        String patientName = rs2.getString("patient_name");
                        String symptoms = rs2.getString("symptoms");
                        Date date = rs2.getDate("appointment_date");
                        Timestamp created = rs2.getTimestamp("created_at");
        
                        appointmentModel.addRow(new Object[]{appointmentId, patientName, symptoms, date.toString(), created.toString()});
                    }
        
                    centerPanel.removeAll();
                    centerPanel.add(appointmentScroll, BorderLayout.CENTER);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                } else {
                    JOptionPane.showMessageDialog(dashboardFrame, "Doctor not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
        
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dashboardFrame, "Error fetching appointments.", "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnPrescriptions.addActionListener(e -> {
            centerPanel.removeAll();
        
            JPanel prescriptionPanel = new JPanel();
            prescriptionPanel.setLayout(new BoxLayout(prescriptionPanel, BoxLayout.Y_AXIS));
            prescriptionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            prescriptionPanel.setBackground(Color.WHITE);
        
            JLabel lblTitle = new JLabel("Enter Prescription");
            lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
            lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            prescriptionPanel.add(lblTitle);
            prescriptionPanel.add(Box.createVerticalStrut(20));
        
            JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lblAppId = new JLabel("Appointment ID:");
            JTextField txtAppId = new JTextField(10);
            JButton btnFetch = new JButton("Fetch");
            idPanel.add(lblAppId);
            idPanel.add(txtAppId);
            idPanel.add(btnFetch);
            prescriptionPanel.add(idPanel);
        
            JLabel lblPatient = new JLabel("Patient Name: ");
            JLabel lblSymptoms = new JLabel("Symptoms: ");
            lblPatient.setFont(new Font("Arial", Font.PLAIN, 16));
            lblSymptoms.setFont(new Font("Arial", Font.PLAIN, 16));
            prescriptionPanel.add(lblPatient);
            prescriptionPanel.add(Box.createVerticalStrut(5));
            prescriptionPanel.add(lblSymptoms);
            prescriptionPanel.add(Box.createVerticalStrut(20));
        
            JLabel lblPrescription = new JLabel("Prescription:");
            JTextArea txtPrescription = new JTextArea(6, 40);
            txtPrescription.setLineWrap(true);
            txtPrescription.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(txtPrescription);
            prescriptionPanel.add(lblPrescription);
            prescriptionPanel.add(scrollPane);
            prescriptionPanel.add(Box.createVerticalStrut(20));
        
            JButton btnSave = new JButton("Save Prescription");
            btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
            prescriptionPanel.add(btnSave);
        
            centerPanel.add(prescriptionPanel, BorderLayout.CENTER);
            centerPanel.revalidate();
            centerPanel.repaint();
        
            // Variables to hold fetched data
            final String[] fetchedPatient = {null};
            final String[] fetchedSymptoms = {null};
        
            // Fetch Button Logic
            btnFetch.addActionListener(fetchEvent -> {
                String appIdStr = txtAppId.getText().trim();
                if (appIdStr.isEmpty()) {
                    JOptionPane.showMessageDialog(centerPanel, "Please enter an Appointment ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
        
                try {
                    int appId = Integer.parseInt(appIdStr);
        
                    // Get doctor ID from doctorName
                    String getIdSql = "SELECT id FROM doctors WHERE name = ?";
                    PreparedStatement pst1 = conn.prepareStatement(getIdSql);
                    pst1.setString(1, doctorName);
                    ResultSet rs1 = pst1.executeQuery();
        
                    if (rs1.next()) {
                        int docId = rs1.getInt("id");
        
                        String sql = "SELECT patient_name, symptoms FROM appointments WHERE id = ? AND doc_id = ?";
                        PreparedStatement pst2 = conn.prepareStatement(sql);
                        pst2.setInt(1, appId);
                        pst2.setInt(2, docId);
                        ResultSet rs2 = pst2.executeQuery();
        
                        if (rs2.next()) {
                            fetchedPatient[0] = rs2.getString("patient_name");
                            fetchedSymptoms[0] = rs2.getString("symptoms");
        
                            lblPatient.setText("Patient Name: " + fetchedPatient[0]);
                            lblSymptoms.setText("Symptoms: " + fetchedSymptoms[0]);
                        } else {
                            JOptionPane.showMessageDialog(centerPanel, "No appointment found for this ID.", "Not Found", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(centerPanel, "Error fetching appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        
            // Save Button Logic
            btnSave.addActionListener(saveEvent -> {
                String appIdStr = txtAppId.getText().trim();
                String prescriptionText = txtPrescription.getText().trim();
        
                if (appIdStr.isEmpty() || fetchedPatient[0] == null || prescriptionText.isEmpty()) {
                    JOptionPane.showMessageDialog(centerPanel, "Please fetch appointment and enter prescription.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
        
                try {
                    int appId = Integer.parseInt(appIdStr);
        
                    // Get doctor ID from doctorName
                    String getIdSql = "SELECT id FROM doctors WHERE name = ?";
                    PreparedStatement pst1 = conn.prepareStatement(getIdSql);
                    pst1.setString(1, doctorName);
                    ResultSet rs1 = pst1.executeQuery();
        
                    if (rs1.next()) {
                        int docId = rs1.getInt("id");
        
                        String insertSql = "INSERT INTO prescription (appointment_id, doctor_id, patient_name, symptoms, prescription) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement pstInsert = conn.prepareStatement(insertSql);
                        pstInsert.setInt(1, appId);
                        pstInsert.setInt(2, docId);
                        pstInsert.setString(3, fetchedPatient[0]);
                        pstInsert.setString(4, fetchedSymptoms[0]);
                        pstInsert.setString(5, prescriptionText);
        
                        int rows = pstInsert.executeUpdate();
        
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(centerPanel, "Prescription saved successfully.");
                            txtPrescription.setText("");
                        } else {
                            JOptionPane.showMessageDialog(centerPanel, "Failed to save prescription.", "DB Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
        
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(centerPanel, "Error saving prescription.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    
        // Default center content
        JLabel centerLabel = new JLabel("Main Content Area", SwingConstants.CENTER);
        centerLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        centerPanel.add(centerLabel, BorderLayout.CENTER);
    
        dashboardFrame.setVisible(true);
    }
    
    private void showPatientDashboard(String patientName) {
        JFrame dashboardFrame = new JFrame("Patient Dashboard");
        dashboardFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setLocationRelativeTo(null);
    
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel dashboardLabel = new JLabel("PATIENT DASHBOARD");
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel welcomeLabel = new JLabel("Welcome, " + patientName);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(dashboardLabel, BorderLayout.WEST);
        topPanel.add(welcomeLabel, BorderLayout.EAST);
    
        // --- Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();
    
        // ================== DOCTORS TAB ==================
        JPanel doctorsTab = new JPanel(new BorderLayout());
    
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JTextField txtNameFilter = new JTextField(15);
        JComboBox<String> specializationCombo = new JComboBox<>();
        specializationCombo.addItem("All");
        JButton btnSearch = new JButton("Search");
    
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT specialization FROM doctors WHERE specialization IS NOT NULL ORDER BY specialization");
            while (rs.next()) {
                String spec = rs.getString("specialization");
                specializationCombo.addItem(spec);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        filterPanel.add(new JLabel("Search by Name:"));
        filterPanel.add(txtNameFilter);
        filterPanel.add(new JLabel("Specialization:"));
        filterPanel.add(specializationCombo);
        filterPanel.add(btnSearch);
    
        // Table
        String[] doctorColumns = {"ID", "Name", "Specialization", "Phone Number", "Email"};
        DefaultTableModel doctorModel = new DefaultTableModel(doctorColumns, 0);
        JTable doctorsTable = new JTable(doctorModel);
        JScrollPane tableScrollPane = new JScrollPane(doctorsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Available Doctors"));
    
        // Hide ID column
        doctorsTable.getColumnModel().getColumn(0).setMinWidth(0);
        doctorsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        doctorsTable.getColumnModel().getColumn(0).setWidth(0);
    
        populateDoctorsTable(doctorModel, "", "");
    
        btnSearch.addActionListener(e -> {
            String nameFilter = txtNameFilter.getText().trim();
            String selectedSpec = specializationCombo.getSelectedItem().toString();
            String specializationFilter = selectedSpec.equals("All") ? "" : selectedSpec;
            populateDoctorsTable(doctorModel, nameFilter, specializationFilter);
        });
    
        doctorsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = doctorsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int docId = Integer.parseInt(doctorsTable.getValueAt(selectedRow, 0).toString());
                        String doctorName = doctorsTable.getValueAt(selectedRow, 1).toString();
                        openAppointmentWindow(docId, doctorName, patientName);
                    }
                }
            }
        });
    
        doctorsTab.add(filterPanel, BorderLayout.NORTH);
        doctorsTab.add(tableScrollPane, BorderLayout.CENTER);
    
        // ================== PRESCRIPTIONS TAB ==================
        JPanel prescriptionsTab = new JPanel(new BorderLayout());
        String[] presColumns = {"Prescription ID", "Doctor ID", "Appointment ID", "Symptoms", "Prescription", "Date"};
        DefaultTableModel presModel = new DefaultTableModel(presColumns, 0);
        JTable presTable = new JTable(presModel);
        JScrollPane presScrollPane = new JScrollPane(presTable);
        JButton btnAcknowledge = new JButton("Acknowledge");
    
        // Load prescriptions
        loadPatientPrescriptions(presModel, patientName);
    
        btnAcknowledge.addActionListener(e -> {
            int selectedRow = presTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dashboardFrame, "Please select a prescription row.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            int appointmentId = Integer.parseInt(presModel.getValueAt(selectedRow, 2).toString());
    
            try {
                // Delete prescription
                PreparedStatement deletePresStmt = conn.prepareStatement("DELETE FROM prescription WHERE appointment_id = ?");
                deletePresStmt.setInt(1, appointmentId);
                deletePresStmt.executeUpdate();
    
                // Delete appointment
                PreparedStatement deleteAppStmt = conn.prepareStatement("DELETE FROM appointments WHERE id = ?");
                deleteAppStmt.setInt(1, appointmentId);
                deleteAppStmt.executeUpdate();
    
                JOptionPane.showMessageDialog(dashboardFrame, "Prescription acknowledged and appointment removed.");
                presModel.removeRow(selectedRow);
    
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dashboardFrame, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        prescriptionsTab.add(presScrollPane, BorderLayout.CENTER);
        prescriptionsTab.add(btnAcknowledge, BorderLayout.SOUTH);
    
        // Add tabs
        tabbedPane.addTab("Search Doctors", doctorsTab);
        tabbedPane.addTab("Prescriptions", prescriptionsTab);
    
        // Frame layout
        dashboardFrame.setLayout(new BorderLayout());
        dashboardFrame.add(topPanel, BorderLayout.NORTH);
        dashboardFrame.add(tabbedPane, BorderLayout.CENTER);
        dashboardFrame.setVisible(true);
    }
    private void loadPatientPrescriptions(DefaultTableModel model, String patientName) {
        model.setRowCount(0);
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, doctor_id, appointment_id, symptoms, prescription, created_at FROM prescription WHERE patient_name = ?");
            stmt.setString(1, patientName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getInt("doctor_id"),
                    rs.getInt("appointment_id"),
                    rs.getString("symptoms"),
                    rs.getString("prescription"),
                    rs.getTimestamp("created_at")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void populateDoctorsTable(DefaultTableModel model, String nameFilter, String specializationFilter) {
        try {
            String sql = "SELECT id, name, specialization, phone, email FROM doctors WHERE name ILIKE ? AND specialization ILIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + nameFilter + "%");
            pst.setString(2, "%" + specializationFilter + "%");
            ResultSet rs = pst.executeQuery();
    
            model.setRowCount(0); // Clear table
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String specialization = rs.getString("specialization");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                model.addRow(new Object[]{id, name, specialization, phone, email});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void openAppointmentWindow(int docId, String doctorName, String patientName) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Book Appointment with Dr. " + doctorName);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
    
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JLabel lblSymptom = new JLabel("Describe your symptoms:");
        JTextArea txtSymptoms = new JTextArea(5, 30);
        txtSymptoms.setLineWrap(true);
        txtSymptoms.setWrapStyleWord(true);
        JScrollPane symptomScroll = new JScrollPane(txtSymptoms);
    
        JLabel lblDate = new JLabel("Choose Appointment Date:");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
    
        JButton btnConfirm = new JButton("Confirm Appointment");
    
        btnConfirm.addActionListener(e -> {
            String symptoms = txtSymptoms.getText().trim();
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
    
            if (symptoms.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter your symptoms.");
                return;
            }
    
            try {
                String sql = "INSERT INTO appointments (doc_id, patient_name, symptoms, appointment_date) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, docId);
                pst.setString(2, patientName);
                pst.setString(3, symptoms);
                pst.setDate(4, sqlDate);
                pst.executeUpdate();
    
                JOptionPane.showMessageDialog(dialog, "Appointment booked successfully!");
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Failed to book appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        panel.add(lblSymptom);
        panel.add(symptomScroll);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblDate);
        panel.add(dateSpinner);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnConfirm);
    
        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalManagementSystem());
    }
}
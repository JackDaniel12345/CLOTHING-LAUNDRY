
    import java.sql.*;
import java.util.*;

public class maine {

    static Scanner sc = new Scanner(System.in);

    // --- Database connection setup ---
    public static Connection connect() {
        Connection con = null;
        try {
            // Adjust DB path if needed
            String url = "jdbc:sqlite:clothing.db";
            con = DriverManager.getConnection(url);
            System.out.println("✅ Connected to database.");
        } catch (SQLException e) {
            System.out.println("❌ Connection error: " + e.getMessage());
        }
        return con;
    }

    // --- Helper: view records (like your Conf.viewRecords) ---
    public static void viewRecords(String qry, String[] headers, String[] fields) {
        try (Connection con = connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(qry)) {

            System.out.println("\n-----------------------------------------------------------");
            for (String h : headers) System.out.printf("%-20s", h);
            System.out.println("\n-----------------------------------------------------------");

            while (rs.next()) {
                for (String f : fields) {
                    System.out.printf("%-20s", rs.getString(f));
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("❌ View error: " + e.getMessage());
        }
    }

    // --- Registration ---
    public static void registerUser() {
        try (Connection con = connect()) {
            System.out.println("\n--- REGISTER NEW USER ---");
            System.out.print("Full Name: ");
            String name = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Phone: ");
            String phone = sc.nextLine();
            System.out.print("Address: ");
            String address = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();
            System.out.print("Role (Admin/Customer): ");
            String type = sc.nextLine();

            String qry = "INSERT INTO tbl_users(u_name, u_email, u_phonenumber, u_address, u_registerdate, u_type, u_status, u_pass) VALUES(?,?,?,?,DATE('now'),?,?,?)";
            PreparedStatement ps = con.prepareStatement(qry);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, type);
            ps.setString(6, "Active");
            ps.setString(7, pass);
            ps.executeUpdate();

            System.out.println("✅ User registered successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Register error: " + e.getMessage());
        }
    }

    // --- Login ---
    public static String loginUser() {
        try (Connection con = connect()) {
            System.out.println("\n--- LOGIN ---");
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            String qry = "SELECT * FROM tbl_users WHERE u_email=? AND u_pass=?";
            PreparedStatement ps = con.prepareStatement(qry);
            ps.setString(1, email);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("u_type");
                System.out.println("✅ Login successful as " + role);
                return role;
            } else {
                System.out.println("❌ Invalid credentials.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
        return "";
    }

    // --- Add Laundry Order (Customer) ---
    public static void addLaundryOrder() {
        try (Connection con = connect()) {
            System.out.println("\n--- ADD LAUNDRY ORDER ---");
            System.out.print("Customer ID: ");
            int cid = sc.nextInt();
            sc.nextLine(); // consume newline
            System.out.print("Service Type (match tbl_services.s_desc): ");
            String service = sc.nextLine();
            System.out.print("Total Cost: ");
            double total = sc.nextDouble();
            sc.nextLine();
            System.out.print("Contact: ");
            String contact = sc.nextLine();

            String qry = "INSERT INTO tbl_LaundryOrder(lo_customerid, lo_orderdate, lo_servicetype, lo_status, lo_totalcost, lo_contact) " +
                         "VALUES(?, DATE('now'), ?, 'Pending', ?, ?)";
            PreparedStatement ps = con.prepareStatement(qry);
            ps.setInt(1, cid);
            ps.setString(2, service);
            ps.setDouble(3, total);
            ps.setString(4, contact);
            ps.executeUpdate();

            System.out.println("✅ Laundry order added.");

        } catch (SQLException e) {
            System.out.println("❌ Add order error: " + e.getMessage());
        }
    }

    // --- View All Users (Admin) ---
    public static void viewUsers() {
        String qry = "SELECT * FROM tbl_users";
        String[] headers = {"ID", "Name", "Email", "Phone", "Address", "Type", "Status"};
        String[] fields = {"u_id", "u_name", "u_email", "u_phonenumber", "u_address", "u_type", "u_status"};
        viewRecords(qry, headers, fields);
    }

    // --- View Services (Admin/Customer) ---
    public static void viewServices() {
        String qry = "SELECT * FROM tbl_services";
        String[] headers = {"Service ID", "Description", "Price"};
        String[] fields = {"s_id", "s_desc", "s_price"};
        viewRecords(qry, headers, fields);
    }

    // --- View Laundry Orders with JOIN ---
    public static void viewLaundryOrders() {
        String qry = "SELECT lo.lo_id, u.u_name, u.u_email, u.u_phonenumber, " +
                     "s.s_desc, s.s_price, lo.lo_orderdate, lo.lo_status, lo.lo_totalcost " +
                     "FROM tbl_LaundryOrder lo " +
                     "JOIN tbl_users u ON lo.lo_customerid = u.u_id " +
                     "JOIN tbl_services s ON lo.lo_servicetype = s.s_desc";
        String[] headers = {"Order ID", "Customer", "Email", "Phone", "Service", "Price", "Date", "Status", "Total"};
        String[] fields = {"lo_id", "u_name", "u_email", "u_phonenumber", "s_desc", "s_price", "lo_orderdate", "lo_status", "lo_totalcost"};
        viewRecords(qry, headers, fields);
    }

    // --- Admin Dashboard ---
    public static void adminMenu() {
        while (true) {
            System.out.println("\n=== ADMIN DASHBOARD ===");
            System.out.println("1. View Users");
            System.out.println("2. View Services");
            System.out.println("3. View Laundry Orders (JOIN)");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            int c = sc.nextInt();
            sc.nextLine();
            switch (c) {
                case 1 -> viewUsers();
                case 2 -> viewServices();
                case 3 -> viewLaundryOrders();
                case 4 -> { return; }
                default -> System.out.println("❌ Invalid option.");
            }
        }
    }

    // --- Customer Dashboard ---
    public static void customerMenu() {
        while (true) {
            System.out.println("\n=== CUSTOMER DASHBOARD ===");
            System.out.println("1. View Services");
            System.out.println("2. Add Laundry Order");
            System.out.println("3. View All Orders (JOIN)");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            int c = sc.nextInt();
            sc.nextLine();
            switch (c) {
                case 1 -> viewServices();
                case 2 -> addLaundryOrder();
                case 3 -> viewLaundryOrders();
                case 4 -> { return; }
                default -> System.out.println("❌ Invalid option.");
            }
        }
    }

    // --- MAIN PROGRAM ---
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n========= CLOTHING LAUNDRY SYSTEM =========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> {
                    String role = loginUser();
                    if (role.equalsIgnoreCase("Admin")) adminMenu();
                    else if (role.equalsIgnoreCase("Customer")) customerMenu();
                }
                case 3 -> {
                    System.out.println("👋 Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }
}
}

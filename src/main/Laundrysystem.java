package Main;

import java.util.*;
import config.config;

public class Laundrysystem {

    static Scanner sc = new Scanner(System.in);
    static config db = new config();

    public static void main(String[] args) {

        initializeDB();

        int choice;
        System.out.println("===== CLOTHING LAUNDRY SERVICE TRACKER =====");

        do {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            choice = safeReadInt("Enter choice: ");

            switch (choice) {
                case 1: register(); break;
                case 2: login(); break;
                case 3: System.out.println("👋 Exiting..."); break;
                default: System.out.println("❌ Invalid choice!");
            }

        } while (choice != 3);
    }

    // =============================
    // INITIALIZE DATABASE
    // =============================
    public static void initializeDB() {

        String sqlUsers =
            "CREATE TABLE IF NOT EXISTS tbl_users (" +
            "u_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "u_name TEXT NOT NULL," +
            "u_email TEXT NOT NULL UNIQUE," +
            "u_pass TEXT NOT NULL," +
            "u_type TEXT NOT NULL," +
            "u_status TEXT NOT NULL" +
            ");";

        String sqlServices =
            "CREATE TABLE IF NOT EXISTS tbl_services (" +
            "s_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "s_description TEXT," +
            "s_price REAL" +
            ");";

        String sqlOrders =
            "CREATE TABLE IF NOT EXISTS tbl_LaundryOrder (" +
            "lo_orderid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "lo_customerid INTEGER," +
            "lo_serviceid INTEGER," +
            "lo_orderdate TEXT," +
            "lo_weightkg REAL," +
            "lo_totalamount REAL," +
            "lo_status TEXT" +
            ");";

        db.execute(sqlUsers);
        db.execute(sqlServices);
        db.execute(sqlOrders);

        initializeDefaultServices();

        System.out.println("🔵 Database initialized.");
    }

    // =============================
    // INSERT DEFAULT SERVICES
    // =============================
    public static void initializeDefaultServices() {

        List<Map<String, Object>> s = db.fetchRecords("SELECT * FROM tbl_services");

        if (s.isEmpty()) {

            db.addRecord(
                "INSERT INTO tbl_services (s_description, s_price) VALUES (?, ?)",
                "Wash Only", 50.0
            );
            db.addRecord(
                "INSERT INTO tbl_services (s_description, s_price) VALUES (?, ?)",
                "Wash and Fold", 70.0
            );
            db.addRecord(
                "INSERT INTO tbl_services (s_description, s_price) VALUES (?, ?)",
                "Dry Clean", 100.0
            );

            System.out.println("Default services inserted.");
        }
    }

    // =============================
    // SAFE INPUT
    // =============================
    private static int safeReadInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    private static String safeReadLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    // =============================
    // REGISTER USER (UPDATED)
    // =============================
    public static void register() {

        String name = safeReadLine("Enter Name: ");
        String email = safeReadLine("Enter Email: ");
        String pass = safeReadLine("Enter Password: ");

        System.out.println("\nSelect Account Type:");
        System.out.println("1. Admin");
        System.out.println("2. Customer");
        System.out.println("3. Staff");

        int typeChoice = safeReadInt("Enter choice: ");

        String type;
        switch (typeChoice) {
            case 1: type = "Admin"; break;
            case 2: type = "Customer"; break;
            case 3: type = "Staff"; break;
            default: System.out.println("Invalid type!"); return;
        }

        String hashedPass = config.hashPassword(pass);

        // NEW: default status depends on type
        String status = (type.equals("Customer")) ? "Pending" : "Approved";

        db.addRecord(
            "INSERT INTO tbl_users (u_name, u_email, u_pass, u_type, u_status) VALUES (?, ?, ?, ?, ?)",
            name, email, hashedPass, type, status
        );

        System.out.println("✔ Registration Successful!");

        if (type.equals("Customer")) {
            System.out.println("⏳ Waiting for admin approval...");
            return;  // DO NOT auto-login customers
        }

        // AUTO FETCH USER ID AFTER REGISTER (for admin/staff only)
        List<Map<String, Object>> result = db.fetchRecords(
            "SELECT * FROM tbl_users WHERE u_email = ?", email
        );

        Map<String, Object> user = result.get(0);
        int uid = ((Number) user.get("u_id")).intValue();

        // DIRECTLY OPEN DASHBOARD BASED ON TYPE
        switch (type.toLowerCase()) {
            case "admin":
                System.out.println("\n✔ Redirecting to Admin Dashboard...");
                adminDashboard();
                break;

            case "staff":
                System.out.println("\n✔ Redirecting to Staff Dashboard...");
                staffDashboard();
                break;
        }
    }

    // =============================
    // LOGIN (UPDATED WITH APPROVAL CHECK)
    // =============================
    public static void login() {

        String email = safeReadLine("Enter Email: ");
        String pass = safeReadLine("Enter Password: ");

        String sql = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";

        List<Map<String, Object>> result =
            db.fetchRecords(sql, email, config.hashPassword(pass));

        if (result.isEmpty()) {
            System.out.println("❌ Invalid email or password.");
            return;
        }

        Map<String, Object> user = result.get(0);

        String type = user.get("u_type").toString();
        String status = user.get("u_status").toString();
        int uid = ((Number) user.get("u_id")).intValue();

        // NEW: STOP CUSTOMER IF PENDING
        if (type.equalsIgnoreCase("customer") && status.equalsIgnoreCase("Pending")) {
            System.out.println("⛔ Your account is still pending approval.");
            return;
        }

        // NEW: STOP CUSTOMER IF REJECTED
        if (type.equalsIgnoreCase("customer") && status.equalsIgnoreCase("Rejected")) {
            System.out.println("❌ Your registration was rejected by the admin.");
            return;
        }

        System.out.println("\n✔ Login successful!");
        System.out.println("Welcome " + user.get("u_name"));

        switch (type.toLowerCase()) {
            case "admin": adminDashboard(); break;
            case "staff": staffDashboard(); break;
            case "customer": customerDashboard(uid); break;
            default: System.out.println("Unknown user type.");
        }
    }

    // =============================
    // ADMIN DASHBOARD (UPDATED)
    // =============================
    public static void adminDashboard() {
        int c;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Manage Users");
            System.out.println("2. Approve/Reject Customers");
            System.out.println("3. View Orders");
            System.out.println("4. Update Order Status");
            System.out.println("5. Logout");
            c = safeReadInt("Enter choice: ");

            switch (c) {
                case 1: manageUsers(); break;
                case 2: approveCustomers(); break;
                case 3: viewAllOrders(); break;
                case 4: updateOrderStatus(); break;
                case 5: System.out.println("Logging out..."); break;
            }

        } while (c != 5);
    }

    // =============================
    // APPROVE / REJECT CUSTOMERS
    // =============================
    public static void approveCustomers() {

        List<Map<String, Object>> pending =
            db.fetchRecords("SELECT * FROM tbl_users WHERE u_type='Customer' AND u_status='Pending'");

        if (pending.isEmpty()) {
            System.out.println("✔ No pending customers.");
            return;
        }

        System.out.println("\n===== PENDING CUSTOMERS =====");
        for (Map<String, Object> u : pending) {
            System.out.println(
                u.get("u_id") + " | " +
                u.get("u_name") + " | " +
                u.get("u_email")
            );
        }

        int uid = safeReadInt("\nEnter User ID to update: ");
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        int choice = safeReadInt("Enter choice: ");

        if (choice == 1) {
            db.updateRecord("UPDATE tbl_users SET u_status='Approved' WHERE u_id=?", uid);
            System.out.println("✔ Customer Approved!");
        } else if (choice == 2) {
            db.updateRecord("UPDATE tbl_users SET u_status='Rejected' WHERE u_id=?", uid);
            System.out.println("✔ Customer Rejected!");
        } else {
            System.out.println("❌ Invalid choice.");
        }
    }

    // =============================
    // STAFF DASHBOARD
    // =============================
    public static void staffDashboard() {
        int c;
        do {
            System.out.println("\n===== STAFF DASHBOARD =====");
            System.out.println("1. View Orders");
            System.out.println("2. Update Order Status");
            System.out.println("3. Logout");
            c = safeReadInt("Enter choice: ");

            switch (c) {
                case 1: viewAllOrders(); break;
                case 2: updateOrderStatus(); break;
                case 3: System.out.println("Logging out..."); break;
            }

        } while (c != 3);
    }

    // =============================
    // CUSTOMER DASHBOARD
    // =============================
    public static void customerDashboard(int customerId) {

        int choice;

        do {
            System.out.println("\n===== CUSTOMER DASHBOARD =====");
            System.out.println("1. Place Order");
            System.out.println("2. View My Orders");
            System.out.println("3. Logout");

            choice = safeReadInt("Enter choice: ");

            switch (choice) {
                case 1: placeOrder(customerId); break;
                case 2: viewMyOrders(customerId); break;
                case 3: break;
            }

        } while (choice != 3);
    }

    // =============================
    // PLACE ORDER
    // =============================
    public static void placeOrder(int customerId) {

        List<Map<String, Object>> services =
            db.fetchRecords("SELECT * FROM tbl_services");

        if (services.isEmpty()) {
            System.out.println("❌ No services available!");
            return;
        }

        for (Map<String, Object> s : services) {
            System.out.println(
                s.get("s_id") + " | " +
                s.get("s_description") + " | ₱" +
                s.get("s_price")
            );
        }

        int sid = safeReadInt("Enter Service ID: ");
        double kg = Double.parseDouble(safeReadLine("KG: "));

        List<Map<String, Object>> p =
            db.fetchRecords("SELECT s_price FROM tbl_services WHERE s_id = ?", sid);

        if (p.isEmpty()) {
            System.out.println("❌ Service not found.");
            return;
        }

        double price = ((Number) p.get(0).get("s_price")).doubleValue();
        double total = price * kg;

        db.addRecord(
            "INSERT INTO tbl_LaundryOrder (lo_customerid, lo_serviceid, lo_orderdate, lo_weightkg, lo_totalamount, lo_status) " +
            "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, 'Pending')",
            customerId, sid, kg, total
        );

        System.out.println("✔ Order placed! Total = ₱" + total);
    }

    // =============================
    // VIEW CUSTOMER ORDERS
    // =============================
    public static void viewMyOrders(int customerId) {

        String sql =
            "SELECT o.lo_orderid AS ID, s.s_description AS Service, o.lo_orderdate AS Date, " +
            "o.lo_weightkg AS KG, o.lo_totalamount AS Total, o.lo_status AS Status " +
            "FROM tbl_LaundryOrder o " +
            "JOIN tbl_services s ON o.lo_serviceid = s.s_id " +
            "WHERE o.lo_customerid = ?";

        List<Map<String, Object>> orders = db.fetchRecords(sql, customerId);

        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        }

        for (Map<String, Object> o : orders) {
            System.out.println(
                "ID: " + o.get("ID") +
                " | " + o.get("Service") +
                " | KG: " + o.get("KG") +
                " | ₱" + o.get("Total") +
                " | " + o.get("Status")
            );
        }
    }

    // =============================
    // MANAGE USERS
    // =============================
    public static void manageUsers() {

        List<Map<String, Object>> users =
            db.fetchRecords("SELECT * FROM tbl_users");

        for (Map<String, Object> u : users) {
            System.out.println(
                u.get("u_id") + " | " +
                u.get("u_name") + " | " +
                u.get("u_type") + " | " +
                u.get("u_status")
            );
        }
    }

    // =============================
    // VIEW ALL ORDERS
    // =============================
    public static void viewAllOrders() {

        String sql =
            "SELECT o.lo_orderid AS ID, u.u_name AS Customer, s.s_description AS Service, " +
            "o.lo_weightkg AS KG, o.lo_totalamount AS Total, o.lo_status AS Status " +
            "FROM tbl_LaundryOrder o " +
            "JOIN tbl_users u ON o.lo_customerid = u.u_id " +
            "JOIN tbl_services s ON o.lo_serviceid = s.s_id";

        List<Map<String, Object>> orders = db.fetchRecords(sql);

        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        for (Map<String, Object> o : orders) {
            System.out.println(
                "ID: " + o.get("ID") +
                " | " + o.get("Customer") +
                " | " + o.get("Service") +
                " | KG: " + o.get("KG") +
                " | ₱" + o.get("Total") +
                " | " + o.get("Status")
            );
        }
    }

    // =============================
    // UPDATE ORDER STATUS
    // =============================
    public static void updateOrderStatus() {

        int id = safeReadInt("Order ID: ");
        String status = safeReadLine("New Status: ");

        db.updateRecord(
            "UPDATE tbl_LaundryOrder SET lo_status = ? WHERE lo_orderid = ?",
            status, id
        );

        System.out.println("✔ Status Updated!");
    }
}

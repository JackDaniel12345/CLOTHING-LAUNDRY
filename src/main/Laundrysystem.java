package Main;

import java.util.*;       // for List, Map, Scanner
import config.config;     // import your database helper

public class Laundrysystem {

    static Scanner sc = new Scanner(System.in);
    static config db = new config();  // Use config class instance

    public static void main(String[] args) {
        int choice;
        System.out.println("===== CLOTHING LAUNDRY SERVICE TRACKER =====");

        do {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("👋 Exiting...");
                    break;
                default:
                    System.out.println("❌ Invalid choice!");
                    break;
            }

        } while (choice != 3);
    }

    // ================================
    // REGISTER FUNCTION (UPDATED)
    // ================================
    public static void register() {
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.next();
        System.out.print("Enter Password: ");
        String pass = sc.next();
        System.out.print("Enter Type (Admin / Customer / Staff): ");
        String type = sc.next();

        String hashedPass = config.hashPassword(pass);
        String status = "Pending";

        // Automatically approve Admins and redirect to dashboard
        if (type.equalsIgnoreCase("Admin")) {
            status = "Approved";
        }

        String sql = "INSERT INTO tbl_users (u_name, u_email, u_pass, u_type, u_status) VALUES (?, ?, ?, ?, ?)";
        db.addRecord(sql, name, email, hashedPass, type, status);

        System.out.println("\n✅ Registration successful!");

        // Immediately go to Admin Dashboard after registration
        if (type.equalsIgnoreCase("Admin") && status.equalsIgnoreCase("Approved")) {
            System.out.println("👑 Welcome, Admin! Redirecting to your dashboard...");
            adminDashboard();
        }
    }

    // ================================
    // LOGIN FUNCTION
    // ================================
    public static void login() {
        System.out.print("Enter Email: ");
        String email = sc.next();
        System.out.print("Enter Password: ");
        String pass = sc.next();

        String hashedPass = config.hashPassword(pass);
        String sql = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
        List<Map<String, Object>> result = db.fetchRecords(sql, email, hashedPass);

        if (!result.isEmpty()) {
            Map<String, Object> user = result.get(0);
            String type = (String) user.get("u_type");
            String status = (String) user.get("u_status");

            if (status.equalsIgnoreCase("Pending")) {
                System.out.println("⚠️ Account is Pending. Please contact the Admin!");
                return;
            }

            System.out.println("\n✅ Login successful!");
            System.out.println("Welcome, " + user.get("u_name") + " (" + type + ")");

            switch (type.toLowerCase()) {
                case "admin":
                    adminDashboard();
                    break;
                case "staff":
                    staffDashboard();
                    break;
                case "customer":
                    customerDashboard((int) user.get("u_id"));
                    break;
                default:
                    System.out.println("Unknown user type!");
                    break;
            }

        } else {
            System.out.println("\n❌ Invalid email or password.");
        }
    }

    // ================================
    // ADMIN DASHBOARD
    // ================================
    public static void adminDashboard() {
        int choice;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Manage Users");
            System.out.println("2. Approve Pending Accounts");
            System.out.println("3. View Laundry Orders");
            System.out.println("4. Update Laundry Order Status");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    approvePendingAccounts();
                    break;
                case 3:
                    viewLaundryOrders();
                    break;
                case 4:
                    updateOrderStatus();
                    break;
                case 5:
                    System.out.println("👋 Logging out...");
                    break;
                default:
                    System.out.println("❌ Invalid choice! Please try again.");
                    break;
            }

        } while (choice != 5);
    }

    // ================================
    // STAFF DASHBOARD
    // ================================
    public static void staffDashboard() {
        int choice;
        do {
            System.out.println("\n===== STAFF DASHBOARD =====");
            System.out.println("1. View Laundry Orders");
            System.out.println("2. Update Laundry Order Status");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewLaundryOrders();
                    break;
                case 2:
                    updateOrderStatus();
                    break;
                case 3:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

        } while (choice != 3);
    }

    // ================================
    // CUSTOMER DASHBOARD
    // ================================
    public static void customerDashboard(int userId) {
        System.out.println("\n===== CUSTOMER DASHBOARD =====");
        System.out.println("(Feature: Place Orders — coming soon)");
    }

    // ================================
    // APPROVE PENDING ACCOUNTS
    // ================================
    public static void approvePendingAccounts() {
        String sql = "SELECT * FROM tbl_users WHERE u_status = 'Pending'";
        List<Map<String, Object>> pending = db.fetchRecords(sql);

        if (pending.isEmpty()) {
            System.out.println("No pending accounts found!");
            return;
        }

        for (Map<String, Object> user : pending) {
            System.out.println(user.get("u_id") + " - " + user.get("u_name") + " (" + user.get("u_type") + ")");
        }

        System.out.print("Enter user ID to approve: ");
        int id = sc.nextInt();
        db.updateRecord("UPDATE tbl_users SET u_status = 'Approved' WHERE u_id = ?", id);
    }

    // ================================
    // MANAGE USERS
    // ================================
    public static void manageUsers() {
        List<Map<String, Object>> users = db.fetchRecords("SELECT * FROM tbl_users");
        for (Map<String, Object> user : users) {
            System.out.println(user.get("u_id") + " | " + user.get("u_name") + " | " +
                    user.get("u_type") + " | " + user.get("u_status"));
        }
    }

    // ================================
    // VIEW LAUNDRY ORDERS
    // ================================
    public static void viewLaundryOrders() {
        String sql =
            "SELECT o.lo_orderid AS Order_ID, " +
            "u.u_name AS Customer, " +
            "s.s_desc AS Service, " +
            "o.lo_orderdate AS Order_Date, " +
            "o.lo_weightkg AS Weight_KG, " +
            "o.lo_totalamount AS Total_Amount, " +
            "o.lo_status AS Status " +
            "FROM tbl_LaundryOrder o " +
            "JOIN tbl_users u ON o.lo_customerid = u.u_id " +
            "JOIN tbl_services s ON o.lo_serviceid = s.s_id";

        List<Map<String, Object>> orders = db.fetchRecords(sql);

        if (orders == null || orders.isEmpty()) {
            System.out.println("No laundry orders found!");
            return;
        }

        System.out.println("\n===== LAUNDRY ORDERS =====");
        for (Map<String, Object> order : orders) {
            System.out.printf(
                "ID: %s | Customer: %s | Service: %s | Date: %s | Weight: %s | Total: %s | Status: %s%n",
                order.get("Order_ID"),
                order.get("Customer"),
                order.get("Service"),
                order.get("Order_Date"),
                order.get("Weight_KG"),
                order.get("Total_Amount"),
                order.get("Status")
            );
        }
    }

    // ================================
    // UPDATE ORDER STATUS
    // ================================
    public static void updateOrderStatus() {
        System.out.print("Enter Order ID: ");
        int id = sc.nextInt();
        System.out.print("Enter New Status: ");
        String status = sc.next();
        db.updateRecord("UPDATE tbl_LaundryOrder SET lo_status = ? WHERE lo_orderid = ?", status, id);
    }
}

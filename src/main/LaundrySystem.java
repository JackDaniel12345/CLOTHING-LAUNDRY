package Main;

import config.config;
import java.util.*;

public class LaundrySystem {

    static config conf = new config();
    static Scanner sc = new Scanner(System.in);
    static int loggedInUserId = -1; // store the current logged-in customer

    public static void main(String[] args) {
        conf.connectDB();
        int choice;
        char cont;

        do {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("👋 Thank you! Program ended.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
            }

            System.out.print("\nDo you want to continue? (Y/N): ");
            cont = sc.next().charAt(0);
        } while (cont == 'Y' || cont == 'y');
    }

    // =============================
    // LOGIN SYSTEM
    // =============================
    public static void login() {
        System.out.print("Enter Email: ");
        String em = sc.next();
        System.out.print("Enter Password: ");
        String pas = sc.next();

        String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
        List<Map<String, Object>> result = conf.fetchRecords(qry, em, pas);

        if (result.isEmpty()) {
            System.out.println("❌ INVALID CREDENTIALS");
        } else {
            Map<String, Object> user = result.get(0);
            String stat = user.get("u_status").toString();
            String type = user.get("u_type").toString();

            if (stat.equalsIgnoreCase("Pending")) {
                System.out.println("⚠️ Account is Pending. Please contact the Admin!");
            } else {
                loggedInUserId = Integer.parseInt(user.get("u_id").toString());
                System.out.println("✅ LOGIN SUCCESSFUL!");

                switch (type.toLowerCase()) {
                    case "admin":
                        adminDashboard();
                        break;
                    case "staff":
                        staffDashboard();
                        break;
                    case "customer":
                        customerDashboard();
                        break;
                    default:
                        System.out.println("⚠️ Unknown user type!");
                }
            }
        }
    }

    // =============================
    // REGISTER SYSTEM
    // =============================
    public static void register() {
        System.out.print("Enter Name: ");
        sc.nextLine(); // clear buffer
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.next();

        // Check for duplicate email
        while (true) {
            String checkQry = "SELECT * FROM tbl_users WHERE u_email = ?";
            List<Map<String, Object>> checkResult = conf.fetchRecords(checkQry, email);
            if (checkResult.isEmpty()) break;
            System.out.print("⚠️ Email already exists, enter another email: ");
            email = sc.next();
        }

        System.out.println("Select User Type:");
        System.out.println("1 - Admin");
        System.out.println("2 - Staff");
        System.out.println("3 - Customer");
        System.out.print("Enter choice: ");
        int typeChoice = sc.nextInt();

        while (typeChoice < 1 || typeChoice > 3) {
            System.out.print("Invalid choice! Choose between 1-3: ");
            typeChoice = sc.nextInt();
        }

        String tp = (typeChoice == 1) ? "Admin"
                   : (typeChoice == 2) ? "Staff"
                   : "Customer";

        System.out.print("Enter Password: ");
        String pass = sc.next();

        String sql = "INSERT INTO tbl_users (u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
        conf.addRecord(sql, name, email, tp, "Pending", pass);
        System.out.println("✅ Registration successful! Await admin approval.");
    }

    // =============================
    // ADMIN DASHBOARD
    // =============================
    public static void adminDashboard() {
        int choice;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Manage Users");
            System.out.println("2. Approve Pending Accounts");
            System.out.println("3. Logout");
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
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 3);
    }

    // Manage Users (View, Update, Delete)
    public static void manageUsers() {
        int userChoice;
        do {
            System.out.println("\n===== USER MANAGEMENT =====");
            System.out.println("1. View Users");
            System.out.println("2. Update User");
            System.out.println("3. Delete User");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");
            userChoice = sc.nextInt();

            switch (userChoice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    viewUsers();
                    updateUser();
                    break;
                case 3:
                    viewUsers();
                    deleteUser();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (userChoice != 4);
    }

    public static void viewUsers() {
        String qry = "SELECT * FROM tbl_users";
        String[] hdrs = {"ID", "Name", "Email", "Phone", "Address", "Registered", "Type", "Status"};
        String[] clms = {"u_id", "u_name", "u_email", "u_phonenumber", "u_address", "u_registereddate", "u_type", "u_status"};
        conf.viewRecords(qry, hdrs, clms);
    }

    public static void updateUser() {
        System.out.print("Enter User ID to Update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("New Name: ");
        String name = sc.nextLine();
        System.out.print("New Email: ");
        String email = sc.nextLine();
        System.out.print("New Phone: ");
        String phone = sc.nextLine();
        System.out.print("New Address: ");
        String address = sc.nextLine();
        System.out.print("New Registered Date: ");
        String regDate = sc.nextLine();
        System.out.print("New Type (Admin/Staff/Customer): ");
        String type = sc.nextLine();
        System.out.print("New Status (Approved/Pending): ");
        String status = sc.nextLine();
        System.out.print("New Password: ");
        String pass = sc.nextLine();

        String qry = "UPDATE tbl_users SET u_name=?, u_email=?, u_phonenumber=?, u_address=?, u_registereddate=?, u_type=?, u_status=?, u_pass=? WHERE u_id=?";
        conf.updateRecord(qry, name, email, phone, address, regDate, type, status, pass, id);
        System.out.println("✅ User updated successfully!");
    }

    public static void deleteUser() {
        System.out.print("Enter User ID to Delete: ");
        int id = sc.nextInt();

        String qry = "DELETE FROM tbl_users WHERE u_id=?";
        conf.deleteRecord(qry, id);
        System.out.println("🗑️ User deleted successfully!");
    }

    public static void approvePendingAccounts() {
        System.out.println("\n===== PENDING ACCOUNTS =====");
        String qry = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_users WHERE u_status = 'Pending'";
        String[] hdrs = {"ID", "Name", "Email", "Type", "Status"};
        String[] clms = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        conf.viewRecords(qry, hdrs, clms);

        System.out.print("Enter ID to Approve (or 0 to cancel): ");
        int id = sc.nextInt();

        if (id != 0) {
            String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
            conf.updateRecord(sql, "Approved", id);
            System.out.println("✅ User ID " + id + " has been approved!");
        } else {
            System.out.println("Approval canceled.");
        }
    }

    // =============================
    // STAFF DASHBOARD (UPDATED)
    // =============================
    public static void staffDashboard() {
        int choice;
        do {
            System.out.println("\n===== STAFF DASHBOARD =====");
            System.out.println("1. View Users");
            System.out.println("2. View Laundry Orders");
            System.out.println("3. Update Laundry Order Status");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    viewLaundryOrders();
                    break;
                case 3:
                    updateOrderStatus();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } while (choice != 4);
    }

    public static void viewLaundryOrders() {
        String qry = "SELECT o.order_id, u.u_name AS Customer, s.service_name AS Service, o.order_date, o.weight_kg, o.total_amount, o.status " +
                     "FROM tbl_laundry_orders o " +
                     "JOIN tbl_users u ON o.customer_id = u.u_id " +
                     "JOIN tbl_services s ON o.service_id = s.service_id";
        String[] hdrs = {"Order ID", "Customer", "Service", "Date", "Weight (kg)", "Total", "Status"};
        String[] clms = {"order_id", "Customer", "Service", "order_date", "weight_kg", "total_amount", "status"};
        conf.viewRecords(qry, hdrs, clms);
    }

    public static void updateOrderStatus() {
        viewLaundryOrders();
        System.out.print("\nEnter Order ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter new status (Pending/In Progress/Completed/Delivered): ");
        String status = sc.nextLine();

        String qry = "UPDATE tbl_laundry_orders SET status = ? WHERE order_id = ?";
        conf.updateRecord(qry, status, id);
        System.out.println("✅ Order status updated successfully!");
    }

    // =============================
    // CUSTOMER DASHBOARD (UPDATED)
    // =============================
    public static void customerDashboard() {
        int choice;
        do {
            System.out.println("\n===== CUSTOMER DASHBOARD =====");
            System.out.println("1. View Available Services");
            System.out.println("2. Place Laundry Order");
            System.out.println("3. View My Orders");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewServices();
                    break;
                case 2:
                    placeLaundryOrder();
                    break;
                case 3:
                    viewMyOrders();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } while (choice != 4);
    }

    // CUSTOMER FUNCTIONS
    public static void viewServices() {
        String qry = "SELECT * FROM tbl_services WHERE s_status = 'Available'";
        String[] hdrs = {"Service ID", "Name", "Description", "Price"};
        String[] clms = {"service_id", "service_name", "service_description", "service_price"};
        conf.viewRecords(qry, hdrs, clms);
    }

    public static void placeLaundryOrder() {
        viewServices();
        System.out.print("\nEnter Service ID: ");
        int serviceId = sc.nextInt();
        System.out.print("Enter Weight (kg): ");
        double weight = sc.nextDouble();

        // Get service price
        String qry = "SELECT service_price FROM tbl_services WHERE service_id = ?";
        List<Map<String, Object>> res = conf.fetchRecords(qry, serviceId);
        if (res.isEmpty()) {
            System.out.println("❌ Invalid service!");
            return;
        }

        double pricePerKg = Double.parseDouble(res.get(0).get("service_price").toString());
        double total = pricePerKg * weight;

        String sql = "INSERT INTO tbl_laundry_orders (customer_id, service_id, order_date, weight_kg, total_amount, status) VALUES (?, ?, datetime('now'), ?, ?, ?)";
        conf.addRecord(sql, loggedInUserId, serviceId, weight, total, "Pending");

        System.out.println("✅ Laundry order placed successfully! Total: ₱" + total);
    }

    public static void viewMyOrders() {
        String qry = "SELECT o.order_id, s.service_name AS Service, o.order_date, o.weight_kg, o.total_amount, o.status " +
                     "FROM tbl_laundry_orders o " +
                     "JOIN tbl_services s ON o.service_id = s.service_id " +
                     "WHERE o.customer_id = ?";
        String[] hdrs = {"Order ID", "Service", "Date", "Weight (kg)", "Total", "Status"};
        String[] clms = {"order_id", "Service", "order_date", "weight_kg", "total_amount", "status"};
        conf.viewRecords(qry, hdrs, clms, loggedInUserId);
    }
}

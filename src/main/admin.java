
import config.config;
import java.sql.*;
import java.util.*;

public class admin {
    static Scanner sc = new Scanner(System.in);
    static config conf = new config();

    public static void showAdminDashboard() {
        int choice;

        do {
            System.out.println("\n====== 👑 ADMIN DASHBOARD ======");
            System.out.println("1. View All Users");
            System.out.println("2. Approve Pending Accounts");
            System.out.println("3. Delete User");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    approveAccounts();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    System.out.println("Logging out... 👋");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 4);
    }

    // =============================
    // OPTION 1: VIEW ALL USERS
    // =============================
    public static void viewAllUsers() {
        String query = "SELECT * FROM tbl_users";
        try {
            conf.connect();
            ResultSet rs = conf.statement.executeQuery(query);

            System.out.println("\n------------------------------------------------------------------------------------------");
            System.out.printf("%-5s %-20s %-25s %-10s %-10s%n", "ID", "Name", "Email", "Type", "Status");
            System.out.println("------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-25s %-10s %-10s%n",
                        rs.getInt("u_id"),
                        rs.getString("u_name"),
                        rs.getString("u_email"),
                        rs.getString("u_type"),
                        rs.getString("u_status"));
            }
            System.out.println("------------------------------------------------------------------------------------------");

            conf.disconnect();
        } catch (SQLException e) {
            System.out.println("Error viewing users: " + e.getMessage());
        }
    }

    // =============================
    // OPTION 2: APPROVE ACCOUNTS
    // =============================
    public static void approveAccounts() {
        viewAllUsers(); // display users first

        System.out.print("\nEnter ID of user to approve: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "UPDATE tbl_users SET u_status = 'Approved' WHERE u_id = ?";
        try {
            conf.connect();
            PreparedStatement pstmt = conf.connection.prepareStatement(query);
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ User ID " + id + " has been approved successfully!");
            } else {
                System.out.println("⚠️ User ID not found.");
            }

            conf.disconnect();
        } catch (SQLException e) {
            System.out.println("Error approving account: " + e.getMessage());
        }
    }

    // =============================
    // OPTION 3: DELETE USER
    // =============================
    public static void deleteUser() {
        viewAllUsers(); // display users first

        System.out.print("\nEnter ID of user to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "DELETE FROM tbl_users WHERE u_id = ?";
        try {
            conf.connect();
            PreparedStatement pstmt = conf.connection.prepareStatement(query);
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("🗑️ User ID " + id + " has been deleted successfully!");
            } else {
                System.out.println("⚠️ User ID not found.");
            }

            conf.disconnect();
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}

}

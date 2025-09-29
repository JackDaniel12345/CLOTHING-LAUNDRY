package main;

import testappv2a.config;
import java.util.Scanner;

public class LaundrySystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String resp;
        do {
            System.out.println("\n====== LAUNDRY MANAGEMENT SYSTEM ======");
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Exit");
            
            System.out.print("Enter Action: ");
            int action = sc.nextInt();
            LaundrySystem app = new LaundrySystem();

            switch(action){
                case 1:
                    app.addUser();
                    break;              
                case 2:
                    app.viewUser();
                    break;
                case 3:
                    app.viewUser();
                    app.updateUser();
                    break;
                case 4:   
                    app.viewUser();
                    app.deleteUser();
                    break;
                case 5:
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid Option!");
                    break;
            }

            if (action == 5) break;

            System.out.print("Continue? (yes/no): ");
            resp = sc.next();
                    
        } while (resp.equalsIgnoreCase("yes"));
    }                  

    // Add User
    public void addUser() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Phone Number: ");
        String phone = sc.nextLine();
        System.out.print("Address: ");
        String address = sc.nextLine();
        System.out.print("Registered Date (YYYY-MM-DD): ");
        String regDate = sc.nextLine();
        System.out.print("Type: ");
        String type = sc.nextLine();
        
        String sql = "INSERT INTO tbl_users (u_name, u_email, u_phonenumber, u_address, u_registereddate, u_type) VALUES (?,?,?,?,?,?)";
        conf.addRecord(sql, name, email, phone, address, regDate, type);
    }

    // View Users
    public void viewUser() {
        String qry = "SELECT * FROM tbl_users";
        String[] hdrs = {"ID", "Name", "Email", "Phone Number", "Address", "Registered Date", "Type"};
        String[] clms = {"id", "u_name", "u_email", "u_phonenumber", "u_address", "u_registereddate", "u_type"};
        
        config conf = new config();
        conf.viewRecords(qry, hdrs, clms);
    }

    // Update User
    public void updateUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter User ID to Update: ");
        int id = sc.nextInt(); sc.nextLine();
        
        System.out.print("New Name: ");
        String name = sc.nextLine();
        System.out.print("New Email: ");
        String email = sc.nextLine();
        System.out.print("New Phone Number: ");
        String phone = sc.nextLine();
        System.out.print("New Address: ");
        String address = sc.nextLine();
        System.out.print("New Registered Date (YYYY-MM-DD): ");
        String regDate = sc.nextLine();
        System.out.print("New User Type: ");
        String type = sc.nextLine();
        
        String qry = "UPDATE tbl_users SET u_name=?, u_email=?, u_phonenumber=?, u_address=?, u_registereddate=?, u_type=? WHERE id=?";
        config conf = new config();
        conf.updateRecord(qry, name, email, phone, address, regDate, type, id);
    }

    // Delete User
    public void deleteUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter User ID to Delete: ");
        int id = sc.nextInt();
        
        String qry = "DELETE FROM tbl_users WHERE id=?";
        config conf = new config();
        conf.deleteRecord(qry, id);
    }
}

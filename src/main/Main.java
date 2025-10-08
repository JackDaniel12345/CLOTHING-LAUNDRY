package Main;

import config.config;
import java.util.Scanner;

public class Main {
    public static void viewUsers() {
        String Query = "SELECT * FROM tbl_users";
        
        String[] votersHeaders = {"ID", "Name", "Email", "Type", "Status"};
        String[] votersColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        config conf = new config();
        conf.viewRecords(Query, votersHeaders, votersColumns);
    }
    
    public static void main(String[] args) {
        config conf = new config();
        conf.connectDB();
        int choice;
        char cont;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter email: ");
                    String em = sc.next();
                    System.out.print("Enter Password: ");
                    String pas = sc.next();
                    
                    while (true) {
                            
                            String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
                            java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, em, pas);
                            
                            
                            if (result.isEmpty()) {
                                System.out.println("INVALID CREDENTIALS");
                                break;
                            } else {
                                java.util.Map<String, Object> user = result.get(0);
                                String stat = user.get("u_status").toString();
                                String type = user.get("u_type").toString();
                                if(stat.equals("Pending")){
                                    System.out.println("Account is Pending, Contact tha Admin!");
                                    break;
                                }else{
                                    System.out.println("LOGIN SUCCESS!");
                                    if(type.equals("Admin")){
                                        System.out.println("WELCOME TO ADMIN DASHBOARD");
                                        System.out.println("1. Approve Account!");
                                        int respo = sc.nextInt();
                                        
                                            switch(respo){
                                                case 1:
                                                    viewUsers();
                                                    System.out.print("Enter ID to Approve: ");
                                                    int ids = sc.nextInt();
                                                    
                                                    String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                                                    conf.updateRecord(sql, "Approved", ids);
                                                    break;
                                            }
                                    }else if(type.equals("Customer")){
                                        System.out.println("WELCOME TO CUSTOMER DASHBOARD");
                                        System.out.println("1. Approve Account!");
                                        int respo = sc.nextInt();
                                    }
                                    
                                    break;
                                }
                                
                            }
                        }
                    
                    break;

                case 2:
                    System.out.print("Enter user name: ");
                    String name = sc.next();
                    System.out.print("Enter user email: ");
                    String email = sc.next();
                    
                    
                        while (true) {
                            
                            String qry = "SELECT * FROM tbl_users WHERE u_email = ?";
                            java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, email);

                            if (result.isEmpty()) {
                                break;
                            } else {
                                System.out.print("Email already exists, Enter other Email: ");
                                email = sc.next();
                            }
                        }

                    
                    System.out.print("Enter user Type (1 - Admin/2 -Customer): ");
                    int type = sc.nextInt();
                        while(type > 2 || type < 1){
                            System.out.print("Invalid, choose between 1 & 2 only: ");
                            type = sc.nextInt();
                        }
                    String tp = "";
                        if(type == 1){
                            tp = "Admin";
                        }else{
                            tp = "Customer";
                        }
                    
                    System.out.print("Enter Password: ");
                    String pass = sc.next();
                    
                    String sql = "INSERT INTO tbl_users (u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
                    conf.addRecord(sql, name, email, tp, "Pending", pass);
                    break;

                case 3:
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue? (Y/N): ");
            cont = sc.next().charAt(0);

        } while (cont == 'Y' || cont == 'y');

        System.out.println("Thank you! Program ended.");
    }
}
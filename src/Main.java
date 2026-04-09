import java.sql.*;
import java.util.Scanner;

class VehicleServiceSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            // 1. Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

// 2. Create Connection
            String password = System.getenv("DB_PASSWORD");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/vehicle_db?useSSL=false&allowPublicKeyRetrieval=true",
                    "root",
                    password
            );2

            while (true) {

                System.out.println("\n===== VEHICLE SERVICE SYSTEM =====");
                System.out.println("1. Add Service Record");
                System.out.println("2. View All Records (Sorted by Date)");
                System.out.println("3. Search by Vehicle Number");
                System.out.println("4. Total Service Cost");
                System.out.println("5. Service-wise Cost Breakdown");
                System.out.println("6. Delete Record");
                System.out.println("7. Exit");

                System.out.print("Enter choice: ");

                if (!sc.hasNextInt()) {
                    System.out.println("❌ Invalid input!");
                    sc.next();
                    continue;
                }

                int ch = sc.nextInt();

                switch (ch) {

                    // ✅ 1. ADD RECORD
                    case 1:
                        sc.nextLine();

                        System.out.print("Customer Name: ");
                        String name = sc.nextLine();

                        System.out.print("Vehicle No: ");
                        String vno = sc.nextLine().toUpperCase();

                        System.out.print("Service Type: ");
                        String type = sc.nextLine();

                        System.out.print("Cost: ");
                        double cost = sc.nextDouble();

                        sc.nextLine();
                        System.out.print("Service Date (YYYY-MM-DD): ");
                        String date = sc.nextLine();

                        PreparedStatement ps1 = con.prepareStatement(
                                "INSERT INTO service(customer_name, vehicle_no, service_type, cost, service_date) VALUES(?,?,?,?,?)"
                        );

                        ps1.setString(1, name);
                        ps1.setString(2, vno);
                        ps1.setString(3, type);
                        ps1.setDouble(4, cost);
                        ps1.setString(5, date);

                        ps1.executeUpdate();
                        System.out.println("✅ Record Added!");
                        break;

                    // ✅ 2. VIEW RECORDS
                    case 2:
                        Statement st2 = con.createStatement();

                        ResultSet rs2 = st2.executeQuery(
                                "SELECT * FROM service ORDER BY service_date DESC"
                        );

                        System.out.println("\n--- Service Records ---");

                        while (rs2.next()) {
                            System.out.println(
                                    rs2.getInt("id") + " | " +
                                            rs2.getString("customer_name") + " | " +
                                            rs2.getString("vehicle_no") + " | " +
                                            rs2.getString("service_type") + " | " +
                                            rs2.getDouble("cost") + " | " +
                                            rs2.getDate("service_date")
                            );
                        }
                        break;

                    // ✅ 3. SEARCH
                    case 3:
                        sc.nextLine();

                        System.out.print("Enter Vehicle No: ");
                        String searchVno = sc.nextLine().toUpperCase();

                        PreparedStatement ps3 = con.prepareStatement(
                                "SELECT * FROM service WHERE vehicle_no=?"
                        );

                        ps3.setString(1, searchVno);

                        ResultSet rs3 = ps3.executeQuery();

                        boolean found = false;

                        while (rs3.next()) {
                            found = true;
                            System.out.println(
                                    rs3.getString("customer_name") + " | " +
                                            rs3.getString("service_type") + " | " +
                                            rs3.getDouble("cost")
                            );
                        }

                        if (!found)
                            System.out.println("❌ No records found!");
                        break;

                    // ✅ 4. TOTAL COST
                    case 4:
                        Statement st4 = con.createStatement();
                        ResultSet rs4 = st4.executeQuery("SELECT SUM(cost) FROM service");

                        if (rs4.next()) {
                            System.out.println("💰 Total Cost: " + rs4.getDouble(1));
                        }
                        break;

                    // ✅ 5. SERVICE-WISE BREAKDOWN
                    case 5:
                        Statement st5 = con.createStatement();

                        ResultSet rs5 = st5.executeQuery(
                                "SELECT service_type, SUM(cost) FROM service GROUP BY service_type"
                        );

                        System.out.println("\n--- Service-wise Report ---");

                        while (rs5.next()) {
                            System.out.println(
                                    rs5.getString(1) + " → " + rs5.getDouble(2)
                            );
                        }
                        break;

                    // ✅ 6. DELETE
                    case 6:
                        System.out.print("Enter ID to delete: ");
                        int id = sc.nextInt();

                        PreparedStatement ps6 = con.prepareStatement(
                                "DELETE FROM service WHERE id=?"
                        );

                        ps6.setInt(1, id);

                        int rows = ps6.executeUpdate();

                        if (rows > 0)
                            System.out.println("✅ Deleted!");
                        else
                            System.out.println("❌ ID not found!");
                        break;

                    // ✅ 7. EXIT
                    case 7:
                        con.close();
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("❌ Invalid choice!");
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
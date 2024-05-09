package database_insert;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aleks
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InitializeWithCar {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, ParseException {
        String url = "jdbc:mysql://localhost";
        String databaseName = "test";
        int port = 3306;
        String username = "root";
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection con = DriverManager.getConnection(
                url + ":" + port + "/" + databaseName + "?characterEncoding=UTF-8", username, password);
        System.out.println("Connected to the database!");

        // Insert Car into Vehicles table
        insertCar(con);

        // Perform a rental
        rentCar(con, "John Doe", "2024-01-15", "2024-01-20");
    }

    private static void insertCar(Connection con) throws SQLException {
        String type = "Car";
        String brand = "Lamborghini";
        String model = "Huracan";
        String color = "Gold";
        double autonomy = 500.0;
        String registrationNumber = "ABC123";
        String carType = "Sports Car";
        String rentable = "yes";
        int passengers = 2;
        double rentPrice = 500.0;
        double insurancePrice = 20.0;
        String insertQuery = "INSERT INTO Vehicles (type, brand, model, color, autonomy, rentable, registration_number, "
                + "car_type, passengers, rent_price, insurance_price) VALUES "
                + "('" + type + "', '" + brand + "', '" + model + "', '" + color + "', " + autonomy + ", 'Yes', '"
                + registrationNumber + "', '" + carType + "', " + passengers + ", " + rentPrice + ", " + insurancePrice + ")";

        try ( Statement statement = con.createStatement()) {
            int rowsAffected = statement.executeUpdate(insertQuery);

            if (rowsAffected > 0) {
                System.out.println("New vehicle inserted successfully!");
            } else {
                System.out.println("Failed to insert the new vehicle.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void rentCar(Connection con, String customerName, String fromDate, String toDate) throws SQLException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateObj = sdf.parse(fromDate);
        Date toDateObj = sdf.parse(toDate);

        String registrationNumber = "ABC123"; // Replace with the actual registration number of the car

        double totalCost = 500;

        String insertRentingQuery = "INSERT INTO Renting (name, fromdate, todate, total_cost, type, registration_number) VALUES "
                + "('" + customerName + "', '" + fromDate + "', '" + toDate + "', " + totalCost + ", 'Car', '" + registrationNumber + "')";

        try ( Statement statement = con.createStatement()) {
            int rowsAffected = statement.executeUpdate(insertRentingQuery);

            if (rowsAffected > 0) {
                System.out.println("Rental record inserted successfully!");

                String updateVehicleQuery = "UPDATE Vehicles SET rentable = 'No' WHERE registration_number = '" + registrationNumber + "'";
                int updateRowsAffected = statement.executeUpdate(updateVehicleQuery);

                if (updateRowsAffected > 0) {
                    System.out.println("Vehicles table updated successfully!");
                } else {
                    System.out.println("Failed to update the Vehicles table.");
                }
            } else {
                System.out.println("Failed to insert the rental record.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

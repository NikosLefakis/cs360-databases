package database_insert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class createVehicleTable {

    public static void main(String[] args) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost";
        int port = 3306;
        String username = "root";
        String password = "";
        String databaseName = "test";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url + ":" + port, username, password);
            System.out.println("Connected to the database!");
            Statement statement = con.createStatement();

            /* Create Database */
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            statement.executeUpdate(createDatabaseQuery);
            System.out.println("Database created successfully!");
            statement.executeUpdate("USE " + databaseName);

            /* Create Vehicle Table */
            String createTableQuery = "CREATE TABLE IF NOT EXISTS Vehicles ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "type VARCHAR(30), "
                    + "brand VARCHAR(30), "
                    + "model VARCHAR(30), "
                    + "color VARCHAR(30), "
                    + "autonomy DECIMAL(10,2), "
                    + "rentable VARCHAR(30), "
                    + "registration_number VARCHAR(30), "
                    + "car_type VARCHAR(30), "
                    + "passengers INT, "
                    + "rent_price DECIMAL(10,2), "
                    + "insurance_price DECIMAL(10,2))";
            statement.executeUpdate(createTableQuery);
            System.out.println("Vehicles created successfully!");

            /* Create Unavailable Table */
            String createAnotherTableQuery = "CREATE TABLE IF NOT EXISTS Unavailable ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "vehicle_id INT, "
                    + "type VARCHAR(30), "
                    + "reason VARCHAR(30), "
                    + "fromdate DATE, "
                    + "todate DATE, "
                    + "replacedby VARCHAR(30))";
            statement.executeUpdate(createAnotherTableQuery);
            System.out.println("Unavailable created successfully!");

            /* Create Renting Table */
            String createBookingsTableQuery = "CREATE TABLE IF NOT EXISTS Renting ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "renter_id INT, "
                    + "driver_name VARCHAR(30), "
                    + "vehicle_id INT, "
                    + "fromdate DATE, "
                    + "todate DATE, "
                    + "total_cost DECIMAL(10,2), "
                    + "type VARCHAR(30), "
                    + "hasPaid VARCHAR(30))";
            statement.executeUpdate(createBookingsTableQuery);
            System.out.println("Renting created successfully!");

            /* Create Customer Table */
            String createCustomersTableQuery = "CREATE TABLE IF NOT EXISTS Customer ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "firstname VARCHAR(30), "
                    + "lastname VARCHAR(30), "
                    + "birthdate VARCHAR(30), "
                    + "address VARCHAR(30), "
                    + "card_number VARCHAR(16), "
                    + "cardholder_name VARCHAR(30), "
                    + "exp_date VARCHAR(7), "
                    + "CVV int, "
                    + "licence_num VARCHAR(30))";
            statement.executeUpdate(createCustomersTableQuery);
            System.out.println("Customer created successfully!");
            con.close();
        } catch (SQLException e) {
            System.out.println("Error at createVehicleTable");
        }
    }
}

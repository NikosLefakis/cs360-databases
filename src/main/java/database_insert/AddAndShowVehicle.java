/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package database_insert;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aleks
 */
@WebServlet("/addandshowvehicle")
public class AddAndShowVehicle extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AddVehicle</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddVehicle at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            String url = "jdbc:mysql://localhost";
            String databaseName = "test";
            int port = 3306;
            String username = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = null;
            try {
                con = DriverManager.getConnection(
                        url + ":" + port + "/" + databaseName + "?characterEncoding=UTF-8", username, password);
            } catch (SQLException ex) {
                Logger.getLogger(AddAndShowVehicle.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Connected to the database!");
            Statement stmt = con.createStatement();

            StringBuilder allTables = new StringBuilder(); // Accumulate all table content

            // Query for Cars
            try ( Statement carStmt = con.createStatement();  ResultSet carResultSet = carStmt.executeQuery("SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'car'")) {
                allTables.append(generateTable("Cars", carResultSet));
            }

            // Query for Scooters
            try ( Statement scooterStmt = con.createStatement();  ResultSet scooterResultSet = scooterStmt.executeQuery("SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'scooter'")) {
                allTables.append(generateTable("Scooters", scooterResultSet));
            }

            // Query for Motorbikes
            try ( Statement motorbikeStmt = con.createStatement();  ResultSet motorbikeResultSet = motorbikeStmt.executeQuery("SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'motorbike'")) {
                allTables.append(generateTable("Motorbikes", motorbikeResultSet));
            }

            // Query for Bikes
            try ( Statement bikeStmt = con.createStatement();  ResultSet bikeResultSet = bikeStmt.executeQuery("SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'bike'")) {
                allTables.append(generateTable("Bikes", bikeResultSet));
            }

            // Print the accumulated tables
            out.println("<html><head><style>");
            out.println("body { display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; }");
            out.println("#RentableDetails { width: 80%; padding: 20px; border: 1px solid #ccc; background-color: #f9f9f9; }");
            out.println("table { width: 100%; margin-top: 20px; }");
            out.println("</style></head><body>");
            out.println("<div id='RentableDetails'>");
            out.append(allTables.toString());
            out.println("</div></body></html>");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AddAndShowVehicle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AddAndShowVehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String generateTable(String vehicleType, ResultSet resultSet) throws SQLException {
        StringBuilder tableContent = new StringBuilder();
        tableContent.append("<h2>").append(vehicleType).append("</h2>");
        tableContent.append("<table border='1'>");
        tableContent.append("<tr><th>ID</th><th>Type</th><th>Brand</th><th>Model</th><th>Color</th><th>Autonomy</th><th>Car Type</th><th>Passengers</th><th>Rent Price</th><th>Insurance Price</th></tr>");

        while (resultSet.next()) {

            generateTableRow(resultSet, tableContent);
        }

        tableContent.append("</table>");
        return tableContent.toString();
    }


    private void generateTableRow(ResultSet resultSet, StringBuilder tableContent) throws SQLException {
        tableContent.append("<tr>");
        tableContent.append("<td>").append(resultSet.getString("id")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("type")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("brand")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("model")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("color")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("autonomy")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("car_type")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("passengers")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("rent_price")).append("</td>");
        tableContent.append("<td>").append(resultSet.getString("insurance_price")).append("</td>");
        tableContent.append("</tr>");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String url = "jdbc:mysql://localhost";

            String databaseName = "test";
            int port = 3306;
            String username = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = null;
            try {
                con = DriverManager.getConnection(
                        url + ":" + port + "/" + databaseName + "?characterEncoding=UTF-8", username, password);
            } catch (SQLException ex) {
                Logger.getLogger(AddAndShowVehicle.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Connected to the database!");

            String type = request.getParameter("type");
            String brand = request.getParameter("brand");
            String model = request.getParameter("model");
            String color = request.getParameter("color");
            String autonomy = request.getParameter("autonomy");
            String registrationNumber = request.getParameter("registrationNumber");
            String carType = request.getParameter("carType");
            String rentable = request.getParameter("rentable");
            String passengers = request.getParameter("passengers");
            String rentPrice = request.getParameter("rentPrice");
            String insurancePrice = request.getParameter("insurancePrice");

            String insertQuery = "INSERT INTO Vehicles (type, brand, model, color, autonomy, rentable, registration_number, "
                    + "car_type, passengers, rent_price, insurance_price) VALUES "
                    + "('" + type + "', '" + brand + "', '" + model + "', '" + color + "', '" + autonomy + "', '" + rentable + "', '"
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
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AddAndShowVehicle.class

.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

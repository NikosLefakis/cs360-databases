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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aleks
 */
@WebServlet("/replacevehicle")
public class ReplaceVehicle extends HttpServlet {

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
            out.println("<title>Servlet ReplaceCar</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReplaceCar at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con = null;
        try {
            String url = "jdbc:mysql://localhost";
            String databaseName = "test";
            int port = 3306;
            String username = "root";
            String password = "";

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    url + ":" + port + "/" + databaseName + "?characterEncoding=UTF-8", username, password);

            // Start transaction
            con.setAutoCommit(false);

            System.out.println("Connected to the database!");

            String type = request.getParameter("type");
            String name = request.getParameter("name");
            String car_id = request.getParameter("car_id");
            String reason = request.getParameter("reason");
            String fromdate = request.getParameter("fromdate");
            String todate = request.getParameter("todate");

            // Searching for vehicle to replace the other
            String findQuery = "SELECT * FROM vehicles WHERE type = '" + type + "' AND rentable = 'Yes' LIMIT 1";

            try ( Statement statement = con.createStatement();  ResultSet resultSet = statement.executeQuery(findQuery)) {

                if (resultSet.next()) {
                    String foundCarId = resultSet.getString("id");
                    String FoundregistrationNumber = resultSet.getString("registration_number");

                    // Insert the found vehicle into the unavailable table
                    String insertQuery = "INSERT INTO unavailable (vehicle_id, type, reason, fromdate, todate, replacedby) "
                            + "VALUES ('" + foundCarId + "', '" + type + "', '" + "rented" + "', '" + fromdate + "', '" + todate + "', '" + "none" + "')";

                    int rowsAffectedInsert = statement.executeUpdate(insertQuery);

                    // Update the vehicle in the Renting table
                    String updateQueryRenting = "UPDATE renting SET registration_number = '" + FoundregistrationNumber + "' WHERE name = '" + name + "'";
                    int rowsAffectedUpdateRenting = statement.executeUpdate(updateQueryRenting);

                    // Update the vehicle in the unavailable table
                    String updateQueryUnavailable = "UPDATE unavailable SET reason = 'damaged', replacedby = '" + FoundregistrationNumber + "' WHERE vehicle_id = '" + car_id + "'";
                    int rowsAffectedUpdateUnavailable = statement.executeUpdate(updateQueryUnavailable);

                    // Update the vehicle in the vehicles table to set rentable to 'No'
                    String updateQueryVehicles = "UPDATE vehicles SET rentable = 'No' WHERE id = '" + foundCarId + "'";
                    int rowsAffectedUpdateVehicles = statement.executeUpdate(updateQueryVehicles);

                    // Commit the transaction if all updates succeed
                    con.commit();

                    System.out.println("Transaction committed successfully!");
                } else {
                    System.out.println("No matching vehicle found.");
                }

            } catch (SQLException e) {
                // Rollback the transaction in case of any exception
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
            } finally {

                if (con != null) {
                    try {
                        con.setAutoCommit(true);
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }



    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

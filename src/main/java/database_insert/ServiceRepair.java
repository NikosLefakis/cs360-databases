/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package database_insert;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
@WebServlet("/servicerepair")
public class ServiceRepair extends HttpServlet {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String url = "jdbc:mysql://localhost";
            String databaseName = "test";
            int port = 3306;
            String username = "root";
            String password = "";

            Class.forName("com.mysql.cj.jdbc.Driver");

            try ( Connection con = DriverManager.getConnection(
                    url + ":" + port + "/" + databaseName + "?characterEncoding=UTF-8", username, password)) {
                System.out.println("Connected to the database!");

                String type = request.getParameter("type");
                String id = request.getParameter("id");
                String reason = request.getParameter("reason");
                String fromdate = request.getParameter("fromdate");
                String replacedby = request.getParameter("replacedby");
                String todate = request.getParameter("todate");

                String insertQuery = "INSERT INTO unavailable (vehicle_id, type, reason, fromdate, todate, replacedby) "
                        + "VALUES ('" + id + "', '" + type + "', '" + reason + "', '" + fromdate + "', '" + todate + "', '" + replacedby + "')";

                String updateQuery = "UPDATE vehicles SET rentable = 'No' WHERE id = '" + id + "'";

                try ( Statement statement = con.createStatement()) {
                    // Execute the insert query
                    int rowsAffectedInsert = statement.executeUpdate(insertQuery);

                    if (rowsAffectedInsert > 0) {
                        System.out.println("New vehicle inserted successfully!");
                    } else {
                        System.out.println("Failed to insert the new vehicle.");
                    }

                    // Execute the update query
                    int rowsAffectedUpdate = statement.executeUpdate(updateQuery);

                    if (rowsAffectedUpdate > 0) {
                        System.out.println("Vehicle updated successfully!");
                    } else {
                        System.out.println("Failed to update the vehicle.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AddAndShowVehicle.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AddAndShowVehicle.class.getName()).log(Level.SEVERE, null, ex);
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

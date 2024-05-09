package database_insert;

import jakarta.servlet.annotation.WebServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/renting")
public class Renting extends HttpServlet {

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
            out.println("<title>Servlet Renting</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Renting at " + request.getContextPath() + "</h1>");
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
        try {
            String url = "jdbc:mysql://localhost";
            String databaseName = "test";
            int port = 3306;
            String username = "root";
            String password = "";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url + ":" + port + "/" + databaseName + "?characterEncoding=UTF-8", username, password);
            System.out.println("Connected to the database!");

            Statement statement = con.createStatement();
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            JSONObject json = new JSONObject(reader.readLine());

            // Check if there is already a renting record with the same driver's name
            String checkQuery = "SELECT * FROM renting WHERE driver_name = '" + json.getString("driver_name") + "'";
            ResultSet existingRecords = statement.executeQuery(checkQuery);

            while (existingRecords.next()) {
                // Check if the existing record has a renting date in the future
                Date existingToDate = existingRecords.getDate("todate");
                Date currentDate = new Date();
                if (existingToDate != null && existingToDate.after(currentDate)) {
                    // Driver's name already exists in the renting table with a future renting date
                    response.setStatus(409); // Conflict
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().print("{\"error\": \"Driver's name already exists in the renting records with a future renting date\"}");
                    return;
                }
            }

            // Continue with the insertion process
            String query = "INSERT INTO renting (renter_id, driver_name, vehicle_id, fromdate, todate, total_cost, type, hasPaid) "
                    + "VALUES ('" + json.getString("renter_id") + "', '" + json.getString("driver_name") + "', '"
                    + json.getString("vehicle_id") + "', '" + json.getString("from_date") + "', '" + json.getString("to_date")
                    + "', '" + json.getString("total_cost") + "', '" + json.getString("type") + "', '"
                    + json.getString("hasPaid") + "')";

            statement.executeUpdate(query);

            String updateavailable = "INSERT INTO unavailable (vehicle_id, type, reason, fromdate, todate, replacedby) "
                    + "VALUES ('" + json.getString("vehicle_id") + "', '" + json.getString("type") + "', '" + "rented" + "', '"
                    + json.getString("from_date") + "', '" + json.getString("to_date") + "', '" + "none" + "')";

            statement.executeUpdate(updateavailable);

            String updateQueryRenting = "UPDATE vehicles SET rentable = 'No' WHERE id = '"
                    + json.getString("vehicle_id") + "'";

            statement.executeUpdate(updateQueryRenting);

            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print("{\"message\": \"Customer registered successfully\"}");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Renting.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(404);
            response.getWriter().print("Error registering customer");
        } catch (SQLException ex) {
            Logger.getLogger(Renting.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(403);
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

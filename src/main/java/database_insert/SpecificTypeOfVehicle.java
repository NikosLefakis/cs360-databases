package database_insert;

import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/SpecificTypeOfVehicle")
public class SpecificTypeOfVehicle extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String url = "jdbc:mysql://localhost";
            String databaseName = "test";
            int port = 3306;
            String username = "root";
            String password = "";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SpecificTypeOfVehicle.class.getName()).log(Level.SEVERE, null, ex);
            }
            Connection con = DriverManager.getConnection(url + ":" + port + "/" + databaseName + "?characterEncoding=UTF-8", username, password);
            System.out.println("Connected to the database!");

            String vehicleType = request.getParameter("type");
            String fromdate = request.getParameter("from_date");
            String todate = request.getParameter("to_date");

            String sql = "";
            if ("car".equals(vehicleType)) {
                sql = "SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'Car'";
            } else if ("motorbike".equals(vehicleType)) {
                sql = "SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'Motorbike'";
            } else if ("bike".equals(vehicleType)) {
                sql = "SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'Bike'";
            } else if ("scooter".equals(vehicleType)) {
                sql = "SELECT * FROM vehicles WHERE rentable = 'Yes' AND type = 'Scooter'";
            }

            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            JSONArray json = new JSONArray();

            try {
                while (result.next()) {
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("id", result.getString("id"));
                    jsonobj.put("type", result.getString("type"));
                    jsonobj.put("brand", result.getString("brand"));
                    jsonobj.put("model", result.getString("model"));
                    jsonobj.put("color", result.getString("color"));
                    jsonobj.put("autonomy", result.getString("autonomy"));
                    jsonobj.put("car_type", result.getString("car_type"));
                    jsonobj.put("passengers", result.getString("passengers"));
                    jsonobj.put("rent_price", result.getString("rent_price"));
                    jsonobj.put("insurance_price", result.getString("insurance_price"));

                    json.put(jsonobj);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SpecificTypeOfVehicle.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Write the JSON response back to the client
            response.getWriter().write(json.toString());
            response.setStatus(200);

        } catch (SQLException ex) {
            Logger.getLogger(SpecificTypeOfVehicle.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500);
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

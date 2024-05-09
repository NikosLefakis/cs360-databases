package database_insert;

import java.io.IOException;
import org.json.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/registerCustomer")
public class RegisterCustomer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // processRequest(request, response);
    }

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

            String query = "INSERT INTO customer (firstname,lastname,birthdate,address,card_number,cardholder_name,exp_date,CVV,licence_num) "
                    + "VALUES ('" + json.getString("firstname") + "', '" + json.getString("lastname") + "', '" + json.getString("birthdate")
                    + "', '" + json.getString("address") + "', '" + json.getString("card_number") + "', '" + json.getString("card_holder")
                    + "', '" + json.getString("exp_date") + "', '" + json.getString("cvv") + "', '" + json.getString("licence_num") + "')";

            int check = statement.executeUpdate(query);

            if (check > 0) {
                response.setStatus(200);
                response.getWriter().print("Customer registered successfully");
            } else {
                response.setStatus(500);
                response.getWriter().print("Error registering customer");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RegisterCustomer.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(404);
            response.getWriter().print("Error registering customer");
        } catch (SQLException ex) {
            Logger.getLogger(RegisterCustomer.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(403);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}

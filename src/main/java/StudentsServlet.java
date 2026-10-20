
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/students")
public class StudentsServlet extends HttpServlet {

    private static final String DB_URL =
        "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "123456";

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try {
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection con = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = con.prepareStatement(
                    "SELECT USER_ID, FULL_NAME, EMAIL, USER_ROLE " +
                    "FROM APP_USERS WHERE USER_ROLE = 'STUDENT' " +
                    "ORDER BY USER_ID");
                 ResultSet rs = ps.executeQuery();
                 PrintWriter out = response.getWriter()) {

                out.println("<!DOCTYPE html><html><head>");
                out.println("<title>Student Details</title>");
                out.println("<style>");
                out.println("body{font-family:Arial;margin:35px;background:#f3f6fb}");
                out.println("table{border-collapse:collapse;width:100%;background:white}");
                out.println("th,td{border:1px solid #ccc;padding:10px;text-align:left}");
                out.println("th{background:#243b64;color:white}");
                out.println("input{padding:9px;margin:5px}");
                out.println("button{padding:10px;background:#243b64;color:white;border:0}");
                out.println("</style></head><body>");

                out.println("<h1>Student Details</h1>");
                out.println("<h2>Registered Students</h2>");
                out.println("<table><tr><th>ID</th><th>Name</th>");
                out.println("<th>Email</th><th>Role</th></tr>");

                while (rs.next()) {
                    out.println("<tr><td>" + rs.getInt("USER_ID") +
                        "</td><td>" + escape(rs.getString("FULL_NAME")) +
                        "</td><td>" + escape(rs.getString("EMAIL")) +
                        "</td><td>" + escape(rs.getString("USER_ROLE")) +
                        "</td></tr>");
                }

                out.println("</table>");
                out.println("<h2>Add a Student</h2>");
                out.println("<form method='post' action='students'>");
                out.println("Full name: <input name='fullName' required>");
                out.println("<br>Email: <input type='email' name='email' required>");
                out.println("<br>Password: <input type='password' name='password' required>");
                out.println("<br><button type='submit'>Add Student</button>");
                out.println("</form>");
                out.println("<p><a href='index.html'>Back to Home</a></p>");
                out.println("</body></html>");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("Could not load student details.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (name == null || email == null || password == null ||
            name.isBlank() || email.isBlank() || password.isBlank()) {
            response.sendError(400, "Please fill in all fields.");
            return;
        }

        try {
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection con = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO APP_USERS " +
                    "(FULL_NAME, EMAIL, PASSWORD_HASH, USER_ROLE) " +
                    "VALUES (?, ?, ?, 'STUDENT')")) {

                ps.setString(1, name.trim());
                ps.setString(2, email.trim());
                ps.setString(3, password);

                ps.executeUpdate();
                response.sendRedirect("students?added=1");

            } catch (SQLException e) {
                if ("23000".equals(e.getSQLState())) {
                    response.sendError(400,
                        "That email may already be registered. Try another email.");
                } else {
                    throw e;
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("Could not add student.", e);
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}

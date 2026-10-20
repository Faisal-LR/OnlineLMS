
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String URL =
        "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "123456";

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql =
            "SELECT USER_ID, FULL_NAME, USER_ROLE " +
            "FROM APP_USERS WHERE EMAIL = ? AND PASSWORD_HASH = ?";

        try {
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection con =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, email);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        HttpSession oldSession = request.getSession(false);
                        if (oldSession != null) {
                            oldSession.invalidate();
                        }

                        HttpSession session = request.getSession(true);
                        session.setAttribute("userId", rs.getInt("USER_ID"));
                        session.setAttribute("fullName", rs.getString("FULL_NAME"));
                        session.setAttribute("userRole", rs.getString("USER_ROLE"));

                        response.sendRedirect("dashboard");
                    } else {
                        response.sendRedirect("login.html?error=1");
                    }
                }
            }
        } catch (Exception e) {
            getServletContext().log("LMS login error", e);
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Login could not be completed. Please check the server logs."
            );
        }
    }
}

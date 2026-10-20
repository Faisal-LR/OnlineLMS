
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/progress")
public class ProgressServlet extends HttpServlet {

    private static final String URL =
        "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "123456";

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String email = request.getParameter("email");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>My Learning Progress</title>");
            out.println("<style>");
            out.println("body{font-family:Arial;background:#f3f6fb;color:#243047;padding:24px}");
            out.println("main{max-width:700px;margin:auto;background:white;padding:24px;border-radius:12px}");
            out.println("input,button{padding:10px;margin:6px 0;border-radius:6px}");
            out.println("input{width:95%;border:1px solid #ccd2df}");
            out.println("button{background:#243b80;color:white;border:0;cursor:pointer}");
            out.println(".course{background:#edf2ff;padding:16px;margin:14px 0;border-radius:8px}");
            out.println(".bar{background:#ddd;border-radius:8px;overflow:hidden;height:18px}");
            out.println(".fill{background:#16805d;height:18px}");
            out.println("</style></head><body><main>");

            out.println("<h1>My Learning Progress</h1>");
            out.println("<p><a href='index.html'>Home</a> | <a href='courses'>View Courses</a></p>");

            out.println("<form method='get' action='progress'>");
            out.println("<label for='email'>Enter your student email:</label><br>");
            out.println("<input type='email' id='email' name='email' required value='" +
                escapeHtml(email == null ? "" : email) + "'>");
            out.println("<button type='submit'>View Progress</button>");
            out.println("</form>");

            if (email != null && !email.isBlank()) {
                String sql = "SELECT c.COURSE_NAME, e.PROGRESS, e.ENROLLMENT_DATE " +
                             "FROM ENROLLMENTS e " +
                             "JOIN APP_USERS u ON e.USER_ID = u.USER_ID " +
                             "JOIN COURSES c ON e.COURSE_ID = c.COURSE_ID " +
                             "WHERE u.EMAIL = ? ORDER BY c.COURSE_NAME";

                try {
                    Class.forName("oracle.jdbc.OracleDriver");

                    try (Connection con = DriverManager.getConnection(
                             URL, DB_USER, DB_PASSWORD);
                         PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setString(1, email);

                        try (ResultSet rs = ps.executeQuery()) {
                            int count = 0;

                            while (rs.next()) {
                                count++;
                                int progress = rs.getInt("PROGRESS");

                                out.println("<div class='course'>");
                                out.println("<h2>" +
                                    escapeHtml(rs.getString("COURSE_NAME")) + "</h2>");
                                out.println("<p>Progress: " + progress + "%</p>");
                                out.println("<div class='bar'><div class='fill' style='width:" +
                                    progress + "%'></div></div>");
                                out.println("<p>Enrollment date: " +
                                    rs.getDate("ENROLLMENT_DATE") + "</p>");
                                out.println("</div>");
                            }

                            if (count == 0) {
                                out.println("<p>No enrollments found for that email.</p>");
                            }
                        }
                    }
                } catch (Exception e) {
                    getServletContext().log("Progress page error", e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("<p>Could not load progress. Please check the Tomcat logs.</p>");
                }
            }

            out.println("</main></body></html>");
        }
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
}

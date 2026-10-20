
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

@WebServlet("/courses")
public class CoursesServlet extends HttpServlet {

    private static final String URL =
        "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "123456";

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String sql = "SELECT COURSE_ID, COURSE_NAME, INSTRUCTOR, FEE " +
                     "FROM COURSES ORDER BY COURSE_ID";

        try {
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection con =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery();
                 PrintWriter out = response.getWriter()) {

                out.println("<!DOCTYPE html><html lang='en'><head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
                out.println("<title>Available Courses</title>");
                out.println("<style>");
                out.println("body{font-family:Arial;background:#f3f6fb;color:#243047;margin:0;padding:25px}");
                out.println("main{max-width:900px;margin:auto;background:white;padding:25px;border-radius:12px}");
                out.println("table{width:100%;border-collapse:collapse;margin-top:20px}");
                out.println("th,td{padding:12px;border:1px solid #ddd;text-align:left}");
                out.println("th{background:#243b80;color:white}");
                out.println("a{color:#243b80}");
                out.println("</style></head><body><main>");

                out.println("<h1>Available Courses</h1>");
                out.println("<p>Browse courses available in the LMS.</p>");
                out.println("<table><tr><th>Course ID</th>");
                out.println("<th>Course Name</th><th>Instructor</th><th>Fee</th></tr>");

                boolean found = false;

                while (rs.next()) {
                    found = true;
                    out.println("<tr><td>" + escapeHtml(rs.getString("COURSE_ID")) +
                        "</td><td>" + escapeHtml(rs.getString("COURSE_NAME")) +
                        "</td><td>" + escapeHtml(rs.getString("INSTRUCTOR")) +
                        "</td><td>" + rs.getBigDecimal("FEE") +
                        "</td></tr>");
                }

                if (!found) {
                    out.println("<tr><td colspan='4'>No courses found.</td></tr>");
                }

                out.println("</table>");
                out.println("<p><a href='dashboard'>Back to Dashboard</a> | ");
                out.println("<a href='index.html'>Home</a></p>");
                out.println("</main></body></html>");

            }
        } catch (Exception e) {
            getServletContext().log("Could not load courses", e);
            response.sendError(500,
                "Could not load courses. Please check the Tomcat logs.");
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

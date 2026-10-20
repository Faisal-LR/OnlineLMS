
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String name = escapeHtml(
            (String) session.getAttribute("fullName"));
        String role = escapeHtml(
            (String) session.getAttribute("userRole"));

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>LMS Dashboard</title>");

            out.println("<style>");
            out.println("body{margin:0;font-family:Arial,sans-serif;background:#f3f6fb;color:#243047}");
            out.println("header{background:#243b80;color:white;padding:28px;text-align:center}");
            out.println("main{max-width:900px;margin:35px auto;padding:24px}");
            out.println(".welcome{background:white;padding:25px;border-radius:12px;margin-bottom:25px}");
            out.println(".cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:18px}");
            out.println(".card{background:white;padding:22px;border-radius:12px;box-shadow:0 2px 8px #00000010}");
            out.println(".card a{display:inline-block;background:#243b80;color:white;padding:10px 14px;border-radius:6px;text-decoration:none}");
            out.println(".logout{display:inline-block;margin-top:20px;color:#243b80}");
            out.println("</style></head><body>");

            out.println("<header>");
            out.println("<h1>Online Learning Management System</h1>");
            out.println("<p>Your learning dashboard</p>");
            out.println("</header>");

            out.println("<main>");
            out.println("<section class='welcome'>");
            out.println("<h2>Welcome, " + name + "!</h2>");
            out.println("<p>You have successfully logged in.</p>");
            out.println("<p><strong>Role:</strong> " + role + "</p>");
            out.println("</section>");

            out.println("<h2>Quick Access</h2>");
            out.println("<div class='cards'>");

            out.println("<section class='card'><h3>Available Courses</h3>");
            out.println("<p>Explore the courses in the LMS.</p>");
            out.println("<a href='courses'>View Courses</a></section>");

            out.println("<section class='card'><h3>My Progress</h3>");
            out.println("<p>Check your course progress.</p>");
            out.println("<a href='progress'>View Progress</a></section>");

            out.println("<section class='card'><h3>Quiz Results</h3>");
            out.println("<p>Review quiz scores and results.</p>");
            out.println("<a href='quizzes'>View Results</a></section>");

            out.println("<section class='card'><h3>Student Details</h3>");
            out.println("<p>View registered student details.</p>");
            out.println("<a href='students'>View Students</a></section>");

            out.println("<section class='card'><h3>Add Course</h3>");
            out.println("<p>Add a course to the database.</p>");
            out.println("<a href='add-course'>Add Course</a></section>");

            out.println("</div>");
            out.println("<a class='logout' href='logout'>Log out</a>");
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

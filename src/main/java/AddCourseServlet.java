
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/add-course")
public class AddCourseServlet extends HttpServlet {

    private static final String URL =
        "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String DB_USER = "SYSTEM";
    private static final String DB_PASSWORD = "123456";
    static {
    try {
        Class.forName("oracle.jdbc.OracleDriver");
    } catch (ClassNotFoundException e) {
        throw new RuntimeException("Oracle JDBC driver could not be loaded", e);
    }
}

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Add Course - LMS</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background: #f3f6fb;
                        margin: 40px;
                    }
                    form {
                        background: white;
                        padding: 25px;
                        max-width: 450px;
                        border-radius: 10px;
                    }
                    input, textarea {
                        box-sizing: border-box;
                        width: 100%;
                        padding: 10px;
                        margin: 8px 0 16px;
                    }
                    button {
                        background: #2457c5;
                        color: white;
                        padding: 12px 20px;
                        border: 0;
                        cursor: pointer;
                    }
                </style>
            </head>
            <body>
                <h1>Add a New Course</h1>
                <form method="post" action="add-course">
                    <label>Course ID</label>
                    <input type="number" name="courseId"
                           min="1" required>

                    <label>Course Name</label>
                    <input type="text" name="courseName"
                           maxlength="150" required>

                    <label>Description</label>
                    <textarea name="description"
                              maxlength="500"></textarea>

                    <label>Instructor</label>
                    <input type="text" name="instructor"
                           maxlength="100" required>

                    <label>Duration (hours)</label>
                    <input type="number" name="duration"
                           min="1" required>

                    <button type="submit">Save Course</button>
                </form>
                <p><a href="courses">View Courses</a></p>
                <p><a href="./">Back to Home</a></p>
            </body>
            </html>
            """);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String courseId = request.getParameter("courseId");
        String courseName = request.getParameter("courseName");
        String description = request.getParameter("description");
        String instructor = request.getParameter("instructor");
        String duration = request.getParameter("duration");

        try {
            int id = Integer.parseInt(courseId);
            int hours = Integer.parseInt(duration);

            if (id < 1 || hours < 1 ||
                courseName == null || courseName.isBlank() ||
                instructor == null || instructor.isBlank()) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Please enter a valid course ID, name, instructor, and duration.");
                return;
            }

            String sql = """
                INSERT INTO COURSES
                (COURSE_ID, COURSE_NAME, DESCRIPTION,
                 INSTRUCTOR, DURATION_HOURS, STATUS)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE')
                """;

            try (Connection conn =
                     DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ps.setString(2, courseName.trim());
                ps.setString(3, description);
                ps.setString(4, instructor.trim());
                ps.setInt(5, hours);

                int rows = ps.executeUpdate();

                if (rows == 1) {
                    response.sendRedirect(
                        request.getContextPath() + "/courses");
                    return;
                }
            }

        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Course ID and duration must be valid numbers.");
            return;

        } catch (SQLException e) {
            log("Database error while adding course", e);

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html><html><body>");
                out.println("<h2>Could not save course</h2>");
                out.println("<p>Oracle returned error code: "
                    + e.getErrorCode() + "</p>");
                out.println("<p>Error type: "
                    + e.getClass().getSimpleName() + "</p>");
                out.println("<p>Please check the Tomcat log for details.</p>");
                out.println("<p><a href='add-course'>Go back</a></p>");
                out.println("</body></html>");
            }
        }
    }
}

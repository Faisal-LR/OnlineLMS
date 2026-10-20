
# Online Learning Management System (LMS)

## 1. Project Overview
This web application allows students to log in, view available courses,
check their course progress, and view quiz results.

## 2. Technologies Used
- Java
- HTML
- Apache Tomcat 10.1
- Oracle Database 21c XE
- JDBC (Oracle ojdbc17.jar)
- Visual Studio Code

## 3. Requirements
- JDK 17 or later
- Oracle Database 21c XE
- Apache Tomcat 10.1
- Oracle JDBC driver (ojdbc17.jar)

## 4. Database Configuration
1. Start Oracle Database and its listener.
2. Connect to the Oracle XE service using SQL*Plus.
3. For a fresh schema, run database/schema.sql first.
4. Then run database/sample_data.sql.
5. The Java application uses the JDBC URL:
   jdbc:oracle:thin:@//localhost:1521/XE
6. Update DB_USER and DB_PASSWORD in the Java servlet files to match
   the local Oracle account.

Note: The existing development database uses the SYSTEM account.
For a fresh setup, use a suitable application database account instead.

## 5. JDBC Driver Setup
1. Obtain the Oracle JDBC driver compatible with your JDK.
2. Place ojdbc17.jar in:
   src/main/webapp/WEB-INF/lib/
3. The driver must also be available on the compilation classpath.

## 6. Compile the Application
Open PowerShell in the OnlineLMS project folder and run:

javac -cp "C:\PATH\TO\apache-tomcat\lib\servlet-api.jar;src\main\webapp\WEB-INF\lib\ojdbc17.jar" -d "src\main\webapp\WEB-INF\classes" src\main\java\LoginServlet.java src\main\java\CoursesServlet.java src\main\java\ProgressServlet.java src\main\java\QuizServlet.java

Replace C:\PATH\TO\apache-tomcat with the actual Tomcat installation path.

## 7. Deploy and Run
1. Copy the project web files and compiled classes into Tomcat's
   webapps/OnlineLMS/ directory, preserving the folder structure.
2. Start Tomcat using bin/catalina.bat run.
3. Open http://localhost:8080/OnlineLMS/

## 8. Application Pages
- Homepage: /OnlineLMS/
- Login: /OnlineLMS/login.html
- Courses: /OnlineLMS/courses
- Progress: /OnlineLMS/progress
- Quiz results: /OnlineLMS/quizzes

## 9. Demo Login
Student email: asha@example.com
Demo password: demo123

Instructor email: priya@example.com
Demo password: demo123

## 10. SQL Commands
DDL commands are provided in database/schema.sql.
DML sample data commands are provided in database/sample_data.sql.

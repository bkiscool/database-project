package me.brandonkey.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Option6 {

    private static final Logger logger = LoggerFactory.getLogger(Option6.class);

    @GetMapping("/display-compatible-students-table")
    public String displayCompatibleStudentsTable(@RequestParam(value = "student-id") String studentId, @RequestParam(value = "wants-ac") String wantsAC, @RequestParam(value = "wants-dining") String wantsDining, @RequestParam(value = "wants-private-bathroom") String wantsPrivateBathroom)
    {
        // Create text table for the response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-84s\n", "").replace(" ", "-"));
        table.append(String.format("|%-10s|%-25s|%-9s|%-12s|%-22s|\n", "Student ID", "Name", "Wants A/C", "Wants Dining", "Wants Private Bathroom"));
        table.append(String.format("%-84s\n", "").replace(" ", "-"));

        // Get compatible students
        try {
            // Create a string of requirement conditions for the SQL query
            StringBuilder requirements = new StringBuilder();
            if (Boolean.valueOf(wantsAC))
            {
                requirements.append("AND wantsAC = 1 ");
            }

            if (Boolean.valueOf(wantsDining))
            {
                requirements.append("AND wantsDining = 1 ");
            }

            if (Boolean.valueOf(wantsPrivateBathroom))
            {
                requirements.append("AND wantsPrivateBathroom = 1 ");
            }

            String query = String.format("SELECT * FROM Student WHERE studentID != '%s' %sORDER BY studentID;", studentId, requirements.toString());
            ResultSet result = DB.query(query);

            // Add compatible students to the text table
            boolean isEmpty = true;
            while (result.next())
            {
                isEmpty = false;

                String compatibleStudentId = result.getString(1);
                String compatibleName = result.getString(2);
                boolean compatibleWantsAC = result.getBoolean(3);
                boolean compatibleWantsDining = result.getBoolean(4);
                boolean compatibleWantsPrivateBathroom = result.getBoolean(5);

                table.append(String.format("|%-10s|%-25s|%-9s|%-12s|%-22s|\n", compatibleStudentId, compatibleName, compatibleWantsAC, compatibleWantsDining, compatibleWantsPrivateBathroom));
            }

            if (isEmpty)
            {
                table.append(String.format("|%-82s|\n", "The table is empty."));
            }

            result.close();

        } catch (SQLException e)
        {
            e.printStackTrace();

            table.append(String.format("|%-82s|\n", "Error: Could not get compatible students, please try again."));
            table.append(String.format("%-84s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        table.append(String.format("%-84s\n", "").replace(" ", "-"));

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }

    @GetMapping("/display-student-buttons-option-6")
    public String displayStudentButtonsOption6()
    {
        // Create html for the response
        StringBuilder builder = new StringBuilder();
        
        // Get all students
        ResultSet result;
        try {
            String query = "SELECT * FROM Student;";
            result = DB.query(query);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return "Error: Could not get students. Please reload the page and try again.";
        }

        // Create html for each student
        try {
            while (result.next())
            {
                String studentId = result.getString(1);
                String name = result.getString(2);
                boolean wantsAC = result.getBoolean(3);
                boolean wantsDining = result.getBoolean(4);
                boolean wantsPrivateBathroom = result.getBoolean(5);
                
                // <button>onclick="option5(studentId, name, wantsAC, wantsDining, wantsPrivateBathroom)">name (studentId)</button>
                builder.append(HTML.BUTTON // calls javascript function option6() when clicked
                                .addAttribute("onclick", String.format("option6(\"%s\",\"%s\",\"%b\",\"%b\",\"%b\")", studentId, name, wantsAC, wantsDining, wantsPrivateBathroom))
                                .apply(String.format("%s (%s)", name, studentId)));
                
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Send the html as a response
        String htmlResponse = builder.toString();
        logger.info(htmlResponse);

        return htmlResponse;
    }
    
}

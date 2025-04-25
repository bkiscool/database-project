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
public class Option1 {

    private static final Logger logger = LoggerFactory.getLogger(Option1.class);

    @GetMapping("/add-student")
    public String addStudent(@RequestParam(value = "student-id") String studentId, @RequestParam(value = "name") String name,
                                @RequestParam(value = "wants-ac") String wantsAC, @RequestParam(value = "wants-dining") String wantsDining,
                                @RequestParam(value = "wants-private-bathroom") String wantsPrivateBathroom)
    {
        // Check for null/empty values
        try {
            if (studentId == null || studentId.isEmpty()
                || name == null  || name.isEmpty()
                || wantsAC == null || wantsAC.isEmpty()
                || wantsDining == null || wantsDining.isEmpty()
                || wantsPrivateBathroom == null || wantsPrivateBathroom.isEmpty())
            {
                throw new NullPointerException("Request parameters cannot be empty.");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return "Error: Parameters cannot be empty.";
        }

        // Insert student into database
        try {
            int wantsACValue = Boolean.valueOf(wantsAC) ? 1 : 0;
            int wantsDiningValue = Boolean.valueOf(wantsDining) ? 1 : 0;
            int wantsPrivateBathroomValue = Boolean.valueOf(wantsPrivateBathroom) ? 1 : 0;

            String query = String.format("INSERT INTO Student VALUES('%s', '%s', %d, %d, %d);", studentId, name, wantsACValue, wantsDiningValue, wantsPrivateBathroomValue);
            Main.DB.query(query);
        } catch (SQLException e)
        {
            // Error code 1062 is duplicate primary key
            if (e.getErrorCode() == 1062)
            {
                return "Error: Could not add student because of a duplicate primary key.";
            }

            return "Error: Could not add student. Error code: " + e.getErrorCode();
        }

        return String.format("Added student: %s", name);
    }

    @GetMapping("/display-student-table")
    public String displayStudentTable()
    {
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-84s\n", "").replace(" ", "-"));
        table.append(String.format("|%-10s|%-25s|%-9s|%-12s|%-22s|\n", "Student ID", "Name", "Wants A/C", "Wants Dining", "Wants Private Bathroom"));
        table.append(String.format("%-84s\n", "").replace(" ", "-"));

        ResultSet result;
        try {
            String query = "SELECT * FROM Student";
            result = Main.DB.query(query);
        } catch (SQLException e)
        {
            e.printStackTrace();
            
            table.append(String.format("|%-82s|\n", "Error: Could not get Student table. Please try again."));
            table.append(String.format("%-84s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        try {
            boolean isEmpty = true;

            while (result.next())
            {
                isEmpty = false;

                String wantsACValue = result.getBoolean(3) ? "Yes" : "No";
                String wantsDiningValue = result.getBoolean(4) ? "Yes" : "No";
                String wantsPrivateBathroomValue = result.getBoolean(5) ? "Yes" : "No";
                table.append(String.format("|%-10s|%-25s|%-9s|%-12s|%-22s|\n", result.getString(1), result.getString(2), wantsACValue, wantsDiningValue, wantsPrivateBathroomValue));
            }

            if (isEmpty)
            {
                table.append(String.format("|%-82s|\n", "The table is empty."));
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.append(String.format("%-84s\n", "").replace(" ", "-"));

        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }
    
}

package me.brandonkey.project;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Option8 { // THIS IS THE BONUS OPTION

    private static final Logger logger = LoggerFactory.getLogger(Option8.class);

    @GetMapping("/display-ra-table")
    public String displayRATable()
    {
        // Create a text table to send as a response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-85s\n", "").replace(" ", "-"));
        table.append(String.format("|%-10s|%-25s|%-7s|%-17s|%-20s|\n", "Student ID", "Name", "Stipend", "Emergency Contact", "Resident Building"));
        table.append(String.format("%-85s\n", "").replace(" ", "-"));

        // Get the RAs
        try {
            String query = "SELECT RA.studentID, Student.name, RA.stipend, RA.emergencyContact, Building.name FROM RA JOIN Student ON RA.studentID = Student.studentID JOIN Assignment ON RA.studentID = Assignment.studentID JOIN Building ON Assignment.buildingID = Building.buildingID ORDER BY RA.studentID;";
            ResultSet result = Main.DB.query(query);

            // Add the RAs to the text table
            while (result.next())
            {
                String studentId = result.getString(1);
                String name = result.getString(2);
                double stipend = result.getDouble(3);
                String emergencyContact = result.getString(4);
                String buildingName = result.getString(5);

                table.append(String.format("|%-10s|%-25s|%-7.2f|%-17s|%-20s|\n", studentId, name, stipend, emergencyContact, buildingName));

            }

            result.close();

        } catch (Exception e)
        {
            e.printStackTrace();

            table.append(String.format("|%-83s|\n", "Error: Could not get RAs. Please try again."));
            table.append(String.format("%-85s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        table.append(String.format("%-85s\n", "").replace(" ", "-"));

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }
    
}

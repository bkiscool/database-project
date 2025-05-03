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
public class Option3 {

    private static final Logger logger = LoggerFactory.getLogger(Option3.class);

    @GetMapping("/display-assignments-in-building-table")
    public String displayAssignmentsInBuildingTable(@RequestParam(value = "building-id") String buildingId)
    {
        // Check for null/empty values
        try {
            if (buildingId == null || buildingId.isEmpty())
            {
                throw new NullPointerException("Request parameters cannot be empty.");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return "Error: Parameters cannot be empty.";
        }

        // Create a text table for the response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-39s\n", "").replace(" ", "-"));
        table.append(String.format("|%-25s|%-11s|\n", "Name", "Room Number"));
        table.append(String.format("%-39s\n", "").replace(" ", "-"));

        // Get assignments in a building
        ResultSet result;
        try {
            String query = String.format("SELECT Student.name, Assignment.roomNumber FROM Student JOIN Assignment ON Student.studentID = Assignment.studentID WHERE Assignment.buildingID = '%s' ORDER BY Student.name;", buildingId);
            result = DB.query(query);
        } catch (SQLException e)
        {
            e.printStackTrace();
            
            table.append(String.format("|%-37s|\n", "Error: Could not get Student table."));
            table.append(String.format("|%-37s|\n", "Please try again."));
            table.append(String.format("%-39s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        // Add the assignments to the text table
        try {
            boolean isEmpty = true;

            while (result.next())
            {
                isEmpty = false;

                String name = result.getString(1);
                String roomNumber = result.getString(2);
                table.append(String.format("|%-25s|%-11s|\n", name, roomNumber));
            }

            if (isEmpty)
            {
                table.append(String.format("|%-37s|\n", "The table is empty."));
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.append(String.format("%-39s\n", "").replace(" ", "-"));

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }

    @GetMapping("/display-building-buttons")
    public String displayBuildingButtons()
    {
        // Create html for the response
        StringBuilder builder = new StringBuilder();
        
        // Get all the buildings
        ResultSet result;
        try {
            String query = "SELECT buildingID, name FROM Building;";
            result = DB.query(query);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return "Error: Could not get buildings. Please reload the page and try again.";
        }

        // Create the html buttons
        try {
            while (result.next())
            {
                String buildingId = result.getString(1);
                String name = result.getString(2);
                
                // <button>onclick="option3(buildingId)">name (buildingId)</button>
                builder.append(HTML.BUTTON
                                .addAttribute("onclick", String.format("option3(\"%s\")", buildingId)) // Calls a javascript function when clicked
                                .apply(String.format("%s (%s)", name, buildingId)));
                
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Send the html buttons as a response
        String htmlResponse = builder.toString();
        logger.info(htmlResponse);

        return htmlResponse;
    }
    
}

package me.brandonkey.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Option4 {

    private static final Logger logger = LoggerFactory.getLogger(Option4.class);

    @GetMapping("/display-available-rooms-table")
    public String displayAvailableRoomsTable()
    {
        // Create a text table for the response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-44s\n", "").replace(" ", "-"));
        table.append(String.format("|%-11s|%-11s|%-18s|\n", "Building ID", "Room Number", "Available Bedrooms"));
        table.append(String.format("%-44s\n", "").replace(" ", "-"));

        // Get an ordered map of building ID to building name
        LinkedHashMap<String, String> buildlings = new LinkedHashMap<>();
        try {
            String query = String.format("SELECT buildingID, name FROM Building ORDER BY buildingID;");
            ResultSet result = Main.DB.query(query);

            while (result.next())
            {
                String buildingId = result.getString(1);
                String name = result.getString(2);

                buildlings.put(buildingId, name);
            }

            result.close();
        } catch (SQLException e)
        {
            e.printStackTrace();

            table.append(String.format("|%-42s|\n", "Error: Could not get buildings from"));
            table.append(String.format("|%-42s|\n", "database. Please refresh and try again."));
            table.append(String.format("%-44s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        // Count the assignments and calculate the available rooms
        try {
            String assignmentQuery = "SELECT buildingID, roomNumber FROM Assignment ORDER BY buildingID;";
            ResultSet assignmentResult = Main.DB.query(assignmentQuery);

            // Map of room to assignment count.
            // Key is buildingId + roomNumber
            LinkedHashMap<String, Integer> roomAssignmentsCount = new LinkedHashMap<>();
            while (assignmentResult.next())
            {
                String buildilngId = assignmentResult.getString(1);
                String roomNumber = assignmentResult.getString(2);

                int count = Optional.ofNullable(roomAssignmentsCount.get(buildilngId + roomNumber)).orElse(0);
                roomAssignmentsCount.put(buildilngId + roomNumber, count + 1); // Increase the count
            }

            String roomQuery = "SELECT buildingID, roomNumber, numBedrooms FROM Room ORDER BY buildingID;";
            ResultSet roomResult = Main.DB.query(roomQuery);

            // Calculate the available rooms and
            // add it to the text table
            boolean isEmpty = true;
            while (roomResult.next())
            {
                isEmpty = false;

                String buildingId = roomResult.getString(1);
                String roomNumber = roomResult.getString(2);
                int numBedrooms = roomResult.getInt(3);

                int roomAssignmentCount = Optional.ofNullable(roomAssignmentsCount.get(buildingId + roomNumber)).orElse(0);
                int availableBedrooms = numBedrooms - roomAssignmentCount;

                table.append(String.format("|%-11s|%-11s|%-18s|\n", buildingId, roomNumber, String.valueOf(availableBedrooms + " of " + numBedrooms)));

            }

            if (isEmpty)
            {
                table.append(String.format("|%-42s|\n", "The table is empty."));
            }

            assignmentResult.close();
            roomResult.close();

        } catch (SQLException e)
        {
            e.printStackTrace();

            table.append(String.format("|%-42s|\n", "Error: Could not get room assignments"));
            table.append(String.format("|%-42s|\n", "from database. Please try again."));
            table.append(String.format("%-44s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        table.append(String.format("%-44s\n", "").replace(" ", "-"));

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }
    
}

package me.brandonkey.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Option7 {
    private static final Logger logger = LoggerFactory.getLogger(Option7.class);

    @GetMapping("/display-building-report-table")
    public String displayCompatibleStudentsTable()
    {
        // Create a text table for the response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-110s\n", "").replace(" ", "-"));
        table.append(String.format("|%-11s|%-20s|%-11s|%-14s|%-23s|%-24s|\n", "Building ID", "Name", "Total Rooms", "Total Bedrooms", "Rooms With Availability", "Total Available Bedrooms"));
        table.append(String.format("%-110s\n", "").replace(" ", "-"));

        // An ordered map of building ID to building name
        LinkedHashMap<String, String> buildings = new LinkedHashMap<>();
        try {
            String query = String.format("SELECT buildingID, name FROM Building ORDER BY buildingID;");
            ResultSet result = DB.query(query);

            while (result.next())
            {
                String buildingId = result.getString(1);
                String name = result.getString(2);

                buildings.put(buildingId, name);
            }

            result.close();
        } catch (SQLException e)
        {
            e.printStackTrace();

            table.append(String.format("|%-108s|\n", "Error: Could not get buildings from database. Please refresh and try again."));
            table.append(String.format("%-110s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        // A map of building ID to number of rooms in that building
        HashMap<String, Integer> buildingsAndRoomCounts = new HashMap<>();
        try {
            for (String buildingId : buildings.keySet())
            {
                String query = String.format("SELECT COUNT(*) FROM Room WHERE buildingID = '%s';", buildingId);
                ResultSet result = DB.query(query);

                result.next();
                int numRooms = result.getInt(1);
                buildingsAndRoomCounts.put(buildingId, numRooms);

                result.close();
            }

            
        } catch (SQLException e)
        {
            e.printStackTrace();

            table.append(String.format("|%-108s|\n", "Error: Could not get rooms from database. Please refresh and try again."));
            table.append(String.format("%-110s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        // Map of building ID to number of:
        // * Number of available bedrooms
        // * Number of available rooms
        // * Number of bedrooms
        HashMap<String, Integer> buildingAndNumAvailableBedrooms = new HashMap<>();
        HashMap<String, Integer> buildingAndAvailableRooms = new HashMap<>();
        HashMap<String, Integer> buildingsAndNumBedrooms = new HashMap<>();
        try {
            String assignmentQuery = "SELECT buildingID, roomNumber FROM Assignment;";
            ResultSet assignmentResult = DB.query(assignmentQuery);

            // Map of room to number of assignments
            HashMap<String, Integer> roomAssignmentsCount = new HashMap<>(); // Key is buildingId = roomNumber
            while (assignmentResult.next())
            {
                String buildilngId = assignmentResult.getString(1);
                String roomNumber = assignmentResult.getString(2);

                int count = Optional.ofNullable(roomAssignmentsCount.get(buildilngId + roomNumber)).orElse(0);
                roomAssignmentsCount.put(buildilngId + roomNumber, count + 1);
            }

            String roomQuery = "SELECT buildingID, roomNumber, numBedrooms FROM Room;";
            ResultSet roomResult = DB.query(roomQuery);

            // Calculate and add values to the maps
            while (roomResult.next())
            {
                String buildingId = roomResult.getString(1);
                String roomNumber = roomResult.getString(2);
                int numBedrooms = roomResult.getInt(3);

                // Available bedrooms in this room
                int roomAssignmentCount = Optional.ofNullable(roomAssignmentsCount.get(buildingId + roomNumber)).orElse(0);
                int availableBedrooms = numBedrooms - roomAssignmentCount;

                // Number of bedrooms in this room
                int numBedroomsInBuilding = Optional.ofNullable(buildingsAndNumBedrooms.get(buildingId)).orElse(0);
                buildingsAndNumBedrooms.put(buildingId, numBedroomsInBuilding + numBedrooms);

                if (availableBedrooms > 0)
                {
                    // Available bedrooms in the building
                    int availableBedroomsInBuilding = Optional.ofNullable(buildingAndNumAvailableBedrooms.get(buildingId)).orElse(0);
                    buildingAndNumAvailableBedrooms.put(buildingId, availableBedroomsInBuilding + availableBedrooms);

                    // Available rooms in the building
                    int availableRoomsInBuilding = Optional.ofNullable(buildingAndAvailableRooms.get(buildingId)).orElse(0);
                    buildingAndAvailableRooms.put(buildingId, availableRoomsInBuilding + 1);

                }

            }

            assignmentResult.close();
            roomResult.close();

        } catch (SQLException e)
        {
            e.printStackTrace();

            table.append(String.format("|%-108s|\n", "Error: Could not get assignments from database. Please refresh and try again."));
            table.append(String.format("%-110s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        // Add all the values to the text table
        int totalBedroomAvailability = 0; // Keep track of total bedroom availability for the summary
        for (Entry<String, String> building : buildings.entrySet())
        {
            String buildingId = building.getKey();
            String name = building.getValue();
            int totalRooms = buildingsAndRoomCounts.get(buildingId);
            int totalBedrooms = buildingsAndNumBedrooms.get(buildingId);
            int roomAvailability = buildingAndAvailableRooms.get(buildingId);
            int bedroomAvailability = buildingAndNumAvailableBedrooms.get(buildingId);

            table.append(String.format("|%-11s|%-20s|%-11d|%-14d|%-23d|%-24d|\n", buildingId, name, totalRooms, totalBedrooms, roomAvailability, bedroomAvailability));

            totalBedroomAvailability += bedroomAvailability;
        }

        table.append(String.format("%-110s\n", "").replace(" ", "-"));

        String summary = HTML.PARAGRAPH.apply(String.format("The total number of available bedrooms on campus is %d.", totalBedroomAvailability));
        table.append(summary);

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }

}

package me.brandonkey.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Option5 {

    private static final Logger logger = LoggerFactory.getLogger(Option5.class);

    @GetMapping("/display-compatible-rooms-table")
    public String displayCompatibleRoomsTable(@RequestParam(value = "wants-ac") String wantsAC, @RequestParam(value = "wants-dining") String wantsDining, @RequestParam(value = "wants-private-bathroom") String wantsPrivateBathroom)
    {
        // Create text table for the response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-59s\n", "").replace(" ", "-"));
        table.append(String.format("|%-11s|%-20s|%-11s|%-12s|\n", "Building ID", "Name", "Room Number", "Availability"));
        table.append(String.format("%-59s\n", "").replace(" ", "-"));

        // Get compatible rooms
        try {
            String assignmentQuery = "SELECT buildingID, roomNumber FROM Assignment;";
            ResultSet assignmentResult = DB.query(assignmentQuery);

            // Map of room to number of assignments
            HashMap<String, Integer> roomAssignmentsCount = new HashMap<>(); // Key is buildingId + roomNumber
            while (assignmentResult.next())
            {
                String buildilngId = assignmentResult.getString(1);
                String roomNumber = assignmentResult.getString(2);

                // Increase count
                int count = Optional.ofNullable(roomAssignmentsCount.get(buildilngId + roomNumber)).orElse(0);
                roomAssignmentsCount.put(buildilngId + roomNumber, count + 1);
            }

            // Create a string of requirement conditions for the SQL query
            StringBuilder requirements = new StringBuilder();
            if (Boolean.valueOf(wantsAC))
            {
                requirements.append("WHERE Building.hasAC = 1 ");
            }

            if (Boolean.valueOf(wantsDining))
            {
                if (requirements.isEmpty())
                {
                    requirements.append("WHERE ");
                } else
                {
                    requirements.append("AND ");
                }

                requirements.append("Building.hasDining = 1 ");
            }

            if (Boolean.valueOf(wantsPrivateBathroom))
            {
                if (requirements.isEmpty())
                {
                    requirements.append("WHERE ");
                } else
                {
                    requirements.append("AND ");
                }

                requirements.append("Room.privateBathrooms = 1 ");
            }

            String roomQuery = String.format("SELECT Building.buildingID, Building.name, Room.roomNumber, Room.numBedrooms FROM Building JOIN Room ON Building.buildingID = Room.buildingID %sORDER BY Building.buildingID, Room.roomNumber;", requirements.toString());
            ResultSet roomResult = DB.query(roomQuery);

            // Add the rooms to the text table with a column saying if it is available or unavailable
            boolean isEmpty = true;
            while (roomResult.next())
            {
                isEmpty = false;

                String buildingId = roomResult.getString(1);
                String name = roomResult.getString(2);
                String roomNumber = roomResult.getString(3);
                int numBedrooms = roomResult.getInt(4);

                int roomAssignmentCount = Optional.ofNullable(roomAssignmentsCount.get(buildingId + roomNumber)).orElse(0);
                int availableBedrooms = numBedrooms - roomAssignmentCount;
                String isAvailable = availableBedrooms > 0 ? "Available" : "Unavailable";

                table.append(String.format("|%-11s|%-20s|%-11s|%-12s|\n", buildingId, name, roomNumber, isAvailable));

            }

            if (isEmpty)
            {
                table.append(String.format("|%-57s|\n", "The table is empty."));
            }

            assignmentResult.close();
            roomResult.close();

        } catch (SQLException e)
        {
            e.printStackTrace();

            table.append(String.format("|%-57s|\n", "Error: Could not get compatible rooms, please try again."));
            table.append(String.format("%-59s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        table.append(String.format("%-59s\n", "").replace(" ", "-"));

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }

    @GetMapping("/display-student-buttons-option-5")
    public String displayStudentButtonsOption5()
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

        // Create html buttons for each student
        try {
            while (result.next())
            {
                String studentId = result.getString(1);
                String name = result.getString(2);
                boolean wantsAC = result.getBoolean(3);
                boolean wantsDining = result.getBoolean(4);
                boolean wantsPrivateBathroom = result.getBoolean(5);
                
                // <button>onclick="option5(studentId, name, wantsAC, wantsDining, wantsPrivateBathroom)">name (studentId)</button>
                builder.append(HTML.BUTTON // calls javascript function option5() when clicked
                                .addAttribute("onclick", String.format("option5(\"%s\",\"%s\",\"%b\",\"%b\",\"%b\")", studentId, name, wantsAC, wantsDining, wantsPrivateBathroom))
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

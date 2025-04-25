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
public class Option2 {

    private static final Logger logger = LoggerFactory.getLogger(Option2.class);

    @GetMapping("/add-assignment")
    public String addAssignment(@RequestParam(value = "student-id") String studentId, @RequestParam(value = "building-id") String buildingId, @RequestParam(value = "room-number") String roomNumber)
    {
        String studentName;
        String buildingName;

        // Check for null/empty values
        try {
            if (studentId == null || studentId.isEmpty()
                || buildingId == null  || buildingId.isEmpty()
                || roomNumber == null || roomNumber.isEmpty())
            {
                throw new NullPointerException("Request parameters cannot be empty.");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return "Error: Parameters cannot be empty.";
        }

        // Check that student ID exists
        try {
            String query = String.format("SELECT COUNT(*) FROM Student WHERE studentId = '%s';", studentId);
            ResultSet result = Main.DB.query(query);

            if (!(result.next() && result.getInt(1) > 0))
            {
                throw new Exception(String.format("Error: Student ID (%s) does not exist.", studentId));
            } else
            {
                result.close();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            return String.format("Error: Student ID (%s) does not exist.", studentId);
        }
        
        // Check that building ID exists
        try {
            String query = String.format("SELECT COUNT(*) FROM Building WHERE buildingId = '%s';", buildingId);
            ResultSet result = Main.DB.query(query);

            if (!(result.next() && result.getInt(1) > 0))
            {
                throw new Exception(String.format("Error: Building ID (%s) does not exist.", buildingId));
            } else
            {
                result.close();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            return String.format("Error: Building ID (%s) does not exist.", buildingId);
        }
        
        // Check that room number in the building exists
        try {
            String query = String.format("SELECT COUNT(*) FROM Room WHERE buildingId = '%s' AND roomNumber = '%s';", buildingId, roomNumber);
            ResultSet result = Main.DB.query(query);

            if (!(result.next() && result.getInt(1) > 0))
            {
                throw new Exception(String.format("Error: Room Number (%s) for Building ID (%s) does not exist.", roomNumber, buildingId));
            } else
            {
                result.close();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            return String.format("Error: Room Number (%s) for Building ID (%s) does not exist.", roomNumber, buildingId);
        }
        
        // Check that the room isn't full
        try {
            // Get number of bedrooms
            String bedroomsQuery = String.format("SELECT numBedrooms FROM Room WHERE buildingId = '%s' AND roomNumber = '%s';", buildingId, roomNumber);
            ResultSet bedroomsResult = Main.DB.query(bedroomsQuery);

            bedroomsResult.next();
            int numBedrooms = bedroomsResult.getInt(1);

            // Get number of assignments to this room
            String assignmentsQuery = String.format("SELECT COUNT(*) FROM Assignment WHERE buildingId = '%s' AND roomNumber = '%s';", buildingId, roomNumber);
            ResultSet assignmentsResult = Main.DB.query(assignmentsQuery);

            assignmentsResult.next();
            int numAssignments = assignmentsResult.getInt(1);

            // Check if there is an available bedroom
            if (numBedrooms - numAssignments == 0)
            {
                throw new Exception("Error: This room is full. Please choose a different room to assign.");
            } else
            {
                bedroomsResult.close();
                assignmentsResult.close();
            }
            
        } catch (Exception e)
        {
            e.printStackTrace();
            return "Error: This room is full. Please choose a different room to assign.";
        }

        // Check that the student's room requirements are met
        try {
            // Get student with query
            String studentQuery = String.format("SELECT * FROM Student WHERE studentId = '%s';", studentId);
            ResultSet studentResult = Main.DB.query(studentQuery);

            // Parse requirements from student query
            studentResult.next();
            boolean wantsAC = studentResult.getBoolean(3);
            @SuppressWarnings("unused")
            boolean wantsDining = studentResult.getBoolean(4);
            @SuppressWarnings("unused")
            boolean wantsPrivateBathroom = studentResult.getBoolean(5);

            studentName = studentResult.getString(2);

            // Get requirements from Room
            String roomQuery = String.format("SELECT * FROM Room WHERE buildingId = '%s' AND roomNumber = '%s';", buildingId, roomNumber);
            ResultSet roomResult = Main.DB.query(roomQuery);

            // Parse requirements from room query
            roomResult.next();
            boolean hasPrivateBathroom = roomResult.getBoolean(4);

            // Get requirements from Building
            String buildingQuery = String.format("SELECT * FROM Building WHERE buildingId = '%s';", buildingId);
            ResultSet buildingResult = Main.DB.query(buildingQuery);

            // Parse requirements from building query
            buildingResult.next();
            boolean hasAC = buildingResult.getBoolean(4);
            boolean hasDining = buildingResult.getBoolean(5);
            
            buildingName = buildingResult.getString(2);
            
            // Check requirements
            if ((wantsAC && !hasAC) || (wantsDining && !hasDining) || (wantsPrivateBathroom && !hasPrivateBathroom))
            {
                throw new Exception("Error: Student room requirements not met.");
            } else
            {
                studentResult.close();
                roomResult.close();
                buildingResult.close();
            }
            
        } catch (Exception e)
        {
            e.printStackTrace();
            return "Error: Student room requirements not met.";
        }

        // Add assignment to database
        try {
            String query = String.format("INSERT INTO Assignment VALUES('%s', '%s', '%s');", studentId, buildingId, roomNumber);
            Main.DB.query(query);
        } catch (SQLException e)
        {
            if (e.getErrorCode() == 1062)
            {
                return "Error: Could not add assignment because of a duplicate primary key.";
            }

            return "Error: Could not add assignment. Error code: " + e.getErrorCode();
        }

        return String.format("Added assignment: %s to room %s at %s", studentName, roomNumber, buildingName);
    }

    @GetMapping("/display-building-room-table-joined")
    public String displayBuildingRoomTableJoined()
    {
        // Create a text table for the response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-116s\n", "").replace(" ", "-"));
        table.append(String.format("|%-11s|%-11s|%-20s|%-20s|%-8s|%-6s|%-10s|%-21s|\n", "Building ID", "Room Number", "Name", "Address", "Bedrooms", "Has AC", "Has Dining", "Has Private Bathrooms"));
        table.append(String.format("%-116s\n", "").replace(" ", "-"));

        // Join the building and room tables
        ResultSet result;
        try {
            String query = "SELECT Building.buildingID, Room.roomNumber, Building.name, Building.address, Room.numBedrooms, Building.hasAC, Building.hasDining, Room.privateBathrooms FROM Building JOIN Room ON Building.buildingID = Room.buildingID;";
            result = Main.DB.query(query);
        } catch (SQLException e)
        {
            e.printStackTrace();
            
            table.append(String.format("|%-114s|\n", "Error: Could not get the Building and Room tables. Please try again."));
            table.append(String.format("%-116s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        // Add each row to the text table
        try {
            boolean isEmpty = true;

            while (result.next())
            {
                isEmpty = false;

                String hasACValue = result.getBoolean(6) ? "Yes" : "No";
                String hasDiningValue = result.getBoolean(7) ? "Yes" : "No";
                String hasPrivateBathroomsValue = result.getBoolean(8) ? "Yes" : "No";
                table.append(String.format("|%-11s|%-11s|%-20s|%-20s|%-8d|%-6s|%-10s|%-21s|\n", result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getInt(5), hasACValue, hasDiningValue, hasPrivateBathroomsValue));

            }

            if (isEmpty)
            {
                table.append(String.format("|%-114s|\n", "The table is empty."));
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.append(String.format("%-116s\n", "").replace(" ", "-"));

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }

    @GetMapping("/display-assignment-table")
    public String displayAssignmentTable()
    {
        // Create a text table for the response
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-36s\n", "").replace(" ", "-"));
        table.append(String.format("|%-10s|%-11s|%-11s|\n", "Student ID", "Building ID", "Room Number"));
        table.append(String.format("%-36s\n", "").replace(" ", "-"));

        // Get the assignments
        ResultSet result;
        try {
            String query = "SELECT * FROM Assignment;";
            result = Main.DB.query(query);
        } catch (SQLException e)
        {
            e.printStackTrace();
            
            table.append(String.format("|%-34s|\n", "Error: Could not get"));
            table.append(String.format("|%-34s|\n", "Assignment table. Please try again."));
            table.append(String.format("%-36s\n", "").replace(" ", "-"));

            String response = HTML.PRE.apply(table.toString());
            return response;
        }

        // Add the assignments to the text table
        try {
            boolean isEmpty = true;

            while (result.next())
            {
                isEmpty = false;

                table.append(String.format("|%-10s|%-11s|%-11s|\n", result.getString(1), result.getString(2), result.getString(3)));

            }

            if (isEmpty)
            {
                table.append(String.format("|%-34s|\n", "The table is empty."));
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.append(String.format("%-36s\n", "").replace(" ", "-"));

        // Send the text table as a response
        String response = HTML.PRE.apply(table.toString());
        logger.info(response);

        return response;
    }

    // endpoint display student table is in option1

}

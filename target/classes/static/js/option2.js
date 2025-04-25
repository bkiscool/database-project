/**
 * Add a new assignment
 */
async function option2()
{
    // Values from form
    let studentId = document.getElementById("student-id").value.toString().padStart(5, "0"); // Pad with zeros
    let buildingId = document.getElementById("building-id").value.toString().padStart(3, "0");
    let roomNumber = document.getElementById("room-number").value.toString().padStart(3, "0");

    // Set the fields to the padded values
    document.getElementById("student-id").value = studentId;
    document.getElementById("building-id").value = buildingId;
    document.getElementById("room-number").value = roomNumber;

    console.log(`Submitted values: ${studentId}, ${buildingId}, ${roomNumber}`);

    // Error checking, max character length
    if (studentId == "" || buildingId == "" || roomNumber == "")
    {
        document.getElementById("error").innerHTML = "Error: Student ID, Building ID, and Room Number cannot be empty.";
        return;
    } else if (studentId.length > 5)
    {
        document.getElementById("error").innerHTML = "Error: Student ID cannot be longer than 5 characters.";
        return;
    } else if (buildingId.length > 3)
    {
        document.getElementById("error").innerHTML = "Error: Building ID cannot be longer than 3 characters.";
        return;
    } else if (roomNumber.length > 3)
    {
        document.getElementById("error").innerHTML = "Error: Room number cannot be longer than 3 characters.";
        return;
    } else
    {
        document.getElementById("error").innerHTML = "";
    }

    const response = await fetch(`/api/add-assignment?student-id=${studentId}&building-id=${buildingId}&room-number=${roomNumber}`);
    const data = await response.text();

    console.log(data);

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
        return;
    }

    document.getElementById("result").innerHTML = data;

    /**
     * Must run "display table functions" one at a time
     * so that the SQL Server doesn't receive more
     * than one request at a time.
     */
    await displayBuildingRoomTableJoined();
    await displayStudentTable();
    await displayAssignmentTable();
}

/**
 * Show a table with the buildings and rooms
 */
async function displayBuildingRoomTableJoined()
{
    const response = await fetch("/api/display-building-room-table-joined");
    const data = await response.text();

    console.log("Building and Room table joined:\n" + data);
    document.getElementById("building-room-table-joined").innerHTML = data;
}

/**
 * Show the students table
 */
async function displayStudentTable()
{
    const response = await fetch("/api/display-student-table");
    const data = await response.text();

    console.log("Student table:\n" + data);
    document.getElementById("student-table").innerHTML = data;
}

/**
 * Show the assignments table
 */
async function displayAssignmentTable()
{
    const response = await fetch("/api/display-assignment-table");
    const data = await response.text();

    console.log("Assignment table:\n" + data);
    document.getElementById("assignment-table").innerHTML = data;
}

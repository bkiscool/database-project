/**
 * Add student
 */
async function option1()
{
    // Values from form
    let studentId = document.getElementById("student-id").value.toString().padStart(5, "0"); // Pad with zeros
    let name = document.getElementById("name").value;
    let wantsAC = document.getElementById("wants-ac").checked;
    let wantsDining = document.getElementById("wants-dining").checked;
    let wantsPrivateBathroom = document.getElementById("wants-private-bathroom").checked;

    // Set form field to the padded student id
    document.getElementById("student-id").value = studentId;

    console.log(`Submitted values: ${studentId}, ${name}, ${wantsAC}, ${wantsDining}, ${wantsPrivateBathroom}`);

    // Error checking, max character length
    if (studentId == "" || name == "")
    {
        document.getElementById("error").innerHTML = "Error: Student ID and Name cannot be empty.";
        return;
    } else if (studentId.length > 5)
    {
        document.getElementById("error").innerHTML = "Error: Student ID cannot be longer than 5 characters.";
        return;
    } else if (name.length > 20)
    {
        document.getElementById("error").innerHTML = "Error: Student name cannot be longer than 25 characters.";
        return;
    } else
    {
        document.getElementById("error").innerHTML = "";
    }

    const response = await fetch(`/api/add-student?student-id=${studentId}&name=${name}&wants-ac=${wantsAC}&wants-dining=${wantsDining}&wants-private-bathroom=${wantsPrivateBathroom}`);
    const data = await response.text();

    console.log(data);

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
        return;
    }

    document.getElementById("result").innerHTML = data;

    // Display the new student table
    displayStudentTable();
}

/**
 * Show the student table
 */
async function displayStudentTable()
{
    const response = await fetch("/api/display-student-table");
    const data = await response.text();

    console.log("Student table:\n" + data);
    document.getElementById("student-table").innerHTML = data;
}

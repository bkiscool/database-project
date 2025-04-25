/**
 * Show a table of compatible students
 */
async function option6(studentId, name, wantsAC, wantsDining, wantsPrivateBathroom) // Arguments come from the button's on click attribute
{
    console.log(`Submitted values: ${studentId}, ${name}, ${wantsAC}, ${wantsDining}, ${wantsPrivateBathroom}`);

    document.getElementById("error").innerHTML = "";

    const response = await fetch(`/api/display-compatible-students-table?student-id=${studentId}&wants-ac=${wantsAC}&wants-dining=${wantsDining}&wants-private-bathroom=${wantsPrivateBathroom}`);
    const data = await response.text();

    console.log(data);

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
        return;
    }

    document.getElementById("result").innerHTML = `Showing compatible students for ${name}<br>Wants AC: ${wantsAC}<br>Wants Dining: ${wantsDining}<br>Wants Private Bathroom: ${wantsPrivateBathroom}`;
    document.getElementById("compatible-students-table").innerHTML = data;
}

/**
 * Show a list of buttons for each student
 */
async function displayStudentButtonsOption6()
{
    const response = await fetch(`/api/display-student-buttons-option-6`);
    const data = await response.text(); // Backend returns html

    console.log(data);

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
        return;
    }

    document.getElementById("student-buttons").innerHTML = data;

    console.log("Inner HTML:");
    console.log(document.getElementById("student-buttons").innerHTML);
}

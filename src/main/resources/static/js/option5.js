/**
 * Show compatible rooms
 */
async function option5(studentId, name, wantsAC, wantsDining, wantsPrivateBathroom) // Arguments come from the button's on click attribute
{
    console.log(`Submitted values: ${studentId}, ${name}, ${wantsAC}, ${wantsDining}, ${wantsPrivateBathroom}`);

    document.getElementById("error").innerHTML = "";

    const response = await fetch(`/api/display-compatible-rooms-table?wants-ac=${wantsAC}&wants-dining=${wantsDining}&wants-private-bathroom=${wantsPrivateBathroom}`);
    const data = await response.text();

    console.log(data);

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
        return;
    }

    document.getElementById("result").innerHTML = `Showing compatible rooms for ${name}.`;
    document.getElementById("compatible-rooms-table").innerHTML = data;
}

/**
 * Shows a list of buttons for each student
 */
async function displayStudentButtonsOption5()
{
    const response = await fetch(`/api/display-student-buttons-option-5`);
    const data = await response.text(); // Backend returns html with the buttons

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

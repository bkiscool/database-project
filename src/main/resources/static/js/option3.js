/**
 * Show assignments for the selected building
 */
async function option3(buildingId) // buildingId comes from the button onclick attribute
{
    console.log(`Submitted values: ${buildingId}`);

    document.getElementById("error").innerHTML = "";

    const response = await fetch(`/api/display-assignments-in-building-table?building-id=${buildingId}`);
    const data = await response.text();

    console.log(data);

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
        return;
    }

    document.getElementById("assignments-in-building-table").innerHTML = data;
}

/**
 * Shows a list of buttons for each building
 */
async function displayBuildingButtons()
{
    const response = await fetch(`/api/display-building-buttons`);
    const data = await response.text(); // Backend returns html with the buttons

    console.log(data);

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
        return;
    }

    document.getElementById("building-buttons").innerHTML = data;

    console.log("Inner HTML:");
    console.log(document.getElementById("building-buttons").innerHTML);
}

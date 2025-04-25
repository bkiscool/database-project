/**
 * Show the available rooms as a table
 */
async function displayAvailableRoomsTable()
{
    const response = await fetch("/api/display-available-rooms-table");
    const data = await response.text();

    console.log("Available rooms table:\n" + data);
    document.getElementById("available-rooms-table").innerHTML = data;
}

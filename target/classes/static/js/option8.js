/**
 * Show a table of RAs
 */
async function displayRATable() // BONUS OPTION
{
  const response = await fetch("/api/display-ra-table");
  const data = await response.text();

  console.log("RA table:\n" + data);
  document.getElementById("ra-table").innerHTML = data;
}

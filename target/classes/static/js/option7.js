/**
 * Show a table that reports room/bedroom availability
 */
async function displayBuildingReportTable()
{
  const response = await fetch("/api/display-building-report-table");
  const data = await response.text();

  console.log("Building report table:\n" + data);
  document.getElementById("building-report-table").innerHTML = data;
}

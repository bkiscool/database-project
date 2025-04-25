async function reset()
{
    const response = await fetch("/api/reset");
    const data = await response.text();

    document.getElementById("error").innerHTML = "";

    if (data.startsWith("Error"))
    {
        document.getElementById("error").innerHTML = data;
    }

    console.log("The SQL database has been reset.\n" + data);
    document.getElementById("result").innerHTML = "The SQL database has been reset.";
}

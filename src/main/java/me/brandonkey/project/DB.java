package me.brandonkey.project;

import java.sql.*;

public class DB {

    // The instance variables for the class
    private static DB db;
    private Connection connection;
    private Statement statement;

    // Singleton
    public static DB getInstance()
    {
        try {
            if (db != null && db.connection != null && db.statement != null && db.connection.isValid(2) && !db.statement.isClosed()) return db;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // My credentials
        String username = "blkey";
        String password = "ielee1Ea";

        db = new DB();

        try {
            // Make a connection to the SQL server
            db.connect(username, password);
            // create a statement to hold mysql queries
            db.statement = db.connection.createStatement();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return db;
    }

    // Connect to the database
    public void connect(String username, String password) throws SQLException {
        try {
            String url = "jdbc:mysql://localhost/" + username + "?useSSL=false";
            System.out.println(url);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw e;
        }
    }

    // Disconnect from the database
    public void disconnect() throws SQLException {
        connection.close();
        statement.close();
    }

    // Execute an SQL query passed in as a String parameter
    public static ResultSet query(String q) throws SQLException {
        DB db = getInstance();
        boolean hasResultSet = db.statement.execute(q);

        if (hasResultSet)
        {
            return db.statement.getResultSet();
        }

        return null;
    }

    // Print the results of a query with attribute names on the first line
    // Followed by the tuples, one per line
    public void print(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numColumns = metaData.getColumnCount();

            printHeader(metaData, numColumns);
            printRecords(resultSet, numColumns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Print the attribute names
    public void printHeader(ResultSetMetaData metaData, int numColumns) throws SQLException {
        for (int i = 1; i <= numColumns; i++) {
            if (i > 1)
                System.out.print(",  ");
            System.out.print(metaData.getColumnName(i));
        }
        System.out.println();
    }

    // Print the attribute values for all tuples in the result
    public void printRecords(ResultSet resultSet, int numColumns) throws SQLException {
        String columnValue;
        while (resultSet.next()) {
            for (int i = 1; i <= numColumns; i++) {
                if (i > 1)
                    System.out.print(",  ");
                columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }

    // Insert into any table, any values from data passed in as String parameters
    public void insert(String table, String values) {
        String q = "INSERT into " + table + " values (" + values + ")";
        try {
            statement.executeUpdate(q);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

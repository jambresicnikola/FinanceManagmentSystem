package hr.java.financemanagementsystem.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection connectToDatabase() throws IOException, SQLException {
        Properties props = new Properties();

        try (FileReader reader = new FileReader("database.properties")) {
            props.load(reader);
        }

        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password"));
    }
}

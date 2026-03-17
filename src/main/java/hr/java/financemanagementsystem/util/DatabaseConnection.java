package hr.java.financemanagementsystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for establishing a connection to the database.
 * Connection details are read from the {@code database.properties} file.
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private DatabaseConnection() {
    }

    /**
     * Reads connection properties and opens a new database connection.
     * @return an open {@link Connection} to the database
     * @throws IOException if the properties file cannot be read
     * @throws SQLException if the connection cannot be established
     */
    public static Connection connectToDatabase() throws IOException, SQLException {
        Properties props = new Properties();

        try (FileReader reader = new FileReader("database.properties")) {
            props.load(reader);
        }

        logger.debug("Connecting to database at: {}", props.getProperty("db.url"));
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password"));
    }
}

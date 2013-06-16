package net.a1337ism.util;

import net.a1337ism.RawrBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class SqliteDb extends Db {
    // Set up the logger stuff
    private static Logger logger    = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");

    public SqliteDb(String sDriverKey, String sUrlKey) {
        try {
            init(sDriverKey, sUrlKey);
        } catch (Exception ex) {
            logger.error(ex);
        }
        if (conn != null) {
            logger.debug("Connected OK using " + sDriverKey + " to " + sUrlKey);
        } else {
            logger.debug("Connection failed");
        }
    }

    public String sqlQuote(String i) {
        // Just a simple method to encapsulate a string with quotes that are destined to be used in an SQL statement.
        return "'" + i + "'";
    }
}
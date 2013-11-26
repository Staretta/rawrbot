package net.a1337ism.util;

import net.a1337ism.RawrBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqliteDb extends Db {
    // Set up the logger stuff
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

    public SqliteDb(String sDriverKey, String sUrlKey) {
        try {
            init(sDriverKey, sUrlKey);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        if (conn != null) {
            // logger.debug("Connected OK using " + sDriverKey + " to " + sUrlKey);
        } else {
            logger.debug("Connection failed for" + sDriverKey + " to " + sUrlKey);
        }
    }

    public static String sqlQuote(String i) {
        // Just a simple method to encapsulate a string with quotes that are destined to be used in an SQL statement.
        return "'" + i + "'";
    }
}
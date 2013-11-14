package net.a1337ism.modules;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.a1337ism.RawrBot;
import net.a1337ism.util.SqliteDb;

import org.apache.commons.dbutils.DbUtils;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rules extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger     = LoggerFactory.getLogger(RawrBot.class);

    // Set up the database stuff
    String                dbUrl      = "jdbc:sqlite:data/rules.db";
    String                dbDriver   = "org.sqlite.JDBC";

    // Creates the database tables if they haven't been created yet.
    private boolean       tableExist = createTableIfNotExist();

    // Going with a private error code, because I can't think of a better way to do error reporting, atm.
    private byte          ERROR      = 0;

    private SqliteDb dbConnect() {
        // Open a connection to the database.
        SqliteDb db = new SqliteDb(dbDriver, dbUrl);
        return db;
    }

    private boolean createTableIfNotExist() {
        // Set up the connection,
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        try {
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "RULES", null);
            if (tables.next()) {
                // Table exists
                // logger.info("Table Greeting exists");
                return true;
            } else {
                // Table doesn't exist
                logger.info("Table Rules doesn't exist.");
                db.executeStmt("CREATE TABLE Rules (ID INTEGER PRIMARY KEY, Line TEXT, Nickname TEXT, Date TEXT)");
                logger.info("Created table.");
                return true;
            }
        } catch (SQLException pass) {
            return false;
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private boolean addRule(String rule, String nickname, String date) {
        return false;
    }

    private boolean delRule(String ID) {
        return false;
    }

    private List<String> getRules() {
        return null;
    }

    public void onMessage(MessageEvent event) throws Exception {
        if (event.getMessage().toLowerCase().trim().startsWith("!rules")) {
            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;
            // ircUtil.sendNotice(event, "someMessage");
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {

    }

}
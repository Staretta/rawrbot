package net.a1337ism.modules;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.a1337ism.RawrBot;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.SqliteDb;
import net.a1337ism.util.ircUtil;

import org.apache.commons.dbutils.DbUtils;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Greeter extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger     = LoggerFactory.getLogger(RawrBot.class);

    // Database stuff. Should probably move to the config file at some point.
    private String        dbUrl      = "jdbc:sqlite:data/greeter.db";
    private String        dbDriver   = "org.sqlite.JDBC";

    // Creates the database tables if they haven't been created yet.
    private boolean       tableExist = createTableIfNotExist();

    // Going with a private error code, because I can't think of a better way to do error reporting, atm.
    private byte          ERROR      = 0;

    // Settings and shiz?
    private boolean       RandGreet  = false;

    private SqliteDb dbConnect() {
        // Open a connection to the database.
        SqliteDb db = new SqliteDb(dbDriver, dbUrl);
        return db;
    }

    private boolean createTableIfNotExist() {
        // Set up the connection,
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet greeting = null;
        ResultSet settings = null;
        try {
            DatabaseMetaData dbm = conn.getMetaData();
            greeting = dbm.getTables(null, null, "GREETING", null);
            settings = dbm.getTables(null, null, "SETTINGS", null);
            if (!greeting.next() && !settings.next()) {
                // Table doesn't exist
                logger.info("Tables for Greetings do not exist, creating...");
                db.executeStmt("CREATE TABLE Greeting (ID INTEGER PRIMARY KEY, Line TEXT, Nickname TEXT, Date TEXT)");
                db.executeStmt("CREATE TABLE Settings (Setting TEXT, Bool BOOLEAN, Text TEXT, Int INTEGER)");
                logger.info("Created table.");
                return true;
            } else {
                return true;
            }
        } catch (SQLException pass) {
            return false;
        } finally {
            DbUtils.closeQuietly(greeting);
            DbUtils.closeQuietly(settings);
            DbUtils.closeQuietly(conn);
        }
    }

    private void loadSettings() {

    }

    private boolean addGreeting(String greeting, String nickname, String date) {
        // Set up the connection, and the statement
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        PreparedStatement insGreet = null;
        try {
            insGreet = conn.prepareStatement("INSERT INTO Greeting ( 'Line', 'Nickname', 'Date' ) VALUES (?,?,?)");
            insGreet.setString(1, greeting);
            insGreet.setString(2, nickname);
            insGreet.setString(3, date);
            insGreet.execute();
            return true;
        } catch (SQLException ex) {
            ERROR = 3;
        } finally {
            DbUtils.closeQuietly(insGreet);
            DbUtils.closeQuietly(conn);
        }
        return false;
    }

    private boolean delGreeting(String ID) {
        // Set up the connection, and some variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        PreparedStatement selGreet = null;
        PreparedStatement delGreet = null;
        ResultSet rs = null;

        try {
            // Try to parse input to see if it's a valid number
            int inputNumber = Integer.parseInt(ID);

            // Now we need to see if there the number relates to a greeting in the db
            selGreet = conn.prepareStatement("SELECT * FROM Greeting WHERE ID = ?");
            selGreet.setInt(1, inputNumber);
            rs = selGreet.executeQuery();

            if (rs.isBeforeFirst()) {
                delGreet = conn.prepareStatement("DELETE FROM Greeting WHERE ID = ?");
                delGreet.setInt(1, inputNumber);
                delGreet.execute();
                return true;
            } else {
                ERROR = 2;
            }
        } catch (SQLException ex) {
            ERROR = 3;
        } catch (NumberFormatException nfe) {
            ERROR = 4;
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(delGreet);
            DbUtils.closeQuietly(selGreet);
            DbUtils.closeQuietly(conn);
        }
        return false;
    }

    private List<String> getGreeting() {
        // Set up the connection and some variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet rs = null;
        PreparedStatement selGreet = null;

        try {
            // Need to see if there is a greeting in the database, and if there is, grab a random one
            // selGreet = conn.prepareStatement("SELECT * FROM Greeting ORDER BY RANDOM() LIMIT 1");
            // rs = selGreet.executeQuery();
            //
            // if (rs.isBeforeFirst()) {
            // List<String> message = rsParser(rs);
            // Object[] finalMsg = message.toArray();
            // return finalMsg[0].toString();

            if (RandGreet) {
                selGreet = conn.prepareStatement("SELECT * FROM Greeting ORDER BY RANDOM() LIMIT 1");
            } else {
                selGreet = conn.prepareStatement("SELECT * FROM Greeting ORDER BY ID");
            }
            rs = selGreet.executeQuery();

            if (rs.isBeforeFirst()) {
                List<String> message = rsParser(rs);
                return message;
            } else {
                ERROR = 2;
            }
        } catch (SQLException ex) {
            ERROR = 3;
        } finally {
            DbUtils.closeQuietly(conn, selGreet, rs);
        }
        return null;
    }

    private String getGreeting(String ID) {
        // Set up the connection and the ResultSet
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet rs = null;
        PreparedStatement selGreet = null;

        try {
            // Parse input to see if it's a valid number
            int inputNumber = Integer.parseInt(ID);

            // Now we need to see if there the number relates to a greeting in the db
            selGreet = conn.prepareStatement("SELECT * FROM Greeting WHERE ID = ?");
            selGreet.setInt(1, inputNumber);
            rs = selGreet.executeQuery();

            if (rs.isBeforeFirst()) {
                List<String> message = rsParser(rs, true);
                Object[] finalMsg = message.toArray();
                return finalMsg[0].toString();
            } else {
                ERROR = 2;
            }
        } catch (SQLException ex) {
            ERROR = 3;
        } catch (NumberFormatException nfe) {
            ERROR = 4;
        } finally {
            DbUtils.closeQuietly(conn, selGreet, rs);
        }
        return null;
    }

    private List<String> getGreetingAll() {
        // Set up the connection and the ResultSet
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet rs = null;
        PreparedStatement selGreet = null;

        try {
            // Get all the greetings from the database
            selGreet = conn.prepareStatement("SELECT * FROM Greeting ORDER BY ID");
            rs = selGreet.executeQuery();

            if (rs.isBeforeFirst()) {
                List<String> message = rsParser(rs, true);
                return message;
            } else {
                ERROR = 2;
            }
        } catch (SQLException ex) {
            ERROR = 3;
        } finally {
            DbUtils.closeQuietly(conn, selGreet, rs);
        }
        return null;
    }

    /**
     * Parses a result set containing entries for Greetings.
     * 
     * @param resultSet
     *            A result set containing ID, Nickname, Line, and Date
     * @throws SQLException
     *             If any Exceptions might be thrown, throw them up and let MessageEvent handle it
     * @return a list of lines to be sent to the user
     * 
     */
    private List<String> rsParser(ResultSet resultSet) throws SQLException {
        return rsParser(resultSet, false);
    }

    /**
     * Parses a result set containing entries for Greetings.
     * 
     * @param resultSet
     *            A result set containing ID, Nickname, Line, and Date
     * @param operator
     *            if the user is an operator, we want to display data differently
     * @throws SQLException
     *             If any Exceptions might be thrown, throw them up and let MessageEvent handle it
     * @return a list of lines to be sent to the user
     * 
     */
    private List<String> rsParser(ResultSet resultSet, boolean operator) throws SQLException {
        // Function to build a nice list from the results of the various searches.
        List<String> message = new ArrayList<>();
        while (resultSet.next()) {
            int ID = resultSet.getInt("ID");
            String line = resultSet.getString("Line");
            String nickname = resultSet.getString("Nickname");
            String date = resultSet.getString("Date");
            if (operator) {
                message.add("[" + date + "] ID: " + ID + " | " + nickname + ": " + line);
            } else {
                message.add(line);
            }
        }
        return message;
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // If message starts with !greeter and user is an operator of the channel
        if (event.getMessage().trim().toLowerCase().startsWith("!greeter") && ircUtil.isOP(event, RawrBot.irc_channel)) {
            // Get the message and split it into parameters
            String[] param = event.getMessage().trim().split("\\s", 3);

            if (param.length == 1) {
                // If they entered !greeter -all, display all the current greetings being used.
                List<String> greeting = getGreetingAll();

                if (greeting != null) {
                    Iterator itr = greeting.iterator();
                    while (itr.hasNext()) {
                        ircUtil.sendMessage(event, itr.next().toString());
                    }
                } else if (ERROR == 3) {
                    String sqlError = "Error " + ERROR + ": SQL Error.";
                    ircUtil.sendMessage(event, sqlError);
                } else {
                    String none = "There are no greetings.";
                    ircUtil.sendMessage(event, none);
                }
            } else if (param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                // If they entered !greeter, send the user the help information
                String[] syntax = {
                        "!greeter : Displays all greetings currently in use.",
                        "!greeter -add <greeting> : Adds a greeting to the list to be displayed to a user when they join the channel.",
                        "!greeter -del <ID> : Deletes a greeting with the specified ID number.",
                        "!greeter -id <ID> : Displays greeting associated with ID.",
                        "!greeter -help : Displays this help information." };
                for (int i = 0; i < syntax.length; i++) {
                    ircUtil.sendMessage(event, syntax[i]);
                }
            } else if (param[1].equalsIgnoreCase("-add") || param[1].equalsIgnoreCase("-a")) {
                if (param.length == 2) {
                    // If they entered !greeter -add, send them the correct syntax.
                    String syntax = "!greeter -add <greeting>: Adds a greeting to the list to be displayed to a user when they join the channel.";
                    ircUtil.sendMessage(event, syntax);
                } else {
                    // We know they entered a line, add it to the database so it can be displayed to users upon joining
                    // the channel.
                    String line = param[2];
                    boolean result = addGreeting(line, event.getUser().getNick(), MiscUtil.dateFormat());

                    if (result) {
                        String success = "Your greeting has been added.";
                        ircUtil.sendMessage(event, success);
                    } else {
                        String sqlError = "Error " + ERROR + ": Failed to add your greeting.";
                        ircUtil.sendMessage(event, sqlError);
                    }
                }
            } else if (param[1].equalsIgnoreCase("-del") || param[1].equalsIgnoreCase("-d")) {
                if (param.length == 2) {
                    // If they entered !greeter -del, send the user the correct syntax.
                    String syntax = "!greeter -del <ID> : Deletes a greeting with the specified ID number.";
                    ircUtil.sendMessage(event, syntax);
                } else {
                    // We know they entered an ID, so lets see if we can delete it.
                    String ID = param[2];
                    boolean result = delGreeting(ID);

                    // 2 = No greeting, 3 = SQL Exception, 4 = Not a number
                    if (result) {
                        String success = "Sucessfully deleted greeting with ID: " + ID;
                        ircUtil.sendMessage(event, success);
                    } else if (ERROR == 3) {
                        String sqlError = "Error " + ERROR + ": SQL Error.";
                        ircUtil.sendMessage(event, sqlError);
                    } else if (ERROR == 4) {
                        String nonNumber = "Error " + ERROR + ": " + ID + " is not a valid number.";
                        ircUtil.sendMessage(event, nonNumber);
                    } else {
                        String none = "No greeting for ID: " + ID;
                        ircUtil.sendMessage(event, none);
                    }
                }
            } else if (param[1].equalsIgnoreCase("-id")) {
                if (param.length == 2) {
                    // If they entered !greeter -id, then send the user the correct syntax.
                    String syntax = "!greeter -id <ID> : Displays greeting associated with ID.";
                    ircUtil.sendMessage(event, syntax);
                } else {
                    // We know they entered an ID, lets get the corresponding greeting for that id.
                    String ID = param[2];
                    String result = getGreeting(ID);

                    // 2 = No greeting, 3 = SQL Exception, 4 = Not a number
                    if (result != null) {
                        ircUtil.sendMessage(event, result);
                    } else if (ERROR == 3) {
                        String sqlError = "Error " + ERROR + ": SQL Error.";
                        ircUtil.sendMessage(event, sqlError);
                    } else if (ERROR == 4) {
                        String nonNumber = "Error " + ERROR + ": " + ID + " is not a valid number.";
                        ircUtil.sendMessage(event, nonNumber);
                    } else {
                        String none = "No greeting for ID: " + ID;
                        ircUtil.sendMessage(event, none);
                    }
                }
            }
        }
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception {
        // Need to filter out the bot joining channel from other people joining channel.
        if (!event.getUser().getNick().equals(event.getBot().getNick())) {
            // Send a greeting to users who join the channel.
            List<String> greeting = getGreeting();

            if (greeting != null) {
                Iterator itr = greeting.iterator();
                while (itr.hasNext()) {
                    event.getUser().send().notice(itr.next().toString());
                }
            }
        }
    }
}

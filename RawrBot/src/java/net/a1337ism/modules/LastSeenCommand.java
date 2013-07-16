package net.a1337ism.modules;

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
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastSeenCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger        = LoggerFactory.getLogger(RawrBot.class);

    // Set up the database stuff
    String                sUrlString    = "jdbc:sqlite:data/lastseen.db";
    String                sDriverString = "org.sqlite.JDBC";

    // SqliteDb db = new SqliteDb("org.sqlite.JDBC", sUrlString);

    public void onMessage(MessageEvent event) throws Exception {
        // Get the last message to the channel and update the database
        setLastSeenChannel(event);
        try {
            // Check if message starts with !lastseen
            if (event.getMessage().trim().toLowerCase().startsWith("!lastseen")) {

                // If they are rate limited, then return.
                if (RateLimiter.isRateLimited(event.getUser().getNick()))
                    return;

                // Split the message, so we can get the different parameters
                String[] param = event.getMessage().trim().split("\\s", 3);
                if (param.length == 1) {
                    // If message contains only 1 string, then send them the correct syntax
                    String lastseen_syntax = "!lastseen <Nickname> : Displays the last thing a user said, and when the user was last seen.";
                    ircUtil.sendMessage(event, lastseen_syntax);
                } else if (param[1].compareToIgnoreCase("-help") == 0 || param[1].compareToIgnoreCase("-h") == 0) {
                    // If message equals -help, or -h then do this awesome stuff! :D
                    logger.info("stuff");
                    String[] lastseen_help = {
                            "!lastseen <Nickname> : Displays the last thing a user said, based on their nickname, and when the user was last seen.",
                            "!lastseen -nick <Nickname> : Displays the last thing a user said, based on their nickname.",
                            "!lastseen -host <User's Hostname> : Displays the last thing a user said, based on their hostname.",
                            "Example: If the username and hostname is ~Bot@serenity.1337ism.net you want to enter everything after the @ sign." };
                    for (int i = 0; i < lastseen_help.length; i++) {
                        ircUtil.sendMessage(event, lastseen_help[i]);
                    }
                } else if (param[1].compareToIgnoreCase("-host") == 0) {
                    // If the second parameter equals -host, then do this stuff.
                    if (param.length == 2) { // If message only contains !lastseen -host, then send correct syntax
                        String lastseen_syntax = "!lastseen -host <User's Hostname> : Displays the last thing a user said, based on their hostname.";
                        ircUtil.sendMessage(event, lastseen_syntax);
                    } else { // Otherwise search for the host string, and send them the results
                        String hostSearch = param[2].toString();
                        List sentMsg = getLastSeenHost(hostSearch);
                        if (sentMsg == null) {
                            String lastseenError = hostSearch + " hasn't said anything.";
                            ircUtil.sendMessage(event, lastseenError);
                        } else {
                            Iterator itr = sentMsg.iterator();
                            while (itr.hasNext()) {
                                ircUtil.sendMessage(event, itr.next().toString());
                            }
                        }
                    }
                } else if (param[1].compareToIgnoreCase("-nick") == 0) {
                    // if message contains -nick, then do this stuff
                    if (param.length == 2) { // If message only contains !lastseen -nick, then send correct syntax
                        String lastseen_syntax = "!lastseen -nick <Nickname> : Displays the last thing a user said, based on their nickname.";
                        ircUtil.sendMessage(event, lastseen_syntax);
                    } else { // Otherwise search for the nick string, and send them the results
                        String nickSearch = param[2].toString();
                        List sentMsg = getLastSeenNick(nickSearch);
                        if (sentMsg == null) {
                            String lastseenError = nickSearch + " hasn't said anything.";
                            ircUtil.sendMessage(event, lastseenError);
                        } else {
                            Iterator itr = sentMsg.iterator();
                            while (itr.hasNext()) {
                                ircUtil.sendMessage(event, itr.next().toString());
                            }
                        }
                    }
                } else {
                    // Otherwise, we know they want to search a nickname, and send them the results.
                    String nickSearch = param[1].toString();
                    List sentMsg = getLastSeenNick(nickSearch);
                    if (sentMsg == null) {
                        String lastseenError = nickSearch + " hasn't said anything.";
                        ircUtil.sendMessage(event, lastseenError);
                    } else {
                        Iterator itr = sentMsg.iterator();
                        while (itr.hasNext()) {
                            ircUtil.sendMessage(event, itr.next().toString());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        try {
            // Check if message starts with !lastseen
            if (event.getMessage().trim().toLowerCase().startsWith("!lastseen")) {
                // Spilt the message, so we can get the different parameters
                String[] param = event.getMessage().trim().split("\\s", 3);
                if (param.length == 1) {
                    // If message contains only 1 string, then send them the correct syntax
                    String lastseen_syntax = "!lastseen <Nickname> : Displays the last thing a user said, and when the user was last seen.";
                    ircUtil.sendMessage(event, lastseen_syntax);
                } else if (param[1].compareToIgnoreCase("-help") == 0 || param[1].compareToIgnoreCase("-h") == 0) {
                    // If message equals -help, or -h then do this awesome stuff! :D
                    logger.info("stuff");
                    String[] lastseen_help = {
                            "!lastseen <Nickname> : Displays the last thing a user said, based on their nickname, and when the user was last seen.",
                            "!lastseen -nick <Nickname> : Displays the last thing a user said, based on their nickname.",
                            "!lastseen -host <User's Hostname> : Displays the last thing a user said, based on their hostname.",
                            "Example: If the username and hostname is ~Bot@serenity.1337ism.net you want to enter everything after the @ sign." };
                    for (int i = 0; i < lastseen_help.length; i++) {
                        ircUtil.sendMessage(event, lastseen_help[i]);
                    }
                } else if (param[1].compareToIgnoreCase("-host") == 0) {
                    // If the second parameter equals -host, then do this stuff.
                    if (param.length == 2) { // If message only contains !lastseen -host, then send correct syntax
                        String lastseen_syntax = "!lastseen -host <User's Hostname> : Displays the last thing a user said, based on their hostname.";
                        ircUtil.sendMessage(event, lastseen_syntax);
                    } else { // Otherwise search for the host string, and send them the results
                        String hostSearch = param[2].toString();
                        List sentMsg = getLastSeenHost(hostSearch);
                        if (sentMsg == null) {
                            String lastseenError = hostSearch + " hasn't said anything.";
                            ircUtil.sendMessage(event, lastseenError);
                        } else {
                            Iterator itr = sentMsg.iterator();
                            while (itr.hasNext()) {
                                ircUtil.sendMessage(event, itr.next().toString());
                            }
                        }
                    }
                } else if (param[1].compareToIgnoreCase("-nick") == 0) {
                    // if message contains -nick, then do this stuff
                    if (param.length == 2) { // If message only contains !lastseen -nick, then send correct syntax
                        String lastseen_syntax = "!lastseen -nick <Nickname> : Displays the last thing a user said, based on their nickname.";
                        ircUtil.sendMessage(event, lastseen_syntax);
                    } else { // Otherwise search for the nick string, and send them the results
                        String nickSearch = param[2].toString();
                        List sentMsg = getLastSeenNick(nickSearch);
                        if (sentMsg == null) {
                            String lastseenError = nickSearch + " hasn't said anything.";
                            ircUtil.sendMessage(event, lastseenError);
                        } else {
                            Iterator itr = sentMsg.iterator();
                            while (itr.hasNext()) {
                                ircUtil.sendMessage(event, itr.next().toString());
                            }
                        }
                    }
                } else {
                    // Otherwise, we know they want to search a nickname, and send them the results.
                    String nickSearch = param[1].toString();
                    List sentMsg = getLastSeenNick(nickSearch);
                    if (sentMsg == null) {
                        String lastseenError = nickSearch + " hasn't said anything.";
                        ircUtil.sendMessage(event, lastseenError);
                    } else {
                        Iterator itr = sentMsg.iterator();
                        while (itr.hasNext()) {
                            ircUtil.sendMessage(event, itr.next().toString());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onAction(ActionEvent event) throws Exception {
        // Get the last message to the channel and update the database
        setLastSeenAction(event);
    }

    private SqliteDb dbConnect() {
        SqliteDb db = new SqliteDb(sDriverString, sUrlString);
        return db;
    }

    private List<String> getLastSeenNick(String nick) {
        // Need to send a default for limit, if not specified.
        return getLastSeenNick(nick, 5);
    }

    private List<String> getLastSeenNick(String nick, int limit) {
        ResultSet resultSet = null; // If somehow the statement dies, it'll return null.
        List<String> message = new ArrayList<>();
        SqliteDb db = dbConnect(); // Connect to the database.
        PreparedStatement selectNickname = null;

        try {
            selectNickname = db.getConnection().prepareStatement(
                    "SELECT * FROM lastseen_nick WHERE Nickname LIKE ? ORDER BY Time DESC LIMIT ?");
            selectNickname.setString(1, "%" + nick + "%");
            selectNickname.setInt(2, limit);
            resultSet = selectNickname.executeQuery();
            message = getLastSeen(resultSet, limit);
        } catch (SQLException ex) {
            logger.error(ex.toString());
        } finally {
            DbUtils.closeQuietly(resultSet); // Close the result set
            DbUtils.closeQuietly(selectNickname); // close the prepared statement.
            DbUtils.closeQuietly(db.getConnection()); // After everything is done, close the connection to the database.
        }

        return message;
    }

    private List<String> getLastSeen(ResultSet resultSet, int limit) throws SQLException {
        // Most of the getLastSeen stuff happens here.
        // TODO: Find a way to make both nick and host into one function instead of two. Since it's more or less the
        // same method except for one line, searching for hostname instead of nickname, and vice versa.
        List<String> message = new ArrayList<>();
        if (!resultSet.isBeforeFirst())
            return null;
        while (resultSet.next()) {
            String nickname = resultSet.getString("Nickname");
            String hostname = resultSet.getString("Hostname");
            String line = resultSet.getString("Line");
            int lastTime = resultSet.getInt("Time");
            boolean userAction = resultSet.getBoolean("Action");
            String time = MiscUtil.timeFormat(lastTime);
            if (userAction) {
                message.add("[Last seen " + time + " ago] * " + nickname + " " + line);
            } else {
                message.add("[Last seen " + time + " ago] <" + nickname + "> " + line);
            }
        }
        if (message.size() == limit) {
            String newMessage = "Please be more specific with your search if you want better results. Limit of "
                    + limit + " reached.";
            message.add(0, newMessage);
        }
        return message;
    }

    private List<String> getLastSeenHost(String host) {
        // Need to send a default for limit, if not specified.
        return getLastSeenHost(host, 5);
    }

    private List<String> getLastSeenHost(String host, int limit) {
        ResultSet resultSet = null; // If somehow the statement dies, it'll return null.
        List<String> message = new ArrayList<>();
        SqliteDb db = dbConnect(); // Connect to the database.
        PreparedStatement selectHostname = null;

        try {
            selectHostname = db.getConnection().prepareStatement(
                    "SELECT * FROM lastseen_host WHERE Hostname LIKE ? ORDER BY Time DESC LIMIT ?");
            selectHostname.setString(1, "%" + host + "%");
            selectHostname.setInt(2, limit);
            resultSet = selectHostname.executeQuery();
            message = getLastSeen(resultSet, limit);
        } catch (SQLException ex) {
            logger.error(ex.toString());
        } finally {
            DbUtils.closeQuietly(resultSet); // Close the result set
            DbUtils.closeQuietly(selectHostname); // close the prepared statement.
            DbUtils.closeQuietly(db.getConnection()); // After everything is done, close the connection to the database.
        }

        return message;
    }

    private void setLastSeenChannel(MessageEvent event) {
        // Filter out commands
        if (event.getMessage().startsWith("!"))
            return;

        String nickname = event.getUser().getNick();
        String hostname = event.getUser().getLogin() + "@" + event.getUser().getHostmask();
        String message = event.getMessage();
        message = MiscUtil.redactURL(message);
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        boolean action = false;
        // Throw some values to lastseen function so that we save space probably!
        setLastSeen(nickname, hostname, message, timeNow, action);

    }

    private void setLastSeenAction(ActionEvent event) {
        // Filter out commands
        if (event.getMessage().startsWith("!"))
            return;

        String nickname = event.getUser().getNick();
        String hostname = event.getUser().getLogin() + "@" + event.getUser().getHostmask();
        String message = event.getMessage();
        message = MiscUtil.redactURL(message);
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        boolean action = true;
        // Throw some values to lastseen function so that we save space probably!
        setLastSeen(nickname, hostname, message, timeNow, action);
    }

    private void setLastSeen(String nickname, String hostname, String message, int timeNow, boolean action) {
        ResultSet resultSet = null;
        SqliteDb db = dbConnect();
        PreparedStatement updateHostname = null;
        PreparedStatement updateNickname = null;
        PreparedStatement insertHostname = null;
        PreparedStatement insertNickname = null;

        try {
            db.getConnection().setAutoCommit(false);
            updateHostname = db.getConnection().prepareStatement(
                    "UPDATE lastseen_host SET Nickname = ? , Time = ? , Line = ? , Action = ? WHERE Hostname = ?");
            updateNickname = db.getConnection().prepareStatement(
                    "UPDATE lastseen_nick SET Hostname = ? , Time = ? , Line = ? , Action = ? WHERE Nickname = ?");
            insertHostname = db.getConnection().prepareStatement("INSERT INTO lastseen_host VALUES (?,?,?,?,?)");
            insertNickname = db.getConnection().prepareStatement("INSERT INTO lastseen_nick VALUES (?,?,?,?,?)");

            // Insert by nickname
            resultSet = db.executeQry("SELECT * FROM lastseen_nick WHERE Nickname = " + db.sqlQuote(nickname));
            // If the set is empty, then the nickname doesn't exist in the database.
            if (!resultSet.isBeforeFirst()) {
                insertNickname.setString(1, nickname);
                insertNickname.setString(2, hostname);
                insertNickname.setString(3, message);
                insertNickname.setInt(4, timeNow);
                // If it's an action message, then insert as an action.
                if (action == true)
                    insertNickname.setBoolean(5, true);
                else
                    insertNickname.setBoolean(5, false);
                insertNickname.executeUpdate();
            } else {
                updateNickname.setString(1, hostname);
                updateNickname.setInt(2, timeNow);
                updateNickname.setString(3, message);
                updateNickname.setString(5, nickname);
                // If it's an action message, then insert as an action.
                if (action == true)
                    updateNickname.setBoolean(4, true);
                else
                    updateNickname.setBoolean(4, false);
                updateNickname.executeUpdate();
            }

            // Insert by hostname
            resultSet = db.executeQry("SELECT * FROM lastseen_host WHERE Hostname = " + db.sqlQuote(hostname));
            // If the set is emtpy, then the hostname doesn't exist in the database.
            if (!resultSet.isBeforeFirst()) {
                insertHostname.setString(1, nickname);
                insertHostname.setString(2, hostname);
                insertHostname.setString(3, message);
                insertHostname.setInt(4, timeNow);
                // If it's an action message, then insert as an action.
                if (action == true)
                    insertHostname.setBoolean(5, true);
                else
                    insertHostname.setBoolean(5, false);
                insertHostname.executeUpdate();
            } else {
                updateHostname.setString(1, nickname);
                updateHostname.setInt(2, timeNow);
                updateHostname.setString(3, message);
                updateHostname.setString(5, hostname);
                // If it's an action message, then insert as an action.
                if (action == true)
                    updateHostname.setBoolean(4, true);
                else
                    updateHostname.setBoolean(4, false);
                updateHostname.executeUpdate();
            }
        } catch (SQLException ex) {
            if (db.getConnection() != null) {
                try {
                    logger.error("Lastseen DB Update is being rolled back.");
                    DbUtils.rollback(db.getConnection());
                } catch (SQLException ex1) {
                    logger.error("Lastseen DB rollback failed.");
                }
            }
        } finally {
            try {
                db.getConnection().setAutoCommit(true);
            } catch (SQLException ingore) {
            } finally {
                // Need to close the statements and result sets since we are done with them.
                DbUtils.closeQuietly(resultSet);
                DbUtils.closeQuietly(insertNickname);
                DbUtils.closeQuietly(updateNickname);
                DbUtils.closeQuietly(insertHostname);
                DbUtils.closeQuietly(updateHostname);
                DbUtils.commitAndCloseQuietly(db.getConnection()); // then we close the connection to the db.
            }
        }
    }
}
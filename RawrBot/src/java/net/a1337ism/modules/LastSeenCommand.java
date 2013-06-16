package net.a1337ism.modules;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.a1337ism.RawrBot;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.SqliteDb;
import net.a1337ism.util.ircUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class LastSeenCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger     = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT  = MarkerManager.getMarker("LOG_EVENT");

    // Set up the database stuff
    String                sUrlString = "jdbc:sqlite:data/lastseen.db";
    SqliteDb              sqlitedb   = new SqliteDb("org.sqlite.JDBC", sUrlString);

    public void onMessage(MessageEvent event) throws Exception {
        // Get the last message to the channel and update the database
        setLastSeenChannel(event);
        try {
            // Check if message starts with !lastseen
            if (event.getMessage().trim().toLowerCase().startsWith("!lastseen")) {
            	
            	// If they are rate limited, then return. 
                if (RateLimiter.isRateLimited(event.getUser().getNick()))
                    return;
                
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

    private List<String> getLastSeenNick(String nick) {
        // Need to send a default for limit, if not specified.
        return getLastSeenNick(nick, 5);
    }

    private List<String> getLastSeenNick(String nick, int limit) {
        ResultSet resultSet;
        String nickSearch = "%" + nick + "%";
        List<String> message = new ArrayList<>();
        if (nick != null) {
            try {
                resultSet = sqlitedb.executeQry("SELECT * FROM lastseen_nick WHERE Nickname LIKE "
                        + sqlitedb.sqlQuote(nickSearch) + " ORDER BY Time DESC LIMIT " + limit);
                message = getLastSeen(resultSet, limit);
            } catch (SQLException ex) {
                logger.error(ex);
            }
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
        ResultSet resultSet;
        String hostSearch = "%" + host + "%";
        List<String> message = new ArrayList<>();
        if (host != null) {
            try {
                resultSet = sqlitedb.executeQry("SELECT * FROM lastseen_nick WHERE Hostname LIKE "
                        + sqlitedb.sqlQuote(hostSearch) + " ORDER BY Time DESC LIMIT " + limit);
                message = getLastSeen(resultSet, limit);
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }
        return message;
    }

    private void setLastSeenChannel(MessageEvent event) {
        // Filter out commands
        if (event.getMessage().startsWith("!"))
            return;

        if (sqlitedb.getConnection() != null) {
            String nickname = event.getUser().getNick();
            String hostname = event.getUser().getLogin() + "@" + event.getUser().getHostmask();
            String message = event.getMessage();
            int timeNow = (int) (System.currentTimeMillis() / 1000);
            boolean action = false;
            // Throw some values to lastseen function so that we save space probably!
            setLastSeen(nickname, hostname, message, timeNow, action);
        }
    }

    private void setLastSeenAction(ActionEvent event) {
        // Filter out commands
        if (event.getMessage().startsWith("!"))
            return;

        if (sqlitedb.getConnection() != null) {
            String nickname = event.getUser().getNick();
            String hostname = event.getUser().getLogin() + "@" + event.getUser().getHostmask();
            String message = event.getMessage();
            int timeNow = (int) (System.currentTimeMillis() / 1000);
            boolean action = true;
            // Throw some values to lastseen function so that we save space probably!
            setLastSeen(nickname, hostname, message, timeNow, action);
        }
    }

    private void setLastSeen(String nickname, String hostname, String message, int timeNow, boolean action) {
        ResultSet resultSet = null;
        try {
            // Insert data by nickname
            resultSet = sqlitedb.executeQry("SELECT * FROM lastseen_nick WHERE Nickname = "
                    + sqlitedb.sqlQuote(nickname));
            if (!resultSet.isBeforeFirst()) {
                // If the result set is empty, then the nickname doesn't exist in the database.
                // Insert the new nickname into the database, along with some additional information.
                if (action == true) {
                    sqlitedb.executeStmt("INSERT INTO lastseen_nick VALUES(" + sqlitedb.sqlQuote(nickname) + ", "
                            + sqlitedb.sqlQuote(hostname) + ", " + sqlitedb.sqlQuote(message) + ", " + timeNow + ", 1)");
                } else {
                    sqlitedb.executeStmt("INSERT INTO lastseen_nick VALUES(" + sqlitedb.sqlQuote(nickname) + ", "
                            + sqlitedb.sqlQuote(hostname) + ", " + sqlitedb.sqlQuote(message) + ", " + timeNow + ", 0)");
                }
            } else {
                // If the result set contains a nickname, then update the information instead.
                if (action == true) {
                    sqlitedb.executeStmt("UPDATE lastseen_nick SET Hostname = " + sqlitedb.sqlQuote(hostname)
                            + ", Time = " + timeNow + ", Line = " + sqlitedb.sqlQuote(message)
                            + ", Action = 1 WHERE Nickname = " + sqlitedb.sqlQuote(nickname));
                } else {
                    sqlitedb.executeStmt("UPDATE lastseen_nick SET Hostname = " + sqlitedb.sqlQuote(hostname)
                            + ", Time = " + timeNow + ", Line = " + sqlitedb.sqlQuote(message)
                            + ", Action = 0 WHERE Nickname = " + sqlitedb.sqlQuote(nickname));
                }
            }

            // Insert data by hostname
            resultSet = sqlitedb.executeQry("SELECT * FROM lastseen_host WHERE Hostname = "
                    + sqlitedb.sqlQuote(hostname));
            if (!resultSet.isBeforeFirst()) {
                // If the result set is empty, then the hostname doesn't exist in the database.
                // Insert the new hostname into the database, along with some additional information.
                if (action == true) {
                    sqlitedb.executeStmt("INSERT INTO lastseen_host VALUES(" + sqlitedb.sqlQuote(nickname) + ", "
                            + sqlitedb.sqlQuote(hostname) + ", " + sqlitedb.sqlQuote(message) + ", " + timeNow + ", 1)");
                } else {
                    sqlitedb.executeStmt("INSERT INTO lastseen_host VALUES(" + sqlitedb.sqlQuote(nickname) + ", "
                            + sqlitedb.sqlQuote(hostname) + ", " + sqlitedb.sqlQuote(message) + ", " + timeNow + ", 0)");
                }
            } else {
                // If the result set contains a nickname, then update the information instead.
                if (action == true) {
                    sqlitedb.executeStmt("UPDATE lastseen_host SET Nickname = " + sqlitedb.sqlQuote(nickname)
                            + ", Time = " + timeNow + ", Line = " + sqlitedb.sqlQuote(message)
                            + ", Action = 1 WHERE Hostname = " + sqlitedb.sqlQuote(hostname));
                } else {
                    sqlitedb.executeStmt("UPDATE lastseen_host SET Nickname = " + sqlitedb.sqlQuote(nickname)
                            + ", Time = " + timeNow + ", Line = " + sqlitedb.sqlQuote(message)
                            + ", Action = 0 WHERE Hostname = " + sqlitedb.sqlQuote(hostname));
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }
}
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
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastSeenCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger        = LoggerFactory.getLogger(RawrBot.class);

    // Set up the database stuff
    private String        sUrlString    = "jdbc:sqlite:data/lastseen.db";
    private String        sDriverString = "org.sqlite.JDBC";

    private boolean       tableExist    = createTableIfNotExist();

    private SqliteDb dbConnect() {
        // Open a connection to the database.
        SqliteDb db = new SqliteDb(sDriverString, sUrlString);
        return db;
    }

    private boolean createTableIfNotExist() {
        // Set up a connection
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tableNick = meta.getTables(null, null, "LASTSEEN_NICK", null);
            ResultSet tableHost = meta.getTables(null, null, "LASTSEEN_HOST", null);
            if (!tableNick.next() && !tableHost.next()) {
                // Tables do not exist
                logger.info("Lastseen tables do not exist, creating.");
                db.executeStmt("CREATE TABLE lastseen_nick(Nickname TEXT, Hostname TEXT, Line TEXT, Time INT, Action BOOLEAN)");
                db.executeStmt("CREATE TABLE lastseen_host(Nickname TEXT, Hostname TEXT, Line TEXT, Time INT, Action BOOLEAN)");
                logger.info("Lastseen tables created successfully");
                return true;
            } else {
                // Tables exists
                return true;
            }
        } catch (SQLException pass) {
            return false;
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private List<String> getLastSeenNick(String nick) {
        // Need to send a default for limit, if not specified.
        return getLastSeenNick(nick, 5);
    }

    private List<String> getLastSeenNick(String nick, int limit) {
        // Open connection to the database, and set up variables.
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        PreparedStatement selNick = null;
        ResultSet rs = null;
        List<String> message = new ArrayList<>();

        try {
            selNick = conn
                    .prepareStatement("SELECT * FROM lastseen_nick WHERE Nickname LIKE ? ORDER BY Time DESC LIMIT ?");
            selNick.setString(1, "%" + nick + "%");
            selNick.setInt(2, limit);
            rs = selNick.executeQuery();
            message = getLastSeen(rs, limit, message);
        } catch (SQLException ex) {
            return null;
        } finally {
            DbUtils.closeQuietly(conn, selNick, rs);
        }

        return message;
    }

    private List<String> getLastSeen(ResultSet rs, int limit, List<String> message) throws SQLException {
        // Most of the getLastSeen stuff happens here.
        // TODO: Find a way to make both nick and host into one function instead of two. Since it's more or less the
        // same method except for one line, searching for hostname instead of nickname, and vice versa.
        if (!rs.isBeforeFirst())
            return null;
        while (rs.next()) {
            String nickname = rs.getString("Nickname");
            String hostname = rs.getString("Hostname");
            String line = rs.getString("Line");
            int lastTime = rs.getInt("Time");
            boolean userAction = rs.getBoolean("Action");
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
        // Open a connection to the database, and set up variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        PreparedStatement selHost = null;
        ResultSet rs = null;
        List<String> message = new ArrayList<>();

        try {
            selHost = conn
                    .prepareStatement("SELECT * FROM lastseen_host WHERE Hostname LIKE ? ORDER BY Time DESC LIMIT ?");
            selHost.setString(1, "%" + host + "%");
            selHost.setInt(2, limit);
            rs = selHost.executeQuery();
            message = getLastSeen(rs, limit, message);
        } catch (SQLException ex) {
            return null;
        } finally {
            DbUtils.closeQuietly(conn, selHost, rs);
        }

        return message;
    }

    private void setLastSeen(String nickname, String hostname, String message, int timeNow, boolean action) {
        // Open a connection to the database, and set up some variables.
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet rs = null;
        PreparedStatement updHost = null;
        PreparedStatement updNick = null;
        PreparedStatement insHost = null;
        PreparedStatement insNick = null;

        try {
            conn.setAutoCommit(false);
            updHost = conn
                    .prepareStatement("UPDATE lastseen_host SET Nickname = ? , Time = ? , Line = ? , Action = ? WHERE Hostname = ?");
            updNick = conn
                    .prepareStatement("UPDATE lastseen_nick SET Hostname = ? , Time = ? , Line = ? , Action = ? WHERE Nickname = ?");
            insHost = conn.prepareStatement("INSERT INTO lastseen_host VALUES (?,?,?,?,?)");
            insNick = conn.prepareStatement("INSERT INTO lastseen_nick VALUES (?,?,?,?,?)");

            // Insert by nickname
            rs = db.executeQry("SELECT * FROM lastseen_nick WHERE Nickname = " + SqliteDb.sqlQuote(nickname));
            // If the set is empty, then the nickname doesn't exist in the database.
            if (!rs.isBeforeFirst()) {
                insNick.setString(1, nickname);
                insNick.setString(2, hostname);
                insNick.setString(3, message);
                insNick.setInt(4, timeNow);
                // If it's an action message, then insert as an action.
                if (action == true)
                    insNick.setBoolean(5, true);
                else
                    insNick.setBoolean(5, false);
                insNick.executeUpdate();
            } else {
                updNick.setString(1, hostname);
                updNick.setInt(2, timeNow);
                updNick.setString(3, message);
                updNick.setString(5, nickname);
                // If it's an action message, then insert as an action.
                if (action == true)
                    updNick.setBoolean(4, true);
                else
                    updNick.setBoolean(4, false);
                updNick.executeUpdate();
            }

            // Insert by hostname
            rs = db.executeQry("SELECT * FROM lastseen_host WHERE Hostname = " + SqliteDb.sqlQuote(hostname));
            // If the set is emtpy, then the hostname doesn't exist in the database.
            if (!rs.isBeforeFirst()) {
                insHost.setString(1, nickname);
                insHost.setString(2, hostname);
                insHost.setString(3, message);
                insHost.setInt(4, timeNow);
                // If it's an action message, then insert as an action.
                if (action == true)
                    insHost.setBoolean(5, true);
                else
                    insHost.setBoolean(5, false);
                insHost.executeUpdate();
            } else {
                updHost.setString(1, nickname);
                updHost.setInt(2, timeNow);
                updHost.setString(3, message);
                updHost.setString(5, hostname);
                // If it's an action message, then insert as an action.
                if (action == true)
                    updHost.setBoolean(4, true);
                else
                    updHost.setBoolean(4, false);
                updHost.executeUpdate();
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    logger.error("Lastseen DB Update is being rolled back.");
                    DbUtils.rollback(conn);
                } catch (SQLException ex1) {
                    logger.error("Lastseen DB rollback failed.");
                }
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ingore) {
            } finally {
                // Need to close the statements and result sets since we are done with them.
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(insNick);
                DbUtils.closeQuietly(insHost);
                DbUtils.closeQuietly(updNick);
                DbUtils.closeQuietly(updHost);
                DbUtils.commitAndCloseQuietly(conn); // then we close the connection to the db.
            }
        }
    }

    public void onMessage(MessageEvent event) throws Exception {
        // Get the last message to the channel and update the database
        if (!event.getMessage().startsWith("!")) {
            String nickname = event.getUser().getNick();
            String hostname = event.getUser().getLogin() + "@" + event.getUser().getHostmask();
            String message = event.getMessage();
            message = MiscUtil.redactURL(message);
            int seconds = MiscUtil.seconds();
            boolean action = false;
            // Throw some values to lastseen function so that we save space probably!
            setLastSeen(nickname, hostname, message, seconds, action);
        }

        // Check if message starts with !lastseen, and check if they are rate limited.
        if (event.getMessage().trim().toLowerCase().startsWith("!lastseen")
                && !RateLimiter.isRateLimited(event.getUser().getNick())) {

            // Split the message, so we can get the different parameters
            String[] param = event.getMessage().trim().split("\\s", 3);
            if (param.length == 1) {
                // If message contains only 1 string, then send them the correct syntax
                String lastseen_syntax = "!lastseen <Nickname> : Displays the last thing a user said, and when the user was last seen.";
                ircUtil.sendMessage(event, lastseen_syntax);
            } else if (param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                // If the second parameter is -help, send them the help information.
                logger.info("stuff");
                String[] lastseen_help = {
                        "!lastseen <Nickname> : Displays the last thing a user said, based on their nickname, and when the user was last seen.",
                        "!lastseen -nick <Nickname> : Displays the last thing a user said, based on their nickname.",
                        "!lastseen -host <User's Hostname> : Displays the last thing a user said, based on their hostname.",
                        "Example: If the username and hostname is ~Bot@serenity.1337ism.net you want to enter everything after the @ sign." };
                for (int i = 0; i < lastseen_help.length; i++) {
                    ircUtil.sendMessage(event, lastseen_help[i]);
                }
            } else if (param[1].equalsIgnoreCase("-host")) {
                if (param.length == 2) {
                    // If message only contains !lastseen -host, then send correct syntax
                    String lastseen_syntax = "!lastseen -host <User's Hostname> : Displays the last thing a user said, based on their hostname.";
                    ircUtil.sendMessage(event, lastseen_syntax);
                } else {
                    // Otherwise search for the host string, and send them the results
                    String hostSearch = param[2].toString();
                    List sentMsg = getLastSeenHost(hostSearch);

                    if (sentMsg == null) {
                        // If sentMsg is null, we know the search didn't find anything.
                        String lastseenError = hostSearch + " hasn't said anything.";
                        ircUtil.sendMessage(event, lastseenError);
                    } else {
                        // Otherwise, we send them the search results.
                        Iterator itr = sentMsg.iterator();
                        while (itr.hasNext()) {
                            ircUtil.sendMessage(event, itr.next().toString());
                        }
                    }
                }
            } else if (param[1].equalsIgnoreCase("-nick")) {
                if (param.length == 2) {
                    // If message only contains !lastseen -nick, then send correct syntax
                    String lastseen_syntax = "!lastseen -nick <Nickname> : Displays the last thing a user said, based on their nickname.";
                    ircUtil.sendMessage(event, lastseen_syntax);
                } else {
                    // Otherwise search for the nick string, and send them the results
                    String nickSearch = param[2].toString();
                    List sentMsg = getLastSeenNick(nickSearch);

                    if (sentMsg == null) {
                        // If sentMsg equals null, we know the search didn't find anything.
                        String lastseenError = nickSearch + " hasn't said anything.";
                        ircUtil.sendMessage(event, lastseenError);
                    } else {
                        // Otherwise, we send them the search results.
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
                    // If sentMsg equals null, we know the search didn't find anything.
                    String lastseenError = nickSearch + " hasn't said anything.";
                    ircUtil.sendMessage(event, lastseenError);
                } else {
                    // Otherwise, we send them the search results.
                    Iterator itr = sentMsg.iterator();
                    while (itr.hasNext()) {
                        ircUtil.sendMessage(event, itr.next().toString());
                    }
                }
            }
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // Check if message starts with !lastseen
        if (event.getMessage().trim().toLowerCase().startsWith("!lastseen")) {
            // Spilt the message, so we can get the different parameters
            String[] param = event.getMessage().trim().split("\\s", 3);

            if (param.length == 1 || param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                // If message contains only 1 string, or if they entered -help, or -h, then send them the help info.
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
                if (param.length == 2) {
                    // If message only contains !lastseen -host, then send correct syntax
                    String lastseen_syntax = "!lastseen -host <User's Hostname> : Displays the last thing a user said, based on their hostname.";
                    ircUtil.sendMessage(event, lastseen_syntax);
                } else {
                    // Otherwise search for the host string, and send them the results
                    String hostSearch = param[2].toString();
                    List sentMsg = getLastSeenHost(hostSearch);

                    if (sentMsg == null) {
                        // If sentMsg equals null, we know the search didn't find anything.
                        String lastseenError = hostSearch + " hasn't said anything.";
                        ircUtil.sendMessage(event, lastseenError);
                    } else {
                        // Otherwise, we send them the search results.
                        Iterator itr = sentMsg.iterator();
                        while (itr.hasNext()) {
                            ircUtil.sendMessage(event, itr.next().toString());
                        }
                    }
                }
            } else if (param[1].compareToIgnoreCase("-nick") == 0) {
                // if message contains -nick, then do this stuff
                if (param.length == 2) {
                    // If message only contains !lastseen -nick, then send correct syntax
                    String lastseen_syntax = "!lastseen -nick <Nickname> : Displays the last thing a user said, based on their nickname.";
                    ircUtil.sendMessage(event, lastseen_syntax);
                } else {
                    // Otherwise search for the nick string, and send them the results
                    String nickSearch = param[2].toString();
                    List sentMsg = getLastSeenNick(nickSearch);

                    if (sentMsg == null) {
                        // If sentMsg equals null, we know the search didn't find anything.
                        String lastseenError = nickSearch + " hasn't said anything.";
                        ircUtil.sendMessage(event, lastseenError);
                    } else {
                        // Otherwise, we send them the search results.
                        Iterator itr = sentMsg.iterator();
                        while (itr.hasNext()) {
                            ircUtil.sendMessage(event, itr.next().toString());
                        }
                    }
                }
            } else {
                // Now we know they want to search a nickname, and send them the results.
                String nickSearch = param[1].toString();
                List sentMsg = getLastSeenNick(nickSearch);

                if (sentMsg == null) {
                    // If sentMsg equals null, we know the search didn't find anything.
                    String lastseenError = nickSearch + " hasn't said anything.";
                    ircUtil.sendMessage(event, lastseenError);

                } else {
                    // Otherwise, send them the search results.
                    Iterator itr = sentMsg.iterator();
                    while (itr.hasNext()) {
                        ircUtil.sendMessage(event, itr.next().toString());
                    }
                }
            }
        }
    }

    public void onAction(ActionEvent event) throws Exception {
        // Get the last message to the channel and update the database
        String nickname = event.getUser().getNick();
        String hostname = event.getUser().getLogin() + "@" + event.getUser().getHostmask();
        String message = event.getMessage();
        message = MiscUtil.redactURL(message);
        int seconds = MiscUtil.seconds();
        boolean action = true;
        setLastSeen(nickname, hostname, message, seconds, action);
    }

}
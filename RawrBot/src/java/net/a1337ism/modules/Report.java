package net.a1337ism.modules;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.a1337ism.RawrBot;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.SqliteDb;
import net.a1337ism.util.ircUtil;

import org.apache.commons.dbutils.DbUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserLevel;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Report extends ListenerAdapter {
    private static Logger logger     = LoggerFactory.getLogger(RawrBot.class);
    private String        dbUrl      = "jdbc:sqlite:data/report.db";
    private String        dbDriver   = "org.sqlite.JDBC";
    private boolean       tableExist = createTableIfNotExist();
    private byte          ERROR      = 0;

    private SqliteDb dbConnect() {
        // Open a connection to the database.
        SqliteDb db = new SqliteDb(dbDriver, dbUrl);
        return db;
    }

    private boolean createTableIfNotExist() {
        // Set up a connection
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tableReports = meta.getTables(null, null, "REPORTS", null);
            if (!tableReports.next()) {
                // Tables do not exist
                logger.info("Reports table does not exist, creating.");
                db.executeStmt("CREATE TABLE reports(ID INTEGER PRIMARY KEY, Reported_Nick TEXT, From_Nick TEXT, Reason TEXT, Time TEXT)");
                logger.info("Reports table created successfully");
                return true;
            } else {
                // Tables exists
                return true;
            }
        } catch (SQLException pass) {
            logger.info("SQLException in Report.createTableIfNotExist: " + pass.toString());
            return false;
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Adds a report from a user to the database
     * 
     * @param reason
     *            the reason for the report
     * @param reportedNick
     *            the nickname of the person who has been reported
     * @param fromNick
     *            the nickname of the person who reported the issue
     * @return true = Success, false = Failure
     */
    private boolean addReport(String reason, String reportedNick, String fromNick) {
        // Set up a connection, and variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        PreparedStatement insertReport = null;

        String date = MiscUtil.dateFormat();
        try {
            insertReport = conn
                    .prepareStatement("INSERT INTO reports ( 'Reported_Nick', 'From_Nick', 'Reason', 'Time' ) VALUES (?,?,?,?)");
            insertReport.setString(1, reportedNick);
            insertReport.setString(2, fromNick);
            insertReport.setString(3, reason);
            insertReport.setString(4, date);
            insertReport.execute();
            return true;
        } catch (SQLException ex) {
            logger.info("SQLException in Report.addReport: " + ex.toString());
            ERROR = 3;
        } finally {
            DbUtils.closeQuietly(insertReport);
            DbUtils.closeQuietly(conn);
        }
        return false;
    }

    /**
     * Deletes a report from the database based on the ID entered
     * 
     * @param ID
     *            number of the report
     * @return true = Success, false = Failure
     */
    private boolean delReport(String ID) {
        // Set up a connection, and variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        PreparedStatement selectReport = null;
        PreparedStatement deleteReport = null;

        try {
            // Try and parse their input to see if it's a valid integer.
            int inputNumber = Integer.parseInt(ID);

            // Check to see if ID exists.
            selectReport = conn.prepareStatement("SELECT * FROM reports WHERE ID = ?");
            selectReport.setInt(1, inputNumber);
            ResultSet resultSet = selectReport.executeQuery();

            if (resultSet.isBeforeFirst()) {
                // If ID exists, delete it.
                deleteReport = conn.prepareStatement("DELETE FROM reports WHERE ID = ?");
                deleteReport.setInt(1, inputNumber);
                deleteReport.execute();
                return true;
            } else
                ERROR = 2;
        } catch (SQLException ex) {
            logger.info("SQLException in Report.delReport: " + ex.toString());
            ERROR = 3;
        } catch (NumberFormatException nfe) {
            ERROR = 4;
        } finally {
            DbUtils.closeQuietly(selectReport);
            DbUtils.closeQuietly(deleteReport);
            DbUtils.closeQuietly(conn);
        }
        return false;
    }

    /**
     * Fetches a report based on the ID of the report entered
     * 
     * @param ID
     *            number of the report
     * @return a formatted report to be sent to the user, if there is none, return null;
     */
    private String getReportID(String ID) {
        // Set up a connection, and variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet resultSet = null;
        PreparedStatement selectReport = null;
        String msg = null;

        try {
            // Try and parse their input to see if it's a valid integer.
            int inputNumber = Integer.parseInt(ID);

            // Check to see if ID exists.
            selectReport = conn.prepareStatement("SELECT * FROM reports WHERE ID = ?");
            selectReport.setInt(1, inputNumber);
            resultSet = selectReport.executeQuery();

            if (resultSet.isBeforeFirst()) {
                // If ID exists, return it.
                List<String> message = resultSetParser(resultSet);
                Object[] finalMsg = message.toArray();
                msg = finalMsg[0].toString();
            } else
                ERROR = 2;
        } catch (SQLException ex) {
            logger.info("SQLException in Report.getReportID: " + ex.toString());
            ERROR = 3;
        } catch (NumberFormatException nfe) {
            ERROR = 4;
        } finally {
            DbUtils.closeQuietly(conn, selectReport, resultSet);
        }
        return msg;
    }

    /**
     * Fetches a report based on the Nickname of the reported
     * 
     * @param nickname
     *            nickname of the reported person
     * @return a formatted report to be sent to the user
     */
    private List<String> getReportNick(String nickname) {
        return getReportNick(nickname, 5);
    }

    /**
     * Fetches a report based on the Nickname of the reported
     * 
     * @param nickname
     *            nickname of the reported person
     * @param limit
     *            Limit of reports we want to get
     * @return a formatted report to be sent to the user, if there is none,
     */
    private List<String> getReportNick(String nickname, int limit) {
        // Set up a connection, and variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet resultSet = null;
        PreparedStatement selectReport = null;
        List<String> message = null;
        try {
            selectReport = conn.prepareStatement("SELECT * FROM reports WHERE Reported_Nick LIKE ? LIMIT ?");
            selectReport.setString(1, "%" + nickname + "%");
            selectReport.setInt(2, limit);
            resultSet = selectReport.executeQuery();

            if (resultSet.isBeforeFirst())
                message = resultSetParser(resultSet);
            else
                ERROR = 2;
        } catch (SQLException ex) {
            logger.info("SQLException in Report.getReportNick: " + ex.toString());
            ERROR = 3;
        } finally {
            DbUtils.closeQuietly(conn, selectReport, resultSet);
        }
        return message;
    }

    private List<String> getReportAll() {
        return getReportAll(5);
    }

    private List<String> getReportAll(int limit) {
        // Set up a connection, and variables
        SqliteDb db = dbConnect();
        Connection conn = db.getConnection();
        ResultSet resultSet = null;
        PreparedStatement selectReports = null;
        List<String> message = null;

        try {
            selectReports = conn.prepareStatement("SELECT * FROM reports ORDER BY ID DESC LIMIT ?");
            selectReports.setInt(1, limit);
            resultSet = selectReports.executeQuery();

            if (resultSet.isBeforeFirst())
                message = resultSetParser(resultSet);
            else
                ERROR = 2;
        } catch (SQLException ex) {
            logger.info("SQLException in Report.getReportAll: " + ex.toString());
            ERROR = 3;
        } finally {
            DbUtils.closeQuietly(db.getConnection(), selectReports, resultSet);
        }
        return message;
    }

    /**
     * Parses a result set containing entries for reported nicknames.
     * 
     * @param resultSet
     *            A result set containing Reported, Reporter, Reason, Time, and ID
     * @throws SQLException
     *             If any Exceptions might be thrown, throw them up and let MessageEvent handle it
     * @return a list of lines to be sent to the user
     * 
     */
    private List<String> resultSetParser(ResultSet rs) throws SQLException {
        // Function to build a nice list from the results of the various searches.
        List<String> message = new ArrayList<>();
        while (rs.next()) {
            int ID = rs.getInt("ID");
            String reportedNick = rs.getString("Reported_Nick");
            String fromNick = rs.getString("From_Nick");
            String reason = rs.getString("Reason");
            String reportTime = rs.getString("Time");
            message.add("[" + reportTime + "] ID: " + ID + " | " + fromNick + " reported " + reportedNick
                    + " with reason: " + reason);
        }
        return message;
    }

    public void onMessage(MessageEvent event) throws Exception {
        if (event.getMessage().trim().toLowerCase().startsWith("!report")
                && !RateLimiter.isRateLimited(event.getUser().getNick())) {
            // If the message starts with !report

            // Split the message into parameters.
            String[] param = event.getMessage().trim().split("\\s", 3);

            if (param.length == 1 || param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                // If the message only contains one item, or they entered -help, or -h, then send them the help
                // information.
                String reportHelp = "!report <user> [reason] : Reports a user, and messages all available channel operators.";
                ircUtil.sendMessage(event, reportHelp);
            } else {
                // We know they are entering a report
                // Set up some variables.
                String reportedNick = param[1];
                String reason = "No reason given.";
                boolean result = false;

                // If they entered a reason, overwrite the default reason.
                if (param.length == 3)
                    reason = param[2];

                // Add the report
                result = addReport(reason, reportedNick, event.getUser().getNick());

                if (result == true) {
                    // Set the success string, and message success
                    String success = "Your report has been added, and the operators of the channel have been notified.";
                    ircUtil.sendMessage(event, success);

                    // Get a list of operators for the channel, and throw it into an iterator.
                    // TODO: Move this to a function in ircUtil.java
                    Set<User> operators = event.getChannel().getOps();
                    Iterator<User> itr = operators.iterator();
                    String reportMessage = event.getUser().getNick() + " reported " + reportedNick + " with reason: "
                            + reason;

                    while (itr.hasNext()) {
                        // Send a message to all the operators currently in channel about the report.
                        // TODO: EMAIL OPERATORS, or something. :O
                        String operator = itr.next().getNick();
                        event.getBot().sendIRC().message(operator, reportMessage);
                    }
                } else {
                    // If adding the report failed for some reason, tell them it failed.
                    String failure = "Error " + ERROR + ": Failed to add report.";
                    ircUtil.sendMessage(event, failure);
                }
            }
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // If the message starts with !reports, need !reports first, because it'll try to do !report before !reports. :P
        if (event.getMessage().trim().toLowerCase().startsWith("!reports")) {
            // Get the message and split it into parameters
            String[] param = event.getMessage().trim().split("\\s", 3);

            // If the user is an operator of the channel, then continue.
            if (ircUtil.isOP(event, RawrBot.irc_channel)) {
                if (param.length == 1) {
                    // Get 5 newest reports
                    List<String> result = getReportAll();

                    if (result != null) {
                        // Send them the reports.
                        Iterator itr = result.iterator();
                        while (itr.hasNext())
                            ircUtil.sendMessage(event, itr.next().toString());
                    } else if (ERROR == 3) {
                        String sqlError = "Error " + ERROR + ": SQL Error.";
                        ircUtil.sendMessage(event, sqlError);
                    } else {
                        String none = "No new reports.";
                        ircUtil.sendMessage(event, none);
                    }
                } else if (param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                    // If the second parameter is -help or -h, send them the help information.
                    String[] reportHelp = {
                            "!report <user> [reason] : Reports a user, and messages all available channel operators.",
                            "Note: The following commands can only be seen and used by Operators.",
                            "!reports [number]: Displays the five most recent reports, unless an optional number is specified.",
                            "!reports -id <ID> : Displays report associated with ID.",
                            "!reports -nick <nickname> : Displays all reports involved with the nickname provided.",
                            "!reports -del <ID>|<all> : Delete a report using the ID, or delete all reports." };
                    for (int i = 0; i < reportHelp.length; i++)
                        ircUtil.sendMessage(event, reportHelp[i]);
                } else if (param[1].equalsIgnoreCase("-del") || param[1].equalsIgnoreCase("-delete")) {
                    // If second parameter equals -del or -delete
                    if (param.length == 2) {
                        // If all that was entered is !reports -del, send them the correct syntax.
                        String reportSyntax = "!report -del <ID> : Deletes a report using the specified ID.";
                        ircUtil.sendMessage(event, reportSyntax);
                    } else {
                        // Otherwise, assume they entered a number, and set up variables.
                        String ID = param[2];
                        boolean result = false;
                        result = delReport(ID);

                        if (result == true) {
                            String success = "Sucessfully deleted report ID: " + ID;
                            ircUtil.sendMessage(event, success);
                        } else if (ERROR == 3) {
                            String sqlError = "Error " + ERROR + ": SQL Error.";
                            ircUtil.sendMessage(event, sqlError);
                        } else if (ERROR == 4) {
                            String nonNumber = "Error " + ERROR + ": " + ID + " is not a valid number.";
                            ircUtil.sendMessage(event, nonNumber);
                        } else {
                            String none = "No report for ID: " + ID;
                            ircUtil.sendMessage(event, none);
                        }
                    }
                } else if (param[1].equalsIgnoreCase("-id")) {
                    // if the second parameter is equal to -id, then we know they want to search for a specific id.
                    if (param.length == 2) {
                        // If they only entered "!reports -id", then send them the proper command syntax.
                        String reportSyntax = "!report -id <ID> : Displays report associated with ID.";
                        ircUtil.sendMessage(event, reportSyntax);
                    } else {
                        String ID = param[2].toString();
                        String result = getReportID(ID);

                        if (result != null) {
                            // Send them the information they requested
                            ircUtil.sendMessage(event, result);
                        } else if (ERROR == 3) {
                            String sqlError = "Error " + ERROR + ": SQL Error.";
                            ircUtil.sendMessage(event, sqlError);
                        } else if (ERROR == 4) {
                            String nonNumber = "Error " + ERROR + ": " + ID + " is not a valid number.";
                            ircUtil.sendMessage(event, nonNumber);
                        } else {
                            String none = "No report for ID: " + ID;
                            ircUtil.sendMessage(event, none);
                        }
                    }
                } else if (param[1].equalsIgnoreCase("-nick") || param[1].equalsIgnoreCase("-n")) {
                    // if the second parameter is equal to -nick, then we know they want to search for nickname.
                    if (param.length == 2) {
                        // If they only entered "!reports -nick", then send them the proper command syntax.
                        String reportSyntax = "!report -nick <Nickname> : Displays all reports involved with the nickname.";
                        ircUtil.sendMessage(event, reportSyntax);
                    } else {
                        // Set nickname to what they entered, and search for it in the database.
                        String nickname = param[2].toString();
                        List<String> result = getReportNick(nickname);

                        if (result != null) {
                            // Send them the information they searched for.
                            Iterator itr = result.iterator();
                            while (itr.hasNext())
                                ircUtil.sendMessage(event, itr.next().toString());
                        } else if (ERROR == 3) {
                            String sqlError = "Error " + ERROR + ": SQL Error.";
                            ircUtil.sendMessage(event, sqlError);
                        } else {
                            String none = "No reports for nickname: " + nickname;
                            ircUtil.sendMessage(event, none);
                        }
                    }
                } else {
                    // Otherwise, they just want "x" amount of the newest available reports.
                    String limit = param[1].toString();

                    // Check to see if the limit they input is a valid number
                    int inputNumber = 5;
                    try {
                        inputNumber = Integer.parseInt(limit);
                    } catch (NumberFormatException nfe) {
                        // If the number they input wasn't a number, let them know.
                        String nonNumber = "Error 4: " + limit + " is not a valid number";
                        ircUtil.sendMessage(event, nonNumber);
                        return;
                    }

                    List<String> result = getReportAll(inputNumber);

                    if (result != null) {
                        // Send them the reports.
                        Iterator itr = result.iterator();
                        while (itr.hasNext())
                            ircUtil.sendMessage(event, itr.next().toString());
                    } else if (ERROR == 3) {
                        String sqlError = "Error " + ERROR + ": SQL Error.";
                        ircUtil.sendMessage(event, sqlError);
                    } else {
                        String none = "No new reports.";
                        ircUtil.sendMessage(event, none);
                    }
                }
            }
        } else if (event.getMessage().trim().toLowerCase().startsWith("!report")) {
            // If the message starts with !report
            // Split the message into parameters.
            String[] param = event.getMessage().trim().split("\\s", 3);

            if (param.length == 1 || param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                // If the message only contains one item, or they entered -help, or -h, then send them the help
                // information.
                String reportHelp = "!report <user> [reason] : Reports a user, and messages all available channel operators.";
                ircUtil.sendMessage(event, reportHelp);
            } else {
                // We know they are entering a report
                // Set up some variables.
                String reportedNick = param[1];
                String reason = "No reason given.";
                boolean result = false;

                // If they entered a reason, overwrite the default reason.
                if (param.length == 3)
                    reason = param[2];

                // Add the report
                result = addReport(reason, reportedNick, event.getUser().getNick());

                if (result == true) {
                    // Set the success string, and message success
                    String success = "Your report has been added, and the operators of the channel have been notified.";
                    ircUtil.sendMessage(event, success);

                    // Get a list of operators for the channel, and throw it into an iterator.
                    // TODO: Move this to a function in ircUtil.java
                    Channel channel = event.getBot().getUserChannelDao().getChannel(RawrBot.irc_channel);
                    Set<User> operators = event.getBot().getUserChannelDao().getUsers(channel, UserLevel.OP);
                    Iterator<User> itr = operators.iterator();
                    String reportMessage = event.getUser().getNick() + " reported " + reportedNick + " with reason: "
                            + reason;

                    while (itr.hasNext()) {
                        // Send a message to all the operators currently in channel about the report.
                        // TODO: EMAIL OPERATORS, or something. :O
                        String operator = itr.next().getNick();
                        event.getBot().sendIRC().message(operator, reportMessage);
                    }
                } else {
                    // If adding the report failed for some reason, tell them it failed.
                    String failure = "Error " + ERROR + ": Failed to add report.";
                    ircUtil.sendMessage(event, failure);
                }
            }
        }
    }
}
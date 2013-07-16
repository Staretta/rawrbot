package net.a1337ism.modules;

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

public class ReportCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger        = LoggerFactory.getLogger(RawrBot.class);

    // Set up the database stuff
    String                sUrlString    = "jdbc:sqlite:data/report.db";
    String                sDriverString = "org.sqlite.JDBC";

    // SqliteDb sqlitedb = new SqliteDb("org.sqlite.JDBC", sUrlString);

    public void onMessage(MessageEvent event) throws Exception {
        // If the message starts with !report
        if (event.getMessage().trim().toLowerCase().startsWith("!report")) {

            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;

            // Split the message into parameters.
            String[] param = event.getMessage().trim().split("\\s", 3);
            // If the message only contains one item, then we know then entered !report
            if (param.length == 1) {
                String reportSyntax = "!report <user> [reason] : Reports a user, and messages all available channel operators.";
                ircUtil.sendMessage(event, reportSyntax);
            } else if (param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                // If they entered !report -help or !report -h
                String reportHelp = "!report <user> [reason] : Reports a user, and messages all available channel operators.";
                ircUtil.sendMessage(event, reportHelp);
            } else {
                // Otherwise, they are entering a report
                String reportedNick = param[1];
                String reason = "No reason given.";

                // If they entered a reason, overwrite the default reason.
                if (param.length == 3) {
                    reason = param[2];
                }
                // Set reportResult to default of 0, for false.
                byte reportResult = 0;
                // Add the report
                reportResult = addReport(reason, reportedNick, event.getUser().getNick());
                if (reportResult == 1) {
                    // Set the success string
                    String reportSuccess = "Your report has been added, and the operators of the channel have been notified.";

                    // If the report was added successfully
                    ircUtil.sendMessage(event, reportSuccess);

                    // Get string ready to message the operators
                    String reportMessage = event.getUser().getNick() + " reported " + reportedNick + " with reason: "
                            + reason;

                    // Get a list of operators for the channel, and throw it into an iterator.
                    Set<User> operators = event.getChannel().getOps();
                    Iterator<User> itr = operators.iterator();
                    // TODO: EMAIL OPERATORS. :O
                    while (itr.hasNext()) {
                        String operator = itr.next().getNick();
                        // Send a message to all the operators currently in channel about the report.
                        event.getBot().sendIRC().message(operator, reportMessage);
                    }
                } else {
                    String reportFailure = "Error " + reportResult + ": Unable to add report.";
                    // If adding the report failed for some reason, tell them it failed, with an error number.
                    // Error 0 for default error
                    // Error 2 for SQLException
                    // Error 3 for no SQL connection
                    ircUtil.sendMessage(event, reportFailure);
                }
            }
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // If the message starts with !reports
        // Need to do this because otherwise, it'll try the !report command first. or something
        if (event.getMessage().trim().toLowerCase().startsWith("!reports")) {
            // Get the message and split it into parameters
            String[] param = event.getMessage().trim().split("\\s", 3);

            // If the user is an operator of the channel, then continue.
            if (ircUtil.isOP(event, RawrBot.irc_channel)) {
                if (param.length == 1) {
                    // If the command is just !reports, then give them 5 newest reports.
                    int limit = 5;
                    List<String> reportResult = getReportAll(limit);
                    // If reportResult == null, then we know there aren't any new reports.
                    if (reportResult == null) {
                        String reportNone = "No new reports.";
                        ircUtil.sendMessage(event, reportNone);
                    } else {
                        // Send them the list of new reports.
                        Iterator itr = reportResult.iterator();
                        while (itr.hasNext())
                            ircUtil.sendMessage(event, itr.next().toString());
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
                        byte reportResult = 0;
                        reportResult = delReport(ID);

                        // Report Result Codes
                        // 1 = Success
                        // 2 = No such ID in database
                        // 3 = No connection to the database
                        // 4 = SQLException
                        // 5 = Not a valid number entered
                        String reportSuccess = "Sucessfully deleted report with ID: " + ID;
                        String reportError = "Error " + reportResult + ": ";
                        String reportFailure = reportError + "Unable to delete report with ID: " + ID;
                        String reportNonNumber = reportError + ID + " is not a valid number.";
                        String reportSQLError = reportError + "No connection to the database.";
                        String reportNone = "No report with ID: " + ID;

                        if (reportResult == 1) {
                            ircUtil.sendMessage(event, reportSuccess);
                        } else if (reportResult == 2) {
                            ircUtil.sendMessage(event, reportNone);
                        } else if (reportResult == 3) {
                            ircUtil.sendMessage(event, reportSQLError);
                        } else if (reportResult == 5) {
                            ircUtil.sendMessage(event, reportNonNumber);
                        } else {
                            ircUtil.sendMessage(event, reportFailure);
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
                        String reportResult = getReportID(ID);
                        String reportError = "Error " + reportResult + ": ";
                        String reportFailure = reportError + "General error.";
                        String reportNonNumber = reportError + ID + " is not a valid number.";
                        String reportNone = "No report with ID: " + ID;

                        // Report Result Codes
                        // 2 = No such ID in database
                        // 3 = No connection to the database
                        // 4 = SQLException
                        // 5 = Not a valid number
                        if (reportResult == "2") {
                            ircUtil.sendMessage(event, reportNone);
                        } else if (reportResult == "3" || reportResult == "4") {
                            ircUtil.sendMessage(event, reportFailure);
                        } else if (reportResult == "5") {
                            ircUtil.sendMessage(event, reportNonNumber);
                        } else {
                            ircUtil.sendMessage(event, reportResult);
                        }
                    }

                } else if (param[1].equalsIgnoreCase("-nick")) {
                    // if the second parameter is equal to -nick, then we know they want to search for nickname.
                    if (param.length == 2) {
                        // If they only entered "!reports -nick", then send them the proper command syntax.
                        String reportSyntax = "!report -nick <Nickname> : Displays all reports involved with the nickname.";
                        ircUtil.sendMessage(event, reportSyntax);
                    } else {
                        String nickname = param[2].toString();
                        // If there is a connection to the database, then do stuff.
                        List<String> reportResult = getReportNick(nickname);
                        // If reportResult is null, then there are no reports for that nickname.
                        if (reportResult == null) {
                            String reportNone = "No reports for nickname: " + nickname;
                            ircUtil.sendMessage(event, reportNone);
                        } else {
                            // Send them the information they searched for.
                            Iterator itr = reportResult.iterator();
                            while (itr.hasNext())
                                ircUtil.sendMessage(event, itr.next().toString());
                        }
                    }
                } else {
                    // Otherwise, they just want "x" amount of the newest available reports.
                    String limit = param[1].toString();
                    try {
                        // Check to see if the limit they input is a valid number
                        int inputNumber = Integer.parseInt(limit);
                        List<String> reportResult = getReportAll(inputNumber);
                        // If reportResult is null, then we know there are no new reports.
                        if (reportResult == null) {
                            String reportNone = "No new reports.";
                            ircUtil.sendMessage(event, reportNone);
                        } else {
                            // Send them the reports.
                            Iterator itr = reportResult.iterator();
                            while (itr.hasNext()) {
                                ircUtil.sendMessage(event, itr.next().toString());
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        // If the number they input wasn't a number, let them know.
                        String reportFailure = "Error 5: " + limit + " is not a valid number";
                        ircUtil.sendMessage(event, reportFailure);
                    }
                }
            }
        }
        // else if the message starts with !report
        else if (event.getMessage().trim().toLowerCase().startsWith("!report")) {
            // Split the message into parameters.
            String[] param = event.getMessage().trim().split("\\s", 3);
            // If the message only contains one item, then we know then entered !report
            if (param.length == 1) {
                String reportSyntax = "!report <user> [reason] : Reports a user, and messages all available channel operators.";
                ircUtil.sendMessage(event, reportSyntax);
            } else if (param[1].equalsIgnoreCase("-help") || param[1].equalsIgnoreCase("-h")) {
                // If they entered !report -help or !report -h
                String reportHelp = "!report <user> [reason] : Reports a user, and messages all available channel operators.";
                ircUtil.sendMessage(event, reportHelp);
            } else {
                // Otherwise, they are entering a report

                String reportedNick = param[1];
                String reason = "No reason given.";

                // If they entered a reason, overwrite the default reason.
                if (param.length == 3) {
                    reason = param[2];
                }
                // Set reportResult to default of 0, for false.
                byte reportResult = 0;
                // Add the report
                reportResult = addReport(reason, reportedNick, event.getUser().getNick());
                if (reportResult == 1) {
                    // Set the success string
                    String reportSuccess = "Your report has been added, and the operators of the channel have been notified.";

                    // If the report was added successfully
                    ircUtil.sendMessage(event, reportSuccess);

                    // Get string ready to message the operators
                    String reportMessage = event.getUser().getNick() + " reported " + reportedNick + " with reason: "
                            + reason;

                    // Get a list of operators for the channel, and throw it into an iterator.
                    Channel channel = event.getBot().getUserChannelDao().getChannel(RawrBot.irc_channel);
                    Set<User> operators = event.getBot().getUserChannelDao().getUsers(channel, UserLevel.OP);
                    Iterator<User> itr = operators.iterator();
                    // TODO: EMAIL OPERATORS. :O
                    while (itr.hasNext()) {
                        String operator = itr.next().getNick();
                        // Send a message to all the operators currently in channel about the report.
                        event.getBot().sendIRC().message(operator, reportMessage);
                    }
                } else {
                    // If adding the report failed for some reason, tell them it failed, with an error number.
                    // Error 0 for default error
                    // Error 2 for SQLException
                    // Error 3 for no SQL connection
                    String reportFailure = "Error " + reportResult + ": Unable to add report.";
                    ircUtil.sendMessage(event, reportFailure);
                }
            }
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
     * @return the error/success code of the added report
     */
    private byte addReport(String reason, String reportedNick, String fromNick) {
        // Set up the connection, and the statement
        SqliteDb db = new SqliteDb(sDriverString, sUrlString);
        PreparedStatement insertReport = null;

        // If there is no connection, return 2.
        if (db.getConnection() != null) {
            String dateNow = MiscUtil.dateFormat();
            try {
                insertReport = db.getConnection().prepareStatement(
                        "INSERT INTO reports ( 'Reported_Nick', 'From_Nick', 'Reason', 'Time' ) VALUES (?,?,?,?)");
                insertReport.setString(1, reportedNick);
                insertReport.setString(2, fromNick);
                insertReport.setString(3, reason);
                insertReport.setString(4, dateNow);
                insertReport.execute();
                return 1;
            } catch (SQLException ex) {
                return 3;
            } finally {
                DbUtils.closeQuietly(insertReport);
                DbUtils.closeQuietly(db.getConnection());
            }
        }
        // No connection to the database.
        return 2;
    }

    /**
     * Deletes a report from the database based on the ID entered
     * 
     * @param ID
     *            number of the report
     * @return the error/success code of the deleted report
     */
    private byte delReport(String ID) {
        // Set up the connection, and the statement
        SqliteDb db = new SqliteDb(sDriverString, sUrlString);
        PreparedStatement selectReport = null;
        PreparedStatement deleteReport = null;

        // If there is no connection, return false.
        if (db.getConnection() != null) {
            try {
                // Try and parse their input to see if it's a valid integer.
                int inputNumber = Integer.parseInt(ID);
                selectReport = db.getConnection().prepareStatement("SELECT * FROM reports WHERE ID = ?");
                selectReport.setInt(1, inputNumber);
                ResultSet resultSet = selectReport.executeQuery();
                if (!resultSet.isBeforeFirst()) {
                    selectReport.close();
                    return 2;
                } else {
                    deleteReport = db.getConnection().prepareStatement("DELETE FROM reports WHERE ID = ?");
                    deleteReport.setInt(1, inputNumber);
                    deleteReport.execute();
                    return 1;
                }
            } catch (SQLException ex) {
                // logger.error("SQLException: Could not delete report from the database. " + ex.getMessage());
                return 4;
            } catch (NumberFormatException nfe) {
                // logger.error("NumberFormatException: Not a valid integer.");
                return 5;
            } finally {
                DbUtils.closeQuietly(selectReport);
                DbUtils.closeQuietly(deleteReport);
                DbUtils.closeQuietly(db.getConnection());
            }
        }
        return 3;
    }

    /**
     * Fetches a report based on the ID of the report entered
     * 
     * @param ID
     *            number of the report
     * @return a formatted report to be sent to the user
     */
    private String getReportID(String ID) {
        // Set up the connection, and the result set
        SqliteDb db = new SqliteDb(sDriverString, sUrlString);
        ResultSet resultSet = null;
        PreparedStatement selectReport = null;

        if (db.getConnection() != null) {
            try {
                // Try and parse their input to see if it's a valid integer.
                int inputNumber = Integer.parseInt(ID);

                selectReport = db.getConnection().prepareStatement("SELECT * FROM reports WHERE ID = ?");
                selectReport.setInt(1, inputNumber);
                resultSet = selectReport.executeQuery();
                if (!resultSet.isBeforeFirst()) {
                    selectReport.close();
                    return "2";
                } else {
                    List<String> message = resultSetParser(resultSet);
                    Object[] finalMsg = message.toArray();
                    selectReport.close();
                    return finalMsg[0].toString();
                }
            } catch (SQLException ex) {
                return "4";
            } catch (NumberFormatException nfe) {
                return "5";
            } finally {
                DbUtils.closeQuietly(db.getConnection(), selectReport, resultSet);
            }
        }
        return "3";
    }

    private List<String> getReportNick(String nickname) {
        // Set up the connection, and the result set
        SqliteDb db = new SqliteDb(sDriverString, sUrlString);
        ResultSet resultSet = null;
        PreparedStatement selectReport = null;
        List<String> message = null;

        // TODO: Add a default limit, and be able to pass limit to reports.
        try {
            int limit = 5;
            selectReport = db.getConnection().prepareStatement(
                    "SELECT * FROM reports WHERE Reported_Nick LIKE ? LIMIT ?");
            selectReport.setString(1, "%" + nickname + "%");
            selectReport.setInt(2, limit);
            resultSet = selectReport.executeQuery();

            if (resultSet.isBeforeFirst())
                message = resultSetParser(resultSet);

        } catch (SQLException ignore) {
        } finally {
            DbUtils.closeQuietly(db.getConnection(), selectReport, resultSet);
        }

        return message;
    }

    private List<String> getReportAll(int inputNumber) {
        // Set up the connection, and the result set
        SqliteDb db = new SqliteDb(sDriverString, sUrlString);
        ResultSet resultSet = null;
        PreparedStatement selectReports = null;
        List<String> message = null;

        try {
            selectReports = db.getConnection().prepareStatement("SELECT * FROM reports ORDER BY ID DESC LIMIT ?");
            selectReports.setInt(1, inputNumber);
            resultSet = selectReports.executeQuery();

            if (resultSet.isBeforeFirst())
                message = resultSetParser(resultSet);

        } catch (SQLException ignore) {
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
    private List<String> resultSetParser(ResultSet resultSet) throws SQLException {
        // Function to build a nice list from the results of the various searches.
        List<String> message = new ArrayList<>();
        while (resultSet.next()) {
            int ID = resultSet.getInt("ID");
            String reportedNick = resultSet.getString("Reported_Nick");
            String fromNick = resultSet.getString("From_Nick");
            String reason = resultSet.getString("Reason");
            String reportTime = resultSet.getString("Time");
            message.add("[" + reportTime + "] ID: " + ID + " | " + fromNick + " reported " + reportedNick
                    + " with reason: " + reason);
        }
        return message;
    }
}
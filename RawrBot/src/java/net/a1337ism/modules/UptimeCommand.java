package net.a1337ism.modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.a1337ism.RawrBot;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.ircUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class UptimeCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger         = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT      = MarkerManager.getMarker("LOG_EVENT");
    private long          bot_start_time = System.currentTimeMillis();                 // Set time when the bot
                                                                                        // starts.

    // Check for channel messages
    public void onMessage(MessageEvent event) throws Exception {
        // Check if message starts with !uptime
        if (event.getMessage().trim().toLowerCase().startsWith("!uptime")) {
        	
        	// If they are rate limited, then return. 
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;
        	
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String uptimeHelp = "!uptime : Displays the bot and the system's current uptime.";
                ircUtil.sendMessage(event, uptimeHelp);
            } else {
                String currentUptime = "Bot Uptime: " + MiscUtil.formatTime(getBotUptime() / 1000) + " | "
                        + "System Uptime: " + MiscUtil.formatTime(getSystemUptime() / 1000);
                ircUtil.sendMessage(event, currentUptime);
            }
        }
    }

    // Check for private messages
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // Check if message starts with !uptime
        if (event.getMessage().trim().toLowerCase().startsWith("!uptime")) {
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String uptimeHelp = "!uptime : Displays the bot and the system's current uptime.";
                ircUtil.sendMessage(event, uptimeHelp);
            } else {
                String currentUptime = "Bot Uptime: " + MiscUtil.formatTime(getBotUptime() / 1000) + " | "
                        + "System Uptime: " + MiscUtil.formatTime(getSystemUptime() / 1000);
                ircUtil.sendMessage(event, currentUptime);
            }
        }
    }

    private long getBotUptime() throws Exception {
        long bot_uptime = System.currentTimeMillis() - bot_start_time;
        return bot_uptime;
    }

    private long getSystemUptime() throws Exception {
        long uptime = -1;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Process uptimeProc = Runtime.getRuntime().exec("net stats srv");
            BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("Statistics since")) {
                    SimpleDateFormat format = new SimpleDateFormat("'Statistics since' MM/dd/yyyy hh:mm:ss a");
                    Date boottime = format.parse(line);
                    uptime = System.currentTimeMillis() - boottime.getTime();
                    break;
                }
            }
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            Process uptimeProc = Runtime.getRuntime().exec("uptime");
            BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
            String line = in.readLine();
            if (line != null) {
                // Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
                Pattern parse = Pattern.compile("up ((\\d+) days,)? (\\d+):(\\d+)");
                Matcher matcher = parse.matcher(line);
                if (matcher.find()) {
                    String _days = matcher.group(2);
                    String _hours = matcher.group(3);
                    String _minutes = matcher.group(4);
                    logger.info(_days + " " + _hours + " " + _minutes + " " + matcher.groupCount() + " "
                            + matcher.group(1));
                    int days = _days != null ? Integer.parseInt(_days) : 0;
                    int hours = _hours != null ? Integer.parseInt(_hours) : 0;
                    int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
                    uptime = TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES)
                            + TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS)
                            + TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
                }
            }
        }
        return uptime;

    }
}
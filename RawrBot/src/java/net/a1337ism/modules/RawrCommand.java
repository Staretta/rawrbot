package net.a1337ism.modules;

import net.a1337ism.RawrBot;
import net.a1337ism.util.FileUtil;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.ircUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class RawrCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger    = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");
    private String        filename  = "data/rawr.txt";                             // Set the rawr.txt location
    private String[]      rawrList  = FileUtil.readLines(filename);                // Throw the lines into a list

    // Check for channel messages
    public void onMessage(MessageEvent event) throws Exception {
        // Check if message starts with !rawr
        if (event.getMessage().trim().toLowerCase().startsWith("!rawr")) {

            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick())) {
                return;
            }

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String rawrHelp = "!rawr: Rawr. :3";
                ircUtil.sendMessage(event, rawrHelp);
            } else if (event.getMessage().trim().startsWith("!RAWR")) {
                // If command starts with !RAWR, in caps.
                String rawr = MiscUtil.randomSelection(rawrList).toUpperCase();
                ircUtil.sendMessage(event, rawr);
            } else {
                // Otherwise, we know they just want regular !rawr
                String rawr = MiscUtil.randomSelection(rawrList);
                ircUtil.sendMessage(event, rawr);
            }
        }
    }

    // Check for private messages
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // Check if message starts with !RAWR
        if (event.getMessage().trim().toLowerCase().startsWith("!rawr")) {
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String rawrHelp = "!rawr: Rawr. :3";
                ircUtil.sendMessage(event, rawrHelp);
            } else if (event.getMessage().trim().startsWith("!RAWR")) {
                // If command starts with !RAWR, in caps.
                String rawr = MiscUtil.randomSelection(rawrList).toUpperCase();
                ircUtil.sendMessage(event, rawr);
            } else {
                // Otherwise, we know they just want regular !rawr
                String rawr = MiscUtil.randomSelection(rawrList);
                ircUtil.sendMessage(event, rawr);
            }
        }
    }
}
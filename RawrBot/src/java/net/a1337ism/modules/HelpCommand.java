package net.a1337ism.modules;

import net.a1337ism.RawrBot;
import net.a1337ism.util.ircUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class HelpCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger    = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");

    public void onMessage(MessageEvent event) throws Exception {
        // Check if message starts with !commands
        if (event.getMessage().trim().toLowerCase().startsWith("!commands")) {

            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;

            String[] commandHelp = { "Commands: !joke !quote !rawr !8ball !lastseen !report !uptime !time !commands",
                    "For command specific help, type \"-help\" after a command." };
            for (String line : commandHelp) {
                ircUtil.sendMessage(event, line);
            }
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // Check if message starts with !commands
        if (event.getMessage().trim().toLowerCase().startsWith("!commands")) {
            String[] commandHelp = { "Commands: !joke !quote !rawr !8ball !lastseen !report !uptime !time !commands",
                    "For command specific help, type \"-help\" after a command." };
            for (String line : commandHelp) {
                ircUtil.sendMessage(event, line);
            }
        }
    }
}
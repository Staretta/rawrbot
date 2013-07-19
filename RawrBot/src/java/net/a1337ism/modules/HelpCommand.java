package net.a1337ism.modules;

import net.a1337ism.RawrBot;
import net.a1337ism.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

    public void onMessage(MessageEvent event) throws Exception {
        // Check if message starts with !commands and if they are rate limited.
        if (event.getMessage().trim().toLowerCase().startsWith("!commands")) {

            // Return if they are rate limited
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
package net.a1337ism.modules;

import net.a1337ism.RawrBot;
import net.a1337ism.util.FileUtil;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawrCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger   = LoggerFactory.getLogger(RawrBot.class);
    private String        filename = "data/rawr.txt";                       // Set the rawr.txt location
    private String[]      rawrList = FileUtil.readLines(filename);          // Throw the lines into a list

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
        } else if (event.getMessage().trim().toLowerCase().startsWith("!meow")) {
            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick())) {
                return;
            }

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String meowHelp = "!meow: Meow. :3";
                ircUtil.sendMessage(event, meowHelp);
            } else if (event.getMessage().trim().startsWith("!MEOW")) {
                // If command starts with !MEOW, in caps.
                String meow = meowReplace(MiscUtil.randomSelection(rawrList));
                ircUtil.sendMessage(event, meow.toUpperCase());
            } else {
                // If command starts with !meow.
                String meow = meowReplace(MiscUtil.randomSelection(rawrList));
                ircUtil.sendMessage(event, meow);
            }
        } else if (event.getMessage().trim().toLowerCase().startsWith("!nyan")) {
            // @formatter:off
            String[] nyan = { 
                    "...,__,......,__,.....____________", 
                    "`·.,¸,.·*¯`·.,¸,.·*¯..|::::::/\\:_|/\\",
                    "`·.,¸,.·*¯`·.,¸,.·*¯.<|:::::(  o wo )", 
                    "-.......-\"\"-.......--\"\"u\"''''u''''u\"" };
            // @formatter:on
            for (String line : nyan) {
                // ircUtil.sendMessage(event, line);
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
        } else if (event.getMessage().trim().toLowerCase().startsWith("!meow")) {
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String meowHelp = "!meow: Meow. :3";
                ircUtil.sendMessage(event, meowHelp);
            } else if (event.getMessage().trim().startsWith("!MEOW")) {
                // If command starts with !MEOW, in caps.
                String meow = meowReplace(MiscUtil.randomSelection(rawrList));
                ircUtil.sendMessage(event, meow.toUpperCase());
            } else {
                // If command starts with !meow.
                String meow = meowReplace(MiscUtil.randomSelection(rawrList));
                ircUtil.sendMessage(event, meow);
            }
        }
    }

    private String meowReplace(String meow) {
        String[] words = null;
        String message = "";
        words = meow.split("\\s");
        for (String word : words) {
            if (word.startsWith("R")) {
                word = word.replaceFirst("R", "M");
            } else {
                word = word.replaceFirst("r", "m");
            }
            word = word.replace("A", "E").replace("a", "e");
            word = word.replace("W", "O").replace("w", "o");
            word = word.replace("R", "W").replace("r", "w");
            message += word + " ";
        }

        return message;
    }
}
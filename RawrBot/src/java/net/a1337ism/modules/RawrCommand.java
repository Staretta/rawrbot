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

        // Check if message starts with !rawr, and if they are rate limited.
        if (event.getMessage().trim().toLowerCase().startsWith("!rawr")
                && !RateLimiter.isRateLimited(event.getUser().getNick())) {

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If the command ends with -help or -h, then send them the help information.
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

            // Check if message starts with !meow, and if they are rate limited
        } else if (event.getMessage().trim().toLowerCase().startsWith("!meow")
                && !RateLimiter.isRateLimited(event.getUser().getNick())) {

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If the command ends with -help or -h, then send them the help information.
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

            // Check if message starts with !nyan, and if they are rate limited
        } else if (event.getMessage().trim().toLowerCase().startsWith("!nyan")
                && !RateLimiter.isRateLimited(event.getUser().getNick())) {

            // We want nyan to be weighted more than any other command. So we add more requests to their queue.
            RateLimiter.addRequest(event.getUser().getNick(), 4);

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If the command ends with -help or -h, then send them the help information.
                String meowHelp = "!nyan: NYAN! :3";
                ircUtil.sendMessage(event, meowHelp);

            } else {
                // Otherwise, we know they just want the cat.
                // @formatter:off
                String[] nyan = { 
                        "...,__,......,__,.....____________", 
                        "`·.,¸,.·*¯`·.,¸,.·*¯..|::::::/\\:_|/\\",
                        "`·.,¸,.·*¯`·.,¸,.·*¯.<|:::::(  o wo )", 
                        "-.......-\"\"-.......--\"\"u\"''''u''''u\"" };
                // @formatter:on
                for (String line : nyan) {
                    ircUtil.sendMessage(event, line);
                }
            }
        }
    }

    // Check for private messages
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {

        // Check if message starts with !rawr
        if (event.getMessage().trim().toLowerCase().startsWith("!rawr")) {

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If the command ends with -help or -h, then send them the help information.
                String rawrHelp = "!rawr: Rawr. :3";
                ircUtil.sendMessage(event, rawrHelp);

            } else if (event.getMessage().trim().toLowerCase().endsWith("-reload")
                    && event.getUser().getNick().equalsIgnoreCase(RawrBot.bot_owner)) {
                // If command ends with -reload, and it's the bot's owner, we know they want to reload the file list.
                ircUtil.sendMessage(event, "Reloading Rawr List");
                rawrList = FileUtil.readLines(filename);

            } else if (event.getMessage().trim().startsWith("!RAWR")) {
                // If command starts with !RAWR, in caps.
                String rawr = MiscUtil.randomSelection(rawrList).toUpperCase();
                ircUtil.sendMessage(event, rawr);

            } else {
                // Otherwise, we know they just want regular !rawr
                String rawr = MiscUtil.randomSelection(rawrList);
                ircUtil.sendMessage(event, rawr);
            }

            // Check if message starts with !meow
        } else if (event.getMessage().trim().toLowerCase().startsWith("!meow")) {

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If the command ends with -help or -h, then send them the help information.
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

            // Check if message starts with !nyan
        } else if (event.getMessage().trim().toLowerCase().startsWith("!nyan")) {

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If the command ends with -help or -h, then send them the help information.
                String meowHelp = "!nyan: NYAN! :3";
                ircUtil.sendMessage(event, meowHelp);

            } else {
                // Otherwise, we know they just want the cat.
                // @formatter:off
                String[] nyan = { 
                        "...,__,......,__,.....____________", 
                        "`·.,¸,.·*¯`·.,¸,.·*¯..|::::::/\\:_|/\\",
                        "`·.,¸,.·*¯`·.,¸,.·*¯.<|:::::(  o wo )", 
                        "-.......-\"\"-.......--\"\"u\"''''u''''u\"" };
                // @formatter:on
                for (String line : nyan) {
                    ircUtil.sendMessage(event, line);
                }
            }
        }
    }

    /**
     * <p>
     * Replaces Rawr with Meow.
     * </p>
     * 
     * <p>
     * We want to split Rawr into individual words in a list, and then for each word, replace the first instance of "R"
     * with "M", and then replace "a" with "e", "w" with "o", and finally "r" with "w". We then merge the words back
     * together into a message, and return message.
     * </p>
     * 
     * @param meow
     *            String of Rawr's we want to replace
     * @return a string of Meow's that we replaced.
     */
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
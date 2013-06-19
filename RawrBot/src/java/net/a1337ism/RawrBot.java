package net.a1337ism;

import net.a1337ism.modules.EightballCommand;
import net.a1337ism.modules.HelpCommand;
import net.a1337ism.modules.JokeCommand;
import net.a1337ism.modules.LastSeenCommand;
import net.a1337ism.modules.LogBot;
import net.a1337ism.modules.QuoteCommand;
import net.a1337ism.modules.RateLimiter;
import net.a1337ism.modules.RawrCommand;
import net.a1337ism.modules.ReportCommand;
import net.a1337ism.modules.RulesCommand;
import net.a1337ism.modules.TimeCommand;
import net.a1337ism.modules.UptimeCommand;
import net.a1337ism.util.ircUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;

public class RawrBot extends ListenerAdapter implements Listener {

    /**
     * Easy and recommended way to handle events: Override respective methods in {@link ListenerAdapter}.
     * 
     * *WARNING:* This example requires using a Threaded listener manager (this is PircBotX's default)
     * 
     * @param event
     *            A MessageEvent
     * @throws Exception
     *             If any Exceptions might be thrown, throw them up and let the {@link ListenerManager} handle it. This
     *             can be removed though if not needed
     */

    // Log4j2 Stuff
    private static Logger       logger       = LogManager.getFormatterLogger(RawrBot.class);
    private static final Marker LOG_EVENT    = MarkerManager.getMarker("LOG_EVENT");

    static Config               cfg          = new Config();
    static String               irc_server   = cfg.getProperty("irc_server");
    static int                  irc_port     = Integer.parseInt(cfg.getProperty("irc_port"));
    public static String        irc_channel  = cfg.getProperty("irc_channel");
    static String               irc_nickname = cfg.getProperty("irc_nickname");
    static String               irc_username = cfg.getProperty("irc_username");
    static String               bot_version  = cfg.getProperty("bot_version");
    private static String       bot_password = cfg.getProperty("bot_password");

    @Override
    public void onMessage(final MessageEvent event) throws Exception {
        PircBotX bot = event.getBot();
        // If this isn't a wait test, ignore.
        // This way to handle commands is useful for listers that only listen for one command
        if (!event.getMessage().startsWith("?waitTest start"))
            return;

        // WaitTest has started
        event.respond("Started...");
        WaitForQueue queue = new WaitForQueue(event.getBot());
        // Infinite loop since we might receive messages that aren't WaitTest's.
        while (true) {
            // Use the waitFor() method to wait for a MessageEvent. This will block (wait) until a message event comes
            // in, ignoring everything else
            MessageEvent currentEvent = queue.waitFor(MessageEvent.class);
            // Check if this message is the "ping" command
            if (currentEvent.getMessage().startsWith("?waitTest ping"))
                event.respond("pong");
            // Check if this message is the "end" command
            else if (currentEvent.getMessage().startsWith("?waitTest end")) {
                event.respond("Stopping");

                queue.close();
                // Very important that we end the infinite loop or else the test will continue forever!
                return;
            }
        }
    }

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        if (!bot_password.isEmpty()) {
            logger.info("(" + event.getBot().getNick() + "->NickServ) IDENTIFY " + "PASSWORD_HERE");
            event.getBot().sendMessage("NickServ", "IDENTIFY " + bot_password);
        }
    }

    /**
     * Older way to handle events. We are given a generic event and must cast to the event type that we want. This is
     * helpful for when you need to funnel all events into a single method, eg logging
     * <p>
     * This also shows the other way to send messages: With PircBotX's send* methods. These should be used when the
     * respond() method of the event doesn't send the message to where you want it to go.
     * <p>
     * *WARNING:* If you are extending ListenerAdapter and implementing Listener in the same class (as this does) you
     * *must* call super.onEvent(event); otherwise none of the methods in ListenerAdapter will get called!
     * 
     * @param rawevent
     *            A generic event
     * @throws Exception
     *             If any Exceptions might be thrown, throw them up and let the {@link ListenerManager} handle it. This
     *             can be removed though if not needed
     */
    @Override
    public void onEvent(Event rawevent) throws Exception {
        // Since we extend ListenerAdapter and implement Listener in the same class call the super onEvent so
        // ListenerAdapter will work. Unless you are doing that, this line shouldn't be added
        super.onEvent(rawevent);
        // logger.error(rawevent);
        // logger.debug(rawevent);
        // logger.info(rawevent);

        // Make sure we're dealing with a private message
        if (rawevent instanceof PrivateMessageEvent) {
            // Cast to get access to all the PrivateMessageEvent specific methods
            PrivateMessageEvent event = (PrivateMessageEvent) rawevent;

            // Log the private message
            // logger.info(PRIV_MSG,
            // "(" + event.getUser().getNick() + "->" + event.getBot().getName() + ") " + event.getMessage());
        }

        // Make sure we're dealing with a channel message
        else if (rawevent instanceof MessageEvent) {
            // Cast to get access to all the MessageEvent specific methods
            MessageEvent event = (MessageEvent) rawevent;

            // Log the message
            // logger.info(CHAN_MSG, "<" + event.getUser().getNick() + "> " + event.getMessage());

            // Basic hello
            if (event.getMessage().startsWith("?hi")) {
                ircUtil.sendMessageChannel(event, "Hello");
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        // Create a new bot
        PircBotX bot = new PircBotX();
        // Configuration stuff

        ListenerManager manager = new ThreadedListenerManager();
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        // Log4j setup

        /* String log4JPropertyFile = "C:/Users/Strong Bad/RawrBot/RawrBot/src/resources/log4j.properties"; Properties p
         * = new Properties();
         * 
         * try { p.load(new FileInputStream(log4JPropertyFile)); PropertyConfigurator.configure(p);
         * logger.info("Wow! I'm configured!"); } catch (IOException e) { // DAMN! I'm not....
         * 
         * } */

        // Setup this bot
        bot.setVersion(bot_version); // Set the bot's version
        bot.setName(irc_nickname); // Set the nick of the bot. CHANGE IN YOUR CODE
        bot.setLogin(irc_username); // login part of hostmask, eg name:login@host
        // bot.identify(bot_password); // Password the bot uses to identify with nick / auth services.
        bot.setVerbose(false); // Print everything, which is what you want to do 90% of the time
        bot.setAutoNickChange(true); // Automatically change nick when the current one is in use
        bot.setCapEnabled(true); // Enable CAP features

        // TODO: Spring framework
        // TODO: Add while loop, and make it grab this stuff from a config file maybe?
        // This class is a listener, so add it to the bots known listeners
        // bot.getListenerManager().addListener(new RawrBot());
        // bot.getListenerManager().addListener(new TimeCommand());
        manager.addListener(new LogBot());
        manager.addListener(new RateLimiter());
        manager.addListener(new RawrBot());
        manager.addListener(new TimeCommand());
        manager.addListener(new JokeCommand());
        manager.addListener(new QuoteCommand());
        manager.addListener(new UptimeCommand());
        manager.addListener(new RawrCommand());
        manager.addListener(new HelpCommand());
        manager.addListener(new LastSeenCommand());
        manager.addListener(new ReportCommand());
        manager.addListener(new RulesCommand());
        manager.addListener(new EightballCommand());

        // Set our own ListenerManager
        bot.setListenerManager(manager);

        // bot.connect throws various exceptions for failures
        try {
            // Connect to the freenode IRC network
            bot.connect(irc_server, irc_port);
            // Join the official #pircbotx channel
            bot.joinChannel(irc_channel);
        } // In your code you should catch and handle each exception separately,
          // but here we just lump them all together for simplicity
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

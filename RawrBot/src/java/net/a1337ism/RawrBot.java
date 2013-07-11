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
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
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
    private static Logger       logger        = LogManager.getFormatterLogger(RawrBot.class);
    private static final Marker LOG_EVENT     = MarkerManager.getMarker("LOG_EVENT");

    static Config               cfg           = new Config();
    static String               irc_server    = cfg.getProperty("irc_server");
    static int                  irc_port      = Integer.parseInt(cfg.getProperty("irc_port"));
    public static String        irc_channel   = cfg.getProperty("irc_channel");
    static String               irc_nickname  = cfg.getProperty("irc_nickname");
    static String               irc_username  = cfg.getProperty("irc_username");
    static String               bot_owner     = cfg.getProperty("bot_owner");
    static String               bot_version   = cfg.getProperty("bot_version");
    private static String       bot_password  = cfg.getProperty("bot_password");

    private boolean             bot_reconnect = true;

    @Override
    public void onMessage(final MessageEvent event) throws Exception {
        PircBotX bot = event.getBot();

    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        if (event.getMessage().equalsIgnoreCase("!quit") && event.getUser().getNick().equalsIgnoreCase(bot_owner)) {
            // bot_reconnect = false;
            ircUtil.sendMessage(event, "Shutting Down.");
            event.getBot().quitServer();
        } else if (event.getMessage().equalsIgnoreCase("!quit") && ircUtil.isOP(event, irc_channel)) {
            // bot_reconnect = false;
            ircUtil.sendMessage(event, "Shutting Down.");
            event.getBot().quitServer();
        }

    }

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        if (!bot_password.isEmpty()) {
            logger.info("(" + event.getBot().getNick() + "->NickServ) IDENTIFY " + "PASSWORD_HERE");
            event.getBot().sendMessage("NickServ", "IDENTIFY " + bot_password);
        }
    }

    @Override
    public void onQuit(QuitEvent event) throws Exception {
        // If we see our default nickname quit, then rename our name to it.
        if (event.getUser().getNick().equalsIgnoreCase(irc_nickname)) {
            event.getBot().changeNick(irc_nickname);
        }
    }

    private String alterCollidedNick(String nickname) {
        // If there is already a nickname with our name on the server, then we need to change our name so it'll work.
        if (nickname.contains("Rawr")) {
            nickname.replace("Rawr", "Rawrr");
            return nickname;
        } else {
            return nickname + "_";
        }
    }

    @Override
    public void onUnknown(UnknownEvent event) throws Exception {
        if (event.getLine().startsWith("NETSPLIT")) {
            logger.info("NETSPLIT EVENT");
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
            // if (event.getMessage().startsWith("?hi")) {
            // ircUtil.sendMessageChannel(event, "Hello");
            // }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        // Create a new bot
        PircBotX bot = new PircBotX();
        // Configuration stuff

        ListenerManager manager = new ThreadedListenerManager();
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");
        Class.forName("com.mysql.jdbc.Driver");

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
        // bot.setAutoReconnect(true); // Apparently doesn't work. Just throws exceptions. Yay.
        // bot.setAutoReconnectChannels(true);

        // TODO: Spring framework
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

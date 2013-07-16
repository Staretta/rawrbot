package net.a1337ism;

import net.a1337ism.modules.EightballCommand;
import net.a1337ism.modules.HelpCommand;
import net.a1337ism.modules.JokeCommand;
import net.a1337ism.modules.LastSeenCommand;
import net.a1337ism.modules.QuoteCommand;
import net.a1337ism.modules.RateLimiter;
import net.a1337ism.modules.RawrCommand;
import net.a1337ism.modules.ReportCommand;
import net.a1337ism.modules.RulesCommand;
import net.a1337ism.modules.TimeCommand;
import net.a1337ism.modules.UptimeCommand;
import net.a1337ism.util.ircUtil;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.managers.ListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawrBot extends ListenerAdapter implements Listener {
    // slf4j Stuff
    private static Logger logger       = LoggerFactory.getLogger(RawrBot.class);

    // Config File
    static Config         cfg          = new Config();
    static String         irc_server   = cfg.getProperty("irc_server");
    static int            irc_port     = Integer.parseInt(cfg.getProperty("irc_port"));
    public static String  irc_channel  = cfg.getProperty("irc_channel");
    static String         irc_nickname = cfg.getProperty("irc_nickname");
    static String         irc_username = cfg.getProperty("irc_username");
    static String         bot_owner    = cfg.getProperty("bot_owner");
    static String         bot_version  = cfg.getProperty("bot_version");
    private static String bot_password = cfg.getProperty("bot_password");

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
    @Override
    public void onMessage(final MessageEvent event) throws Exception {
        PircBotX bot = event.getBot();
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        if (event.getMessage().equalsIgnoreCase("!quit") && event.getUser().getNick().equalsIgnoreCase(bot_owner)) {
            // Shutdown upon receiving a quit command
            ircUtil.sendMessage(event, "Shutting Down.");
            event.getBot().stopBotReconnect();
            event.getBot().sendIRC().quitServer();
        } else if (event.getMessage().equalsIgnoreCase("!quit") && ircUtil.isOP(event, irc_channel)) {
            // Shutdown upon receiving a quit command
            ircUtil.sendMessage(event, "Shutting Down.");
            event.getBot().stopBotReconnect();
            event.getBot().sendIRC().quitServer();
        }

    }

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        if (!bot_password.isEmpty()) {
            // TODO: replace with identify at some point.
            logger.info("(" + event.getBot().getNick() + "->NickServ) IDENTIFY " + "PASSWORD_HERE");
            event.getBot().sendIRC().message("NickServ", "IDENTIFY " + bot_password);
        }
    }

    @Override
    public void onQuit(QuitEvent event) throws Exception {
        // If we see our default nickname quit, then rename our name to it.
        if (event.getUser().getNick().equalsIgnoreCase(irc_nickname)) {
            event.getBot().sendIRC().changeNick(irc_nickname);
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

    // @Override
    // public void onReconnect(ReconnectEvent event) throws Exception {
    // if (!bot_password.isEmpty()) {
    // logger.info("(" + event.getBot().getNick() + "->NickServ) IDENTIFY " + "PASSWORD_HERE");
    // event.getBot().sendMessage("NickServ", "IDENTIFY " + bot_password);
    // }
    //
    // event.getBot().joinChannel(irc_channel);
    // }

    @Override
    public void onDisconnect(DisconnectEvent event) throws Exception {
        // Thread.sleep(10000);
        // try {
        // event.getBot();
        // } catch (NickAlreadyInUseException ex) {
        // event.getBot().changeNick(alterCollidedNick(event.getBot().getNick()));
        // event.getBot().reconnect();
        // }
    }

    @Override
    public void onUnknown(UnknownEvent event) throws Exception {
        logger.info(event.toString());
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
        // Load the sqlite-JDBC and mysql-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");
        Class.forName("com.mysql.jdbc.Driver");

        // @formatter:off
        // Configuration
        Configuration configuration = new Configuration.Builder()
                .setName(irc_nickname)
                .setLogin(irc_username)
                .setVersion(bot_version)
                .setAutoNickChange(true)
                .setAutoReconnect(true)
                .setCapEnabled(true)
                .setIdentServerEnabled(false)
                .setServerHostname(irc_server)
                .setServerPort(irc_port)
                .addAutoJoinChannel(irc_channel)
                //.addListener(new LogBot())
                .addListener(new RateLimiter())
                .addListener(new RawrBot())
                .addListener(new TimeCommand())
                .addListener(new JokeCommand())
                .addListener(new QuoteCommand())
                .addListener(new UptimeCommand())
                .addListener(new RawrCommand())
                .addListener(new HelpCommand())
                .addListener(new LastSeenCommand())
                .addListener(new ReportCommand())
                .addListener(new RulesCommand())
                .addListener(new EightballCommand())
                .buildConfiguration();
        PircBotX bot = new PircBotX(configuration);
        // @formatter:on

        try {
            bot.startBot();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

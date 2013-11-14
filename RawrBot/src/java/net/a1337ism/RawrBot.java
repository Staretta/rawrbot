package net.a1337ism;

import net.a1337ism.modules.Eightball;
import net.a1337ism.modules.Greeter;
import net.a1337ism.modules.Help;
import net.a1337ism.modules.Joke;
import net.a1337ism.modules.LastSeen;
import net.a1337ism.modules.Quote;
import net.a1337ism.modules.RateLimiter;
import net.a1337ism.modules.Rawr;
import net.a1337ism.modules.Report;
import net.a1337ism.modules.Rules;
import net.a1337ism.modules.Uptime;
import net.a1337ism.modules.Youtube;
import net.a1337ism.util.ircUtil;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UnknownEvent;
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
    public static String  bot_owner    = cfg.getProperty("bot_owner");
    static String         bot_version  = cfg.getProperty("bot_version");
    private static String bot_password = cfg.getProperty("bot_password");

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        if ((event.getUser().getNick().equalsIgnoreCase(bot_owner) || ircUtil.isOP(event, irc_channel))
                && event.getMessage().equalsIgnoreCase("!quit")) {
            // Shutdown upon receiving a quit command
            ircUtil.sendMessage(event, "Shutting Down.");
            event.getBot().stopBotReconnect();
            event.getBot().sendIRC().quitServer();
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

    @Override
    public void onUnknown(UnknownEvent event) throws Exception {
        logger.info(event.toString());
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
                .setRealName(bot_version)
                .setAutoNickChange(true)
                .setAutoReconnect(true)
                .setCapEnabled(true)
                .setIdentServerEnabled(false)
                .setServerHostname(irc_server)
                .setServerPort(irc_port)
                .addAutoJoinChannel(irc_channel)
                //.setNickservPassword(bot_password) // REMEMER TO UNCOMMENT THIS BEFORE PUSHING UPDATE
                //.addListener(new LogBot())
                .addListener(new RateLimiter())
                .addListener(new RawrBot())
                .addListener(new Joke())
                .addListener(new Quote())
                .addListener(new Uptime())
                .addListener(new Rawr())
                .addListener(new Help())
                .addListener(new LastSeen())
                .addListener(new Report())
                .addListener(new Rules())
                .addListener(new Eightball())
                .addListener(new Greeter())
                .addListener(new Youtube())
                .buildConfiguration();
        PircBotX bot = new PircBotX(configuration);
        // @formatter:on

        try {
            bot.startBot();
        } catch (Exception ex) {
            logger.info("Exception in RawrBot.main: " + ex.toString());
        }
    }
}

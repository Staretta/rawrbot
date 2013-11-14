package net.a1337ism.modules;

import net.a1337ism.RawrBot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dice extends ListenerAdapter {
    // Logger shit
    private static Logger logger       = LoggerFactory.getLogger(RawrBot.class);
    private int           maxDies      = 1000;
    private int           maxLimit     = 1000;
    private int           defaultDies  = 1;
    private int           defaultLimit = 20;

    private int roll() {
        return defaultDies;

    }

    @Override
    public void onMessage(MessageEvent event) {

    }

}
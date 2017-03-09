package net.staretta.modules;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.Giphy4JConstants;
import at.mukprojects.giphy4j.entity.search.SearchFeed;
import at.mukprojects.giphy4j.entity.search.SearchRandom;
import at.mukprojects.giphy4j.exception.GiphyException;
import com.google.common.collect.ImmutableSet;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.Command;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.RateLimiter;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GiphyListener extends BaseListener {

    private static final String GIFCOMMAND = "!gif";
    private static final String API_KEY = "dc6zaTOxFJmzC";

    private final Giphy giphy = new Giphy(API_KEY);

    @Override
    protected ModuleInfo setModuleInfo() {
        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.setName("Giphy");
        moduleInfo.setAuthor("ElizaRei");
        moduleInfo.setVersion("v1.0");
        moduleInfo.addCommand(new Command(GIFCOMMAND, "!gif [search] finds you a gif"));
        return moduleInfo;
    }

    @Override
    public void OnMessage(MessageEvent event) {
        String message = event.getMessage();
        if (isCommand(event.getMessage(), GIFCOMMAND) && !RateLimiter.isRateLimited(event.getUser())) {
            try {
                String[] split = message.split("\\s", 2);

                if(split.length > 1) {
                    SearchRandom searchRandom = giphy.searchRandom(split[1]);
                    String gif = searchRandom.getData().getImageOriginalUrl();
                    event.getChannel().send().message(gif);
                } else {
                    Optional<Command> command = getModuleInfo().getCommands().stream().filter(c -> c.getCommand().equals("!gif")).findFirst();

                    command.ifPresent(command1 -> event.getChannel().send().message(command1.getCommandHelp().get(0)));
                }
            } catch (GiphyException ignored) {
            }
        }
    }



    @Override
    public void OnPrivateMessage(PrivateMessageEvent event) {

    }
}

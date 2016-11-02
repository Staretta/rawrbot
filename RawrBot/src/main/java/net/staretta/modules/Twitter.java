package net.staretta.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.staretta.RawrBot;
import net.staretta.businesslogic.BaseListener;
import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.entity.GlobalConfigEntity;
import net.staretta.businesslogic.services.ServerService;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter extends BaseListener
{
	private String twitterRegex = "(https?://)?(www\\.)?twitter\\.com/(#!/)?@?([^/]*)(\\/status\\/([0-9]*))?";
	private String tcoRegex = "https?://t\\.co\\/([a-zA-Z0-9]*)";
	private Pattern twitterPattern = Pattern.compile(twitterRegex, Pattern.CASE_INSENSITIVE);
	private Pattern tcoPattern = Pattern.compile(tcoRegex, Pattern.CASE_INSENSITIVE);
	private TwitterFactory twitterFactory;
	private ServerService serverService;
	
	public Twitter()
	{
		serverService = RawrBot.getAppCtx().getBean(ServerService.class);
	}
	
	@Override
	protected ModuleInfo setModuleInfo()
	{
		ModuleInfo moduleInfo = new ModuleInfo();
		moduleInfo.setAuthor("Staretta");
		moduleInfo.setName("Twitter");
		moduleInfo.setVersion("v0.1");
		return new ModuleInfo();
	}
	
	private boolean isTwitterUrl(String link)
	{
		Matcher matcher = twitterPattern.matcher(link);
		if (matcher.find())
			return true;
		return false;
	}
	
	private long getTwitterStatusId(String link)
	{
		Matcher matcher = twitterPattern.matcher(link);
		if (matcher.find())
		{
			String groupIndex6 = matcher.group(6);
			if (groupIndex6 != null)
			{
				return Long.parseLong(groupIndex6);
			}
		}
		return 0L;
	}
	
	private void init(String server) throws NullPointerException
	{
		GlobalConfigEntity config = serverService.getGlobalConfig(server);
		if (config != null && !config.getTwitterConsumerKey().isEmpty())
		{
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true);
			cb.setOAuthConsumerKey(config.getTwitterConsumerKey());
			cb.setOAuthConsumerSecret(config.getTwitterConsumerSecret());
			cb.setOAuthAccessToken(config.getTwitterAccessToken());
			cb.setOAuthAccessTokenSecret(config.getTwitterAccessSecret());
			twitterFactory = new TwitterFactory(cb.build());
		}
	}
	
	private String stripUrls(String message)
	{
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(message.split("\\s")));
		list.remove(list.size() - 1);
		return StringUtils.join(list.toArray(), " ");
	}
	
	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		String m = event.getMessage();
		if (isTwitterUrl(m))
		{
			try
			{
				if (twitterFactory == null)
				{
					init(event.getBot().getServerHostname());
				}
				
				if (twitterFactory != null)
				{
					twitter4j.Twitter twitter = twitterFactory.getInstance();
					
					long id = getTwitterStatusId(m);
					if (id > 0L)
					{
						ResponseList<Status> statusList = twitter.lookup(new long[] { id });
						if (statusList != null && !statusList.isEmpty())
						{
							Status status = statusList.get(0);
							StringBuilder message = new StringBuilder();
							message.append("Twitter: ");
							message.append("@" + status.getUser().getScreenName() + " ");
							message.append(status.getText() + " ");
							
							// if (status.getMediaEntities().length > 0)
							// {
							// MediaEntity[] mediaEntities = status.getMediaEntities();
							// for (MediaEntity entity : mediaEntities)
							// {
							// message.append(entity.getDisplayURL() + " ");
							// }
							// }
							//
							// if (status.getURLEntities().length > 0)
							// {
							// URLEntity[] urlEntities = status.getURLEntities();
							// for (URLEntity entity : urlEntities)
							// {
							// message.append(entity.getURL() + " ");
							// }
							// }
							
							event.getChannel().send().message(message.toString());
						}
					}
				}
			}
			catch (TwitterException | NullPointerException e)
			{
				getLogger().error("ERROR: ", e);
			}
		}
	}
	
	@Override
	public void onAction(ActionEvent event) throws Exception
	{
		String m = event.getMessage();
		if (isTwitterUrl(m))
		{
			try
			{
				if (twitterFactory == null)
				{
					init(event.getBot().getServerHostname());
				}
				
				if (twitterFactory != null)
				{
					twitter4j.Twitter twitter = twitterFactory.getInstance();
					
					ResponseList<Status> statusList = twitter.lookup(new long[] { getTwitterStatusId(m) });
					if (statusList != null && !statusList.isEmpty())
					{
						Status status = statusList.get(0);
						StringBuilder message = new StringBuilder();
						message.append("Twitter: ");
						message.append("@" + status.getUser().getScreenName() + " ");
						message.append(status.getText() + " ");
						
						// if (status.getMediaEntities().length > 0)
						// {
						// MediaEntity[] mediaEntities = status.getMediaEntities();
						// for (MediaEntity entity : mediaEntities)
						// {
						// message.append(entity.getDisplayURL() + " ");
						// }
						// }
						//
						// if (status.getURLEntities().length > 0)
						// {
						// URLEntity[] urlEntities = status.getURLEntities();
						// for (URLEntity entity : urlEntities)
						// {
						// message.append(entity.getURL() + " ");
						// }
						// }
						
						event.getChannel().send().message(message.toString());
					}
				}
			}
			catch (TwitterException | NullPointerException e)
			{
				getLogger().error("ERROR: ", e);
			}
		}
	}
	
	@Override
	public void OnMessage(MessageEvent event)
	{
		// IGNORE
	}
	
	@Override
	public void OnPrivateMessage(PrivateMessageEvent event)
	{
		// IGNORE
	}
}

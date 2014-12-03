package net.staretta.businesslogic.util;

import java.util.Iterator;
import java.util.Set;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ircUtil
{
	private static Logger logger = LoggerFactory.getLogger(ircUtil.class);

	public static Set<User> channelOPs(MessageEvent event, String channel)
	{
		// Returns a list of nicknames of channel operators, divided by channel in the list.
		// Based on channels that the bot is currently in.
		// Something like { #channel1 { Operator1, Operator2, Operator3 }, #channel2 { Operator4, Operator5 } }
		return null;
	}

	public static Set<User> channelOPs(PrivateMessageEvent event, String channel)
	{
		// Returns a list of nicknames of channel operators, divided by channel in the list.
		// Based on channels that the bot is currently in.
		// Something like { #channel1 { Operator1, Operator2, Operator3 }, #channel2 { Operator4, Operator5 } }
		return null;
	}

	public static Set<User> channelVoiced(MessageEvent event, String channel)
	{
		// Returns a list of nicknames of channel voiced, divided by channel in the list.
		// Based on channels that the bot is currently in.
		// Something like { #channel1 { Voiced1, Voiced2, Voiced3 }, #channel2 { Voiced4, Voiced5 } }
		return null;
	}

	public static Set<User> channelVoiced(PrivateMessageEvent event, String channel)
	{
		// Returns a list of nicknames of channel voiced, divided by channel in the list.
		// Based on channels that the bot is currently in.
		// Something like { #channel1 { Voiced1, Voiced2, Voiced3 }, #channel2 { Voiced4, Voiced5 } }
		return null;
	}

	/**
	 * Checks to see if user is an operator of the specified channel.
	 * 
	 * @param event
	 *            MessageEvent
	 * @return boolean
	 * 
	 */
	public static boolean isOP(MessageEvent event)
	{
		// Initialize the variable.
		boolean isOP = false;

		// Get list of operators in a channel.
		Set<User> operators = event.getChannel().getOps();
		Iterator<User> itr = operators.iterator();
		// Step through the set, and see if the user is an operator.
		while (itr.hasNext())
		{
			if (itr.next().getNick().equalsIgnoreCase(event.getUser().getNick()))
				isOP = true;
		}

		return isOP;
	}

	/**
	 * Checks to see if user is an operator of the specified channel.
	 * 
	 * @param event
	 *            MessageEvent
	 * @param channel
	 *            Channel we want to check
	 * @return boolean
	 * 
	 */
	public static boolean isOP(MessageEvent event, String channel)
	{
		// See if user is an operator of the specified channel.
		// Initialize the variable.
		boolean isOP = false;

		// Get list of operators in a channel.
		Set<Channel> channels = event.getUser().getChannelsOpIn();
		Iterator<Channel> itr = channels.iterator();
		// Step through the set, and see if the user is an operator.
		while (itr.hasNext())
		{
			Channel chan = itr.next();
			if (chan.getName().equalsIgnoreCase(channel) && chan.isOp(event.getUser()))
			{
				isOP = true;
			}
		}

		return isOP;
	}

	/**
	 * Checks to see if user is an operator of the specified channel.
	 * 
	 * @param event
	 *            PrivateMessageEvent
	 * @param channel
	 *            Channel we want to check
	 * @return boolean
	 * 
	 */
	public static boolean isOP(PrivateMessageEvent event, String channel)
	{
		// See if user is an operator of the specified channel.
		// Initialize the variable.
		boolean isOP = false;

		// Need to go about this a different way. By getting all channels that the user is an operator in.
		Set<Channel> channels = event.getUser().getChannelsOpIn();
		Iterator<Channel> itr = channels.iterator();
		while (itr.hasNext())
		{
			Channel chan = itr.next();
			if (chan.getName().equalsIgnoreCase(channel) && chan.isOp(event.getUser()))
			{
				isOP = true;
			}
		}

		return isOP;
	}
}
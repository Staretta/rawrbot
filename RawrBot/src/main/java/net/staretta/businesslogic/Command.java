package net.staretta.businesslogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Command
{
	private String command = "";
	private List<String> commandHelp = new ArrayList<String>();
	private Pattern regex = null;
	private List<Option> options;
	
	public Command()
	{
	}
	
	public Command(String command)
	{
		this.command = command;
	}
	
	public Command(String command, String... commandHelp)
	{
		this.command = command;
		this.commandHelp = Arrays.asList(commandHelp);
	}
	
	public Command(String command, Pattern regex, String... commandHelp)
	{
		this.command = command;
		this.regex = regex;
		this.commandHelp = Arrays.asList(commandHelp);
	}
	
	public Command(String command, ArrayList<String> commandHelp)
	{
		this.command = command;
		this.commandHelp = commandHelp;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public void setCommand(String command)
	{
		this.command = command;
	}
	
	public List<String> getCommandHelp()
	{
		return commandHelp;
	}
	
	public void setCommandHelp(String... commandHelp)
	{
		this.commandHelp = Arrays.asList(commandHelp);
	}
	
	public void setCommandHelp(ArrayList<String> commandHelp)
	{
		this.commandHelp = commandHelp;
	}
	
	public Pattern getRegex()
	{
		return regex;
	}
	
	public void setRegex(Pattern regex)
	{
		this.regex = regex;
	}
	
}

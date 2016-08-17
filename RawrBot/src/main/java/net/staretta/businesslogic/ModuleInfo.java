package net.staretta.businesslogic;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ModuleInfo
{
	private String moduleName = "";
	private String authorName = "";
	private String authorEmail = "";
	private String moduleUrl = "";
	private String moduleVersion = "";
	private ArrayList<Command> commandList = new ArrayList<Command>();
	
	public ModuleInfo()
	{
		
	}
	
	public void addCommand(Command command)
	{
		this.commandList.add(command);
	}
	
	public void addCommand(String command, Pattern regex, String... commandHelp)
	{
	}
	
	public ArrayList<Command> getCommands()
	{
		return commandList;
	}
	
	public String getName()
	{
		return moduleName;
	}
	
	public void setName(String moduleName)
	{
		this.moduleName = moduleName;
	}
	
	public String getAuthor()
	{
		return authorName;
	}
	
	public void setAuthor(String authorName)
	{
		this.authorName = authorName;
	}
	
	public String getAuthorEmail()
	{
		return authorEmail;
	}
	
	public void setAuthorEmail(String authorEmail)
	{
		this.authorEmail = authorEmail;
	}
	
	public String getUrl()
	{
		return moduleUrl;
	}
	
	public void setUrl(String moduleUrl)
	{
		this.moduleUrl = moduleUrl;
	}
	
	public String getVersion()
	{
		return moduleVersion;
	}
	
	public void setVersion(String moduleVersion)
	{
		this.moduleVersion = moduleVersion;
	}
}
package net.staretta.businesslogic;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class ModuleInfo
{
	private String moduleName = "";
	private String moduleAuthor = "";
	private String moduleUrl = "";
	private String moduleVersion = "";
	private List<Pair<String, String>> commandList = new ArrayList<Pair<String, String>>();
	
	public ModuleInfo()
	{
		
	}
	
	public void addCommandInfo(String command, String commandHelp)
	{
		Pair<String, String> pair = Pair.with(command, commandHelp);
		this.commandList.add(pair);
	}
	
	public List<Pair<String, String>> getCommands()
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
		return moduleAuthor;
	}
	
	public void setAuthor(String moduleAuthor)
	{
		this.moduleAuthor = moduleAuthor;
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
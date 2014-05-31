package net.staretta.businesslogic;

public class ModuleInfo
{
	private String	moduleName		= "";
	private String	moduleAuthor	= "";
	private String	moduleUrl		= "";
	private String	moduleVersion	= "";
	private String	helpMessage		= "";
	private String	helpCommand		= "";

	public ModuleInfo()
	{

	}

	public ModuleInfo(String moduleName, String helpMessage, String helpCommand)
	{
		this.moduleName = moduleName;
		this.helpMessage = helpMessage;
		this.helpCommand = helpCommand;
	}

	public ModuleInfo(String moduleName, String helpMessage, String helpCommand, String moduleAuthor)
	{
		this.moduleName = moduleName;
		this.helpMessage = helpMessage;
		this.helpCommand = helpCommand;
		this.moduleAuthor = moduleAuthor;
	}

	public String getName()
	{
		return moduleName;
	}

	public void setName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	public String getHelpMessage()
	{
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage)
	{
		this.helpMessage = helpMessage;
	}

	public String getHelpCommand()
	{
		return helpCommand;
	}

	public void setHelpCommand(String helpCommand)
	{
		this.helpCommand = helpCommand;
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

	public String getModuleVersion()
	{
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion)
	{
		this.moduleVersion = moduleVersion;
	}
}
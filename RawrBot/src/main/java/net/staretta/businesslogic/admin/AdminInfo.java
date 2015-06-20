package net.staretta.businesslogic.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.staretta.businesslogic.ModuleInfo;

public class AdminInfo extends ModuleInfo
{
	private String adminVersion = "";
	private HashMap<String, List<String>> adminOptionList = new HashMap<String, List<String>>();
	
	public AdminInfo()
	{
		
	}
	
	public HashMap<String, List<String>> getCommands()
	{
		return adminOptionList;
	}
	
	public void addOption(String option)
	{
		this.adminOptionList.put(option, Arrays.asList());
	}
	
	public void addOption(String option, String optionHelp)
	{
		this.adminOptionList.put(option, Arrays.asList(optionHelp));
	}
	
	public void addOption(String option, List<String> optionHelp)
	{
		this.adminOptionList.put(option, optionHelp);
	}
	
	public String getAdminVersion()
	{
		return adminVersion;
	}
	
	public void setAdminVersion(String adminVersion)
	{
		this.adminVersion = adminVersion;
	}
}
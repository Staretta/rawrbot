package net.staretta.businesslogic.admin;

import java.util.ArrayList;

import net.staretta.businesslogic.ModuleInfo;
import net.staretta.businesslogic.Option;

public class AdminInfo extends ModuleInfo
{
	private String adminVersion = "";
	private ArrayList<Option> adminOptionList = new ArrayList<Option>();
	
	public AdminInfo()
	{
		
	}
	
	public ArrayList<Option> getOptions()
	{
		return adminOptionList;
	}
	
	public void addOption(Option option)
	{
		this.adminOptionList.add(option);
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
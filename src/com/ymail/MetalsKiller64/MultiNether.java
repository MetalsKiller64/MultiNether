package com.ymail.MetalsKiller64;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.*;

public final class MultiNether extends JavaPlugin
{
    
    public List<String> command_list = new ArrayList<String>();
	
	public FileConfiguration portal_config = null;
	public File portal_config_file = null;
	
	public FileConfiguration reverse_portal_config = null;
	public File reverse_portal_config_file = null;
	
	public CmdExecutor cmd;
    
    @Override
    public void onEnable()
    {
		command_list.add("rtp");
		command_list.add("mn-link");
		command_list.add("mn");
		
		cmd = new CmdExecutor(this);
		
		getCommand("rtp").setExecutor(new CmdExecutor(this));
		getCommand("mn-link").setExecutor(new CmdExecutor(this));
		getCommand("cmdtest").setExecutor(new CmdExecutor(this));
		getCommand("mn").setExecutor(new CmdExecutor(this));
		
		this.portal_config = getPortalConfig();
		this.reverse_portal_config = getReversePortalConfig();
		savePortalConfig();
		saveReversePortalConfig();
		getConfig().options().copyDefaults(true);
		getConfig().set("LinkWorlds", true);
		getConfig().set("PortalCount", 0);
		saveConfig();
		
		NetherPortListener npl = new NetherPortListener(this);
		
		//PluginManager pm = Bukkit.getServer().getPluginManager();
		//pm.registerEvents(new NetherPortListener(), this);
    }
    
    @Override
    public void onDisable()
    {
		
    }
	
	public FileConfiguration getPortalConfig()
	{
		if ( portal_config == null )
		{
			loadPortalConfig();
		}
		return portal_config;
	}
	
	public void loadPortalConfig()
	{
		if ( portal_config_file == null )
		{
			portal_config_file = new File(getDataFolder(), "Portals.yml");
		}
		portal_config = YamlConfiguration.loadConfiguration(portal_config_file);
		
		InputStream defConfigStream = getResource("Portals.yml");
		if ( defConfigStream != null )
		{
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			portal_config.setDefaults(defConfig);
		}
	}
	
	public void savePortalConfig()
	{
		if ( !(portal_config == null) && !(portal_config_file == null) )
		{
			try
			{
				portal_config.save(portal_config_file);
			}
			catch ( IOException ioe )
			{
				Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konnte Portal-Config nicht speichern! (IOException)");
			}
		}
	}
	
	
	public FileConfiguration getReversePortalConfig()
	{
		if ( reverse_portal_config == null )
		{
			loadReversePortalConfig();
		}
		return reverse_portal_config;
	}
	
	public void loadReversePortalConfig()
	{
		if ( reverse_portal_config_file == null )
		{
			reverse_portal_config_file = new File(getDataFolder(), "ReversePortals.yml");
		}
		reverse_portal_config = YamlConfiguration.loadConfiguration(reverse_portal_config_file);
		
		InputStream defConfigStream = getResource("ReversePortals.yml");
		if ( defConfigStream != null )
		{
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			reverse_portal_config.setDefaults(defConfig);
		}
	}
	
	public void saveReversePortalConfig()
	{
		if ( !(reverse_portal_config == null) && !(reverse_portal_config_file == null) )
		{
			try
			{
				reverse_portal_config.save(reverse_portal_config_file);
			}
			catch ( IOException ioe )
			{
				Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konnte ReversePortal-Config nicht speichern! (IOException)");
			}
		}
	}
	
}

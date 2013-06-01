package com.ymail.MetalsKiller64;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.*;

public final class MultiNether extends JavaPlugin
{
    
    public List<String> command_list = new ArrayList<String>();
	public FileConfiguration portal_config = null;
	public File portal_config_file = null;
    
    @Override
    public void onEnable()
    {
		command_list.add("rtp");
		command_list.add("netherlink");
		command_list.add("netherport");
		command_list.add("functest");
		//TODO: for-schleife über command_list
		getCommand("rtp").setExecutor(new CmdExecutor(this));
		getCommand("netherlink").setExecutor(new CmdExecutor(this));
		getCommand("netherport").setExecutor(new CmdExecutor(this));
		getCommand("cmdtest").setExecutor(new CmdExecutor(this));
		this.portal_config = getPortalConfig();
		savePortalConfig();
		getConfig().options().copyDefaults(true); //FIXME: Defaults überschreiben??
		getConfig().set("LinkWorlds", true);
		saveConfig();
		//TODO: Befehl zum automatischen Erstellen eines Nethers zur angegebenen Welt mit passendem Seed
		//TODO: Nether-Portale; zu jedem Portal auf der Oberwelt ein Portal im zugehörigen Nether generieren; in Verbindung mit Config
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
			catch (IOException e)
			{
				Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Konnte Portal-Config nicht speichern! (IOException)");
			}
		}
	}
}
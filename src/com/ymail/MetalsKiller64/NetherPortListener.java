package com.ymail.MetalsKiller64;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class NetherPortListener implements Listener
{
	private MultiNether multinether;
	private CmdExecutor cmd;
	private Logger logger;
	public NetherPortListener(MultiNether plugin)
	{
		this.multinether = plugin;
		multinether.getServer().getPluginManager().registerEvents(this, plugin);
		this.cmd = plugin.cmd;
		this.logger = multinether.getLogger();
	}
	
	@EventHandler
	public void onPlayerPortalEnter(PlayerPortalEvent e)
	{
		Player player = e.getPlayer();
		Location player_location = player.getLocation();
		
		boolean is_nether = player_location.getWorld().getEnvironment().equals(Environment.NETHER);
		
		String entrance_world = player_location.getWorld().getName();
		List<World> worlds = Bukkit.getWorlds();
		String nether_name = entrance_world+"_nether";
		String overworld_name = entrance_world.split("_")[0];
		
		logger.log(Level.INFO, "entrance_world: {0}", entrance_world);
		logger.log(Level.INFO, "nether_name: {0}", nether_name);
		logger.log(Level.INFO, "overworld_name: {0}", overworld_name);
		
		World nether = null;
		World overworld = null;
		
		if ( !(is_nether) )
		{
			for ( int h = 0; h < worlds.size(); h++ )
			{
				if ( worlds.get(h).getName().equalsIgnoreCase(nether_name) )
				{
					//nether vorhanden
					nether = worlds.get(h);
				}
			}
			if ( nether == null )
			{
				//kein nether vorhanden
			}
		}
		else
		{
			for ( int i = 0; i < worlds.size(); i++ )
			{
				if ( worlds.get(i).getName().equalsIgnoreCase(overworld_name) )
				{
					overworld = worlds.get(i);
				}
			}
			if ( overworld == null )
			{
				//fail
			}
		}
		
		Location player_location_on_other_world = player_location;
		if ( is_nether )
		{
			player_location_on_other_world.setWorld(overworld);
		}
		else
		{
			player_location_on_other_world.setWorld(nether);
		}
		Location near_portal_location = cmd.get_NearestPortal(player_location_on_other_world);
		logger.log(Level.INFO, player_location_on_other_world.toString());
		if ( near_portal_location == null )
		{
			logger.log(Level.INFO, "fail, kein portal gefunden");
		}
		else
		{
			player.teleport(near_portal_location);
		}
		/*
		ConfigurationSection netherlinks = multinether.getConfig().getConfigurationSection("NetherLinks");
		Set<String> netherlink_keys = netherlinks.getKeys(true);
		Object[] key_list = netherlink_keys.toArray();
		List<Object> keys = Arrays.asList(key_list);
		if ( keys.contains(entrance_world) )
		{
			is_nether = false;
		}
		else
		{
			is_nether = true;
		}
		*/
		
		/*
		Portal entrance_portal;
		String link_to;
		Integer reverse_id;
		
		if ( is_nether )
		{
			entrance_portal = cmd.getNearestReversePortal(player_location);
			link_to = entrance_portal.getLinkTo();
			reverse_id = entrance_portal.getCorrespondingID();
		}
		else
		{
			entrance_portal = cmd.getNearestPortal(player_location);
			if ( !(cmd.linkExists(entrance_world)) )
			{
				cmd.addLink(player, player.getWorld().getName(), entrance_portal.getLinkTo());
			}
			link_to = entrance_portal.getLinkTo();
			reverse_id = entrance_portal.getCorrespondingID();
		}
		
		logger.log(Level.INFO, "is nether: "+is_nether);
		Integer link_portal_id = 0;
		if ( is_nether )
		{
			link_to = multinether.getReversePortalConfig().getString(entrance_portal.getID()+".linkto");
			link_portal_id = multinether.getReversePortalConfig().getInt(entrance_portal.getID()+".linktoid");
		}
		else
		{
			link_to = multinether.getPortalConfig().getString(entrance_portal.getID()+".linkto");
			link_portal_id = multinether.getPortalConfig().getInt(entrance_portal.getID()+".linktoid");
		}
		*/
		
		/*
		logger.log(Level.INFO, "linkt_to: {0}", link_to);
		logger.log(Level.INFO, "reverse id: {0}", reverse_id);
		
		Portal reverse_portal = null;
		
		if ( is_nether )
		{
			reverse_portal = cmd.getPortal(reverse_id);
		}
		else
		{
			reverse_portal = cmd.getReversePortal(reverse_id);
		}
		
		if ( !(reverse_portal == null) )
		{
			//log.log(Level.INFO, "reverse_id: {0}", reverse_portal.getID());
			//log.log(Level.INFO, "r_x: {0}", reverse_portal.getX()+" r_y: "+reverse_portal.getY()+" r_z: "+reverse_portal.getZ());
			//log.log(Level.INFO, "reverse world: {0}", reverse_portal.getWorld());
			//log.log(Level.INFO, "Bukkit.getWorld: {0}", Bukkit.getWorld(reverse_portal.getWorld()).getName());
			/*FIXME: teleportation vom nether funktioniert nur wenn dieser kein standard nether ist (z.B. World1_nether)
			 * ansonsten wird das portal event nicht vom NetherPortListener übernommen
			 * -> folge: man landet nicht auf der in der config eingetragenen welt sondern auf der standard welt (z.B. World1)
			 * /
			double x = reverse_portal.getX();
			Integer y = reverse_portal.getY();
			double z = reverse_portal.getZ();
			int orientation = 0;
			if ( is_nether )
			{
				if ( orientation == 0 )
				{
					x = x + 0.9;
				}
				else if ( orientation == 1 )
				{
					x = x - 0.9;
				}
				else if ( orientation == 2 )
				{
					z = z + 0.9;
				}
				else
				{
					z = z - 0.9;
				}
			}
			logger.log(Level.INFO, y.getClass().getName());
			logger.log(Level.INFO, "x: {0}", x);
			logger.log(Level.INFO, "y: {0}", y);
			logger.log(Level.INFO, "z: {0}", z);
			player.teleport(new Location(Bukkit.getWorld(reverse_portal.getWorld()), x, y, z));
		}
		else
		{
			logger.log(Level.SEVERE, "bug: portal is null");
		}
		*/
	}
	
	@EventHandler
	public void onPortalCreate(PortalCreateEvent pce)
	{
		//TODO: wenn noch kein Nether vorhanden ist, einen neuen mit dem Namen und dem Seed der Oberwelt anlegen (wie in Multiverse, z.B.: Welt_nether)
		
		boolean is_nether = pce.getWorld().getEnvironment().equals(Environment.NETHER);
		
		String entrance_world = pce.getWorld().getName();
		List<World> worlds = Bukkit.getWorlds();
		String nether_name = entrance_world+"_nether";
		String overworld_name = entrance_world.split("_")[0];
		
		logger.log(Level.INFO, "entrance_world: {0}", entrance_world);
		logger.log(Level.INFO, "nether_name: {0}", nether_name);
		logger.log(Level.INFO, "overworld_name: {0}", overworld_name);
		
		World nether = null;
		World overworld = null;
		
		if ( !(is_nether) )
		{
			for ( int h = 0; h < worlds.size(); h++ )
			{
				if ( worlds.get(h).getName().equalsIgnoreCase(nether_name) )
				{
					//nether vorhanden
					nether = worlds.get(h);
				}
			}
			if ( nether == null )
			{
				//kein nether vorhanden
			}
		}
		else
		{
			for ( int i = 0; i < worlds.size(); i++ )
			{
				if ( worlds.get(i).getName().equalsIgnoreCase(overworld_name) )
				{
					overworld = worlds.get(i);
				}
			}
			if ( overworld == null )
			{
				//fail
			}
		}
		
		Location portal_location = null;
		List<Block> portalBlocks = pce.getBlocks();
		for ( int i = 0; i < portalBlocks.size(); i++ )
		{
			logger.log(Level.INFO, "i = {0}", i);
			Block current_block = portalBlocks.get(i);
			logger.log(Level.INFO, current_block.getType().name());
			if ( current_block.getType().equals(Material.FIRE) )
			{
				//z-1?
				portal_location = current_block.getLocation();
				portal_location.setZ(portal_location.getZ() + 1);
				break;
			}
		}
		
		logger.log(Level.INFO, "portal_location: {0}", portal_location.toString());
		Location portal_location_on_other_world = portal_location;
		if ( is_nether )
		{
			portal_location_on_other_world.setWorld(overworld);
		}
		else
		{
			portal_location_on_other_world.setWorld(nether);
		}
		
		logger.log(Level.INFO, "portal_location_on_other_world: {0}", portal_location_on_other_world.toString());
		Location near_portal_location = cmd.get_NearestPortal(portal_location_on_other_world);
		
		if ( near_portal_location == null )
		{
			logger.log(Level.INFO, "kein portal in der nähe");
			Location new_portal_location = null;
			Integer is_safe = 0;
			if ( !(is_nether) )
			{
				Object[] result = cmd.getReverseLocation(portal_location_on_other_world);
				new_portal_location = (Location)result[0];
				is_safe = (Integer)result[1];
			}
			else
			{
				Object[] result = cmd.getLocation(portal_location_on_other_world);
				new_portal_location = (Location)result[0];
				is_safe = (Integer)result[1];
			}
			logger.log(Level.INFO, "new_portal_location: {0}" ,new_portal_location.toString());
			cmd.buildPortalFrame(new_portal_location, is_safe);
		}
		else
		{
			logger.log(Level.INFO, "near_portal_location: {0}", near_portal_location.toString());
			logger.log(Level.INFO, "Portal in der nähe");
		}
	}
}

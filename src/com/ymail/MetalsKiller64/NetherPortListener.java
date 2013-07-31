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
		
		boolean is_nether;
		
		String entrance_world = player_location.getWorld().getName();
		logger.log(Level.INFO, "entrance_world: {0}", entrance_world);
		
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
		/*
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
			 */
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
		/*
		player.sendMessage("PlayerPortalEvent");
		double x = player.getLocation().getBlock().getX();
		double y = player.getLocation().getBlock().getY();
		double z = player.getLocation().getBlock().getZ();
		World world = player.getWorld();
		
		Location player_location = new Location(world, x, y, z);
		if ( !(player_location.getBlock().getType().equals(Material.AIR)) )
		{
			player_location.getBlock().setType(Material.AIR);
			Location pl2 = player_location;
			pl2.setY(y+1);
			pl2.getBlock().setType(Material.AIR);
		}
		log.log(Level.INFO, player_location.toString());
		Portal p = cmd.getNearestPortal(player_location);
		Location destination_location = new Location(Bukkit.getWorld(p.getLinkTo()), p.getX(), p.getY(), p.getZ());
		String path = p.getID()+".linktoid";
		String link_portal_id = "";
		
		ConfigurationSection netherlinks = multinether.getConfig().getConfigurationSection("NetherLinks");
		Set<String> keys = netherlinks.getKeys(true);
		Object[] key_list = keys.toArray();
		boolean is_nether = false;
		for ( int i = 0; i < key_list.length; i++ )
		{
			String key = (String)key_list[i];
			String key_world = multinether.getConfig().getString("NetherLinks."+key);
			if ( world.getName().equals(key) )
			{
				is_nether = false;
			}
			else if ( world.getName().equals(key_world) )
			{
				is_nether = true;
			}
			//log.log(Level.INFO, key_world);
		}
		//log.log(Level.INFO, "conf world: "+conf_world);
		Portal dest_portal = null;
		String link_world = "";
		if ( !(is_nether) )
		{
			log.log(Level.INFO, "is not nether");
			link_portal_id = multinether.getPortalConfig().getString(path);
			log.log(Level.INFO, "link id: "+link_portal_id);
			dest_portal = cmd.getReversePortal(Integer.parseInt(link_portal_id));
		}
		else
		{
			log.log(Level.INFO, "is nether");
			link_portal_id = multinether.getReversePortalConfig().getString(path);
			log.log(Level.INFO, "link id: "+link_portal_id);
			dest_portal = cmd.getPortal(Integer.parseInt(link_portal_id));
		}
		link_world = dest_portal.getLinkTo();
		
		//multinether.getLogger().log(Level.INFO, path+": "+link_portal_id);
		log.log(Level.INFO, "dest_portal y: "+dest_portal.getY().toString());
		destination_location.setY(dest_portal.getY());
		
		//String linkworld = multinether.getConfig().getString("NetherLinks."+world.getName());
		player.sendMessage(link_world);
		log.log(Level.INFO, "teleport to coords: x={0} y={1} z={2} on world '"+link_world+"'", new Object[]{p.getX(), p.getY(), p.getZ()});
		player.teleport(destination_location);
		*/
	}
	
	@EventHandler
	public void onPortalCreate(PortalCreateEvent pce)
	{
		//TODO: prüfen ob schon ein link vorhanden ist, wenn nicht einen neuen anlegen
		//TODO: Speichern des Portals hierher verlegen (wird bisher durch einen Befehl im CmdExecutor erledigt)
		//TODO: wenn noch kein Nether vorhanden ist, einen neuen mit dem Namen und dem Seed der Oberwelt anlegen (wie in Multiverse, z.B.: Welt_nether)
		Portal portal = cmd.createPortal(pce.getBlocks());
		if ( portal != null )
		{
			logger.log(Level.INFO, "Portal successfully created");
			logger.log(Level.INFO, "id: {0}", portal.getID());
		}
		else
		{
			logger.log(Level.SEVERE, "Failed to create portal");
		}
	}
}

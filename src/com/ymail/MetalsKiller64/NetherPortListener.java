package com.ymail.MetalsKiller64;

import java.util.ArrayList;
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
		logger.log(Level.INFO, player.getLocation().getDirection().toString());
		Location player_location = player.getLocation();
		
		float player_direction = player_location.getYaw();
		String portal_orientation = "";
		
		if ( player_direction < 45 || player_direction >= 315 )
		{
			portal_orientation = "X";
		}
		else if ( player_direction >= 45 && player_direction < 135 )
		{
			portal_orientation = "Z";
		}
		else if ( player_direction >= 135 && player_direction < 225 )
		{
			portal_orientation = "X";
		}
		else if ( player_direction >= 225 && player_direction < 315 )
		{
			portal_orientation = "Z";
		}
		
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
		double scale_x = player_location_on_other_world.getX();
		double scale_z = player_location_on_other_world.getZ();
		if ( is_nether )
		{
			player_location_on_other_world.setWorld(overworld);
			scale_x = scale_x * 8;
			scale_z = scale_z * 8;
		}
		else
		{
			player_location_on_other_world.setWorld(nether);
			scale_x = scale_x / 8;
			scale_z = scale_z / 8;
		}
		player_location_on_other_world.setX(scale_x);
		player_location_on_other_world.setZ(scale_z);
		logger.log(Level.INFO, "scale_x, scale_z = {0}, {1}", new Object[]{scale_x, scale_z});
		logger.log(Level.INFO, "portal_location_on_other_world: {0}", player_location_on_other_world.toString());
		Location near_portal_location = cmd.get_NearestPortal(player_location_on_other_world);
		if ( near_portal_location == null )
		{
			logger.log(Level.INFO, "fail, kein portal gefunden");
		}
		else
		{
			safeTeleport(player, near_portal_location, portal_orientation);
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
		String portal_orientation = "";
		for ( int i = 0; i < portalBlocks.size(); i++ )
		{
			logger.log(Level.INFO, "i = {0}", i);
			Block current_block = portalBlocks.get(i);
			logger.log(Level.INFO, current_block.getType().name());
			if ( current_block.getType().equals(Material.FIRE) )
			{
				//z-1?
				portal_location = current_block.getLocation();
				//logger.log(Level.INFO, "portal_location bevor: {0}", portal_location);
				
				Location _x = current_block.getLocation();
				double x1 = current_block.getLocation().getX();
				x1 -= 1;
				_x.setX(x1);
				
				Location __x = current_block.getLocation();
				double x2 = current_block.getLocation().getX();
				x2 -= 2;
				__x.setX(x2);
				
				Location x_ = current_block.getLocation();
				double x3 = current_block.getLocation().getX();
				x3 += 1;
				x_.setX(x3);
				
				Location x__ = current_block.getLocation();
				double x4 = current_block.getLocation().getX();
				x4 += 2;
				x__.setX(x4);
				
				Location _z = current_block.getLocation();
				double z1 = current_block.getLocation().getZ();
				z1 -= 1;
				_z.setZ(z1);
				
				Location __z = current_block.getLocation();
				double z2 = current_block.getLocation().getZ();
				z2 -= 2;
				__z.setZ(z2);
				
				Location z_ = current_block.getLocation();
				double z3 = current_block.getLocation().getZ();
				z3 += 1;
				z_.setZ(z3);
				
				Location z__ = current_block.getLocation();
				double z4 = current_block.getLocation().getZ();
				z4 += 2;
				z__.setZ(z4);
				
				Block bx1 = _x.getBlock();
				Block bx2 = __x.getBlock();
				Block bx_1 = x_.getBlock();
				Block bx_2 = x__.getBlock();
				
				Block bz1 = _z.getBlock();
				Block bz2 = __z.getBlock();
				Block bz_1 = z_.getBlock();
				Block bz_2 = z__.getBlock();
				
				List<Block> x_blocks = new ArrayList<Block>();
				x_blocks.add(bx1);
				x_blocks.add(bx2);
				x_blocks.add(bx_1);
				x_blocks.add(bx_2);
				
				List<Block> z_blocks = new ArrayList<Block>();
				z_blocks.add(bz1);
				z_blocks.add(bz2);
				z_blocks.add(bz_1);
				z_blocks.add(bz_2);
				
				portal_location.setZ(portal_location.getZ() + 1);
				//logger.log(Level.INFO, "portal_location after: {0}", portal_location);
				
				//logger.log(Level.INFO, "x1: {0}, x2: {1}, x3: {2}, x4: {3}, z1: {4}, z2: {5}, z3: {6}, z4: {7}", new Object[]{x1, x2, x3, x4, z1, z2, z3, z4});
				
				//logger.log(Level.INFO, "x, z check: {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}", new Object[]{_x, __x, x_, x__, _z, __z, z_, z__});
				//logger.log(Level.INFO, "_x: {0}, __x: {1}, x_: {2}, x__: {3}, _z: {4}, __z: {5}, z_: {6}, z__: {7}", new Object[]{bx1.getType(), bx2.getType(), bx_1.getType(), bx_2.getType(), bz1.getType(), bz2.getType(), bz_1.getType(), bz_2.getType()});
				
				for ( int j = 0; j < x_blocks.size(); j++ )
				{
					Block current_x_block = x_blocks.get(j);
					if ( current_x_block.getType().equals(Material.OBSIDIAN) )
					{
						portal_orientation = "X";
						break;
					}
				}
				
				if ( portal_orientation.isEmpty() )
				{
					for ( int k = 0; k < z_blocks.size(); k++ )
					{
						Block current_z_block = z_blocks.get(k);
						if ( current_z_block.getType().equals(Material.OBSIDIAN) )
						{
							portal_orientation = "Z";
							break;
						}
					}
				}
				break;
			}
		}
		
		logger.log(Level.INFO, "portal_orientation: {0}", portal_orientation);
		
		logger.log(Level.INFO, "portal_location: {0}", portal_location.toString());
		Location portal_location_on_other_world = portal_location;
		double scale_x = portal_location_on_other_world.getX();
		double scale_z = portal_location_on_other_world.getZ();
		if ( is_nether )
		{
			portal_location_on_other_world.setWorld(overworld);
			scale_x = scale_x * 8;
			scale_z = scale_z * 8;
		}
		else
		{
			portal_location_on_other_world.setWorld(nether);
			scale_x = scale_x / 8;
			scale_z = scale_z / 8;
		}
		
		portal_location_on_other_world.setX(scale_x);
		portal_location_on_other_world.setZ(scale_z);
		logger.log(Level.INFO, "scale_x, scale_z = {0}, {1}", new Object[]{scale_x, scale_z});
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
			if ( portal_orientation.isEmpty() )
			{
				portal_orientation = "X";
			}
			cmd.buildPortalFrame(new_portal_location, is_safe, portal_orientation);
		}
		else
		{
			logger.log(Level.INFO, "near_portal_location: {0}", near_portal_location.toString());
			logger.log(Level.INFO, "Portal in der nähe");
		}
	}
	
	
	public void safeTeleport(Player player, Location target_location, String orientation)
	{
		Block target_block = target_location.getBlock();
		World target_world = target_location.getWorld();
		int y = target_block.getY();
		int x = target_block.getX();
		int z = target_block.getZ();
		
		Block safe_block = null;
		
		if ( orientation.equals("X") )
		{
			int begin_x = x-3;
			int begin_z = z-1;
			int begin_y = y-1;
			
			int end_x = x+2;
			int end_z = z+2;
			int end_y = y+3;
			
			for ( int h = begin_x; h < end_x; h++ )
			{
				for ( int j = begin_z; j < end_z; j++ )
				{
					for ( int i = begin_y; i < end_y; i++ )
					{
						Block current_block = new Location(target_location.getWorld(), h, i, j).getBlock();
						Block block_above = new Location(target_location.getWorld(), h, i+1, j).getBlock();
						Block block_below = new Location(target_location.getWorld(), h, i-1, j).getBlock();
						if ( (current_block.getType().equals(Material.AIR) && block_above.getType().equals(Material.AIR)) || (current_block.getType().equals(Material.PORTAL) && block_above.getType().equals(Material.PORTAL)) )
						{
							Block left = new Location(target_location.getWorld(), h-1, i, j).getBlock();
							Block right = new Location(target_location.getWorld(), h+1, i, j).getBlock();
							if ( !(left.getType().equals(Material.AIR)) && !(left.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h+0.5, i, j).getBlock();
							}
							else if ( !(right.getType().equals(Material.AIR)) && !(right.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h-0.5, i, j).getBlock();
							}
							else
							{
								safe_block = current_block;
							}
						}
						else if ( (current_block.getType().equals(Material.AIR) && block_below.getType().equals(Material.AIR)) || (current_block.getType().equals(Material.PORTAL) && block_below.getType().equals(Material.PORTAL)) )
						{
							Block left = new Location(target_location.getWorld(), h-1, i-1, j).getBlock();
							Block right = new Location(target_location.getWorld(), h+1, i-1, j).getBlock();
							if ( !(left.getType().equals(Material.AIR)) && !(left.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h+0.5, i-1, j).getBlock();
							}
							else if ( !(right.getType().equals(Material.AIR)) && !(right.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h-0.5, i-1, j).getBlock();
							}
							else
							{
								safe_block = block_below;
							}
						}
					}
				}
			}
		}
		else if ( orientation.equals("Z") )
		{
			int begin_x = x-1;
			int begin_z = z-3;
			int begin_y = y-1;
			
			int end_x = x+2;
			int end_z = z+2;
			int end_y = y+3;
			
			for ( int h = begin_x; h < end_x; h++ )
			{
				for ( int j = begin_z; j < end_z; j++ )
				{
					for ( int i = begin_y; i < end_y; i++ )
					{
						Block current_block = new Location(target_location.getWorld(), h, i, j).getBlock();
						Block block_above = new Location(target_location.getWorld(), h, i+1, j).getBlock();
						Block block_below = new Location(target_location.getWorld(), h, i-1, j).getBlock();
						if ( (current_block.getType().equals(Material.AIR) && block_above.getType().equals(Material.AIR)) || (current_block.getType().equals(Material.PORTAL) && block_above.getType().equals(Material.PORTAL)) )
						{
							Block left = new Location(target_location.getWorld(), h, i, j-1).getBlock();
							Block right = new Location(target_location.getWorld(), h, i, j+1).getBlock();
							if ( !(left.getType().equals(Material.AIR)) && !(left.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h, i, j+0.5).getBlock();
							}
							else if ( !(right.getType().equals(Material.AIR)) && !(right.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h, i, j-0.5).getBlock();
							}
							else
							{
								safe_block = current_block;
							}
						}
						else if ( (current_block.getType().equals(Material.AIR) && block_below.getType().equals(Material.AIR)) || (current_block.getType().equals(Material.PORTAL) && block_below.getType().equals(Material.PORTAL)) )
						{
							Block left = new Location(target_location.getWorld(), h, i-1, j-1).getBlock();
							Block right = new Location(target_location.getWorld(), h, i-1, j+1).getBlock();
							if ( !(left.getType().equals(Material.AIR)) && !(left.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h, i-1, j+0.5).getBlock();
							}
							else if ( !(right.getType().equals(Material.AIR)) && !(right.getType().equals(Material.PORTAL)) )
							{
								safe_block = new Location(target_location.getWorld(), h, i-1, j-0.5).getBlock();
							}
							else
							{
								safe_block = block_below;
							}
						}
					}
				}
			}
		}
		
		
		/*
		List<Location> port_blocks = new ArrayList<Location>();
		
		port_blocks.add(new Location(target_location.getWorld(), x, y-2, z));
		port_blocks.add(new Location(target_location.getWorld(), x, y-1, z));
		
		port_blocks.add(new Location(target_location.getWorld(), x-1, y-2, z));
		port_blocks.add(new Location(target_location.getWorld(), x-1, y-1, z));
		
		port_blocks.add(new Location(target_location.getWorld(), x+1, y-2, z));
		port_blocks.add(new Location(target_location.getWorld(), x+1, y-1, z));
		
		port_blocks.add(new Location(target_location.getWorld(), x, y-2, z-1));
		port_blocks.add(new Location(target_location.getWorld(), x, y-1, z-1));
		
		port_blocks.add(new Location(target_location.getWorld(), x, y-2, z+1));
		port_blocks.add(new Location(target_location.getWorld(), x, y-1, z+1));
		
		//port_blocks.add(new Location(target_location.getWorld(), x+1, y-2, z+1));
		port_blocks.add(new Location(target_location.getWorld(), x-1, y-1, z+1));
		
		
		for ( int i = 0; i < port_blocks.size(); i++ )
		{
			Location current_location = port_blocks.get(i);
			Block current_block = port_blocks.get(i).getBlock();
			int current_x = current_block.getX();
			int current_y = current_block.getY();
			int current_z = current_block.getZ();
			
			Block above = new Location(target_world, current_x, current_y+1, current_z).getBlock();
			Block below = new Location(target_world, current_x, current_y-1, current_z).getBlock();
			
			if ( current_block.getType().equals(Material.AIR) || current_block.getType().equals(Material.PORTAL) )
			{
				if ( above.getType().equals(Material.AIR) || above.getType().equals(Material.PORTAL) )
				{
					safe_block = current_block;
					break;
				}
				else if ( below.getType().equals(Material.AIR) || below.getType().equals(Material.PORTAL) )
				{
					safe_block = below;
					break;
				}
			}
		}
		*/
		
		if ( safe_block != null )
		{
			Location safe_location = safe_block.getLocation();
			logger.log(Level.INFO, "target_location: {0}", target_location.toString());
			logger.log(Level.INFO, "safe_location: {0}", safe_location.toString());
			//safe_location.setZ(safe_location.getZ()+0.5);
			//safe_location.setX(safe_location.getX()-0.5);
			player.teleport(safe_location);
		}
		else
		{
			player.sendMessage("no suitable location found");
			player.teleport(target_location);
		}
	}
}

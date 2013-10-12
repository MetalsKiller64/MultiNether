package com.ymail.MetalsKiller64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import java.util.HashSet;
import org.bukkit.util.Vector;

//import java.util.Arrays;
//import java.util.logging.Logger;
//import java.util.logging.Level;

//import org.bukkit.Chunk;
//import org.bukkit.generator.ChunkGenerator;
//import org.bukkit.plugin.Plugin;

public class CmdExecutor implements CommandExecutor
{
	private MultiNether multinether;
	private List<String> command_list;
	private List<String> link_list;
	private List<String> config_path_list;
	private String netherlink_path;
	private String portalcount_path;
	private Logger logger;
	
	public CmdExecutor(MultiNether plugin)
	{
		this.multinether = plugin;
		this.command_list = multinether.command_list;
		this.link_list = new ArrayList<String>();
		this.config_path_list = new ArrayList<String>();
		this.netherlink_path = "NetherLinks.";
		this.portalcount_path = "PortalCount";
		this.logger = multinether.getLogger();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if ( cmd.getName().equalsIgnoreCase("rtp") )
		{
			if ( sender instanceof Player )
			{
				Player player = (Player) sender;
				Location current_location = player.getLocation();
				if ( args.length == 0 )
				{
					sender.sendMessage(cmd.getDescription());
				}
				else if ( args.length == 1 )
				{
					if ( !(isNumber(args[0])) )
					{
						if ( isWorld(args[0]) )
						{
							player.teleport(Bukkit.getWorld(args[0]).getSpawnLocation());
						}
						else if ( isPlayerOnline(args[0])  )
						{
							player.teleport(Bukkit.getPlayer(args[0]));
							return true;
						}
						else if ( args[0].startsWith("Z"))
						{
							String z = args[0];
							z = z.replace("Z", "");
							Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), new Double(Double.parseDouble(z)));
							player.teleport(loc);
							return true;
						}
						else if ( args[0].startsWith("Y") )
						{
							String y = args[0];
							y = y.replace("Y", "");
							Location loc = new Location(player.getWorld(), player.getLocation().getX(), new Double(Double.parseDouble(y)), player.getLocation().getZ());
							player.teleport(loc);
							return true;
						}
					}
					else if ( isNumber(args[0]) )
					{
						Location loc = new Location(player.getWorld(), new Double(Double.parseDouble(args[0])), player.getLocation().getY(), player.getLocation().getZ());
						player.teleport(loc);
						return true;
					}
				}
				else if ( args.length > 1 && args.length <= 4 )
				{
					if ( isNumber(args[0]) && isNumber(args[1]) && isNumber(args[2]) )
					{
						if ( args.length == 4 )
						{
							if ( isWorld(args[3]) )
							{
							Location loc = new Location(Bukkit.getWorld(args[3]), new Double(Double.parseDouble(args[0])), new Double(Double.parseDouble(args[1])), new Double(Double.parseDouble(args[2])));
							player.teleport(loc);
							return true;
							}
						}
						else if ( args.length == 3 )
						{
							Location loc = new Location(player.getWorld(), new Double(Double.parseDouble(args[0])), new Double(Double.parseDouble(args[1])), new Double(Double.parseDouble(args[2])));
							player.teleport(loc);
							return true;
						}
						else if ( args.length == 2 )
						{
							//TODO: zusätzliche möglichkeiten für teleportation mit 2 argumenten: X&Z und Y&Z
							Location loc = new Location(player.getWorld(), new Double(Double.parseDouble(args[0])), new Double(Double.parseDouble(args[1])), player.getLocation().getZ());
							player.teleport(loc);
							
							return true;
						}
					}
				}
				//Location new_location = new Location(Bukkit.getWorld("SpawnWorld"), -1085, 5, -294);
				//World w = player.getWorld();
				//new_location.setWorld(w);
				//new_location.setX(-1085.0);
				//new_location.setY(5.0);
				//new_location.setZ(-294.0);
			}
			else
			{
				sender.sendMessage("rtp kann nur von Spielern ausgeführt werden!");
				return false;
			}
		}
		else if ( cmd.getName().equalsIgnoreCase("mn-link") )
		{
			sender.sendMessage("link_list: "+getLinks());
			if ( args.length > 1 )
			{
				if ( args[0].equalsIgnoreCase("add") )
				{
					if ( args.length == 3 )
					{
						boolean added = addLink(sender, args[1], args[2]);
						if ( added )
						{
							return true;
						}
						else if ( !(added) )
						{
							return false;
						}
						//addLink(sender, args[1], args[2]);
					}
					else if ( args.length == 2 )
					{
						boolean added = addLink(sender, args[1]);
						if ( added )
						{
							return true;
						}
						else if ( !(added) )
						{
							return false;
						}
					}
				}
				else if ( args[0].equalsIgnoreCase("remove") )
				{
					if ( args.length == 2 )
					{
						boolean removed = removeLink(sender, args[1]);
						if ( removed )
						{
							return true;
						}
						else if ( !(removed) )
						{
							return false;
						}
					}
					else if ( args.length < 2 )
					{
						sender.sendMessage("zu wenig Argumente!");
						return false;
					}
					else if ( args.length > 2 )
					{
						sender.sendMessage("zu viele Argumente!");
						return false;
					}
				}
			}
			else if ( args.length == 1 && args[0].equalsIgnoreCase("show") )
			{
				//String links = netherrep.getConfig().getString("LinkList");
				//links = links.replaceAll(":", " <> ");
				
				if ( getLinks().isEmpty() )
				{
					sender.sendMessage("keine Links gesetzt.");
					return true;
				}
				for ( int i = 0; i < getLinks().size(); i++ )
				{
					sender.sendMessage(getLinks().get(i));
				}
				return true;
			}
			else if (args.length > 4)
			{
				sender.sendMessage("zu viele Argumente!");
				return false;
			}
		}
		else if ( cmd.getName().equalsIgnoreCase("mn") )
		{
			if ( args.length > 0 )
			{
				/*
				if ( args[0].equals("create") )
				{
					try
					{
						Portal portal = createPortal(sender);
						if ( !(portal == null) && !(portal.getID() == null) )
						{
							return true;
						}
					}
					catch( ClassCastException e )
					{
						return false;
					}
				}
				else if ( args[0].equals("delete") )
				{
					if ( sender instanceof Player )
					{
						Player p = (Player) sender;
						Location l = p.getLocation();
						Integer portal_id = getPortalId(p.getLocation());
						if ( !(portal_id == null) )
						{
							boolean removed = removePortal(portal_id);
							if ( removed )
							{
								sender.sendMessage("Portal gelöscht.");
								return true;
							}
						}
					}
					else
					{
						sender.sendMessage("This command is only for players!");
					}
				}
				else if ( args[0].equals("open") )
				{
					sender.sendMessage("open portal");
					Player player = (Player)sender;
					Location player_location = player.getLocation();
					Portal portal = getNearestPortal(player_location);
					if ( portal != null )
					{
						sender.sendMessage("found portal: "+portal.getID());
						boolean opened = openPortal(portal);
						if ( opened )
						{
							return true;
						}
						else
						{
							player.sendMessage("Could not open portal, it seems to be broken.");
							return false;
						}
					}
					else
					{
						sender.sendMessage("no portal found");
						return true;
					}
				}
				else if ( args[0].equals("close") )
				{
					sender.sendMessage("not yet implemented!");
					return true;
				}
				*/
			}
		}
		else if ( cmd.getName().equals("cmdtest") )
		{
			if ( sender instanceof Player )
			{
				Player p = (Player)sender;
				Portal portal = getNearestPortal(p.getLocation());
				if ( portal == null )
				{
					sender.sendMessage("kein portal in der nähe");
				}
				else
				{
					sender.sendMessage("Portal gefunden, id: "+portal.getID());
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean isNumber(String value)
	{
		try
		{
			int int_test = Integer.parseInt(value);
		}
		catch(NumberFormatException e)
		{
			try
			{
				double doulbe_test = Double.parseDouble(value);
			}
			catch(NumberFormatException ex)
			{
				return false;
			}
			return true;
		}
		return true;
	}
	
	public boolean isWorld(String value)
	{
		for (int i = 0; i < Bukkit.getWorlds().size(); i++)
		{
			if ( value.equalsIgnoreCase(Bukkit.getWorlds().get(i).getName()) )
			{
			return true;
			}
		}
		return false;
	}
	
	public boolean isPlayerOnline(String value)
	{
		Player[] player_list = Bukkit.getOnlinePlayers();
		for ( int i = 0; i < player_list.length; i++ )
		{
			if ( player_list[i].getName().equals(value) )
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean addLink(CommandSender sender, String World1, String World2)
	{
		if ( isWorld(World1) && isWorld(World2) )
		{
			if ( linkExists(World1) )
			{
				sender.sendMessage("Es existiert bereits ein Link für "+World1+"!");
				return false;
			}
			//multinether.getConfig().addDefault(World1, World2);
			multinether.getConfig().set(netherlink_path+World1, World2);
			multinether.saveConfig();
			//String link = World1+":"+World2;
			//this.link_list.add(link);
			setLinkList();
			String links = "";
			int t = getLinks().size();
			for ( int i = 0; i < getLinks().size(); i++ )
			{
				links = links+", "+getLinks().get(i);
			}
			sender.sendMessage("LinkList: "+t);
			sender.sendMessage("Link gespeichert.");
			return true;
		}
		else if ( !(isWorld(World1)) )
		{
			sender.sendMessage("die angegebene Welt '"+World1+"' existiert nicht!");
		}
		else if ( !(isWorld(World2)) )
		{
			sender.sendMessage("die angegebene Welt '"+World2+"' existiert nicht!");
		}
		else
		{
			sender.sendMessage("falscher Parameter!");
		}
		return false;
	}
	
	public boolean addLink(CommandSender sender, String World2)
	{
		if ( sender instanceof Player && isWorld(World2) )
		{
			Player player = (Player) sender;
			if ( linkExists(player.getWorld().getName()) )
			{
				sender.sendMessage("Es existiert bereits ein Link für diese Welt!");
				return false;
			}
			//multinether.getConfig().addDefault(player.getWorld().getName(), World2);
			multinether.getConfig().set(netherlink_path+player.getWorld().getName(), World2);
			multinether.saveConfig();
			setLinkList();
			sender.sendMessage("Link gespeichert.");
			return true;
		}
		else if ( !(sender instanceof Player) )
		{
			sender.sendMessage("zu wenig Argumente");
		}
		else if ( !(isWorld(World2)) )
		{
			sender.sendMessage("die angegebene Welt '"+World2+"' existiert nicht!");
		}
		return false;
	}
	
	public boolean removeLink(CommandSender sender, String Link)
	{
		/*
		 * if ( !(linkExists(Link)) )
		 * {
		 * sender.sendMessage("Es existiert kein Link von "+Link);
		 * return false;
		 * }
		 */
		if ( isWorld(Link) )
		{
			multinether.getConfig().set(netherlink_path+Link, null);
			multinether.saveConfig();
			setLinkList();
			sender.sendMessage("Link entfernt.");
			return true;
		}
		else if ( !(isWorld(Link)) )
		{
			for ( int i = 0; i < getLinks().size(); i++ )
			{
				if ( getLinks().get(i).equalsIgnoreCase(Link) )
				{
					//String path = this.link_list.get(i).split(":")[0];
					String path = this.config_path_list.get(i);
					multinether.getConfig().set(path, null);
					multinether.saveConfig();
					setLinkList();
					sender.sendMessage("Link entfernt.");
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean linkExists(String Link)
	{
		
		multinether.reloadConfig();
		try
		{
			Object val = multinether.getConfig().get(netherlink_path+Link);
			if ( val.toString().isEmpty() )
			{
				return true;
			}
		}
		catch ( NullPointerException npe )
		{
			
		}
		
		/*
		for ( int i = 0; i < this.config_path_list.size(); i++ )
		{
			if ( config_path_list.get(i).equalsIgnoreCase(Link) )
			{
				return true;
			}
		}
		*/
		return false;
	}
	private void setLinkList()
	{
		//setWorldList();
		multinether.reloadConfig();
		link_list = new ArrayList<String>();
		
		/*
		Object conf_val = getLinks();
		
		if ( conf_val instanceof MemorySection )
		{
			MemorySection m = (MemorySection) conf_val;
			for ( int i = 0; i < world_list.size(); i++ )
			{
				try
				{
					Object link = m.get(netherlink_path+world_list.get(i).getName());
					link_list.add(link.toString());
				}
				catch ( NullPointerException npe )
				{
					continue;
				}
			}
		}
		*/
		this.link_list = new ArrayList<String>();
		for ( int i = 0; i < Bukkit.getWorlds().size(); i++ )
		{
			try
			{
				String link = multinether.getConfig().getString(Bukkit.getWorlds().get(i).getName());
				if ( !(link == null) )
				{
					link = Bukkit.getWorlds().get(i).getName()+":"+link;
					this.link_list.add(link);
					this.config_path_list.add(Bukkit.getWorlds().get(i).getName());
				}
			}
			catch(NullPointerException e)
			{
				continue;
				//netherrep.getLogger().log(Level.SEVERE, e.getMessage());
				//e.printStackTrace();
			}
		}
	}
	
	public List<String> getLinks()
	{
		List<String> links = new ArrayList<String>();
		//setWorldList();
		multinether.reloadConfig();
		for ( int i = 0; i < Bukkit.getWorlds().size(); i++ )
		{
			try
			{
				String link = multinether.getConfig().get(netherlink_path+Bukkit.getWorlds().get(i).getName()).toString();
				links.add(Bukkit.getWorlds().get(i).getName());
			}
			catch ( NullPointerException npe )
			{
				
			}
		}
		return links;
	}
	
	public String getLinkWorld(String current_world)
	{
		//setWorldList();
		String world = "";
		try
		{
			world = multinether.getConfig().get(netherlink_path+current_world).toString();
		}
		catch ( NullPointerException npe )
		{
			
		}
		
		/*
		for ( int i = 0; i < this.link_list.size(); i++ )
		{
			if ( this.link_list.get(i).contains(current_world) )
			{
				world = link_list.get(i).split(":")[1];
				return world;
			}
		}
		*/
		return world;
	}
	
	/*
	public Portal createPortal(CommandSender sender)
	{
		MultiNether mn = multinether;
		Portal p = null;
		if ( sender instanceof Player )
		{
			Player player = (Player) sender;
			Location l = player.getLocation();
			Location location = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
			//sender.sendMessage(""+getPortalIDs().size());
			Integer id = 0;
			List<Integer> portal_ids = getAllPortalIDs();
			if ( !(portal_ids.isEmpty()) )
			{
				//List<Integer> ids = getAllPortalIDs();
				for ( int i = 0; i < portal_ids.size(); i++ )
				{
					sender.sendMessage(portal_ids.get(i).toString());
					if ( !(portal_ids.contains(i)) )
					{
						//sender.sendMessage(""+i);
						Portal check_p = getPortal(i);
						if ( check_p == null )
						{
							sender.sendMessage("take this id: "+id);
							id = i;
						}
						//sender.sendMessage("id = "+i);
					}
				}
				if ( id == 0 )
				{
					sender.sendMessage("take getPortalIDs().size()");
					id = getAllPortalIDs().size();
				}
			}
			sender.sendMessage("id = "+id);
			
			String linkworld = getLinkWorld(location.getWorld().getName());
			if ( !(linkworld.isEmpty()) )
			{
				Double x = location.getX();
				Double y = location.getY();
				Double z = location.getZ();
				Integer p_x = x.intValue();
				Integer p_y = y.intValue();
				Integer p_z = z.intValue();
				
				p = new Portal();
				p.setLocation(location);
				p.setLinkTo(linkworld);
				p.setWorld(location.getWorld().getName());
				p.setID(id);
				mn.getLogger().log(Level.INFO, "x = "+p_x);
				mn.getLogger().log(Level.INFO, "y = "+p_y);
				mn.getLogger().log(Level.INFO, "z = "+p_z);
				p.setX(p_x);
				p.setY(p_y);
				p.setZ(p_z);
				
				boolean saved = savePortal(p);
				if ( saved )
				{
					Location reverse_location = new Location(Bukkit.getWorld(p.getLinkTo()), location.getX(), location.getY(), location.getZ());
					Object[] reverse_result = getReverseLocation(reverse_location);
					Location reverse_portal_location = (Location)reverse_result[0];
					int is_safe = (Integer)reverse_result[1];
					Portal reverse_portal = createReversePortal(reverse_portal_location, p.getWorld(), p.getID(), is_safe);
					sender.sendMessage("Portal erstellt und gespeichert.");
				}
			}
			else
			{
				sender.sendMessage("Kein Link vorhanden.");
			}
		}
		else if ( !(sender instanceof Player) )
		{
			throw new java.lang.ClassCastException("Sender is not a Player");
		}
		return p;
	}
	*/
	
	public Portal createPortal(List<Block> portalBlocks)
	{
		Portal p = null;
		Integer id = 0;
		List<Integer> portal_ids = getAllPortalIDs();
		if ( !(portal_ids.isEmpty()) )
		{
			for ( int i = 0; i < portal_ids.size(); i++ )
			{
				if ( !(portal_ids.contains(i)) )
				{
					Portal check_p = getPortal(i);
					if ( check_p == null )
					{
						id = i;
					}
				}
			}
			if ( id == 0 )
			{
				id = portal_ids.size();
			}
		}
		
		String linkworld = getLinkWorld(portalBlocks.get(0).getWorld().getName());
		
		Location portal_location = null;
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
		
		if ( !(linkworld.isEmpty()) )
		{
			Double x = portal_location.getX();
			Double y = portal_location.getY();
			Double z = portal_location.getZ();
			Integer p_x = x.intValue();
			Integer p_y = y.intValue();
			Integer p_z = z.intValue();

			p = new Portal();
			p.setLocation(portal_location);
			p.setLinkTo(linkworld);
			p.setWorld(portal_location.getWorld().getName());
			p.setID(id);
			p.setX(p_x);
			p.setY(p_y);
			p.setZ(p_z);

			boolean saved = savePortal(p);
			logger.log(Level.INFO, "saved ({0})", saved);
			if ( saved )
			{
				Location reverse_location = new Location(Bukkit.getWorld(p.getLinkTo()), portal_location.getX(), portal_location.getY(), portal_location.getZ());
				Object[] reverse_result = getReverseLocation(reverse_location);
				Location reverse_portal_location = (Location)reverse_result[0];
				int is_safe = (Integer)reverse_result[1];
				Portal reverse_portal = createReversePortal(reverse_portal_location, p.getWorld(), p.getID(), is_safe);
			}
			else
			{
				p = null;
			}
		}
		else
		{
			logger.log(Level.INFO, "linkworld is empty");
		}

		return p;
	}

	public Portal createReversePortal(Location loc, String world, Integer link_portal_id, Integer is_safe)
	{
		Portal p = new Portal();
		Location location = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		Integer id = 0;
		if ( !(getReversePortalIDs().isEmpty()) )
		{
			List<Integer> ids = getReversePortalIDs();
			for ( int i = 0; i < ids.size(); i++ )
			{
				if ( !(ids.contains(i)) )
				{
					Portal check_p = getPortal(i);
					if ( check_p == null )
					{
						id = i;
					}
				}
			}
			if ( id == 0 )
			{
				id = ids.size();
			}
		}
		Double x = location.getX();
		Double y = location.getY();
		Double z = location.getZ();
		Integer p_x = x.intValue();
		Integer p_y = y.intValue();
		Integer p_z = z.intValue();

		p.setLocation(location);
		p.setLinkTo(world);
		p.setID(id);
		p.setX(p_x);
		p.setY(p_y);
		p.setZ(p_z);

		boolean saved = saveReversePortal(p, link_portal_id);
		if ( saved )
		{
			buildPortalFrame(location, is_safe, "");
		}
		
		return p;
	}
	
	public void buildPortalFrame(Location loc, Integer is_safe, String orientation)
	{
		//TODO: portal ausrichtung
		logger.log(Level.INFO, "is_safe: {0}", is_safe);
		logger.log(Level.INFO, "baue portal-rahmen bei {0}...", loc.toString());
		Block loc_block = loc.getBlock();
		int y = loc_block.getY();
		int x = loc_block.getX();
		int z = loc_block.getZ();
		if ( !(loc_block.getChunk().isLoaded()) )
		{
			loc_block.getChunk().load();
		}
		
		List<Location> portal_blocks = new ArrayList<Location>();
		portal_blocks.add(loc);
		
		if ( orientation.equals("X") )
		{
			portal_blocks.add(new Location(loc.getWorld(), (x-1), y, z));
			portal_blocks.add(new Location(loc.getWorld(), (x-1), (y+1), z));
			portal_blocks.add(new Location(loc.getWorld(), x, (y+1), z));
			portal_blocks.add(new Location(loc.getWorld(), x, (y+2), z));
			portal_blocks.add(new Location(loc.getWorld(), (x-1), (y+2), z));
		}
		else if ( orientation.equals("Z") )
		{
			portal_blocks.add(new Location(loc.getWorld(), x, y, (z-1)));
			portal_blocks.add(new Location(loc.getWorld(), x, (y+1), (z-1)));
			portal_blocks.add(new Location(loc.getWorld(), x, (y+1), z));
			portal_blocks.add(new Location(loc.getWorld(), x, (y+2), z));
			portal_blocks.add(new Location(loc.getWorld(), x, (y+2), (z-1)));
		}

		for ( int e = 0; e < portal_blocks.size(); e++ )
		{
			portal_blocks.get(e).getBlock().setType(Material.GLOWSTONE);
		}
		
		for ( int f = 0; f < portal_blocks.size(); f++ )
		{
			portal_blocks.get(f).getBlock().setType(Material.AIR);
		}
		
		List<Location> frame_blocks = new ArrayList<Location>();
		
		if ( orientation.equals("X") )
		{
			frame_blocks.add(new Location(loc.getWorld(), x, (y-1), z));
			frame_blocks.add(new Location(loc.getWorld(), (x-1), (y-1), z));
			frame_blocks.add(new Location(loc.getWorld(), (x-2), (y-1), z));
			frame_blocks.add(new Location(loc.getWorld(), (x+1), (y-1), z));

			frame_blocks.add(new Location(loc.getWorld(), (x-2), y, z));
			frame_blocks.add(new Location(loc.getWorld(), (x+1), y, z));

			frame_blocks.add(new Location(loc.getWorld(), (x-2), (y+1), z));
			frame_blocks.add(new Location(loc.getWorld(), (x+1), (y+1), z));

			frame_blocks.add(new Location(loc.getWorld(), (x-2), (y+2), z));
			frame_blocks.add(new Location(loc.getWorld(), (x+1), (y+2), z));

			frame_blocks.add(new Location(loc.getWorld(), (x-2), (y+3), z));
			frame_blocks.add(new Location(loc.getWorld(), (x-1), (y+3), z));
			frame_blocks.add(new Location(loc.getWorld(), x, (y+3), z));
			frame_blocks.add(new Location(loc.getWorld(), (x+1), (y+3), z));
		}
		else if ( orientation.equals("Z") )
		{
			//FIXME: portalrahmen ist kaputt
			frame_blocks.add(new Location(loc.getWorld(), x, (y-1), z));
			frame_blocks.add(new Location(loc.getWorld(), x, (y-1), (z-1)));
			frame_blocks.add(new Location(loc.getWorld(), x, (y-1), (z-2)));
			frame_blocks.add(new Location(loc.getWorld(), x, (y-1), (z+1)));

			frame_blocks.add(new Location(loc.getWorld(), x, y, (z-2)));
			frame_blocks.add(new Location(loc.getWorld(), x, y, (z+1)));

			frame_blocks.add(new Location(loc.getWorld(), x, (y+1), (z-2)));
			frame_blocks.add(new Location(loc.getWorld(), x, (y+1), (z+1)));

			frame_blocks.add(new Location(loc.getWorld(), x, (y+2), (z-2)));
			frame_blocks.add(new Location(loc.getWorld(), x, (y+2), (z+1)));

			frame_blocks.add(new Location(loc.getWorld(), x, (y+3), (z-2)));
			frame_blocks.add(new Location(loc.getWorld(), x, (y+3), (z-1)));
			frame_blocks.add(new Location(loc.getWorld(), x, (y+3), z));
			frame_blocks.add(new Location(loc.getWorld(), x, (y+3), (z+1)));
		}
			
		for ( int g = 0; g < frame_blocks.size(); g++ )
		{
			frame_blocks.get(g).getBlock().setType(Material.GLOWSTONE);
		}
		
		for ( int h = 0; h < frame_blocks.size(); h++ )
		{
			frame_blocks.get(h).getBlock().setType(Material.OBSIDIAN);
		}
		
		//TODO: verhindern, dass lava zum portal fließt
		List<Material> m_list = new ArrayList<Material>();
		m_list.add(Material.NETHERRACK);
		m_list.add(Material.SOUL_SAND);
		m_list.add(Material.LAVA);
		m_list.add(Material.STONE);
		m_list.add(Material.COBBLESTONE);
		m_list.add(Material.SAND);
		m_list.add(Material.DIRT);
		m_list.add(Material.GRASS);
		m_list.add(Material.GRAVEL);
		m_list.add(Material.COAL_ORE);
		m_list.add(Material.IRON_ORE);
		m_list.add(Material.CLAY);
		m_list.add(Material.WOOD);
		m_list.add(Material.ICE);
		m_list.add(Material.DIAMOND_ORE);
		m_list.add(Material.GOLD_ORE);
		m_list.add(Material.EMERALD_ORE);
		m_list.add(Material.NETHER_BRICK);
		
		List<Block> surrounding_blocks = new ArrayList<Block>();
		if ( orientation.equals("X") )
		{
			int begin_x = x-3;
			int begin_z = z-1;
			int begin_y = y-1;
			
			int end_x = x+3;
			int end_z = z+2;
			int end_y = y+6;
			
			for ( int n = begin_x; n < end_x; n++ )
			{
				for ( int o = begin_y; o < end_y; o++ )
				{
					for ( int p = begin_z; p < end_z; p++ )
					{
						Block current_block = new Location(loc.getWorld(), n, o, p).getBlock();
						if ( m_list.contains(current_block.getType()) )
						{
							surrounding_blocks.add(current_block);
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
			int end_z = z+3;
			int end_y = y+6;
			
			for ( int n = begin_x; n < end_x; n++ )
			{
				for ( int o = begin_y; o < end_y; o++ )
				{
					for ( int p = begin_z; p < end_z; p++ )
					{
						Block current_block = new Location(loc.getWorld(), n, o, p).getBlock();
						if ( m_list.contains(current_block.getType()) )
						{
							surrounding_blocks.add(current_block);
						}
						/*
						else if ( !(current_block.getType().equals(Material.AIR)) )
						{
							surrounding_blocks.add(current_block);
						}
						*/
					}
				}
			}
		}

		for ( int i = 0; i < surrounding_blocks.size(); i++ )
		{
			surrounding_blocks.get(i).setType(Material.GLOWSTONE);
		}

		for ( int j = 0; j < surrounding_blocks.size(); j++ )
		{
			surrounding_blocks.get(j).setType(Material.AIR);
		}
		
		if ( is_safe == 1 )
		{
			//TODO: "dach" bauen damit lava an den seiten herunterfließen kann
		}
		else if ( is_safe == -1 )
		{
			List<Block> platform_blocks = new ArrayList<Block>();
			platform_blocks.add(new Location(loc.getWorld(), x, (y-1), (z+1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-1), (y-1), (z+1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-2), (y-1), (z+1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x+1), (y-1), (z+1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), x, (y-1), (z+2)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-1), (y-1), (z+2)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-2), (y-1), (z+2)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x+1), (y-1), (z+2)).getBlock());
			
			platform_blocks.add(new Location(loc.getWorld(), x, (y-1), (z-1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-1), (y-1), (z-1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-2), (y-1), (z-1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x+1), (y-1), (z-1)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), x, (y-1), (z-2)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-1), (y-1), (z-2)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x-2), (y-1), (z-2)).getBlock());
			platform_blocks.add(new Location(loc.getWorld(), (x+1), (y-1), (z-2)).getBlock());
			
			for ( int k = 0; k < platform_blocks.size(); k++ )
			{
				platform_blocks.get(k).setType(Material.GLOWSTONE);
			}
			for ( int k = 0; k < platform_blocks.size(); k++ )
			{
				platform_blocks.get(k).setType(Material.OBSIDIAN);
			}
		}
		
		for ( int l = 0; l < portal_blocks.size(); l++ )
		{
			portal_blocks.get(l).getBlock().setType(Material.GLOWSTONE);
		}
		for ( int m = 0; m < portal_blocks.size(); m++ )
		{
			portal_blocks.get(m).getBlock().setType(Material.PORTAL);
		}
	}
	
	public boolean savePortal(Portal p)
	{
		if ( !( getAllPortalIDs().contains(p.getID()) ) )
		{
			//Map<String, Object> portal = new HashMap<String, Object>();
			//portal.put(""+p.getID(), p.getLocation()+", "+p.getLinkTo());
			
			Integer id = p.getID();
			Location l = p.getLocation();
			String world = l.getWorld().getName();
			
			/*Double x = l.getX();
			Double y = l.getY();
			Double z = l.getZ();
			*/
			Integer x = p.getX();
			Integer y = p.getY();
			Integer z = p.getZ();
			String link = p.getLinkTo();
			
			//netherrep.getPortalConfig().addDefault(""+id, "");
			ConfigurationSection new_portal_section = multinether.getPortalConfig().createSection(""+id);
			ConfigurationSection portal_location = new_portal_section.createSection("location");
			portal_location.set("world", world);
			portal_location.set("x", x);
			portal_location.set("y", y);
			portal_location.set("z", z);
			//new_plugin_section.addDefault("location",l);
			//new_plugin_section.addDefault("linkto", link);
			new_portal_section.set("linkto", link);
			//this.portal_id_list.add(p.getID());
			//this.portal_list.add(p);
			//netherrep.getPortalConfig().addDefaults(portal);
			multinether.savePortalConfig();
			int portalcount = 0;
			try
			{
				portalcount = (Integer) multinether.getConfig().get(portalcount_path);
			}
			catch ( NullPointerException npe )
			{
				
			}
			portalcount++;
			multinether.getConfig().set(portalcount_path, portalcount);
			return true;
		}
		return false;
	}
	
	public boolean saveReversePortal(Portal p, Integer link_id)
	{
		if ( !( getReversePortalIDs().contains(p.getID()) ) )
		{
			//Map<String, Object> portal = new HashMap<String, Object>();
			//portal.put(""+p.getID(), p.getLocation()+", "+p.getLinkTo());
			
			Integer id = p.getID();
			Location l = p.getLocation();
			String world = l.getWorld().getName();
			
			/*Double x = l.getX();
			Double y = l.getY();
			Double z = l.getZ();
			*/
			Integer x = p.getX();
			Integer y = p.getY();
			Integer z = p.getZ();
			String link = p.getLinkTo();
			
			//netherrep.getPortalConfig().addDefault(""+id, "");
			ConfigurationSection new_portal_section = multinether.getReversePortalConfig().createSection(""+id);
			ConfigurationSection portal_location = new_portal_section.createSection("location");
			portal_location.set("world", world);
			portal_location.set("x", x);
			portal_location.set("y", y);
			portal_location.set("z", z);
			
			new_portal_section.set("linkto", link);
			new_portal_section.set("linktoid", link_id);
			
			multinether.saveReversePortalConfig();
			
			multinether.getPortalConfig().set(Integer.toString(link_id)+".linktoid", id);
			multinether.savePortalConfig();
			/*
			int portalcount = 0;
			try
			{
				portalcount = (Integer) multinether.getConfig().get(portalcount_path);
			}
			catch ( NullPointerException npe )
			{
				
			}
			portalcount++;
			multinether.getConfig().set(portalcount_path, portalcount);
			*/
			return true;
		}
		return false;
	}
	
	public boolean removePortal(Integer id)
	{
		if ( getAllPortalIDs().contains(id) )
		{
			multinether.getPortalConfig().set(""+id, null);
			multinether.savePortalConfig();
			//this.portal_id_list.remove(id);
			Portal p = getPortal(id);
			//this.portal_list.remove(p);
			return true;
		}
		return false;
	}
	
	public Portal getPortal(Integer id)
	{
		Portal p = null;
		List<Portal> allPortals = getAllPortals();
		if ( !(allPortals.isEmpty()) )
		{
			for ( int i = 0; i < allPortals.size(); i++ )
			{
				if ( allPortals.get(i).getID().equals(id) )
				{
					p = allPortals.get(i);
				}
			}
		}
		return p;
	}
	
	public Integer getPortalId(Location l)
	{
		Integer portal_id = null;
		for ( int i = 0; i < getAllPortals().size(); i++ )
		{
			Portal current_portal = getAllPortals().get(i);
			if ( current_portal.getLocation().getX() == l.getX() && current_portal.getLocation().getY() == l.getY() && current_portal.getLocation().getZ() == l.getZ() )
			{
				portal_id = current_portal.getID();
			}
		}
		return portal_id;
	}
	
	public Portal getNearActivePortal(Location l)
	{
		Portal near_active_portal = null;
		List<Portal> allPortals = getAllPortals();
		List<Location> portal_locations = new ArrayList<Location>();
		List<Block> portal_blocks_in_range = new ArrayList<Block>();
		
		Block pb = l.getBlock();
		
		int player_x = pb.getX();
		int player_y = pb.getY();
		int player_z = pb.getZ();
		
		int min_x = player_x - 10;
		int max_x = player_x + 10;
		
		int min_y;
		int max_y;
		if ( player_y < 10 )
		{
			min_y = 0;
			max_y = player_y + 10;
		}
		else if ( player_y > 245 )
		{
			min_y = player_y - 10;
			max_y = 255;
		}
		else
		{
			min_y = player_y - 10;
			max_y = player_y + 10;
		}
		
		int min_z = player_z - 10;
		int max_z = player_z + 10;
		
		for ( int x = min_x; x < max_x; x++ )
		{
			for ( int y = min_y; y < max_y; y++ )
			{
				for ( int z = min_z; z < max_z; z++ )
				{
					Block current_block = Bukkit.getWorld(l.getWorld().getName()).getBlockAt(x, y, z);
					if ( current_block.getType().equals(Material.PORTAL) )
					{
						portal_blocks_in_range.add(current_block);
					}
				}
			}
		}
		
		if ( !(allPortals.isEmpty()) )
		{
			for ( int h = 0; h < allPortals.size(); h++ )
			{
				Portal current_portal = allPortals.get(h);
				Location current_portal_location = new Location(Bukkit.getWorld(current_portal.getWorld()), current_portal.getX(), current_portal.getY(), current_portal.getZ());
				portal_locations.add(current_portal_location);
			}
		}
		
		if ( !(portal_blocks_in_range.isEmpty()) && !(portal_locations.isEmpty()) )
		{
			for ( int i = 0; i < portal_blocks_in_range.size(); i++ )
			{
				for ( int j = 0; j < portal_locations.size(); j++ )
				{
					if ( portal_blocks_in_range.get(i).getLocation().equals(portal_locations.get(j)) )
					{
						Integer p_id = getPortalId(portal_locations.get(j));
						near_active_portal = getPortal(p_id);
					}
				}
			}
		}
		
		return near_active_portal;
		
	}
	
	public List<Portal> getNearPortals(Location location)
	{
		List<Portal> near_portals = new ArrayList<Portal>();
		List<Portal> allPortals = getAllPortals();
		List<Location> portal_locations = new ArrayList<Location>();
		List<Location> near_portal_locations = new ArrayList<Location>();
		
		if ( !(allPortals.isEmpty()) )
		{
			for ( int h = 0; h < allPortals.size(); h++ )
			{
				Portal current_portal = allPortals.get(h);
				Location current_portal_location = new Location(Bukkit.getWorld(current_portal.getWorld()), current_portal.getX(), current_portal.getY(), current_portal.getZ());
				portal_locations.add(current_portal_location);
			}
		}
		else
		{
			
		}
		
		Block pb = location.getBlock();
		
		int player_x = pb.getX();
		int player_y = pb.getY();
		int player_z = pb.getZ();
		
		
		int min_x = player_x - 10;
		int max_x = player_x + 10;
		
		int min_y;
		int max_y;
		if ( player_y < 10 )
		{
			min_y = 0;
			max_y = player_y + 10;
		}
		else if ( player_y > 245 )
		{
			min_y = player_y - 10;
			max_y = 255;
		}
		else
		{
			min_y = player_y - 10;
			max_y = player_y + 10;
		}
		
		int min_z = player_z - 10;
		int max_z = player_z + 10;
		
		
		for ( int x = min_x; x < max_x; x++ )
		{
			for ( int y = min_y; y < max_y; y++ )
			{
				for ( int z = min_z; z < max_z; z++ )
				{
					Block current_block = location.getWorld().getBlockAt(x, y, z);
					Location current_block_loc = current_block.getLocation();
					for ( int i = 0; i < portal_locations.size(); i++ )
					{
						Location current_location = portal_locations.get(i);
						Block portal_loc_block = current_location.getBlock();
						int portal_loc_x = portal_loc_block.getX();
						int portal_loc_y = portal_loc_block.getY();
						int portal_loc_z = portal_loc_block.getZ();
						if ( current_block.getX() == portal_loc_x && current_block.getY() == portal_loc_y && current_block.getZ() == portal_loc_z )
						{
							near_portal_locations.add(portal_locations.get(i));
						}
					}
				}
			}
		}
		
		if ( !(near_portal_locations.isEmpty()) )
		{
			near_portals = new ArrayList<Portal>();
			for ( int i = 0; i < near_portal_locations.size(); i++ )
			{
				Location current_loc = near_portal_locations.get(i);
				int lx = current_loc.getBlock().getX();
				int ly = current_loc.getBlock().getY();
				int lz = current_loc.getBlock().getZ();
				for ( int j = 0; j < allPortals.size(); j++ )
				{
					Portal current_portal = allPortals.get(j);
					int px = current_portal.getX();
					int py = current_portal.getY();
					int pz = current_portal.getZ();
					if ( lx == px && ly == py && lz == pz )
					{
						near_portals.add(current_portal);
					}
					//Location portal_loc = new Location(Bukkit.getWorld(current_portal.getWorld()), Double.parseDouble(current_portal.getX().toString()), Double.parseDouble(current_portal.getY().toString()), Double.parseDouble(current_portal.getZ().toString()));
				}
			}
		}
		
		return near_portals;
	}
	
	public List<Location> getPortalsNearLocation(Location location)
	{
		//TODO: suchalgorithmus ändern, sodass in einem radius um die location herum gesucht wird (beginnend im zentrum)
		logger.log(Level.INFO, "suche nach portalen bei {0}...", location.toString());
		//List<Portal> near_portals = null;
		List<Portal> near_portals = new ArrayList<Portal>();
		List<Location> portal_locations = new ArrayList<Location>();
		
		Block pb = location.getBlock();
		
		int player_x = pb.getX();
		int player_y = pb.getY();
		int player_z = pb.getZ();
		
		int min_x = player_x - 1;
		int max_x = player_x + 15;
		
		int min_y;
		int max_y;
		if ( player_y < 15 )
		{
			min_y = 0;
			max_y = player_y + 15;
		}
		else if ( player_y > 245 )
		{
			min_y = player_y - 15;
			max_y = 255;
		}
		else
		{
			min_y = player_y - 15;
			max_y = player_y + 15;
		}
		
		int min_z = player_z - 15;
		int max_z = player_z + 15;
		
		for ( int x = min_x; x < max_x; x++ )
		{
			for ( int y = min_y; y < max_y; y++ )
			{
				for ( int z = min_z; z < max_z; z++ )
				{
					Block current_block = location.getWorld().getBlockAt(x, y, z);
					Location current_block_loc = current_block.getLocation();
					Location above_current_block = current_block_loc;
					above_current_block.setY(y+1);
					Location below_current_block = current_block_loc;
					below_current_block.setY(y-1);
					
					//FIXME: hier werden für jedes portal 4 locations gefunden, sollten aber nur 2 sein (evtl ursache für den teleportations-bug)
					if ( current_block.getType().equals(Material.OBSIDIAN) )
					{
						/*
						logger.log(Level.INFO, "current_block: {0}, {1}, {2}", new Object[]{x, y, z});
						logger.log(Level.INFO, "block type: {0}", current_block.getType().toString());
						logger.log(Level.INFO, "above is: {0}", above_current_block.getBlock().getType().toString());
						*/
						if ( above_current_block.getBlock().getType().equals(Material.PORTAL) )
						{
							logger.log(Level.INFO, "portal gefunden bei {0}, {1}, {2}", new Object[]{x, y, z});
							portal_locations.add(current_block.getLocation());
						}
					}
					else if ( current_block.getType().equals(Material.PORTAL) )
					{
						/*
						logger.log(Level.INFO, "current_block: {0}, {1}, {2}", new Object[]{x, y, z});
						logger.log(Level.INFO, "block type: {0}", current_block.getType().toString());
						logger.log(Level.INFO, "below is: {0}", below_current_block.getBlock().getType().toString());
						*/
						if ( below_current_block.getBlock().getType().equals(Material.OBSIDIAN) )
						{
							logger.log(Level.INFO, "portal gefunden bei {0}, {1}, {2}", new Object[]{x, y, z});
							portal_locations.add(current_block.getLocation());
						}
					}
				}
			}
		}
		
		return portal_locations;
	}
	
	public Location get_NearestPortal(Location location)
	{
		//List<Location> near_portals = getPortalsNearLocation(location);
		String world = location.getWorld().getName();
		List<Location> portal_locations = getPortalsNearLocation(location);
		Location portal_location = null;
		if ( portal_locations.isEmpty() )
		{
			//no portals found
			logger.log(Level.INFO, "keine portale gefunden");
			return portal_location;
		}
		else if ( portal_locations.size() == 1 )
		{
			portal_location = portal_locations.get(0);
		}
		else
		{
			logger.log(Level.INFO, "{0} portale gefunden", portal_locations.size());
			int l_x = location.getBlock().getX();
			int l_y = location.getBlock().getY();
			int l_z = location.getBlock().getZ();
			
			int final_x = 0;
			int final_y = 0;
			int final_z = 0;
			
			for ( int j = 0; j < portal_locations.size(); j++ )
			{
				Location c_loc = portal_locations.get(j);
				int x = c_loc.getBlock().getX();
				int y = c_loc.getBlock().getY();
				int z = c_loc.getBlock().getZ();
				
				int x_diff = getDiff(x, l_x);
				int y_diff = getDiff(y, l_y);
				int z_diff = getDiff(z, l_z);
				
				if ( j != portal_locations.size()-1 )
				{
					Location next_loc = portal_locations.get(j+1);
					int next_x = next_loc.getBlock().getX();
					int next_y = next_loc.getBlock().getY();
					int next_z = next_loc.getBlock().getZ();
					
					int next_x_diff = getDiff(next_x, l_x);
					int next_y_diff = getDiff(next_y, l_y);
					int next_z_diff = getDiff(next_z, l_z);
					
					if ( next_x_diff <= x_diff && next_y_diff <= y_diff && next_z_diff <= z_diff )
					{
						final_x = next_x;
						final_y = next_y;
						final_z = next_z;
						
						if ( l_x < 0 && final_x > 0 )
						{
							final_x *= -1;
						}
						if ( l_y < 0 && final_y > 0 )
						{
							final_y *= -1;
						}
						if ( l_z < 0 && final_z > 0 )
						{
							final_z *= -1;
						}
					}
					else
					{
						final_x = x;
						final_y = y;
						final_z = z;
						if ( l_x < 0 && final_x > 0 )
						{
							final_x *= -1;
						}
						if ( l_y < 0 && final_y > 0 )
						{
							final_y *= -1;
						}
						if ( l_z < 0 && final_z > 0 )
						{
							final_z *= -1;
						}
					}
				}
			}
			
			Location final_loc = new Location(Bukkit.getWorld(world), final_x, final_y, final_z);
			portal_location = final_loc;
		}
		return portal_location;
	}
	
	public Portal getNearestPortal(Location location)
	{
		//TODO: ersetzen durch neuen Algorithmus in findPortal (getNearPortals und getAllPortals sind obsolet)
		List<Portal> near_portals = getNearPortals(location);
		String world = location.getWorld().getName();
		List<Location> portal_locations = new ArrayList<Location>();
		Map<Location, Portal> locs_ports = new HashMap<Location, Portal>();
		Portal portal = null;
		if ( near_portals.isEmpty() )
		{
			//no portals found
			return portal;
		}
		else if ( near_portals.size() == 1 )
		{
			portal = near_portals.get(0);
		}
		else
		{
			for ( int i = 0; i < near_portals.size(); i++ )
			{
				Portal current_portal = near_portals.get(i);
				int x = current_portal.getX();
				int y = current_portal.getY();
				int z = current_portal.getZ();
				//String world = current_portal.getWorld();
				Location p_loc = new Location(Bukkit.getWorld(world), x, y, z);
				portal_locations.add(p_loc);
				locs_ports.put(p_loc, current_portal);
			}
			int l_x = location.getBlock().getX();
			int l_y = location.getBlock().getY();
			int l_z = location.getBlock().getZ();
			
			int final_x = 0;
			int final_y = 0;
			int final_z = 0;
			
			for ( int j = 0; j < portal_locations.size(); j++ )
			{
				Location c_loc = portal_locations.get(j);
				int x = c_loc.getBlock().getX();
				int y = c_loc.getBlock().getY();
				int z = c_loc.getBlock().getZ();
				
				int x_diff = getDiff(x, l_x);
				int y_diff = getDiff(y, l_y);
				int z_diff = getDiff(z, l_z);
				
				if ( j != portal_locations.size()-1 )
				{
					Location next_loc = portal_locations.get(j+1);
					int next_x = next_loc.getBlock().getX();
					int next_y = next_loc.getBlock().getY();
					int next_z = next_loc.getBlock().getZ();
					
					int next_x_diff = getDiff(next_x, l_x);
					int next_y_diff = getDiff(next_y, l_y);
					int next_z_diff = getDiff(next_z, l_z);
					
					if ( next_x_diff <= x_diff && next_y_diff <= y_diff && next_z_diff <= z_diff )
					{
						final_x = next_x;
						final_y = next_y;
						final_z = next_z;
						
						if ( l_x < 0 && final_x > 0 )
						{
							final_x *= -1;
						}
						if ( l_y < 0 && final_y > 0 )
						{
							final_y *= -1;
						}
						if ( l_z < 0 && final_z > 0 )
						{
							final_z *= -1;
						}
					}
					else
					{
						final_x = x;
						final_y = y;
						final_z = z;
						if ( l_x < 0 && final_x > 0 )
						{
							final_x *= -1;
						}
						if ( l_y < 0 && final_y > 0 )
						{
							final_y *= -1;
						}
						if ( l_z < 0 && final_z > 0 )
						{
							final_z *= -1;
						}
					}
				}
			}
			
			Location final_loc = new Location(Bukkit.getWorld(world), final_x, final_y, final_z);
			portal = locs_ports.get(final_loc);
		}
		return portal;
	}
	
	
	public List<Portal> getNearReversePortals(Location location)
	{
		//List<Portal> near_portals = null;
		List<Portal> near_portals = new ArrayList<Portal>();
		List<Portal> allPortals = getAllReversePortals();
		List<Location> portal_locations = new ArrayList<Location>();
		List<Location> near_portal_locations = new ArrayList<Location>();
		
		if ( !(allPortals.isEmpty()) )
		{
			for ( int h = 0; h < allPortals.size(); h++ )
			{
				Portal current_portal = allPortals.get(h);
				Location current_portal_location = new Location(Bukkit.getWorld(current_portal.getWorld()), current_portal.getX(), current_portal.getY(), current_portal.getZ());
				portal_locations.add(current_portal_location);
			}
		}
		else
		{
			//no portals found
			return near_portals;
		}
		
		Block pb = location.getBlock();
		
		int player_x = pb.getX();
		int player_y = pb.getY();
		int player_z = pb.getZ();
		
		int min_x = player_x - 10;
		int max_x = player_x + 10;
		
		int min_y;
		int max_y;
		if ( player_y < 10 )
		{
			min_y = 0;
			max_y = player_y + 10;
		}
		else if ( player_y > 245 )
		{
			min_y = player_y - 10;
			max_y = 255;
		}
		else
		{
			min_y = player_y - 10;
			max_y = player_y + 10;
		}
		
		int min_z = player_z - 10;
		int max_z = player_z + 10;
		
		for ( int x = min_x; x < max_x; x++ )
		{
			for ( int y = min_y; y < max_y; y++ )
			{
				for ( int z = min_z; z < max_z; z++ )
				{
					Block current_block = location.getWorld().getBlockAt(x, y, z);
					//Location current_block_loc = current_block.getLocation();
					for ( int i = 0; i < portal_locations.size(); i++ )
					{
						Location current_location = portal_locations.get(i);
						Block portal_loc_block = current_location.getBlock();
						int portal_loc_x = portal_loc_block.getX();
						int portal_loc_y = portal_loc_block.getY();
						int portal_loc_z = portal_loc_block.getZ();
						if ( current_block.getX() == portal_loc_x && current_block.getY() == portal_loc_y && current_block.getZ() == portal_loc_z )
						{
							near_portal_locations.add(portal_locations.get(i));
						}
					}
				}
			}
		}
		
		if ( !(near_portal_locations.isEmpty()) )
		{
			near_portals = new ArrayList<Portal>();
			for ( int i = 0; i < near_portal_locations.size(); i++ )
			{
				Location current_loc = near_portal_locations.get(i);
				int lx = current_loc.getBlock().getX();
				int ly = current_loc.getBlock().getY();
				int lz = current_loc.getBlock().getZ();
				for ( int j = 0; j < allPortals.size(); j++ )
				{
					Portal current_portal = allPortals.get(j);
					int px = current_portal.getX();
					int py = current_portal.getY();
					int pz = current_portal.getZ();
					if ( lx == px && ly == py && lz == pz )
					{
						near_portals.add(current_portal);
					}
					//Location portal_loc = new Location(Bukkit.getWorld(current_portal.getWorld()), Double.parseDouble(current_portal.getX().toString()), Double.parseDouble(current_portal.getY().toString()), Double.parseDouble(current_portal.getZ().toString()));
				}
			}
		}
		
		return near_portals;
	}
	
	public Portal getNearestReversePortal(Location location)
	{
		//TODO: suche nach reverse portalen
		List<Portal> near_portals = getNearReversePortals(location);
		String world = location.getWorld().getName();
		List<Location> portal_locations = new ArrayList<Location>();
		Map<Location, Portal> locs_ports = new HashMap<Location, Portal>();
		Portal portal = null ;
		if ( near_portals.isEmpty() )
		{
			//no portals found
			return portal;
		}
		else if ( near_portals.size() == 1 )
		{
			portal = near_portals.get(0);
		}
		else
		{
			for ( int i = 0; i < near_portals.size(); i++ )
			{
				Portal current_portal = near_portals.get(i);
				int x = current_portal.getX();
				int y = current_portal.getY();
				int z = current_portal.getZ();
				//String world = current_portal.getWorld();
				Location p_loc = new Location(Bukkit.getWorld(world), x, y, z);
				portal_locations.add(p_loc);
				locs_ports.put(p_loc, current_portal);
			}
			int l_x = location.getBlock().getX();
			int l_y = location.getBlock().getY();
			int l_z = location.getBlock().getZ();
			
			int final_x = 0;
			int final_y = 0;
			int final_z = 0;
			
			for ( int j = 0; j < portal_locations.size(); j++ )
			{
				Location c_loc = portal_locations.get(j);
				int x = c_loc.getBlock().getX();
				int y = c_loc.getBlock().getY();
				int z = c_loc.getBlock().getZ();
				
				int x_diff = getDiff(x, l_x);
				int y_diff = getDiff(y, l_y);
				int z_diff = getDiff(z, l_z);
				
				if ( j != portal_locations.size()-1 )
				{
					Location next_loc = portal_locations.get(j+1);
					int next_x = next_loc.getBlock().getX();
					int next_y = next_loc.getBlock().getY();
					int next_z = next_loc.getBlock().getZ();
					
					int next_x_diff = getDiff(next_x, l_x);
					int next_y_diff = getDiff(next_y, l_y);
					int next_z_diff = getDiff(next_z, l_z);
					
					if ( next_x_diff <= x_diff && next_y_diff <= y_diff && next_z_diff <= z_diff )
					{
						final_x = next_x;
						final_y = next_y;
						final_z = next_z;
						
						if ( l_x < 0 && final_x > 0 )
						{
							final_x *= -1;
						}
						if ( l_y < 0 && final_y > 0 )
						{
							final_y *= -1;
						}
						if ( l_z < 0 && final_z > 0 )
						{
							final_z *= -1;
						}
					}
					else
					{
						final_x = x;
						final_y = y;
						final_z = z;
						if ( l_x < 0 && final_x > 0 )
						{
							final_x *= -1;
						}
						if ( l_y < 0 && final_y > 0 )
						{
							final_y *= -1;
						}
						if ( l_z < 0 && final_z > 0 )
						{
							final_z *= -1;
						}
					}
				}
			}
			
			Location final_loc = new Location(Bukkit.getWorld(world), final_x, final_y, final_z);
			portal = locs_ports.get(final_loc);
		}
		return portal;
	}
	
	public Integer getDiff(int value1, int value2)
	{
		int diff = 0;
		if ( value1 < 0 && value2 < 0 )
		{
			value1 *= -1;
			value2 *= -1;
			if ( value1 < value2 )
			{
				diff = value2 - value1;
			}
			else
			{
				diff = value1 - value2;
			}
		}
		else if ( value1 > 0 && value2 > 0 )
		{
			if ( value1 > value2 )
			{
				diff = value1 - value2;
			}
			else
			{
				diff = value2 - value1;
			}
		}
		else
		{
			if ( value1 < 0 && value2 > 0 )
			{
				value1 *= -1;
				if ( value1 < value2 )
				{
					diff = value2 - value1;
				}
				else
				{
					diff = value1 - value2;
				}
			}
			else
			{
				value2 *= -1;
				if ( value2 < value1 )
				{
					diff = value1 - value2;
				}
				else
				{
					diff = value2 - value1;
				}
			}
		}
		return diff;
	}
	
	/**
	 * Liest die IDs aller gespeicherten Portale aus der PortalConfig und der ReversePortalConfig.
	 * @return Gibt eine Liste von allen gefundenen IDs zurück.
	 */
	private List<Integer> getAllPortalIDs()
	{
		List<Integer> portal_ids = new ArrayList<Integer>();
		multinether.reloadConfig();
		FileConfiguration portal_conf = multinether.getPortalConfig();
		FileConfiguration reverse_portal_conf = multinether.getReversePortalConfig();
		Set<String> keys = portal_conf.getKeys(true);
		Set<String> reverse_keys = reverse_portal_conf.getKeys(true);
		Object[] key_list = keys.toArray();
		Object[] reverse_key_list = reverse_keys.toArray();
		//int portalcount = multinether.getConfig().getInt(portalcount_path);
		for ( int i = 0; i < key_list.length; i++ )
		{
			//if ( key_list[i].toString().length() == 1 )
			//{
			try
			{
				Integer current_id = Integer.parseInt(key_list[i].toString());
				portal_ids.add(current_id);
			}
			catch ( ClassCastException cce )
			{
				continue;
			}
			catch ( NumberFormatException nfe )
			{
				continue;
			}
			//}
		}
		
		for ( int j = 0; j < reverse_keys.size(); j++ )
		{
			try
			{
				Integer current_id = Integer.parseInt(reverse_key_list[j].toString());
				portal_ids.add(current_id);
			}
			catch ( ClassCastException cce )
			{
				continue;
			}
			catch ( NumberFormatException nfe )
			{
				continue;
			}
		}
		return portal_ids;
	}
	
	private List<Integer> getPortalIDs()
	{
		List<Integer> portal_ids = new ArrayList<Integer>();
		FileConfiguration portal_conf = multinether.getPortalConfig();
		Set<String> keys = portal_conf.getKeys(true);
		Object[] key_list = keys.toArray();
		for ( int i = 0; i < key_list.length; i++ )
		{
			try
			{
				Integer current_id = Integer.parseInt(key_list[i].toString());
				portal_ids.add(current_id);
			}
			catch ( ClassCastException cce )
			{
				continue;
			}
			catch ( NumberFormatException nfe )
			{
				continue;
			}
		}
		return portal_ids;
	}
	
	public List<Integer> getReversePortalIDs()
	{
		List<Integer> portal_ids = new ArrayList<Integer>();
		//multinether.reloadConfig();
		FileConfiguration reverse_portal_conf = multinether.getReversePortalConfig();
		Set<String> keys = reverse_portal_conf.getKeys(true);
		Object[] key_list = keys.toArray();
		for ( int i = 0; i < key_list.length; i++ )
		{
			try
			{
				Integer current_id = Integer.parseInt(key_list[i].toString());
				portal_ids.add(current_id);
			}
			catch ( ClassCastException cce )
			{
				continue;
			}
			catch ( NumberFormatException nfe )
			{
				continue;
			}
		}
		return portal_ids;
	}
	
	public List<Portal> getAllPortals()
	{
		//multinether.reloadConfig();
		List<Portal> portals = new ArrayList<Portal>();
		List<Integer> portal_ids = getPortalIDs();
		for ( Integer current_id : portal_ids )
		{
			//Integer current_id = ids.get(i);
			ConfigurationSection portal_section = multinether.getPortalConfig().getConfigurationSection(current_id.toString());
			ConfigurationSection location_section = portal_section.getConfigurationSection("location");
			Integer p_x = location_section.getInt("x");
			Integer p_y = location_section.getInt("y");
			Integer p_z = location_section.getInt("z");
			String p_world = location_section.getString("world");
			String p_link = portal_section.getString("linkto");
			Integer corresponding_id = portal_section.getInt("linktoid");
			Portal current_portal = new Portal();
			current_portal.setID(current_id);
			current_portal.setWorld(p_world);
			current_portal.setX(p_x);
			current_portal.setY(p_y);
			current_portal.setZ(p_z);
			current_portal.setLinkTo(p_link);
			current_portal.setCorrespondingID(corresponding_id);
			portals.add(current_portal);
		}
		/*
		for ( Integer current_reverse_id : reverse_portal_ids )
		{
			ConfigurationSection reverse_portal_section = multinether.getReversePortalConfig().getConfigurationSection(current_reverse_id.toString());
			ConfigurationSection location_section = reverse_portal_section.getConfigurationSection("location");
			Integer p_x = location_section.getInt("x");
			Integer p_y = location_section.getInt("y");
			Integer p_z = location_section.getInt("z");
			String p_world = location_section.getString("world");
			//logger.log(Level.INFO, "world = {0}", p_world);
			String p_link = reverse_portal_section.getString("linkto");
			Integer corresponding_id = reverse_portal_section.getInt("linktoid");
			Portal current_portal = new Portal();
			current_portal.setID(current_reverse_id);
			current_portal.setWorld(p_world);
			current_portal.setX(p_x);
			current_portal.setY(p_y);
			current_portal.setZ(p_z);
			current_portal.setLinkTo(p_link);
			current_portal.setCorrespondingID(corresponding_id);
			portals.add(current_portal);
		}
		*/
		return portals;
	}
	
	public List<Portal> getAllReversePortals()
	{
		multinether.reloadConfig();
		List<Portal> portals = new ArrayList<Portal>();
		List<Integer> ids = getReversePortalIDs();
		for ( Integer current_id : ids )
		{
			//Integer current_id = ids.get(i);
			ConfigurationSection portal_section = multinether.getReversePortalConfig().getConfigurationSection(current_id.toString());
			ConfigurationSection location_section = portal_section.getConfigurationSection("location");
			Integer p_x = location_section.getInt("x");
			Integer p_y = location_section.getInt("y");
			Integer p_z = location_section.getInt("z");
			String p_world = location_section.getString("world");
			String p_link = portal_section.getString("linkto");
			Integer corresponding_id = portal_section.getInt("linktoid");
			Portal current_portal = new Portal();
			current_portal.setID(current_id);
			current_portal.setWorld(p_world);
			current_portal.setX(p_x);
			current_portal.setY(p_y);
			current_portal.setZ(p_z);
			current_portal.setLinkTo(p_link);
			current_portal.setCorrespondingID(corresponding_id);
			portals.add(current_portal);
		}
		return portals;
	}
	
	public Portal getReversePortal(Integer id)
	{
		Portal p = null;
		List<Portal> all_reverse_portals = getAllReversePortals();
		if ( !(all_reverse_portals.isEmpty()) )
		{
			for ( int i = 0; i < all_reverse_portals.size(); i++ )
			{
				if ( all_reverse_portals.get(i).getID().equals(id) )
				{
					p = all_reverse_portals.get(i);
				}
			}
		}
		return p;
	}
	
	public Integer getReversePortalId(Location l)
	{
		Integer portal_id = null;
		List<Portal> all_reverse_portals = getAllReversePortals();
		for ( int i = 0; i < all_reverse_portals.size(); i++ )
		{
			Portal current_portal = all_reverse_portals.get(i);
			if ( current_portal.getLocation().getX() == l.getX() && current_portal.getLocation().getY() == l.getY() && current_portal.getLocation().getZ() == l.getZ() )
			{
				portal_id = current_portal.getID();
			}
		}
		return portal_id;
	}
	
	public boolean generateNether(String worldname)
	{
		Long seed = Bukkit.getWorld(worldname).getSeed();
		World w = Bukkit.getWorld(worldname);
		String nethername = worldname+"_nether";
		WorldCreator create_nether = new WorldCreator(nethername);
		create_nether.type(WorldType.NORMAL);
		create_nether.environment(Environment.NETHER);
		create_nether.seed(seed);
		create_nether.generateStructures(true);
		create_nether.generator(w.getGenerator());
		Bukkit.getWorlds().add(create_nether.createWorld());	
	
		if ( Bukkit.getWorld(nethername) != null )
		{
			return true;
		}
		return false;
	}
	
	public Object[] getLocation(Location reverse_portal_location)
	{
		logger.log(Level.INFO, ">>> getLocation <<<");
		Object[] result = new Object[2];
		int is_safe = 0;
		//double x = reverse_portal_location.getBlock().getX() * 8;
		//double z = reverse_portal_location.getBlock().getZ() * 8;
		int y = reverse_portal_location.getBlock().getY();
		int y_min = y - 15;
		
		//reverse_portal_location.setX(x);
		//reverse_portal_location.setZ(z);
		
		logger.log(Level.INFO, "y: {0}", y);
		
		/*Random random = new Random();
		int y = random.nextInt(123 - 32) + 32;
		reverse_portal_location.setY(y);
		*/
		
		if ( reverse_portal_location.getBlock().getType().equals(Material.WATER) )
		{
			Location above = reverse_portal_location;
			Location below = reverse_portal_location;
			above.setY(y+1);
			below.setY(y-1);
			if ( !(above.getBlock().getType().equals(Material.WATER)) && below.getBlock().getType().equals(Material.WATER) )
			{
				is_safe = -1;
			}
			if ( above.getBlock().getType().equals(Material.WATER) && below.getBlock().getType().equals(Material.WATER) )
			{
				is_safe = 1;
			}
		}
		else if ( reverse_portal_location.getBlock().getType().equals(Material.AIR) )
		{
			for ( int i = y; i > y_min; i-- )
			{
				Location l = reverse_portal_location;
				l.setY(i-1);
				if ( !(l.getBlock().getType().equals(Material.AIR)) )
				{
					is_safe = 0;
					reverse_portal_location.setY(i);
				}
			}
		}
		result[0] = reverse_portal_location;
		result[1] = is_safe;
		logger.log(Level.INFO, "location = {0}", result[0]);
		return result;
	}
	
	public Object[] getReverseLocation(Location portal_location)
	{
		Object[] result = new Object[2];
		int is_safe = 0;
		//double x = portal_location.getBlock().getX() / 8;
		//double z = portal_location.getBlock().getZ() / 8;
		
		//portal_location.setX(x);
		//portal_location.setZ(z);
		/*		
		Random random = new Random();
		int y = random.nextInt(y_max - y_min) + y_min; //Y ist ein zufallswert zwischen 32 und 122 (von 0 bis 31 ist alles voller lava und 123 ist 5 blöcke unter der obergrenze)
		portal_location.setY(y);
		*/
		int y = portal_location.getBlockY();
		int y_min = 32;
		Location above = portal_location;
		Location below = portal_location;
		above.setY(y+1);
		below.setY(y-1);
		
		if ( portal_location.getBlock().getType().equals(Material.LAVA) ) //wenn die location trotzdem lava ist (z.B in einem lavafall)
		{
			if ( !(above.getBlock().getType().equals(Material.LAVA)) && below.getBlock().getType().equals(Material.LAVA) )
			{
				is_safe = -1;
			}
			if ( above.getBlock().getType().equals(Material.LAVA) && below.getBlock().getType().equals(Material.LAVA) )
			{
				is_safe = 1;
			}
		}
		else if ( portal_location.getBlock().getType().equals(Material.AIR) )
		{
			logger.log(Level.INFO, "debug1");
			boolean changed = false;
			for ( int i = y; i > y_min; i-- )
			{
				Location l = portal_location;
				l.setY(i-1);
				if ( l.getBlock().getType().equals(Material.NETHERRACK) || l.getBlock().getType().equals(Material.SOUL_SAND) )
				{
					logger.log(Level.INFO, "debug2");
					is_safe = 0;
					portal_location.setY(i);
					changed = true;
					break;
				}
				else if ( l.getBlock().getType().equals(Material.LAVA) )
				{
					logger.log(Level.INFO, "debug3");
					is_safe = -1;
					portal_location.setY(i);
					changed = true;
					break;
				}
			}
			if ( !(changed) )
			{
				is_safe = -1;
				portal_location.setY(32);
			}
		}
		else
		{
			if ( above.getBlock().getType().equals(Material.LAVA) )
			{
				is_safe = 1;
			}
			else if ( below.getBlock().getType().equals(Material.LAVA) )
			{
				is_safe = -1;
			}
		}
		result[0] = portal_location;
		result[1] = is_safe;
		return result;
	}
	
	
	public Location findPortal(String World, Location start_location)
	{
		int count = 0;
		int steps = 0;
		int center_steps = 0;
		int a = 129;
		int start_x = start_location.getBlockX();
		int start_y = start_location.getBlockY();
		int start_z = start_location.getBlockZ();
		List<Block> found_blocks = new ArrayList<Block>();
		
		for ( int radius = 0; radius < (a / 2) + 1; radius++ )
		{
			int low = start_y - radius;
			int high = start_y + radius;
			int low_x = start_x - radius;
			int high_x = start_x + radius;
			int low_z = start_z - radius;
			int high_z = start_z + radius;
			//logger.log(Level.INFO, "low high y: {0} {1}", new Object[]{low, high});
			//logger.log(Level.INFO, "low high x: {0} {1}", new Object[]{low_x, high_x});
			//logger.log(Level.INFO, "low high z: {0} {1}", new Object[]{low_z, high_z});
			HashSet<Integer> set = new HashSet<Integer>();
			set.add(low);
			set.add(high);
			
			//logger.log(Level.INFO, "loop #1");
			for ( int y : set )
			{
				for ( int x = low_x; x < high_x+1; x++)
				{
					for ( int z = low_z; z < high_z+1; z++)
					{
						steps += 1;
						//logger.log(Level.INFO, "x, y, z: {0}_{1}_{2}", new Object[]{x, y, z});
						Location current_location = new Location(Bukkit.getWorld(World), x, y, z);
						Block current_block = current_location.getBlock();
						//logger.log(Level.INFO, "x, y, z: {0}, {1}, {2}",new Object[]{x, y, z});
						if ( current_block.getType().equals(Material.PORTAL) )
						{
							Location location_below = current_location;
							location_below.setY(current_location.getY()-1);
							Block block_below = location_below.getBlock();
							if ( block_below.getType().equals(Material.OBSIDIAN) )
							{
								found_blocks.add(current_block);
								logger.log(Level.INFO, "Portalblock found at: {0}, {1}, {2}, after {3} steps!", new Object[]{x, y, z, steps});
							}
						}
					}
				}
			}
			
			set.clear();
			set.add(low_x);
			set.add(high_x);
			
			//logger.log(Level.INFO, "loop #2");
			for ( int y2 = low+1; y2 < high+1-1; y2++)
			{
				for ( int x2 : set )
				{
					for ( int z2 = low_z; z2 < high_z+1; z2++ )
					{
						steps += 1;
						//logger.log(Level.INFO, "x2, y2, z2: {0}_{1}_{2}", new Object[]{x2, y2, z2});
						Location current_location = new Location(Bukkit.getWorld(World), x2, y2, z2);
						Block current_block = current_location.getBlock();
						//logger.log(Level.INFO, "x, y, z: {0}, {1}, {2}",new Object[]{x2, y2, z2});
						if ( current_block.getType().equals(Material.PORTAL) )
						{
							Location location_below = current_location;
							location_below.setY(current_location.getY()-1);
							Block block_below = location_below.getBlock();
							if ( block_below.getType().equals(Material.OBSIDIAN) )
							{
								found_blocks.add(current_block);
								logger.log(Level.INFO, "Portalblock found at: {0}, {1}, {2}, after {3} steps!", new Object[]{x2, y2, z2, steps});
							}
						}
					}
				}
			}
			
			set.clear();
			set.add(low_z);
			set.add(high_z);
			//logger.log(Level.INFO, "loop #3");
			for ( int y3 = low+1; y3 < high+1-1; y3++ )
			{
				for ( int x3 = low_x+1; x3 < high_x+1-1; x3++ )
				{
					for ( int z3 : set )
					{
						steps += 1;
						//logger.log(Level.INFO, "x3, y3, z3: {0}_{1}_{2}", new Object[]{x3, y3, z3});
						Location current_location = new Location(Bukkit.getWorld(World), x3, y3, z3);
						Block current_block = current_location.getBlock();
						//logger.log(Level.INFO, "x, y, z: {0}, {1}, {2}",new Object[]{x3, y3, z3});
						if ( current_block.getType().equals(Material.PORTAL) )
						{
							Location location_below = current_location;
							location_below.setY(current_location.getY()-1);
							Block block_below = location_below.getBlock();
							if ( block_below.getType().equals(Material.OBSIDIAN) )
							{
								found_blocks.add(current_block);
								logger.log(Level.INFO, "Portalblock found at: {0}, {1}, {2}, after {3} steps!", new Object[]{x3, y3, z3, steps});
							}
						}
					}
				}
			}
		}
		//logger.log(Level.INFO, "steps: {0}", steps);
		Block target_block;
		if ( found_blocks.size() == 0 )
		{
			return null;
		}
		double prev_y = 130;
		
		/*
		Block prev_block = found_blocks.get(0);
		for ( int i = 0; i < 6; i++ )
		{
			Block current_block = found_blocks.get(i);
			double current_y = current_block.getLocation().getY();
			if ( prev_y == 130 || prev_y > current_y )
			{
				prev_y = current_y;
				prev_block = current_block;
			}
		}
		*/
		//target_block = prev_block;
		target_block = found_blocks.get(0);
		return target_block.getLocation();
	}
	
}

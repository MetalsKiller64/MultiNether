package com.ymail.MetalsKiller64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
	
	public CmdExecutor(MultiNether plugin)
	{
		this.multinether = plugin;
		this.command_list = multinether.command_list;
		this.link_list = new ArrayList<String>();
		this.config_path_list = new ArrayList<String>();
		this.netherlink_path = "NetherLinks.";
		this.portalcount_path = "PortalCount";
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
		else if ( cmd.getName().equalsIgnoreCase("netherlink") )
		{
			sender.sendMessage("link_list: "+getLinks());
			if ( args.length > 1 )
			{
				if ( args[0].equalsIgnoreCase("add") )
				{
					multinether.getLogger().log(Level.INFO, "exec add");
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
					multinether.getLogger().log(Level.INFO, "exec netherlink remove");
					if ( args.length == 2 )
					{
						multinether.getLogger().log(Level.INFO, "args.length == 2");
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
						multinether.getLogger().log(Level.INFO, "args.length < 2");
						sender.sendMessage("zu wenig Argumente!");
						return false;
					}
					else if ( args.length > 2 )
					{
						multinether.getLogger().log(Level.INFO, "args.length > 2");
						sender.sendMessage("zu viele Argumente!");
						return false;
					}
				}
			}
			else if ( args.length == 1 && args[0].equalsIgnoreCase("show") )
			{
				multinether.getLogger().log(Level.INFO, "exec show");
				//String links = netherrep.getConfig().getString("LinkList");
				//links = links.replaceAll(":", " <> ");
				
				if ( getLinks().isEmpty() )
				{
					multinether.getLogger().log(Level.INFO, "link list is empty");
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
				multinether.getLogger().log(Level.INFO, "args.length > 4");
				sender.sendMessage("zu viele Argumente!");
				return false;
			}
		}
		else if ( cmd.getName().equalsIgnoreCase("netherport") )
		{
			if ( args.length > 0 )
			{
				if ( args[0].equals("create") )
				{
					try
					{
						Portal portal = createPortal(sender);
						if ( !(portal == null) && !(portal.getID() == null) )
						{
							multinether.getLogger().log(Level.INFO, "Portal erstellt:");
							multinether.getLogger().log(Level.INFO, "ID: {0}", portal.getID());
							multinether.getLogger().log(Level.INFO, "Location: {0}", portal.getLocation());
							return true;
						}
					}
					catch( ClassCastException e )
					{
						multinether.getLogger().log(Level.SEVERE, "Konnte Portal nicht erstellen, Fehler in createPortal() (ClassCastException)");
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
							//TODO: Fehlerbehandlung hinzufügen
						}
					}
					else
					{
						
					}
				}
			}
		}
		else if ( cmd.getName().equals("cmdtest") )
		{
			Player pl = (Player)sender;
			Location p_loc = pl.getLocation();
			Portal portal = getNearestPortal(p_loc);
			sender.sendMessage("found Portal: "+portal.getID());
			openPortal(portal, pl);
			//generateNether("Welt3");
			/*
			if ( sender instanceof Player )
			{
				Player player = (Player) sender;
				
				List<Portal> p = getNearPortals(player.getLocation());
				
				if ( !(p == null) )
				{
					sender.sendMessage("Portals found:");
					for ( int i = 0; i < p.size(); i++ )
					{
						sender.sendMessage("Portal ID: "+p.get(i).getID());
					}
				}
				else
				{
					sender.sendMessage("No near portals found...");
				}
				/*
				Portal p = getNearActivePortal(player.getLocation(), sender);
				
				Block b = player.getLocation().getBlock();
				sender.sendMessage(""+b.getX()+" "+b.getY()+" "+b.getZ());
				
				if ( p != null )
				{
					sender.sendMessage("portal id: "+p.getID());
				}
				else
				{
					sender.sendMessage("no portal found");
				}
				* /
			}
			*/
			return true;
		}
		multinether.getLogger().log(Level.INFO, "anything else");
		//sender.sendMessage("not yet implemented");
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
			//netherrep.getLogger().log(Level.INFO, e.getMessage());
			return true;
		}
		return true;
	}
	
	public boolean isWorld(String value)
	{
		for (int i = 0; i < Bukkit.getWorlds().size(); i++)
		{
			//netherrep.getLogger().log(Level.INFO, "{0}.equalsIgnoreCase({1})", new Object[]{value, this.world_list.get(i).getName()});
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
			//TODO: Fehlerbehandlung für das Adden eines Links hinzufügen (Schwerpunkte: getConfig(), saveConfig())
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
		multinether.getLogger().log(Level.INFO, "exec removeLink()");
		/*
		 * if ( !(linkExists(Link)) )
		 * {
		 * sender.sendMessage("Es existiert kein Link von "+Link);
		 * return false;
		 * }
		 */
		if ( isWorld(Link) )
		{
			multinether.getLogger().log(Level.INFO, "isWorld(Link)");
			multinether.getConfig().set(netherlink_path+Link, null);
			multinether.saveConfig();
			setLinkList();
			sender.sendMessage("Link entfernt.");
			return true;
		}
		else if ( !(isWorld(Link)) )
		{
			multinether.getLogger().log(Level.INFO, "!(isWorld(Link))");
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
	
	/**
	 * Erstellen eines neuen Portals.
	 * @param sender Der Sender des Befehls.
	 * @return Gibt das neuerstellte Portalobjekt zurück.
	 * @throws ClassCastException - wenn der Sender kein Spieler ist (!(instanceof Player))
	 */
	public Portal createPortal(CommandSender sender)
	{
		multinether.getLogger().log(Level.INFO, ">>> createPortal <<<");
		Portal p = null;
		if ( sender instanceof Player )
		{
			Player player = (Player) sender;
			Location l = player.getLocation();
			Location location = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
			//TODO: Portal-Rahmen
			//sender.sendMessage(""+getPortalIDs().size());
			Integer id = 0;
			if ( !(getPortalIDs().isEmpty()) )
			{
				List<Integer> ids = getPortalIDs();
				for ( int i = 0; i < ids.size(); i++ )
				{
					sender.sendMessage(ids.get(i).toString());
					if ( !(getPortalIDs().contains(i)) )
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
					id = getPortalIDs().size();
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
				p.setID(id);
				p.setX(p_x);
				p.setY(p_y);
				p.setZ(p_z);
				
				boolean saved = savePortal(p);
				if ( saved )
				{
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
	
	
	public Portal createReversePortal(Location loc, String world)
	{
		multinether.getLogger().log(Level.INFO, ">>> createReversePortal <<<");
		Portal p = new Portal();
		Location location = new Location(Bukkit.getWorld(world), loc.getX(), loc.getY(), loc.getZ());
		//TODO: Portal-Rahmen
		//sender.sendMessage(""+getPortalIDs().size());
		Integer id = 0;
		if ( !(getReversePortalIDs().isEmpty()) )
		{
			List<Integer> ids = getReversePortalIDs();
			for ( int i = 0; i < ids.size(); i++ )
			{
				if ( !(ids.contains(i)) )
				{
					//sender.sendMessage(""+i);
					Portal check_p = getPortal(i);
					if ( check_p == null )
					{
						id = i;
					}
					//sender.sendMessage("id = "+i);
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

		p = new Portal();
		p.setLocation(location);
		p.setLinkTo(loc.getWorld().getName());
		p.setID(id);
		p.setX(p_x);
		p.setY(p_y);
		p.setZ(p_z);

		boolean saved = saveReversePortal(p);
		if ( saved )
		{
			multinether.getLogger().log(Level.INFO, "created and saved reverse-portal");
			buildPortalFrame(location);
		}
		else
		{
			multinether.getLogger().log(Level.SEVERE, "couldn't create reverse-portal!");
		}
		
		return p;
	}
	
	public void buildPortalFrame(Location loc)
	{
		Block loc_block = loc.getBlock();
		int y = loc_block.getY();
		int x = loc_block.getX();
		int z = loc_block.getZ();
		multinether.getLogger().log(Level.INFO, "rev-portal pos: x={0} y={1} z={2}", new Object[]{x, y, z});
		if ( !(loc_block.getChunk().isLoaded()) )
		{
			loc_block.getChunk().load();
		}
		loc_block.setType(Material.GLOWSTONE);
		new Location(loc.getWorld(), (x-1), y, z).getBlock().setType(Material.GLOWSTONE);
		new Location(loc.getWorld(), (x-1), (y+1), z).getBlock().setType(Material.GLOWSTONE);
		new Location(loc.getWorld(), (x), (y+1), z).getBlock().setType(Material.GLOWSTONE);
		new Location(loc.getWorld(), x, (y+2), z).getBlock().setType(Material.GLOWSTONE);
		new Location(loc.getWorld(), (x-1), (y+2), z).getBlock().setType(Material.GLOWSTONE);
		
		loc_block.setType(Material.AIR);
		new Location(loc.getWorld(), (x-1), y, z).getBlock().setType(Material.AIR);
		new Location(loc.getWorld(), (x-1), (y+1), z).getBlock().setType(Material.AIR);
		new Location(loc.getWorld(), (x), (y+1), z).getBlock().setType(Material.AIR);
		new Location(loc.getWorld(), x, (y+2), z).getBlock().setType(Material.AIR);
		new Location(loc.getWorld(), (x-1), (y+2), z).getBlock().setType(Material.AIR);
		
		List<Location> frame_blocks = new ArrayList<Location>();
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
		
		
		for ( int i = 0; i < frame_blocks.size(); i++ )
		{
			frame_blocks.get(i).getBlock().setType(Material.GLOWSTONE);
		}
		
		for ( int i = 0; i < frame_blocks.size(); i++ )
		{
			frame_blocks.get(i).getBlock().setType(Material.OBSIDIAN);
		}
		
		List<Block> surrounding_blocks = new ArrayList<Block>();
		surrounding_blocks.add(new Location(loc.getWorld(), x, y, (z+1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), y, (z+1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), (y+1), (z+1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), x, (y+1), (z+1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), (y+2), (z+1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), x, (y+2), (z+1)).getBlock());
		
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), (y+3), (z+1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), x, (y+3), (z+1)).getBlock());
		
		surrounding_blocks.add(new Location(loc.getWorld(), x, y, (z-1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), y, (z-1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), (y+1), (z-1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), x, (y+1), (z-1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), (y+2), (z-1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), x, (y+2), (z-1)).getBlock());
		
		surrounding_blocks.add(new Location(loc.getWorld(), (x-1), (y+3), (z-1)).getBlock());
		surrounding_blocks.add(new Location(loc.getWorld(), x, (y+3), (z-1)).getBlock());
		
		for ( int j = 0; j < surrounding_blocks.size(); j++ )
		{
			surrounding_blocks.get(j).setType(Material.GLOWSTONE);
		}
		
		for ( int j = 0; j < surrounding_blocks.size(); j++ )
		{
			surrounding_blocks.get(j).setType(Material.AIR);
		}
		
		//TODO: portal öffnen
	}
	
	public boolean savePortal(Portal p)
	{
		multinether.getLogger().log(Level.INFO, ">>> savePortal <<<");
		if ( !( getPortalIDs().contains(p.getID()) ) )
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
			multinether.getLogger().log(Level.INFO, "{0}", id);
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
			//FIXME: PortalConfig wird nicht gespeichert!
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
	
	public boolean saveReversePortal(Portal p)
	{
		multinether.getLogger().log(Level.INFO, ">>> saveReversePortal <<<");
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
			multinether.getLogger().log(Level.INFO, "{0}", id);
			ConfigurationSection new_portal_section = multinether.getReversePortalConfig().createSection(""+id);
			ConfigurationSection portal_location = new_portal_section.createSection("location");
			portal_location.set("world", world);
			portal_location.set("x", x);
			portal_location.set("y", y);
			portal_location.set("z", z);
			
			new_portal_section.set("linkto", link);
			
			multinether.saveReversePortalConfig();
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
		//TODO: von Listen auf Funktionsaufrufe umbauen
		if ( getPortalIDs().contains(id) )
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
		multinether.getLogger().log(Level.INFO, ">>> getPortal <<<");
		Portal p = null;
		multinether.getLogger().log(Level.INFO, getAllPortals().toString());
		if ( !(getAllPortals().isEmpty()) )
		{
			for ( int i = 0; i < getAllPortals().size(); i++ )
			{
				multinether.getLogger().log(Level.INFO, getAllPortals().get(i).toString());
				multinether.getLogger().log(Level.INFO, getAllPortals().get(i).getID().toString());
				if ( getAllPortals().get(i).getID().equals(id) )
				{
					p = getAllPortals().get(i);
					multinether.getLogger().log(Level.INFO, p.toString());
				}
			}
		}
		return p;
	}
	
	public Integer getPortalId(Location l)
	{
		multinether.getLogger().log(Level.INFO, ">>> getPortalId <<<");
		//TODO: Fehlerbehandlung hinzufügen
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
		//TODO: Portale in der Nähe der Spieler-Location suchen (Suche nach Material.PORTAL)
		Portal near_active_portal = null;
		List<Portal> allPortals = getAllPortals();
		List<Location> portal_locations = new ArrayList<Location>();
		List<Block> portal_blocks_in_range = new ArrayList<Block>();
		
		/*Chunk c = l.getChunk();
		 * for ( int x = 0; x <= 16; x++ )
		 * {
		 * for ( int y = 0; y <= 256; y++ )
		 * {
		 * for ( int z = 0; z <= 16; z++ )
		 * {
		 * Block current_block = c.getBlock(x, y, z);
		 * if (current_block.getType().equals(Material.PORTAL))
		 * {
		 * portal_blocks_in_chunk.add(current_block);
		 * }
		 * }
		 * }
		 * }*/
		
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
					//sender.sendMessage(""+current_block.getX()+" "+current_block.getY()+" "+current_block.getZ());
					multinether.getLogger().log(Level.INFO, "{0} {1} {2}", new Object[]{current_block.getX(), current_block.getY(), current_block.getZ()});
					if ( current_block.getType().equals(Material.PORTAL) )
					{
						portal_blocks_in_range.add(current_block);
						//sender.sendMessage("portal block in range: "+current_block.getX()+" "+current_block.getY()+" "+current_block.getZ());
						multinether.getLogger().log(Level.INFO, "portal block in range: "+current_block.getX()+" "+current_block.getY()+" "+current_block.getZ());
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
		//List<Portal> near_portals = null;
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
				//multinether.getLogger().log(Level.INFO, ""+current_portal_location);
			}
		}
		
		Block pb = location.getBlock();
		
		int player_x = pb.getX();
		int player_y = pb.getY();
		int player_z = pb.getZ();
		
		multinether.getLogger().log(Level.INFO, "block: {0} {1} {2}", new Object[]{player_x, player_y, player_z});
		
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
		
		multinether.getLogger().log(Level.INFO, "min_x: {0} max_x: {1}", new Object[]{min_x, max_x});
		
		for ( int x = min_x; x < max_x; x++ )
		{
			for ( int y = min_y; y < max_y; y++ )
			{
				for ( int z = min_z; z < max_z; z++ )
				{
					Block current_block = Bukkit.getWorld(location.getWorld().getName()).getBlockAt(x, y, z);
					Location current_block_loc = current_block.getLocation();
					//multinether.getLogger().log(Level.INFO, "block loc: "+current_block_loc);
					//multinether.getLogger().log(Level.INFO, "x = "+x+" y = "+y+" z = "+z);
					//multinether.getLogger().log(Level.INFO, "broken x = "+current_block.getX());
					//multinether.getLogger().log(Level.INFO, current_block.getX()+" "+current_block.getY()+" "+current_block.getZ());
					//TODO: portal-positionen durchsuchen; prüfen ob eine position mit aktueller block-position übereinstimmt
					for ( int i = 0; i < portal_locations.size(); i++ )
					{
						Location current_location = portal_locations.get(i);
						Block portal_loc_block = current_location.getBlock();
						int portal_loc_x = portal_loc_block.getX();
						int portal_loc_y = portal_loc_block.getY();
						int portal_loc_z = portal_loc_block.getZ();
						if ( current_block.getX() == portal_loc_x && current_block.getY() == portal_loc_y && current_block.getZ() == portal_loc_z )
						{
							multinether.getLogger().log(Level.INFO, "location added: {0}", portal_locations.get(i));
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
				multinether.getLogger().log(Level.INFO, "lx: {0} ly:{1} lz: {2}", new Object[]{lx, ly, lz});
				for ( int j = 0; j < allPortals.size(); j++ )
				{
					Portal current_portal = allPortals.get(j);
					int px = current_portal.getX();
					int py = current_portal.getY();
					int pz = current_portal.getZ();
					multinether.getLogger().log(Level.INFO, "px: {0} py: {1} pz: {2}", new Object[]{px, py, pz});
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
	
	public Portal getNearestPortal(Location location)
	{
		List<Portal> near_portals = getNearPortals(location);
		String world = location.getWorld().getName();
		List<Location> portal_locations = new ArrayList<Location>();
		Map<Location, Portal> locs_ports = new HashMap<Location, Portal>();
		Portal portal = null ;
		if ( near_portals.isEmpty() )
		{
			multinether.getLogger().log(Level.INFO, "no portals found");
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
				
				multinether.getLogger().log(Level.INFO, "portal x = {0}", x);
				multinether.getLogger().log(Level.INFO, "portal y = {0}", y);
				multinether.getLogger().log(Level.INFO, "portal z = {0}", z);
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
				int x_diff = 0;
				int y_diff = 0;
				int z_diff = 0;
				
				x_diff = getDiff(x, l_x);
				y_diff = getDiff(y, l_y);
				z_diff = getDiff(z, l_z);
				
				if ( j != portal_locations.size()-1 )
				{
					Location next_loc = portal_locations.get(j+1);
					int next_x = next_loc.getBlock().getX();
					int next_y = next_loc.getBlock().getY();
					int next_z = next_loc.getBlock().getZ();
					
					int next_x_diff = 0;
					int next_y_diff = 0;
					int next_z_diff = 0;
					
					next_x_diff = getDiff(next_x, l_x);
					next_y_diff = getDiff(next_y, l_y);
					next_z_diff = getDiff(next_z, l_z);
					
					multinether.getLogger().log(Level.INFO, "x_diff = {0}", x_diff);
					multinether.getLogger().log(Level.INFO, "y_diff = {0}", y_diff);
					multinether.getLogger().log(Level.INFO, "z_diff = {0}", z_diff);
					
					multinether.getLogger().log(Level.INFO, "next_x_diff = {0}", next_x_diff);
					multinether.getLogger().log(Level.INFO, "next_y_diff = {0}", next_y_diff);
					multinether.getLogger().log(Level.INFO, "next_z_diff = {0}", next_z_diff);
					
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
						multinether.getLogger().log(Level.INFO, "next");
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
						multinether.getLogger().log(Level.INFO, "current");
					}
					multinether.getLogger().log(Level.INFO, "final_x: {0}", final_x);
					multinether.getLogger().log(Level.INFO, "final_y: {0}", final_y);
					multinether.getLogger().log(Level.INFO, "final_z: {0}", final_z);
				}
			}
			
			multinether.getLogger().log(Level.INFO, "final_x: {0}", final_x);
			multinether.getLogger().log(Level.INFO, "final_y: {0}", final_y);
			multinether.getLogger().log(Level.INFO, "final_z: {0}", final_z);
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
	 * Liest die IDs aller gespeicherten Portale aus der PortalConfig.
	 * @return Gibt eine Liste von allen gefundenen IDs zurück.
	 */
	private List<Integer> getPortalIDs()
	{
		multinether.getLogger().log(Level.INFO, ">>> getPortalIDs <<<");
		List<Integer> portal_ids = new ArrayList<Integer>();
		multinether.reloadConfig();
		FileConfiguration portal_conf = multinether.getPortalConfig();
		Set<String> keys = portal_conf.getKeys(true);
		Object[] key_list = keys.toArray();
		int portalcount = multinether.getConfig().getInt(portalcount_path);
		for ( int i = 0; i < key_list.length; i++ )
		{
			//multinether.getLogger().log(Level.INFO, "i = "+i);
			//multinether.getLogger().log(Level.INFO, "Key{0}: {1}", new Object[]{i, key_list[i]});
			//if ( key_list[i].toString().length() == 1 )
			//{
			try
			{
				Integer current_id = Integer.parseInt(key_list[i].toString());
				portal_ids.add(current_id);
				//multinether.getLogger().log(Level.INFO, "added id: {0}", current_id);
			}
			catch ( ClassCastException cce )
			{
				continue;
				//netherrep.getLogger().log(Level.INFO, "");
			}
			catch ( NumberFormatException nfe )
			{
				continue;
			}
			//}
		}
		return portal_ids;
	}
	
	public List<Portal> getAllPortals()
	{
		multinether.getLogger().log(Level.INFO, ">>> getAllPortals <<<");
		multinether.reloadConfig();
		List<Portal> portals = new ArrayList<Portal>();
		List<Integer> ids = getPortalIDs();
		for ( Integer current_id : ids )
		{
			//Integer current_id = ids.get(i);
			ConfigurationSection portal_section = multinether.getPortalConfig().getConfigurationSection(current_id.toString());
			ConfigurationSection location_section = portal_section.getConfigurationSection("location");
			Integer p_x = location_section.getInt("x");
			Integer p_y = location_section.getInt("y");
			Integer p_z = location_section.getInt("z");
			String p_world = location_section.getString("world");
			multinether.getLogger().log(Level.INFO, "world = {0}", p_world);
			String p_link = portal_section.getString("linkto");
			Portal current_portal = new Portal();
			current_portal.setID(current_id);
			current_portal.setWorld(p_world);
			current_portal.setX(p_x);
			current_portal.setY(p_y);
			current_portal.setZ(p_z);
			current_portal.setLinkTo(p_link);
			portals.add(current_portal);
		}
		return portals;
	}
	
	
	public Portal getReversePortal(Integer id)
	{
		multinether.getLogger().log(Level.INFO, ">>> getPortal <<<");
		Portal p = null;
		List<Portal> all_reverse_portals = getAllReversePortals();
		multinether.getLogger().log(Level.INFO, all_reverse_portals.toString());
		if ( !(all_reverse_portals.isEmpty()) )
		{
			for ( int i = 0; i < all_reverse_portals.size(); i++ )
			{
				multinether.getLogger().log(Level.INFO, all_reverse_portals.get(i).toString());
				multinether.getLogger().log(Level.INFO, all_reverse_portals.get(i).getID().toString());
				if ( all_reverse_portals.get(i).getID().equals(id) )
				{
					p = all_reverse_portals.get(i);
					multinether.getLogger().log(Level.INFO, p.toString());
				}
			}
		}
		return p;
	}
	
	public Integer getReversePortalId(Location l)
	{
		multinether.getLogger().log(Level.INFO, ">>> getPortalId <<<");
		//TODO: Fehlerbehandlung hinzufügen
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
	
	
	public List<Portal> getAllReversePortals()
	{
		multinether.getLogger().log(Level.INFO, ">>> getAllReversePortals <<<");
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
			//multinether.getLogger().log(Level.INFO, "world = {0}", p_world);
			String p_link = portal_section.getString("linkto");
			Portal current_portal = new Portal();
			current_portal.setID(current_id);
			current_portal.setWorld(p_world);
			current_portal.setX(p_x);
			current_portal.setY(p_y);
			current_portal.setZ(p_z);
			current_portal.setLinkTo(p_link);
			portals.add(current_portal);
		}
		return portals;
	}
	
	public List<Integer> getReversePortalIDs()
	{
		multinether.getLogger().log(Level.INFO, ">>> getReversePortalIDs <<<");
		List<Integer> portal_ids = new ArrayList<Integer>();
		multinether.reloadConfig();
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
	
	public void generateNether(String worldname)
	{
		Long seed = Bukkit.getWorld(worldname).getSeed();
		World w = Bukkit.getWorld(worldname);
		WorldCreator create_nether = new WorldCreator(worldname+"_nether");
		create_nether.type(WorldType.NORMAL);
		create_nether.environment(Environment.NETHER);
		create_nether.seed(seed);
		create_nether.generateStructures(true);
		create_nether.generator(w.getGenerator());
		Bukkit.getWorlds().add(create_nether.createWorld());
	}
	
	public void openPortal(Portal p, Player player)
	{
		int x = p.getX();
		int y = p.getY();
		int z = p.getZ();
		String world = p.getWorld();
		z = z-1; //FIXME: coordinate wird falsch gespeichert
		x = x-1;
		Location pl = new Location(Bukkit.getWorld(world), x, y, z);
		Location x_minus_1 = new Location(Bukkit.getWorld(world), (x-1), y, z);
		Location x_minus_2 = new Location(Bukkit.getWorld(world), (x-2), y, z);
		Location x_plus_1 = new Location(Bukkit.getWorld(world), (x+1), y, z);
		Location x_plus_2 = new Location(Bukkit.getWorld(world), (x+2), y, z);
		Location z_minus_1 = new Location(Bukkit.getWorld(world), x, y, (z-1));
		Location z_minus_2 = new Location(Bukkit.getWorld(world), x, y, (z-2));
		Location z_plus_1 = new Location(Bukkit.getWorld(world), x, y, (z+1));
		Location z_plus_2 = new Location(Bukkit.getWorld(world), x, y, (z+2));
		List<Block> portalblocks = new ArrayList<Block>();
		if ( x_minus_1.getBlock().getType().equals(Material.AIR) && !(x_minus_2.getBlock().getType().equals(Material.AIR)) && !(x_plus_1.getBlock().getType().equals(Material.AIR)) )
		{
			portalblocks.add(pl.getBlock());
			portalblocks.add(x_minus_1.getBlock());
			Location y_plus_1 = new Location(Bukkit.getWorld(world), x, (y+1), z);
			Location y_plus_2 = new Location(Bukkit.getWorld(world), x, (y+2), z);
			if ( y_plus_1.getBlock().getType().equals(Material.AIR) && y_plus_2.getBlock().getType().equals(Material.AIR) )
			{
				portalblocks.add(y_plus_1.getBlock());
				portalblocks.add(y_plus_2.getBlock());
				Location y_plus_1_x_minus_1 = new Location(Bukkit.getWorld(world), (x-1), (y+1), z);
				Location y_plus_2_x_minus_1 = new Location(Bukkit.getWorld(world), (x-1), (y+2), z);
				if ( y_plus_1_x_minus_1.getBlock().getType().equals(Material.AIR) && y_plus_2_x_minus_1.getBlock().getType().equals(Material.AIR) )
				{
					portalblocks.add(y_plus_1_x_minus_1.getBlock());
					portalblocks.add(y_plus_2_x_minus_1.getBlock());
					multinether.getLogger().log(Level.INFO, "portal on x axis (1)");
				}
			}
		}
		else if ( !(x_minus_1.getBlock().getType().equals(Material.AIR)) && x_plus_1.getBlock().getType().equals(Material.AIR) && !(x_plus_2.getBlock().getType().equals(Material.AIR)) )
		{
			//FIXME: if-bedingung ändern
			portalblocks.add(pl.getBlock());
			portalblocks.add(x_plus_1.getBlock());
			Location y_plus_1 = new Location(Bukkit.getWorld(world), x, (y+1), z);
			Location y_plus_2 = new Location(Bukkit.getWorld(world), x, (y+2), z);
			if ( y_plus_1.getBlock().getType().equals(Material.AIR) && y_plus_2.getBlock().getType().equals(Material.AIR) )
			{
				portalblocks.add(y_plus_1.getBlock());
				portalblocks.add(y_plus_2.getBlock());
				Location y_plus_1_x_plus_1 = new Location(Bukkit.getWorld(world), (x+1), (y+1), z);
				Location y_plus_2_x_plus_1 = new Location(Bukkit.getWorld(world), (x+1), (y+2), z);
				if ( y_plus_1_x_plus_1.getBlock().getType().equals(Material.AIR) && y_plus_2_x_plus_1.getBlock().getType().equals(Material.AIR) )
				{
					portalblocks.add(y_plus_1_x_plus_1.getBlock());
					portalblocks.add(y_plus_2_x_plus_1.getBlock());
					multinether.getLogger().log(Level.INFO, "portal on x axis (2)");
				}
			}
		}
		else if ( z_minus_1.getBlock().getType().equals(Material.AIR) && !(z_minus_2.getBlock().getType().equals(Material.AIR)) && !(z_plus_1.getBlock().getType().equals(Material.AIR)) )
		{
			portalblocks.add(pl.getBlock());
			portalblocks.add(z_minus_1.getBlock());
			Location y_plus_1 = new Location(Bukkit.getWorld(world), x, (y+1), z);
			Location y_plus_2 = new Location(Bukkit.getWorld(world), x, (y+2), z);
			if ( y_plus_1.getBlock().getType().equals(Material.AIR) && y_plus_2.getBlock().getType().equals(Material.AIR) )
			{
				portalblocks.add(y_plus_1.getBlock());
				portalblocks.add(y_plus_2.getBlock());
				Location y_plus_1_z_minus_1 = new Location(Bukkit.getWorld(world), x, (y+1), (z-1));
				Location y_plus_2_z_minus_1 = new Location(Bukkit.getWorld(world), x, (y+2), (z-1));
				if ( y_plus_1_z_minus_1.getBlock().getType().equals(Material.AIR) && y_plus_2_z_minus_1.getBlock().getType().equals(Material.AIR) )
				{
					portalblocks.add(y_plus_1_z_minus_1.getBlock());
					portalblocks.add(y_plus_2_z_minus_1.getBlock());
					multinether.getLogger().log(Level.INFO, "portal on z axis (1)");
				}
			}
		}
		else if ( !(z_minus_1.getBlock().getType().equals(Material.AIR)) && z_plus_1.getBlock().getType().equals(Material.AIR) && !(z_plus_2.getBlock().getType().equals(Material.AIR)) )
		{
			portalblocks.add(pl.getBlock());
			portalblocks.add(z_plus_1.getBlock());
			Location y_plus_1 = new Location(Bukkit.getWorld(world), x, (y+1), z);
			Location y_plus_2 = new Location(Bukkit.getWorld(world), x, (y+2), z);
			if ( y_plus_1.getBlock().getType().equals(Material.AIR) && y_plus_2.getBlock().getType().equals(Material.AIR) )
			{
				portalblocks.add(y_plus_1.getBlock());
				portalblocks.add(y_plus_2.getBlock());
				Location y_plus_1_z_plus_1 = new Location(Bukkit.getWorld(world), x, (y+1), (z+1));
				Location y_plus_2_z_plus_1 = new Location(Bukkit.getWorld(world), x, (y+2), (z+1));
				if ( y_plus_1_z_plus_1.getBlock().getType().equals(Material.AIR) && y_plus_2_z_plus_1.getBlock().getType().equals(Material.AIR) )
				{
					portalblocks.add(y_plus_1_z_plus_1.getBlock());
					portalblocks.add(y_plus_2_z_plus_1.getBlock());
					multinether.getLogger().log(Level.INFO, "portal on z axis (2)");
				}
			}
		}
		
		multinether.getLogger().log(Level.INFO, "pb size = {0}", portalblocks.size());
		//pl.getBlock().setType(Material.PORTAL);
		
		for ( int i = 0; i < portalblocks.size(); i++ )
		{
			portalblocks.get(i).setType(Material.GLOWSTONE);
		}
		for ( int i = 0; i < portalblocks.size(); i++ )
		{
			portalblocks.get(i).setType(Material.PORTAL);
		}
		createReversePortal(pl, p.getLinkTo());
		
		
		//Block pb = pl.getBlock();
		//if ( pb.getType().equals(Material.AIR) )
		//{
		//	pb.setType(Material.PORTAL);
		//}
	}
}

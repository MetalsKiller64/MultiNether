package com.ymail.MetalsKiller64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
//import java.util.logging.Level;
//import org.bukkit.plugin.Plugin;

public class CmdExecutor implements CommandExecutor
{
    private MultiNether multinether;
    private List<String> command_list;
    private List<World> world_list;
    private List<String> link_list;
    private List<String> config_path_list;
	private List<Integer> portal_id_list;
	private List<Portal> portal_list;
	private String netherlink_path;
	private String portalcount_path;
    
    public CmdExecutor(MultiNether plugin)
    {
		this.multinether = plugin;
		this.command_list = multinether.command_list;
		this.world_list = new ArrayList<World>();
		this.link_list = new ArrayList<String>();
		this.portal_id_list = new ArrayList<Integer>();
		this.portal_list = new ArrayList<Portal>();
		this.config_path_list = new ArrayList<String>();
		this.netherlink_path = "NetherLinks.";
		this.portalcount_path = "PortalCount";
		
		if ( world_list.isEmpty() )
		{
			setWorldList();
		}
		
		if ( portal_id_list.isEmpty() )
		{
			portal_id_list = getPortalIDs();
		}
		
		multinether.getLogger().log(Level.INFO, "world_list: {0}", world_list.toString());
		/*
		for ( int i = 0; i < Bukkit.getWorld("World1").getLoadedChunks().length; i++ )
		{
			if ( Bukkit.getWorld("World1").getLoadedChunks()[i].getBlock(i, i, i).getType().equals(Material.PORTAL) );
		}
		*/
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
		setWorldList();
		setLinkList();
		if ( cmd.getName().equalsIgnoreCase("rtp") )
		{
			if ( sender instanceof Player )
			{
				//TODO: Fehlerbehandlung / Exceptionhandling in rtp-command einbauen
				Player player = (Player) sender;
				Location current_location = player.getLocation();
				if ( args.length == 0 )
				{
					sender.sendMessage(cmd.getDescription());
					//sender.sendMessage(cmd.getUsage());
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
						//TODO: abschließenden else-Block hinzufügen
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
			//TODO: Fehlerbehandlung/Exceptionhandling in netherlink-command einbauen
			sender.sendMessage("link_list: "+getLinks());
			//String links = netherrep.getConfig().getString("LinkList");
			//TODO: Befehlsausführung zum Verlinken von Welten (Eintrag in Config hinzufügen/löschen/bearbeiten/anzeigen)
			if ( args.length > 1 )
			{
				if ( args[0].equalsIgnoreCase("add") )
				{
					multinether.getLogger().log(Level.INFO, "exec add");
					//TODO: prüfen ob Config bereits vorhanden; LinkList prüfen
					//FIXME: Config-Werte in mehrere dynamische Paths schreiben anstatt in einen statischen
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
				//List<String> link_list = Arrays.asList(links.split(" "));
				
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
						//TODO: Ausnahmen in else-block hinzufügen
					}
				}
			}
		}
		else if ( cmd.getName().equals("cmdtest") )
		{
			for ( int i = 0; i < getPortalIDs().size(); i++ )
			{
				sender.sendMessage(""+getPortalIDs().get(i));
			}
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
		//List<World> world_list = Bukkit.getWorlds();
		for (int i = 0; i < this.world_list.size(); i++)
		{
			//netherrep.getLogger().log(Level.INFO, "{0}.equalsIgnoreCase({1})", new Object[]{value, this.world_list.get(i).getName()});
			if ( value.equalsIgnoreCase(this.world_list.get(i).getName()) )
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
    
    private void setWorldList()
    {
		this.world_list = Bukkit.getWorlds();
    }
    
    private void setLinkList()
    {
		setWorldList();
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
		for ( int i = 0; i < this.world_list.size(); i++ )
		{
			try
			{
				String link = multinether.getConfig().getString(this.world_list.get(i).getName());
				if ( !(link == null) )
				{
					link = this.world_list.get(i).getName()+":"+link;
					this.link_list.add(link);
					this.config_path_list.add(this.world_list.get(i).getName());
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
		setWorldList();
		multinether.reloadConfig();
		for ( int i = 0; i < world_list.size(); i++ )
		{
			try
			{
				String link = multinether.getConfig().get(netherlink_path+world_list.get(i).getName()).toString();
				links.add(world_list.get(i).getName());
			}
			catch ( NullPointerException npe )
			{
				
			}
		}
		return links;
	}
	
	public String getLinkWorld(String current_world)
	{
		setWorldList();
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
		Portal p = null;
		if ( sender instanceof Player )
		{
			Player player = (Player) sender;
			Location l = player.getLocation();
			Location location = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
			sender.sendMessage(""+getPortalIDs().size());
			Integer id;
			if ( getPortalIDs().isEmpty() )
			{
				id = 0;
			}
			else
			{
				id = getPortalIDs().size();
			}
			
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
	
	private Portal getPortal(Integer id)
	{
		Portal p = new Portal();
		if ( !(this.portal_list.isEmpty()) )
		{
			for ( int i = 0; i < this.portal_list.size(); i++ )
			{
				if ( this.portal_list.get(i).getID().equals(id) )
				{
					p = this.portal_list.get(i);
				}
			}
		}
		return p;
	}
	
	public Integer getPortalId(Location l)
	{
		//TODO: Fehlerbehandlung hinzufügen
		Integer portal_id = null;
		for ( int i = 0; i < this.portal_list.size(); i++ )
		{
			Portal current_portal = this.portal_list.get(i);
			if ( current_portal.getLocation().getX() == l.getX() && current_portal.getLocation().getY() == l.getY() && current_portal.getLocation().getZ() == l.getZ() )
			{
				portal_id = current_portal.getID();
			}
		}
		return portal_id;
	}
	
	public boolean savePortal(Portal p)
	{
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
			ConfigurationSection new_portal_section = multinether.getPortalConfig().createSection(""+id);
			ConfigurationSection portal_location = new_portal_section.createSection("location");
			portal_location.set("world", world);
			portal_location.set("x", x);
			portal_location.set("y", y);
			portal_location.set("z", z);
			//new_plugin_section.addDefault("location",l);
			//new_plugin_section.addDefault("linkto", link);
			new_portal_section.set("linkto", link);
			this.portal_id_list.add(p.getID());
			this.portal_list.add(p);
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
	
	public boolean removePortal(Integer id)
	{
		if ( this.portal_id_list.contains(id) )
		{
			multinether.getPortalConfig().set(""+id, null);
			multinether.savePortalConfig();
			this.portal_id_list.remove(id);
			Portal p = getPortal(id);
			this.portal_list.remove(p);
			return true;
		}
		return false;
	}
	
	public Portal getNearPortal(Location l)
	{
		//TODO: Portale in der Nähe der Spieler-Location suchen (Suche nach Material.PORTAL)
		Portal near_portal = null;
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
		
		int min_x = player_x - 25;
		int max_x = player_x + 25;
		
		int min_y = player_y - 25;
		int max_y = player_y + 25;
		
		int min_z = player_z - 25;
		int max_z = player_z + 25;
		
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
		
		List<Location> portal_locations = new ArrayList<Location>();
		if ( !(this.portal_list.isEmpty()) )
		{
			for ( int h = 0; h < this.portal_list.size(); h++ )
			{
				Portal current_portal = this.portal_list.get(h);
				Location current_portal_location = new Location(current_portal.getLocation().getWorld(), current_portal.getX(), current_portal.getY(), current_portal.getZ());
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
						near_portal = getPortal(p_id);
					}
				}
			}
		}
		
		return near_portal;
		
	}
	
	/**
	 * Liest die IDs aller gespeicherten Portale aus der PortalConfig.
	 * @return Gibt eine Liste von allen gefundenen IDs zurück.
	 */
	private List<Integer> getPortalIDs()
	{
		List<Integer> portal_ids = new ArrayList<Integer>();
		multinether.reloadConfig();
		FileConfiguration portal_conf = multinether.getPortalConfig();
		Set<String> keys = portal_conf.getKeys(true);
		int portalcount = multinether.getConfig().getInt(portalcount_path);
		for ( int i = 0; i < portalcount; i++ )
		{
			multinether.getLogger().log(Level.INFO, "i = "+i);
			Object[] key_list = keys.toArray();
			//netherrep.getLogger().log(Level.INFO, "Key{0}: {1}", new Object[]{i, key_list[i]});
			try
			{
				//Integer current_id = Integer.parseInt(key_list[i].toString());
				Integer current_id = Integer.parseInt(multinether.getPortalConfig().get(""+i).toString());
				portal_ids.add(current_id);
				multinether.getLogger().log(Level.INFO, "{0}", current_id);
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
		}
		return portal_ids;
	}
	
	public List<Portal> getAllPortals()
	{
		multinether.reloadConfig();
		List<Portal> portals = new ArrayList<Portal>();
		List<Integer> ids = getPortalIDs();
		for ( int i = 0; i < ids.size(); i++ )
		{
			Integer current_id = ids.get(i);
			ConfigurationSection portal_section = multinether.getPortalConfig().getConfigurationSection(current_id.toString());
			ConfigurationSection location_section = portal_section.getConfigurationSection("location");
			Integer p_x = portal_section.getInt("x");
			Integer p_y = portal_section.getInt("y");
			Integer p_z = portal_section.getInt("z");
			String p_world = portal_section.getString("world");
			String p_link = portal_section.getString("linkto");
			Portal current_portal = new Portal();
			current_portal.setID(i);
			current_portal.setWorld(p_world);
			current_portal.setX(p_x);
			current_portal.setY(p_y);
			current_portal.setZ(p_z);
			current_portal.setLinkTo(p_link);
			portals.add(current_portal);
		}
		return portals;
	}
	
	
}

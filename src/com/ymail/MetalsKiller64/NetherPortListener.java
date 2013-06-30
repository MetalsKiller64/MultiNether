package com.ymail.MetalsKiller64;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class NetherPortListener implements Listener
{
	private MultiNether multinether;
	private CmdExecutor cmd;
	public NetherPortListener(MultiNether plugin)
	{
		this.multinether = plugin;
		multinether.getServer().getPluginManager().registerEvents(this, plugin);
		this.cmd = plugin.cmd;
	}
	
	@EventHandler
	public void onPlayerPortalEnter(PlayerPortalEvent e)
	{
		//FIXME: Eingangsportal wird bei Eintritt geschlossen
		Player player = e.getPlayer();
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
		
		Portal p = cmd.getNearestPortal(player_location);
		Location destination_location = new Location(Bukkit.getWorld(p.getLinkTo()), p.getX(), p.getY(), p.getZ());
		String path = p.getID()+".linktoid";
		String link_portal_id = multinether.getPortalConfig().getString(path);
		multinether.getLogger().log(Level.INFO, path+": "+link_portal_id);
		Portal dest_portal = cmd.getReversePortal(Integer.parseInt(link_portal_id));
		multinether.getLogger().log(Level.INFO, dest_portal.getY().toString());
		destination_location.setY(dest_portal.getY());
		
		String linkworld = multinether.getConfig().getString("NetherLinks."+world.getName());
		player.sendMessage(linkworld);
		multinether.getLogger().log(Level.INFO, "teleport to coords: x={0} y={1} z={2}", new Object[]{p.getX(), p.getY(), p.getZ()});
		player.teleport(destination_location);
	}
}

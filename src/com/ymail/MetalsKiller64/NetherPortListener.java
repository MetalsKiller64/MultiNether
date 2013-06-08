package com.ymail.MetalsKiller64;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class NetherPortListener implements Listener
{
	private MultiNether multinether;
	public NetherPortListener(MultiNether plugin)
	{
		this.multinether = plugin;
		multinether.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerPortalEnter(PlayerPortalEvent e)
	{
		Player player = e.getPlayer();
		player.sendMessage("PlayerPortalEvent");
		int x = player.getLocation().getBlock().getX();
		int y = player.getLocation().getBlock().getZ();
		int z = player.getLocation().getBlock().getY();
		World world = player.getWorld();
		
		Location ploc = new Location(world, x, y, z);
		String linkworld = multinether.getConfig().getString("NetherLinks."+world.getName());
		player.sendMessage(linkworld);
		player.teleport(new Location(Bukkit.getWorld(linkworld), ploc.getX(), ploc.getY(), ploc.getZ()));
	}
}

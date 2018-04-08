package me.ccrama.Trails;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class WorldGuardHook
{
	private WorldGuardPlugin wg;
	
	public WorldGuardHook(Main plugin){
		this.wg = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
	}
	
	public boolean canBuild(Player player, Location location){
		if(wg.canBuild(player, location)){
			return true;
		}
		return false;
	}
}
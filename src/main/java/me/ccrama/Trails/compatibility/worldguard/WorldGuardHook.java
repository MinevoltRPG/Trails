package me.ccrama.Trails.compatibility.worldguard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardHook
{
	private WorldGuardPlugin wg;
	
	public WorldGuardHook(){
		this.wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		FlagRegistry registry = this.wg.getFlagRegistry();
	    try {
	        // register our flag with the registry
	//        registry.register(MY_CUSTOM_FLAG);
	    } catch (FlagConflictException e) {
	        // some other plugin registered a flag by the same name already.
	        // you may want to re-register with a different name, but this
	        // could cause issues with saved flags in region files. it's better
	        // to print a message to let the server admin know of the conflict
	    }
	}
	
	public boolean canBuild(Player player, Location location){
		if(wg.canBuild(player, location)){
			return true;
		}
		return false;
	}
}
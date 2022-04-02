package me.ccrama.Trails.compatibility.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardHook
{
	private final WorldGuardPlugin wg;
	
	public WorldGuardHook(){
		this.wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		StateFlag TRAILS_FLAG;
		try {
	        // register our flag with the registry
			// create a flag with the name "my-custom-flag", defaulting to true
			StateFlag flag = new StateFlag("trails-flag", true);
			registry.register(flag);
			TRAILS_FLAG = flag; // only set our field if there was no error
		} catch (FlagConflictException e) {
			// some other plugin registered a flag by the same name already.
			// you can use the existing flag, but this may cause conflicts - be sure to check type
			Flag<?> existing = registry.get("my-custom-flag");
			if (existing instanceof StateFlag) {
				TRAILS_FLAG = (StateFlag) existing;
			} else {
				// types don't match - this is bad news! some other plugin conflicts with you
				// hopefully this never actually happens
			}
		}
	}

	public boolean canBuild(Player player, Location location) {
		//return wg.canBuild(player, location);
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
		if (!hasBypass(player, location)) {
			return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
		}  {
			return true;
		}

	}

	// technically the bypass check inst needed but if it doesnt function properly it can be removed with no issues
	public boolean hasBypass(Player player, Location location) {
		final World world = location.getWorld();
		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(getLocalPlayer(player), (com.sk89q.worldedit.world.World) world);
	}

	private LocalPlayer getLocalPlayer(Player player) {
		return player != null ? WorldGuardPlugin.inst().wrapPlayer(player) : null;
	}
}
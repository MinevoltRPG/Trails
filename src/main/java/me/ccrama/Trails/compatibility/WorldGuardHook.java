package me.ccrama.Trails.compatibility;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import me.ccrama.Trails.Trails;

public class WorldGuardHook
{
	private StateFlag TRAILS_FLAG;
	private StateFlag TRAILS_DECAY_FLAG;
	private Trails plugin;
	
	public WorldGuardHook(Trails plugin){
		this.plugin = plugin;
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
	        // register our flag with the registry
			// create a flag with the name "my-custom-flag", defaulting to true
			StateFlag flag = new StateFlag("trails-flag", true);
			registry.register(flag);
			TRAILS_FLAG = flag; // only set our field if there was no error
		} catch (FlagConflictException e) {
			// some other plugin registered a flag by the same name already.
			// you can use the existing flag, but this may cause conflicts - be sure to check type
			Flag<?> existing = registry.get("trails-flag");
			if (existing instanceof StateFlag) {
				TRAILS_FLAG = (StateFlag) existing;
			} else {
				// types don't match - this is bad news! some other plugin conflicts with you
				// hopefully this never actually happens
			}
		}
		catch (java.lang.IllegalStateException ex) {
			Flag<?> existing = registry.get("trails-flag");
			if (existing instanceof StateFlag) {
				TRAILS_FLAG = (StateFlag) existing;
			} else {
				// types don't match - this is bad news! some other plugin conflicts with you
				// hopefully this never actually happens
			}
		}



		if(plugin.getConfigManager().wgDecayFlag) {
			// Register trail decay flag
			try {
				// register our flag with the registry
				// create a flag with the name "my-custom-flag", defaulting to true
				StateFlag flag = new StateFlag("trails-decay-flag", true);
				registry.register(flag);
				TRAILS_DECAY_FLAG = flag; // only set our field if there was no error
			} catch (FlagConflictException e) {
				// some other plugin registered a flag by the same name already.
				// you can use the existing flag, but this may cause conflicts - be sure to check type
				Flag<?> existing = registry.get("trails-decay-flag");
				if (existing instanceof StateFlag) {
					TRAILS_FLAG = (StateFlag) existing;
				} else {
					// types don't match - this is bad news! some other plugin conflicts with you
					// hopefully this never actually happens
				}
			} catch (java.lang.IllegalStateException ex) {
				Flag<?> existing = registry.get("trails-decay-flag");
				if (existing instanceof StateFlag) {
					TRAILS_FLAG = (StateFlag) existing;
				} else {
					// types don't match - this is bad news! some other plugin conflicts with you
					// hopefully this never actually happens
				}
			}
		}
		
	}

	public boolean canCreateTrails(Player player, Location location) {
		//return wg.canBuild(player, location);
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
		if (hasBypass(player, location) && plugin.getConfigManager().checkBypass) {
			return true;
		}else  {
			return query.testState(loc, getLocalPlayer(player), TRAILS_FLAG);
		}
	}

	// technically the bypass check inst needed but if it doesnt function properly it can be removed with no issues
	public boolean hasBypass(Player player, Location location) {
		World world = location.getWorld();
		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(getLocalPlayer(player), BukkitAdapter.adapt(world));
	}

	public boolean canDecay(Location location) {
		if(!plugin.getConfigManager().wgDecayFlag) return true;
		//return wg.canBuild(player, location);
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
		ApplicableRegionSet set = query.getApplicableRegions(loc);
		return set.testState( null, TRAILS_DECAY_FLAG);
	}

	private LocalPlayer getLocalPlayer(Player player) {
		return player != null ? WorldGuardPlugin.inst().wrapPlayer(player) : null;
	}
}
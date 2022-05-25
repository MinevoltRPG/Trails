package me.ccrama.Trails.compatibility;

import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ccrama.Trails.Trails;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

public class GriefPreventionHook {
	
	private GriefPrevention prevent;
	private Trails plugin;
	
	public GriefPreventionHook(Trails plugin) {
		this.plugin = plugin;
		prevent = GriefPrevention.instance;
	}
	
	public boolean canMakeTrails(Player p, Location location) {
		PlayerData playerData = prevent.dataStore.getPlayerData(p.getUniqueId());
		Claim claim = prevent.dataStore.getClaimAt(location, false, null);
		if (claim != null)
        {
		    if(playerData!=null) {
			    playerData.lastClaim = claim;
			}
			Supplier<String> noAccessReason = claim.checkPermission(p, ClaimPermission.Build, null);
	        if (noAccessReason != null)
	            return false;
	        else
	         	return true;
        }else
          	return plugin.getConfigManager().gpPathsWilderness;
	}
}
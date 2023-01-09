package me.ccrama.Trails.compatibility;

import java.util.function.Supplier;

import org.bukkit.Bukkit;
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
		if(this.prevent == null){
			if(GriefPrevention.instance == null){
				plugin.getLogger().severe("Grief prevention instance is NULL?!");
				plugin.getLogger().info("Is GP enabled?: "+Bukkit.getPluginManager().isPluginEnabled("GriefPrevention"));
				return true;
			} else this.prevent = GriefPrevention.instance;
		}
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
          	return Trails.config.gpPathsWilderness;
	}
}
package me.ccrama.Trails.compatibility;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.angeschossen.lands.api.flags.types.RoleFlag;
import me.angeschossen.lands.api.flags.types.RoleFlag.Category;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import me.ccrama.Trails.Trails;

public class LandsAPIHook 
{
	private final LandsIntegration landsIntegration;
	private final RoleFlag roleflag;
	private Trails plugin;

	public LandsAPIHook(Trails plugin) {
		this.plugin = plugin;
		this.landsIntegration = new LandsIntegration(plugin);
		//seems like should use RoleFlag instead of LandFlag
		this.roleflag = (RoleFlag) new RoleFlag(plugin, Category.ACTION, "ALLOW_TRAILS", plugin.getConfigManager().applyInSubAreas, plugin.getConfigManager().landsPathsWilderness);
		roleflag.setDisplayName(plugin.getLanguage().displayName);
		roleflag.setIcon(new ItemStack(plugin.getLanguage().material));
		roleflag.setDescription(plugin.getLanguage().description);		
		roleflag.setDisplay(true);
		Bukkit.getServer().getConsoleSender().sendMessage(plugin.getCommands().getFormattedMessage(Bukkit.getConsoleSender().getName(),
    			(plugin.getLanguage().pluginPrefix + ChatColor.GREEN + " hooked into " + ChatColor.YELLOW + "Lands!")));
		Bukkit.getServer().getConsoleSender().sendMessage(plugin.getCommands().getFormattedMessage(Bukkit.getConsoleSender().getName(),
    			(plugin.getLanguage().pluginPrefix + ChatColor.YELLOW + " ALLOW_TRAILS " + ChatColor.GREEN + "Role Flag registered!")));
		landsIntegration.registerFlag(roleflag);
	}

	public LandsIntegration getLandsIntegration() {
		return landsIntegration;
	}
	
	public Area getArea(Location location) {
		return landsIntegration.getAreaByLoc(location);
	}
	
	public boolean isClaimed(Location location) {
		return landsIntegration.isClaimed(location);
	}
	
	public LandPlayer getLandPlayer(UUID id) {
		return landsIntegration.getLandPlayer(id);
	}
	
	private Land getLand(Location location) {
		return landsIntegration.getLand(location);
	}
	
	public boolean isClaimedbyCurrentPlayer(Location location, Player player) {
		if(!isWilderness(location)) {
			Land land = getLand(location);
			if(land.getOwnerUID()==player.getUniqueId())
				return true;
		}
		return false;
	}
	
	public boolean isWilderness(Location location) {
		if(getLand(location) == null)
			return true;
		return false;
	}
	
	//looks like correct method
	public boolean hasTrailsFlag(Player player, Location location) {
		if(getArea(location) != null)
		    return getArea(location).hasFlag(player.getUniqueId(), roleflag);
		else if(isWilderness(location) && plugin.getConfigManager().landsPathsWilderness)
            return true;
		else
			return false;

	}

}
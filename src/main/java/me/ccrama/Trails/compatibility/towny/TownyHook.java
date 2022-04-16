package me.ccrama.Trails.compatibility.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TownyHook
{

	public TownyHook() {
	}
	
	public boolean hasTownPermission(Player p) {
		return p.hasPermission("trails.towny.town");
	}
	
	public boolean hasNationPermission(Player p) {
		return p.hasPermission("trails.towny.nation");
	}
	
	public boolean isWilderness(Location loc) {
		//return TownyUniverse.isWilderness(loc.getBlock());
		return TownyAPI.getInstance().isWilderness(loc);
	}
	
	public boolean isWilderness(Player p) {
		return isWilderness(p.getLocation().subtract(0.0D, 1.0D, 0.0D));
	}
	
	public boolean isWilderness(Block block) {
		TownyAPI.getInstance().isWilderness(block);
		return false;
	}
	
	public boolean isInHomeTown(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = TownyUniverse.getInstance().getResident(p.getUniqueId());
			block = WorldCoord.parseWorldCoord(p).getTownBlock();
			if(block.hasTown()) {
				if(resident.getTown() == block.getTown()) {
					return true;
				}
			}
		} catch (NotRegisteredException e) {
			p.sendMessage("[Trails] Are you sure you are in a town/nation?");
		}		
		return false;
	}
	
	public boolean isInHomeNation(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = TownyUniverse.getInstance().getResident(p.getName());
			block = WorldCoord.parseWorldCoord(p).getTownBlock();
			if(block.hasTown()) {
				if(block.getTown().hasNation()) {
					if(resident.getTown().getNation() == block.getTown().getNation()) {
						return true;
					}
				}				
			}
		} catch (NotRegisteredException e) {
			p.sendMessage("[Trails] Are you sure you are in a town/nation?");
		}		
		return false;
	}

}

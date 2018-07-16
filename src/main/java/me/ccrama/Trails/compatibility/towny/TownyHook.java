package me.ccrama.Trails.compatibility.towny;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class TownyHook
{
	
	public TownyHook() {
	}
	
	public boolean hasTownPermission(Player p) {
		if(p.hasPermission("trails.towny.town")) {
			return true;
		}
		return false;
	}
	
	public boolean hasNationPermission(Player p) {
		if(p.hasPermission("trails.towny.nation")) {
			return true;
		}
		return false;
	}
	
	public boolean isWilderness(Location loc) {
		if(TownyUniverse.isWilderness(loc.getBlock())) {
			return true;
		}
		return false;
	}
	
	public boolean isWilderness(Player p) {
		return isWilderness(p.getLocation().subtract(0.0D, 1.0D, 0.0D));
	}
	
	public boolean isWilderness(Block block) {
		return TownyUniverse.isWilderness(block);		
	}
	
	public boolean isInHomeTown(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = TownyUniverse.getDataSource().getResident(p.getName());
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
			resident = TownyUniverse.getDataSource().getResident(p.getName());
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

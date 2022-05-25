package me.ccrama.Trails.compatibility;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;

import me.ccrama.Trails.Trails;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TownyHook
{
	private boolean isPathsInWilderness = true;
    private boolean isTownyPathsPerm = true;
	
	public TownyHook(Trails main) {
		this.isPathsInWilderness = main.getConfigManager().townyPathsWilderness;
        this.isTownyPathsPerm =  main.getConfigManager().townyPathsPerm;
	}
	
	public boolean hasTownPermission(Player p) {
		return p.hasPermission("trails.towny.town");
	}
	
	public boolean hasNationPermission(Player p) {
		return p.hasPermission("trails.towny.nation");
	}
	
	public boolean isWilderness(Location loc) {
		//return TownyUniverse.isWilderness(loc.getBlock());
		return isWilderness(loc.getBlock());
	}
	
	public boolean isWilderness(Player p) {
		return isWilderness(p.getLocation().getBlock());
	}
	
	public boolean isWilderness(Block block) {
		return TownyAPI.getInstance().isWilderness(block);
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
		}		
		return false;
	}
	
	public boolean isInOtherTown(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = TownyUniverse.getInstance().getResident(p.getUniqueId());
			block = WorldCoord.parseWorldCoord(p).getTownBlock();
			if(block.hasTown()) {
				if(resident.getTown() != block.getTown()) {
					return true;
				}
			}
		} catch (NotRegisteredException e) {
		}		
		return false;
	}
	
	public boolean isInOtherNation(Player p) {
		Resident resident;
		TownBlock block;
		try {
			resident = TownyUniverse.getInstance().getResident(p.getName());
			block = WorldCoord.parseWorldCoord(p).getTownBlock();
			if(block.hasTown()) {
				if(block.getTown().hasNation()) {
					if(resident.getTown().getNation() != block.getTown().getNation()) {
						return true;
					}
				}				
			}
		} catch (NotRegisteredException e) {
		}		
		return false;
	}
	
	public boolean isPathsInWilderness() {
		return this.isPathsInWilderness;
	}

	public boolean isTownyPathsPerms() {
		return this.isTownyPathsPerm;
	}
}

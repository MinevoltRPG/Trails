package me.ccrama.Trails.compatibility;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RedProtectHook {

    public RedProtectHook() {
    }

    public Region getRegion(Location loc){
        return RedProtect.get().getAPI().getRegion(loc);
    }

    public boolean canBuild(Player p, Location location){
        Region region = getRegion(location);
        if(region == null) return true;
        return region.canBuild(p);
    }

}

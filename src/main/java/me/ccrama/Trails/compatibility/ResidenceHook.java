package me.ccrama.Trails.compatibility;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class ResidenceHook {

    public ResidenceHook(){
    }

    public boolean canCreateTrails(Location location, Player player){
        ClaimedResidence res = ResidenceApi.getResidenceManager().getByLoc(location);
        if(res==null)return true;
        ResidencePermissions perms = res.getPermissions();
        //System.out.println(perms.playerHas(player, Flags.build, true));
        return perms.playerHas(player, Flags.build, true);
    }

}

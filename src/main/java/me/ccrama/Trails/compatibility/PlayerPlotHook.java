package me.ccrama.Trails.compatibility;

import de.whitescan.playerplot.PlayerPlotAPI;
import me.ccrama.Trails.Trails;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class PlayerPlotHook {

    private Trails plugin;
    private PlayerPlotAPI playerPlotAPI;

    public PlayerPlotHook(Trails plugin) {
        this.plugin = plugin;
        playerPlotAPI = PlayerPlotAPI.getInstance();
    }

    public boolean canMakeTrails(Player p, Location location){
        return playerPlotAPI.hasAccessAt(p, location);
    }

}

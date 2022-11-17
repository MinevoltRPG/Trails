package me.ccrama.Trails.listeners;

import me.ccrama.Trails.Trails;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    Plugin main;

    public PlayerLeaveListener(Trails plugin) {
        this.main = plugin;
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        MoveEventListener.Booster booster = MoveEventListener.speedBoostedPlayers.get(uuid);
        if(booster != null) MoveEventListener.removeBoostedPlayer(uuid, true);
        else if(!event.getPlayer().hasPermission("trails.bypass") && event.getPlayer().getWalkSpeed() != 0.2F) event.getPlayer().setWalkSpeed(0.2F);
    }
}

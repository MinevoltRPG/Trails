package me.ccrama.Trails.listeners;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.Links;
import me.ccrama.Trails.objects.TrailBlock;
import me.ccrama.Trails.objects.WrappedLocation;
import me.drkmatr1984.customevents.moveEvents.SignificantPlayerMoveEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class MoveEventListener implements Listener {

    private final Trails main;

    private final Links links;

    public MoveEventListener(Trails plugin) {
        this.main = plugin;
        this.links = this.main.getConfigManager().getLinks();
    }

    @EventHandler
    public void walk(SignificantPlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (main.getToggles().isDisabled(p)) {
        	p.sendMessage("Trails is toggled off");
            return;
        }
        // Check towny conditions
        if (main.getTownyHook() != null) {
            if (main.getTownyHook().isWilderness(p)){
            	if(!main.getTownyHook().isPathsInWilderness()) {
                    return;
                }
            }
            
            if (main.getTownyHook().isTownyPathsPerms()) {
                if (main.getTownyHook().isInHomeNation(p) && !main.getTownyHook().hasNationPermission(p) && !main.getTownyHook().isInHomeTown(p)) {
                    return;
                }
                if (main.getTownyHook().isInHomeTown(p) && !main.getTownyHook().hasTownPermission(p)) {
                    return;
                }
            }else {
            	if (main.getTownyHook().isInOtherNation(p)) {
                    return;
                }
                if (main.getTownyHook().isInOtherTown(p)) {
                    return;
                }
            }
        }
        // Check worldguard conditions
        if (main.getWorldGuardHook() != null) {
        		if(!main.getWorldGuardHook().canCreateTrails(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
        			if(!main.wgPlayers.contains(p.getUniqueId())) {
        				p.sendMessage("You can't create trails here!");
        				main.wgPlayers.add(p.getUniqueId());
        				Bukkit.getScheduler().runTaskLater(main, () -> delayWGMessage(p.getUniqueId()),20*3);           			
        			}
        			
        	        return;
        	    }
        }           
        makePath(e.getFrom().subtract(0.0D, 1.0D, 0.0D).getBlock());
        //log blocks
    }

    private void makePath(Block block) {
        Material type = block.getType();
        for (Link link : this.links) {
            if (link.getMat() == type) {
                double foo = Math.random() * 100.0D;
                if (foo <= (double) link.chanceOccurance()) {
                    for (TrailBlock b : this.main.getBlockDataManager().getTrailBlocks()) {
                        if (b.getWrappedLocation().isLocation(block.getLocation())) {
                            int walked = b.getWalks();
                            if (walked >= link.decayNumber()) {
                                this.main.getBlockDataManager().removeTrailBlock(b);
                                try {
                                    this.changeNext(block);
                                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } else {
                                this.main.getBlockDataManager().removeTrailBlock(b);
                                this.main.getBlockDataManager().addTrailBlock(new TrailBlock(b.getWrappedLocation(), (walked + 1)));
                                return;
                            }
                        }
                    }
                    this.main.getBlockDataManager().walkedOver.add(new TrailBlock(new WrappedLocation(block.getLocation()), 1));
                }
            }
        }
    }

    private void changeNext(Block block) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Material type = block.getType();
        for (Link link : links) {
            if (link.getMat() == type) {
                if (link.getNext() != null) {
                    Material nextMat = link.getNext().getMat();
                    block.setType(nextMat);
                    block.getState().update(true);
                }
            }
        }
    }
    
    private void delayWGMessage(UUID id) {
    	if(main.wgPlayers.contains(id)){
    		main.wgPlayers.remove(id);
		}
    }

}
	
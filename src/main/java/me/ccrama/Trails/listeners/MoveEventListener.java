package me.ccrama.Trails.listeners;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.TrailBlock;
import me.ccrama.Trails.objects.WrappedLocation;
import me.drkmatr1984.customevents.moveEvents.SignificantPlayerMoveEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.diddiz.LogBlock.Actor;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class MoveEventListener implements Listener {

    private final Trails main;

    public MoveEventListener(Trails plugin) {
        this.main = plugin;
    }

    @EventHandler
    public void walk(SignificantPlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (main.getToggles().isDisabled(p)) {
            return;
        }
        // Check towny conditions
        if (main.getTownyHook() != null) {
            if (main.getTownyHook().isWilderness(p)){
            	if(!main.getTownyHook().isPathsInWilderness()) {
            		if(main.getConfigManager().sendDenyMessage)
            		    sendDelayedMessage(p);
                    return;
                }
            }
            
            if (main.getTownyHook().isTownyPathsPerms()) {
                if (main.getTownyHook().isInHomeNation(p) && !main.getTownyHook().hasNationPermission(p) && !main.getTownyHook().isInHomeTown(p)) {
                	if(main.getConfigManager().sendDenyMessage)
            		    sendDelayedMessage(p);
                    return;
                }
                if (main.getTownyHook().isInHomeTown(p) && !main.getTownyHook().hasTownPermission(p)) {
                	if(main.getConfigManager().sendDenyMessage)
            		    sendDelayedMessage(p);
                    return;
                }
            }else {
            	if (main.getTownyHook().isInOtherNation(p)) {
            		if(main.getConfigManager().sendDenyMessage)
            		    sendDelayedMessage(p);
                    return;
                }
                if (main.getTownyHook().isInOtherTown(p)) {
                	if(main.getConfigManager().sendDenyMessage)
            		    sendDelayedMessage(p);
                    return;
                }
            }
        }
        // Check Lands conditions
        if(main.getLandsHook() != null) {
        	if(!main.getLandsHook().hasTrailsFlag(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
        		if(main.getConfigManager().sendDenyMessage)
        		    sendDelayedMessage(p);    			
    	        return;
        	}
        }
        // Check GriefPrevention conditions
        if(main.getGpHook() != null) {
        	 if(!main.getGpHook().canMakeTrails(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
        		 if(main.getConfigManager().sendDenyMessage)
         		    sendDelayedMessage(p);     			
         	    return;
             }
        }      
        // Check worldguard conditions
        if (main.getWorldGuardHook() != null) {
        	if(!main.getWorldGuardHook().canCreateTrails(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
        		if(main.getConfigManager().sendDenyMessage)
        		    sendDelayedMessage(p);     			
        	    return;
        	}
        }  
        makePath(p, e.getFrom().subtract(0.0D, 1.0D, 0.0D).getBlock());
    }

    private void makePath(Player p, Block block) {
        for (Link link : this.main.getConfigManager().getLinksConfig().getLinks()) {
            if (link.getMat() == block.getType()) {
                double foo = Math.random() * 100.0D;
                if (foo <= (double) link.chanceOccurance()) {
                    for (TrailBlock b : this.main.getBlockDataManager().getTrailBlocks()) {
                        if (b.getWrappedLocation().isLocation(block.getLocation())) {
                            int walked = b.getWalks();
                            if (walked >= link.decayNumber()) {
                                this.main.getBlockDataManager().removeTrailBlock(b);
                                try {
                                    this.changeNext(p, block);
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

    private void changeNext(Player p, Block block) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Material type = block.getType();
        BlockState state = block.getState();
        BlockData data = block.getBlockData();
        for (Link link : this.main.getConfigManager().getLinksConfig().getLinks()) {
            if (link.getMat() == type) {
                if (link.getNext() != null) {
                    Material nextMat = link.getNext().getMat();
                    block.setType(nextMat);
                    block.getState().update(true);
                    //log block changes in LogBlock and CoreProtect
                    if(main.getLbHook()!=null && main.getConfigManager().logBlock) {
                    	main.getLbHook().getLBConsumer().queueBlockReplace(new Actor(p.getName()), state, block.getState());
                    }
                    if(main.getCpHook()!=null && main.getConfigManager().coreProtect) {
                    	main.getCpHook().getAPI().logRemoval(p.getName(), block.getLocation(), type, data);
                    	main.getCpHook().getAPI().logPlacement(p.getName(), block.getLocation(), nextMat, block.getBlockData());
                    }
                }
            }
        }
    }
    
    private void sendDelayedMessage(Player p) {
    	if(!main.messagePlayers.contains(p.getUniqueId())) {
			p.sendMessage(main.getCommands().getFormattedMessage(p.getName(), main.getLanguage().cantCreateTrails));
			main.messagePlayers.add(p.getUniqueId());
			Bukkit.getScheduler().runTaskLater(main, () -> delayWGMessage(p.getUniqueId()),20*main.getConfigManager().messageInterval);           			
		}
    }
    
    private void delayWGMessage(UUID id) {
    	if(main.messagePlayers.contains(id)){
    		main.messagePlayers.remove(id);
		}
    }

}
	
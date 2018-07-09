package me.ccrama.Trails.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.Links;
import me.ccrama.Trails.objects.TrailBlock;
import me.ccrama.Trails.objects.WrappedLocation;
import me.drkmatr1984.customevents.moveEvents.SignificantPlayerMoveEvent;

public class MoveEventListener implements Listener {
	
	private Trails main;
	
	private Links links;
	
	public MoveEventListener(Trails plugin) {
		this.main = plugin;
	    this.links = this.main.getLinksDataManager().getLinks();
	}
	
	@EventHandler
	public void walk(SignificantPlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(main.off.contains(p.getName())) {
			return;
		}
		if(main.usingTowny && !TownyUniverse.isWilderness(p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock()))
			return;
		if(main.hook!=null && !main.hook.canBuild(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D)))	
			return;          		
		makePath(e.getFrom().subtract(0.0D, 1.0D, 0.0D).getBlock());
	}
	
	@SuppressWarnings("deprecation")
	private void makePath(Block block) {
		Material type = block.getType();
	    byte dataValue = block.getData();
	    for(Link link : this.links){
	    	if(link.getMat() == type && link.getDataValue() == dataValue){
	    		double foo = Math.random() * 100.0D;
	            if(foo <= (double)link.chanceOccurance()) {
	            	for(TrailBlock b : this.main.getBlockDataManager().getTrailBlocks()){
	            		if(b.getWrappedLocation().isLocation(block.getLocation())){
	            			int walked = b.getWalks();
	           			 	if(walked >= link.decayNumber()){
	           			 		this.main.getBlockDataManager().removeTrailBlock(b);
	           			 		this.changeNext(block);
	           			 		return;
	           			 	}else{
	           			 		this.main.getBlockDataManager().removeTrailBlock(b);
	           			 		this.main.getBlockDataManager().addTrailBlock(new TrailBlock(b.getWrappedLocation(),(walked+1)));
	           			 		return;
	           			 	}
	           		 	}
	           	 	}
	            	this.main.getBlockDataManager().walkedOver.add(new TrailBlock(new WrappedLocation(block.getLocation()), 1));
	            }
	      	}
	    }  	
	}

	@SuppressWarnings("deprecation")
	private void changeNext(Block block) {
		Material type = block.getType();
		byte dataValue = block.getData();
	    for(Link link : links){
	       	if(link.getMat() == type && link.getDataValue() == dataValue){
	       		if(link.getNext() != null) {
	       	         Material nextMat = link.getNext().getMat();
	       	         byte dValue = link.getNext().getDataValue();
	       	         block.setType(nextMat);
	       	         block.setData(dValue);
	       	         block.getState().update(true);
	       		}
	       	}
	    }     
	}
	
}
	
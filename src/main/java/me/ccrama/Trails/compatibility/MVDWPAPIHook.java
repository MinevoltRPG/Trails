package me.ccrama.Trails.compatibility;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.ccrama.Trails.Trails;

public class MVDWPAPIHook{
	
	private Trails plugin;
	
	public MVDWPAPIHook(Trails plugin) {
		this.plugin = plugin;
	}
	
	public boolean trailsToggledOn(){
		PlaceholderReplacer trailsToggled = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
					if(event.getPlayer()!=null){
						Player p = event.getPlayer();
						if(plugin!=null){
							if(!plugin.getToggles().isDisabled(p))
								return "true";
							else
								return "false";
						}				
					}
					return "";		
				}
		};
		return PlaceholderAPI.registerPlaceholder(plugin, "trails_toggled_on", trailsToggled);
	}	
	
}
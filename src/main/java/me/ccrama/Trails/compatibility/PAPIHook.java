package me.ccrama.Trails.compatibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import me.ccrama.Trails.Trails;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIHook extends PlaceholderExpansion {
	
	private static Trails plugin;
	
	public PAPIHook(JavaPlugin trails) {
		plugin = (Trails) trails;
	}
	
    @Override
    public boolean persist(){
        return true;
    }  

   @Override
   public boolean canRegister(){
       return true;
   }

   @Override
   public String getAuthor(){
       return plugin.getDescription().getAuthors().toString();
   }

	@Override
	public String getIdentifier(){
		return "trails";
	}

	@Override
	public String getVersion(){
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier){
		if(player != null){
			if(identifier.equals("toggled_on")){
	        	if(!plugin.getToggles().isDisabled(player))
	        		return "true";
	        	else
	        		return "false";
			}
		}		
		return "";
	}
}

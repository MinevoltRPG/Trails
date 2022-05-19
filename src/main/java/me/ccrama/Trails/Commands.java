package me.ccrama.Trails;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor
{
    private Trails plugin;
	
    public Commands(Trails plugin) {
    	this.plugin = plugin;
    }
    
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		try{
			if (command.getName().equalsIgnoreCase("trails") || command.getName().equalsIgnoreCase("paths")) {
				if ((args.length == 0) || (args.equals(null)))
			    {   
					if (sender instanceof Player) {
						if(sender.hasPermission("trails.toggle")){
							// toggle their trails and send them the trails toggle message
						}else{
				       		//send no permission message
				       	}
					}else {
						//send must specify player name from console message 
					}									
			    }else if(args.length > 0){
			    	if(sender.hasPermission("trails.other")) {
					    for(String s : args){
						    if(getOfflinePlayerUUID(s)!=null) {
						    	UUID id = getOfflinePlayerUUID(s);
							    //toggle player's messages
							    if(Bukkit.getOfflinePlayer(id).isOnline()) {
							    	//send toggle message to player
							    }
						    }else {
						    	//send no player with that name has played on server before message
						    }
					    }
			        }else {
			        	//send no permission message
			        }
			    }
			}
		}catch (Exception e) {
			//send error message to console
			return false;
		}
		return true;
	}
	
	private UUID getOfflinePlayerUUID(String playerName){
		for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
			if(op.getName().equalsIgnoreCase(playerName)) {
				return op.getUniqueId();
			}
		}
		return null;
	}
}

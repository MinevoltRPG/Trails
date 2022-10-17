package me.ccrama.Trails;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor
{
    private Trails plugin;
	
    public Commands(Trails plugin) {
    	this.plugin = plugin;
    }
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try{
			if (command.getName().equalsIgnoreCase(plugin.getLanguage().command)) {
				if ((args.length == 0) || (args.equals(null)))
				{   
				    if (sender instanceof Player) {
					    Player p = (Player) sender;
						if(p.hasPermission("trails.toggle")){
						    if(this.plugin.getToggles().isDisabled(p)) {
							    this.plugin.getToggles().enablePlayer(p);
								p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().toggledOn)); //send toggled on message to player
							}else {
							    this.plugin.getToggles().disablePlayer(p);
							    p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().toggledOff)); //send toggled off message to player
							}
						}else{
							p.sendMessage(getFormattedMessage(p.getName(), plugin.getLanguage().noPerm));
							return false;
					    }
					}else {
						sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().consoleSpecify));
						return false;
					}									
				}else if(args.length > 0){
				   	if(sender.hasPermission("trails.other") || sender instanceof ConsoleCommandSender) {
					    for(String s : args){
						    if(getOfflinePlayerUUID(s)!=null) {
						    	UUID id = getOfflinePlayerUUID(s);
						    	if(this.plugin.getToggles().isDisabled(id)) {
									this.plugin.getToggles().enablePlayer(id);
									sender.sendMessage(getFormattedMessage(s ,plugin.getLanguage().toggledOnOther));
									if(Bukkit.getOfflinePlayer(id).isOnline()) {
										Bukkit.getOfflinePlayer(id).getPlayer().sendMessage(getFormattedMessage(Bukkit.getOfflinePlayer(id).getPlayer().getName(),plugin.getLanguage().toggledOn));
								    }
								}else {
									this.plugin.getToggles().disablePlayer(id);
									sender.sendMessage(getFormattedMessage(s, plugin.getLanguage().toggledOffOther));
									if(Bukkit.getOfflinePlayer(id).isOnline()) {
										Bukkit.getOfflinePlayer(id).getPlayer().sendMessage(getFormattedMessage(Bukkit.getOfflinePlayer(id).getPlayer().getName(),plugin.getLanguage().toggledOff));
								    }
								}
						    }else {
						    	sender.sendMessage(getFormattedMessage(s, plugin.getLanguage().notPlayedBefore));
						    	return false;
						    }
					    }
				    }else {
				    	sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().noPermOthers));
				    	return false;
				    }
				}
			}		
		}catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(getFormattedMessage("", "%plugin_prefix% has encountered a serious error."));
			Bukkit.getConsoleSender().sendMessage(getFormattedMessage("", "%plugin_prefix% Please report to DrkMatr1984"));
			Bukkit.getConsoleSender().sendMessage(e.getMessage());
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
	
	public String getFormattedMessage(String name, String message) {
		message = message.replace("%plugin_prefix%", plugin.getLanguage().pluginPrefix);
		message = message.replace("%name%", name);			 
		message = message.replace("%command%",plugin.getLanguage().command);
		// Do Colors
	    message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
}

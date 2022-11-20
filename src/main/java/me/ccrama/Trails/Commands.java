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
						String uuid = p.getUniqueId().toString();
						if(p.hasPermission("trails.toggle")){
						    if(this.plugin.getToggles().isDisabled(uuid)) {
							    this.plugin.getToggles().enablePlayer(uuid);
								p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().toggledOn)); //send toggled on message to player
							}else {
							    this.plugin.getToggles().disablePlayer(uuid);
							    p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().toggledOff)); //send toggled off message to player
							}
							return true;
						}else{
							p.sendMessage(getFormattedMessage(p.getName(), plugin.getLanguage().noPerm));
							return false;
					    }
					}else {
						sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().consoleSpecify));
						return false;
					}									
				}else if(args.length == 1){
					if(sender instanceof Player){
						Player p = (Player) sender;
						String uuid = p.getUniqueId().toString();
						if(args[0].equalsIgnoreCase("on")){
							if(p.hasPermission("trails.toggle")){
								if(this.plugin.getToggles().isDisabled(uuid)) {
									this.plugin.getToggles().enablePlayer(uuid);
									p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().toggledOn)); //send toggled on message to player
								}else {
									p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().alreadyOn)); //send toggled off message to player
								}
								return true;
							}else{
								p.sendMessage(getFormattedMessage(p.getName(), plugin.getLanguage().noPerm));
								return false;
							}
						} else if(args[0].equalsIgnoreCase("off")){
							if(p.hasPermission("trails.toggle")){
								if(this.plugin.getToggles().isDisabled(uuid)) {
									p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().alreadyOff)); //send toggled on message to player
								}else {
									this.plugin.getToggles().disablePlayer(uuid);
									p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().toggledOff)); //send toggled off message to player
								}
								return true;
							}else{
								p.sendMessage(getFormattedMessage(p.getName(), plugin.getLanguage().noPerm));
								return false;
							}
						} else if(args[0].equalsIgnoreCase("boost")){
							if(p.hasPermission("trails.toggle-boost")){
								if(this.plugin.getToggles().isBoost(uuid)) {
									this.plugin.getToggles().disableBoost(uuid);
									p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().boostOff)); //send toggled on message to player
								}else {
									this.plugin.getToggles().enableBoost(uuid);
									p.sendMessage(getFormattedMessage(p.getName(),plugin.getLanguage().boostOn)); //send toggled off message to player
								}
								return true;
							}else{
								p.sendMessage(getFormattedMessage(p.getName(), plugin.getLanguage().noPerm));
								return false;
							}
						} else if(args[0].equalsIgnoreCase("reload")){
							if(p.hasPermission("trails.reload")){
								plugin.reloadConfig();
								plugin.onDisable();
								plugin.onEnable();
								plugin.onLoad();
								p.sendMessage("Plugin reloaded");
								return true;
							}
						}
					}
				   	if(sender.hasPermission("trails.other") || sender instanceof ConsoleCommandSender) {
					    String playerName = args[0];
						    if(getOfflinePlayerUUID(playerName)!=null) {
						    	UUID uuid = getOfflinePlayerUUID(playerName);
						    	if(this.plugin.getToggles().isDisabled(uuid.toString())) {
									this.plugin.getToggles().enablePlayer(uuid.toString());
									sender.sendMessage(getFormattedMessage(playerName ,plugin.getLanguage().toggledOnOther));
									if(Bukkit.getOfflinePlayer(uuid).isOnline()) {
										Bukkit.getOfflinePlayer(uuid).getPlayer().sendMessage(getFormattedMessage(Bukkit.getOfflinePlayer(uuid).getPlayer().getName(),plugin.getLanguage().toggledOn));
								    }
								}else {
									this.plugin.getToggles().disablePlayer(uuid.toString());
									sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().toggledOffOther));
									if(Bukkit.getOfflinePlayer(uuid).isOnline()) {
										Bukkit.getOfflinePlayer(uuid).getPlayer().sendMessage(getFormattedMessage(Bukkit.getOfflinePlayer(uuid).getPlayer().getName(),plugin.getLanguage().toggledOff));
								    }
								}
						    }else {
						    	sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().notPlayedBefore));
						    	return false;
						    }
				    }else {
				    	sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().noPermOthers));
				    	return false;
				    }
				} else{
					sender.sendMessage("Too many args");
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

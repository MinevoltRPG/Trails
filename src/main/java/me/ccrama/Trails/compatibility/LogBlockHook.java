package me.ccrama.Trails.compatibility;

import me.ccrama.Trails.Trails;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

public class LogBlockHook
{
	
	private Consumer lbconsumer = null;
	
	public LogBlockHook(Trails plugin) {
		final PluginManager pm = plugin.getServer().getPluginManager();
	    final Plugin logBlock = pm.getPlugin("LogBlock");
	    if (logBlock != null) {
	    	lbconsumer = ((LogBlock) logBlock).getConsumer();
	    	Bukkit.getServer().getConsoleSender().sendMessage(plugin.getCommands().getFormattedMessage(Bukkit.getConsoleSender().getName(),
	    			(plugin.getLanguage().pluginPrefix + ChatColor.GREEN + " hooked into " + ChatColor.YELLOW + "LogBlock!")));
	    }
	    	
	}
	
	public Consumer getLBConsumer() {
		return this.lbconsumer;
	}
}
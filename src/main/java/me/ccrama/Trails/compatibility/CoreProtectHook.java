package me.ccrama.Trails.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import me.ccrama.Trails.Trails;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class CoreProtectHook {
	
	private CoreProtectAPI api;
	
	public CoreProtectHook(Trails plugin) {
		api = getCoreProtectAPI();
		if(api!=null)
			Bukkit.getServer().getConsoleSender().sendMessage(plugin.getCommands().getFormattedMessage(Bukkit.getConsoleSender().getName(),
	    			(plugin.getLanguage().pluginPrefix + ChatColor.GREEN + " hooked into "  + ChatColor.YELLOW + "CoreProtect!")));
	}
	
	private CoreProtectAPI getCoreProtectAPI() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 9) {
            return null;
        }
        return CoreProtect;
	}
	
	public CoreProtectAPI getAPI() {
		return this.api;
	}
}
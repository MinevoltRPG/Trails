package me.ccrama.Trails.configs;

import me.ccrama.Trails.Trails;

public class Config {
	
	private LinksConfig linksData;
	
	public boolean pathsWilderness = true;
	public boolean townyPathsPerm = false;
	public boolean logBlock = true;
	public boolean coreProtect = true;
	public boolean checkBypass = false;
	public int saveInterval = 5;
	public String langType;
	
	public Config(Trails plugin) {
		plugin.saveDefaultConfig();
		this.linksData = new LinksConfig(plugin);
		loadConfig(plugin);
	}
	
	private void loadConfig(Trails plugin) {
		
		// Plugin Integrations
		pathsWilderness = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.Towny.PathsInWilderness"));
		townyPathsPerm = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.Towny.TownyPathsPerm"));
		logBlock = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.LogBlock.LogPathBlocks"));
		logBlock = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.CoreProtect.LogPathBlocks"));
		checkBypass = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.WorldGuard.CheckBypass"));
		langType = plugin.getConfig().getString("General.Language");
		
		saveInterval = plugin.getConfig().getInt("Data-Saving.Interval");
	}
	
	public LinksConfig getLinksConfig() {
        return this.linksData;
    }
}
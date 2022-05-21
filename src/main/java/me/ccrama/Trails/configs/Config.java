package me.ccrama.Trails.configs;

import me.ccrama.Trails.Trails;

public class Config {
	
	private LinksConfig linksData;
	
	public boolean pathsWilderness;
	public boolean townyPathsPerm;
	public boolean logBlock;
	public double saveInterval;
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
		langType = plugin.getConfig().getString("General.Language");
		
		saveInterval = plugin.getConfig().getDouble("Data-Saving.Interval");
	}
	
	public LinksConfig getLinksConfig() {
        return this.linksData;
    }
}
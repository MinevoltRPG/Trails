package me.ccrama.Trails.configs;

import me.ccrama.Trails.Trails;

public class Config {
	
	private LinksConfig linksData;
	
	public boolean townyPathsWilderness = true;
	public boolean townyPathsPerm = false;
	public boolean landsPathsWilderness = true;
	public boolean gpPathsWilderness = true;
	public boolean applyInSubAreas = true;
	public boolean logBlock = true;
	public boolean coreProtect = true;
	public boolean checkBypass = false;
	public boolean sendDenyMessage = true;
	public int messageInterval = 10;
	public int saveInterval = 5;
	public String langType;
	public boolean sneakBypass = true;
	public double runModifier = 1.0;
	
	public Config(Trails plugin) {
		plugin.saveDefaultConfig();
		this.linksData = new LinksConfig(plugin);
		loadConfig(plugin);
	}
	
	private void loadConfig(Trails plugin) {
		
		// Plugin Integrations
		townyPathsWilderness = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.Towny.PathsInWilderness"));
		townyPathsPerm = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.Towny.TownyPathsPerm"));
		landsPathsWilderness = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.Lands.PathsInWilderness"));
		gpPathsWilderness = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.GriefPrevention.PathsInWilderness"));
		applyInSubAreas = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.Lands.ApplyInSubAreas"));
		logBlock = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.LogBlock.LogPathBlocks"));
		logBlock = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.CoreProtect.LogPathBlocks"));
		checkBypass = Boolean.valueOf(plugin.getConfig().getString("Plugin-Integration.WorldGuard.CheckBypass"));
		sendDenyMessage = Boolean.valueOf(plugin.getConfig().getString("Messages.SendDenyMessage"));
		messageInterval = plugin.getConfig().getInt("Messages.Interval");
		langType = plugin.getConfig().getString("General.Language");
		sneakBypass = plugin.getConfig().getBoolean("General.Sneak-Bypass", true);
		runModifier = plugin.getConfig().getDouble("General.Run-Modifier", 1.0);
		
		saveInterval = plugin.getConfig().getInt("Data-Saving.Interval");
	}
	
	public LinksConfig getLinksConfig() {
        return this.linksData;
    }
}
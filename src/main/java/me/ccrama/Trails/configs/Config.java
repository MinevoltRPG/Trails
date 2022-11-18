package me.ccrama.Trails.configs;

import me.ccrama.Trails.Trails;

import java.util.ArrayList;
import java.util.List;

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
	public long speedBoostInterval = 5L;
	public float speedBoostStep = 0.025F;
	public boolean usePermission = false;
	public boolean onlyTrails = true;
	public List<String> enabledWorlds = new ArrayList<>();
	public boolean allWorldsEnabled = false;
	
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
		speedBoostInterval = plugin.getConfig().getLong("General.Speed-Boost-Interval", 5L);
		speedBoostStep = (float)plugin.getConfig().getDouble("General.Speed-Boost-Step", 0.025);
		usePermission = plugin.getConfig().getBoolean("General.Use-Permission-For-Trails", false);
		onlyTrails = plugin.getConfig().getBoolean("General.Speed-Boost-Only-Trails", true);
		enabledWorlds = (ArrayList<String>) plugin.getConfig().getList("General.Enabled-Worlds", new ArrayList<>());
		saveInterval = plugin.getConfig().getInt("Data-Saving.Interval");

		for(String s : enabledWorlds){
			if(s.equalsIgnoreCase("all")){
				allWorldsEnabled=true;
				break;
			}
		}
	}
	
	public LinksConfig getLinksConfig() {
        return this.linksData;
    }
}
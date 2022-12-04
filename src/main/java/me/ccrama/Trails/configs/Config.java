package me.ccrama.Trails.configs;

import me.ccrama.Trails.Trails;
import org.bukkit.Material;

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
	public boolean usePermissionBoost = false;
	public boolean onlyTrails = true;
	public List<String> enabledWorlds = new ArrayList<>();
	public boolean allWorldsEnabled = false;
	public boolean enabledDefault = true;
	public boolean boostEnabledDefault = true;
	public boolean immediatelyRemoveBoost = false;
	public boolean dynmapRender = true;
	public boolean playerPlotIntegration = true;
	public boolean wgIntegration = true;
	public Material trailTool = Material.IRON_SHOVEL;
	public Material infoTool = Material.STICK;
	public boolean trailDecay = true;
	public double chunkChance = 0.2;
	public double decayFraction = 0.03;
	public long decayTimer = 1200L;
	public double decayDistance = 5.0;
	public double stepDecayFraction = 0.1;
	public boolean wgDecayFlag = true;
	public boolean strictLinks = false;
	
	public Config(Trails plugin) {
		plugin.saveDefaultConfig();
		this.linksData = new LinksConfig(plugin);
		loadConfig(plugin);
	}
	
	private void loadConfig(Trails plugin) {
		
		// Plugin Integrations
		townyPathsWilderness = plugin.getConfig().getBoolean("Plugin-Integration.Towny.PathsInWilderness");
		townyPathsPerm = plugin.getConfig().getBoolean("Plugin-Integration.Towny.TownyPathsPerm");
		landsPathsWilderness = plugin.getConfig().getBoolean("Plugin-Integration.Lands.PathsInWilderness");
		gpPathsWilderness = plugin.getConfig().getBoolean("Plugin-Integration.GriefPrevention.PathsInWilderness");
		applyInSubAreas = plugin.getConfig().getBoolean("Plugin-Integration.Lands.ApplyInSubAreas");
		logBlock = plugin.getConfig().getBoolean("Plugin-Integration.LogBlock.LogPathBlocks");
		logBlock = plugin.getConfig().getBoolean("Plugin-Integration.CoreProtect.LogPathBlocks");
		checkBypass = plugin.getConfig().getBoolean("Plugin-Integration.WorldGuard.CheckBypass");
		sendDenyMessage = plugin.getConfig().getBoolean("Messages.SendDenyMessage");
		messageInterval = plugin.getConfig().getInt("Messages.Interval");
		langType = plugin.getConfig().getString("General.Language");
		sneakBypass = plugin.getConfig().getBoolean("General.sneak-bypass", true);
		runModifier = plugin.getConfig().getDouble("General.run-modifier", 1.0);
		speedBoostInterval = plugin.getConfig().getLong("General.speed-boost-interval", 5L);
		speedBoostStep = (float)plugin.getConfig().getDouble("General.speed-boost-step", 0.025);
		usePermission = plugin.getConfig().getBoolean("General.use-permission-for-trails", false);
		usePermissionBoost = plugin.getConfig().getBoolean("General.use-permission-for-boost", false);
		onlyTrails = plugin.getConfig().getBoolean("General.speed-boost-only-trails", true);
		immediatelyRemoveBoost = plugin.getConfig().getBoolean("General.immediately-remove-boost", false);
		enabledWorlds = (ArrayList<String>) plugin.getConfig().getList("General.enabled-worlds", new ArrayList<>());
		saveInterval = plugin.getConfig().getInt("Data-Saving.Interval");
		enabledDefault = plugin.getConfig().getBoolean("General.enabled-by-default", true);
		boostEnabledDefault = plugin.getConfig().getBoolean("General.boost-enabled-by-default", true);
		dynmapRender = plugin.getConfig().getBoolean("Plugin-Integration.Dynmap.trails-trigger-render", true);
		playerPlotIntegration = plugin.getConfig().getBoolean("Plugin-Integration.PlayerPlot.integration-enabled", playerPlotIntegration);
		wgIntegration = plugin.getConfig().getBoolean("Plugin-Integration.WorldGuard.IntegrationEnabled", true);
		trailDecay = plugin.getConfig().getBoolean("General.trail-decay", trailDecay);
		decayFraction = plugin.getConfig().getDouble("General.decay-fraction", decayFraction);
		decayTimer = plugin.getConfig().getLong("General.decay-timer", decayTimer);
		chunkChance = plugin.getConfig().getDouble("General.chunk-chance", chunkChance);
		decayDistance = plugin.getConfig().getDouble("General.decay-distance", decayDistance);
		stepDecayFraction = plugin.getConfig().getDouble("General.step-decay-fraction", stepDecayFraction);
		wgDecayFlag = plugin.getConfig().getBoolean("Plugin-Integration.WorldGuard.decay-flag", wgDecayFlag);
		strictLinks = plugin.getConfig().getBoolean("General.strict-links", strictLinks);


		for(String s : enabledWorlds){
			if(s.equalsIgnoreCase("all")){
				allWorldsEnabled=true;
				break;
			}
		}

		try {
			trailTool = Material.getMaterial(plugin.getConfig().getString("General.trail-tool").toUpperCase());
		} catch (Exception ex){
			ex.printStackTrace();
		}

		try {
			infoTool = Material.getMaterial(plugin.getConfig().getString("General.info-tool").toUpperCase());
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public LinksConfig getLinksConfig() {
        return this.linksData;
    }
}
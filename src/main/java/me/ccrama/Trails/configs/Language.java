package me.ccrama.Trails.configs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.util.ResourceUtils;
import net.md_5.bungee.api.ChatColor;

public class Language
{
	private File languageFile;
	private FileConfiguration language;
	private File languageFolder;
	
	public String command = "trails";
	public String pluginPrefix = "&7[&eTrails&7]&r";
	public String noPerm = "%plugin_prefix% &cYou do not have permission to toggle trails";
	public String noPermOthers = "%plugin_prefix% &cYou do not have permission to toggle other peoples' Trails";
	public String consoleSpecify = "%plugin_prefix% &cYou must specify a &fname &cwhen running from &7Console";
	public String toggledOn = "%plugin_prefix% &fYour &eTrails &fhave been toggle &aOn!";
	public String toggledOff = "%plugin_prefix% &fYour &eTrails &fhave been toggle &cOff!";
	public String boostOn = "%plugin_prefix% &fYour &eBoost &fhave been toggle &aOn!";
	public String boostOff = "%plugin_prefix% &fYour &eBoost &fhave been toggle &cOff!";
	public String toggledOnOther = "%plugin_prefix% %name%'s &eTrails &fhave been toggle &aOn!";
	public String toggledOffOther = "%plugin_prefix% %name%'s &eTrails &fhave been toggle &cOff!";
	public String notPlayedBefore = "%plugin_prefix% %name% has not Played on this server before.";
	public String cantCreateTrails = "%plugin_prefix% You can't create trails here.";
	public String saveMessage = "%plugin_prefix% has successfully saved data!";
	public Material material = Material.DIRT_PATH;
	public String displayName = "&eTrails Flag";
	public String alreadyOff = "%plugin_prefix% &fYour &eTrails &fare already toggled &cOff!";
	public String alreadyOn = "%plugin_prefix% &fYour &eTrails &fare already toggled &aOn!";
	public List<String> description;
	
	private Trails plugin;
	
	
	public Language(Trails plugin){
		this.plugin = plugin;
		initLanguageFile();
	}
	
	public void initLanguageFile(){
		loadLanguageFile(saveDefaultLanguageFile(plugin.getConfigManager().langType + ".yml"));
	}
	
	public File saveDefaultLanguageFile(String languageType) {
		this.languageFolder = new File(this.plugin.getDataFolder().toString() + "/lang");
		if (!this.languageFolder.exists())
		      this.languageFolder.mkdir();
		if (languageFile == null) {
			languageFile = new File(this.languageFolder, languageType);
	    }
		
	    if (!languageFile.exists()) {
	    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &aSaving default language files..."));
	    	List<String> files;
			try {
				files = ResourceUtils.listFiles(plugin.getClass(), "/lang");
				if(files!=null) {
					for(String file : files) {
						plugin.saveResource("lang/" + file, false);
						Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] " + "&aTrails/lang/" + file + " saved"));
			    	}
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    return languageFile;
	}
	
	
	  
	public void loadLanguageFile(File languageFile){	
		if(languageFile.exists() && YamlConfiguration.loadConfiguration(languageFile)!=null) {
			language = YamlConfiguration.loadConfiguration(languageFile);
			command = language.getString("command-name");
			pluginPrefix = language.getString("plugin-prefix");
			noPerm = language.getString("messages.noPerm");
			noPermOthers = language.getString("messages.noPermOthers");
			consoleSpecify = language.getString("messages.consoleSpecify");
			toggledOn = language.getString("messages.toggledOn");
			toggledOff = language.getString("messages.toggledOff");
			toggledOnOther = language.getString("messages.toggledOnOther");
			toggledOffOther = language.getString("messages.toggledOffOther");
			notPlayedBefore = language.getString("messages.notPlayedBefore");
			cantCreateTrails = language.getString("messages.cantCreateTrails");
			saveMessage = language.getString("messages.saveMessage");
			material = Material.matchMaterial(language.getString("lands.flag.icon-material"));
			displayName = language.getString("lands.flag.display-name");
			description = language.getStringList("lands.flag.description");
		}else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cCannot find " + languageFile.getName()));
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cCheck Trails/lang/ folder and make sure that"));
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cyou have a filename in that folder that matches"));
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cLanguage setting in config.yml"));
		}	
	}
}
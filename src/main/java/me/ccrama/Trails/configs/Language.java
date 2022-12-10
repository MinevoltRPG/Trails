package me.ccrama.Trails.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.ccrama.Trails.Trails;
import net.md_5.bungee.api.ChatColor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class Language
{
	public String alreadyMaxLevel = "%plugin_prefix% &fYour &eTrail &fis already max level!";
	private File languageFile;
	private FileConfiguration language;
	private File languageFolder;
	
	public String command = "trails";
	public String pluginPrefix = "&7[&eTrails&7]&r";
	public String noPerm = "%plugin_prefix% &cYou do not have permission to toggle trails";
	public String noPermBoost = "%plugin_prefix% &cYou do not have permission to toggle boost";
	public String noPermOthers = "%plugin_prefix% &cYou do not have permission to toggle other peoples' Trails";
	public String consoleSpecify = "%plugin_prefix% &cYou must specify a &fname &cwhen running from &7Console";
	public String toggledOn = "%plugin_prefix% &fYour &eTrails &fhave been toggle &aOn!";
	public String toggledOff = "%plugin_prefix% &fYour &eTrails &fhave been toggle &cOff!";
	public String boostOn = "%plugin_prefix% &fYour &eBoost &fhave been toggle &aOn!";
	public String boostOff = "%plugin_prefix% &fYour &eBoost &fhave been toggle &cOff!";
	public String toggledOnOther = "%plugin_prefix% %name%'s &eTrails &fhave been toggle &aOn!";
	public String toggledOffOther = "%plugin_prefix% %name%'s &eTrails &fhave been toggle &cOff!";
	public String toggledOnBoostOther = "%plugin_prefix% %name%'s &eBoost &fhave been toggle &aOn!";
	public String toggledOffBoostOther = "%plugin_prefix% %name%'s &eBoost &fhave been toggle &aOff!";
	public String notPlayedBefore = "%plugin_prefix% %name% has not Played on this server before.";
	public String cantCreateTrails = "%plugin_prefix% You can't create trails here.";
	public String saveMessage = "%plugin_prefix% has successfully saved data!";
	public Material material = Material.DIRT_PATH;
	public String displayName = "&eTrails Flag";
	public String alreadyOff = "%plugin_prefix% &fYour &eTrails &fare already toggled &cOff!";
	public String alreadyOn = "%plugin_prefix% &fYour &eTrails &fare already toggled &aOn!";
	public String alreadyOffBoost = "%plugin_prefix% &fYour &eBoost &fis already toggled &cOff!";
	public String alreadyOnBoost = "%plugin_prefix% &fYour &eBoost &fis already toggled &aOn!";
	public String alreadyOnBoostOther = "%plugin_prefix% &f%name%'s &eBoost &fis already toggled &aOn!";
	public String alreadyOffBoostOther = "%plugin_prefix% &f%name%'s &eBoost &fis already toggled &aOff!";
	public String reload = "%plugin_prefix% &6Plugin reloaded!";
	public String reloadNoPerm = "%plugin_prefix% &cYou don't have permissions to do this!";
	public String tooManyArgs = "%plugin_prefix% &cToo many arguments!";
	public String wrongArgs = "%plugin_prefix% &cWrong command arguments!";
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
		this.languageFolder = new File(this.plugin.getDataFolder().toString()+ File.separator + "lang");
		if (!this.languageFolder.exists())
		      this.languageFolder.mkdirs();
		if (languageFile == null) {
			languageFile = new File(this.languageFolder, languageType);
	    }
		
	    if (!languageFile.exists()) {
	    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &aSaving default language files..."));
	    	Reflections reflections = new Reflections("lang", Scanners.Resources);
			Set<String> set = reflections.getResources(".*").stream().map(s -> '/' + s).collect(Collectors.toSet());
			String path = plugin.getDataFolder()+File.separator;
			for(String s : set){
				copy(plugin.getClass().getResourceAsStream(s), (path + s));
			}
	    }
	    return languageFile;
	}

	public void copy(InputStream source, String destination) {
		try {
			File file = new File(destination);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				Files.copy(source, Paths.get(destination));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	  
	public void loadLanguageFile(File languageFile){	
		if(languageFile.exists()) {
			language = YamlConfiguration.loadConfiguration(languageFile);
			command = language.getString("command-name", command);
			pluginPrefix = language.getString("plugin-prefix", pluginPrefix);
			noPerm = language.getString("messages.noPerm", noPerm);
			noPermBoost = language.getString("messages.noPermBoost", noPermBoost);
			noPermOthers = language.getString("messages.noPermOthers", noPermOthers);
			consoleSpecify = language.getString("messages.consoleSpecify", consoleSpecify);
			toggledOn = language.getString("messages.toggledOn", toggledOn);
			toggledOff = language.getString("messages.toggledOff", toggledOff);
			boostOn = language.getString("messages.boostOn", boostOn);
			boostOff = language.getString("messages.boostOff", boostOff);
			toggledOnOther = language.getString("messages.toggledOnOther", toggledOnOther);
			toggledOffOther = language.getString("messages.toggledOffOther", toggledOffOther);
			notPlayedBefore = language.getString("messages.notPlayedBefore", notPlayedBefore);
			cantCreateTrails = language.getString("messages.cantCreateTrails", cantCreateTrails);
			saveMessage = language.getString("messages.saveMessage", saveMessage);
			material = Material.matchMaterial(language.getString("lands.flag.icon-material", "DIRT_PATH").toUpperCase());
			displayName = ChatColor.translateAlternateColorCodes('&',language.getString("lands.flag.display-name", "Create trails"));
			alreadyMaxLevel = language.getString("messages.alreadyMaxLevel", alreadyMaxLevel);
			alreadyOff = language.getString("messages.alreadyOff", alreadyOff);
			alreadyOn = language.getString("messages.alreadyOn", alreadyOn);
			alreadyOffBoost = language.getString("messages.alreadyOffBoost", alreadyOffBoost);
			alreadyOnBoost = language.getString("messages.alreadyOnBoost", alreadyOnBoost);
			reload = language.getString("messages.reload", reload);
			reloadNoPerm = language.getString("messages.reloadNoPerm", reloadNoPerm);
			tooManyArgs = language.getString("messages.tooManyArgs", tooManyArgs);
			wrongArgs = language.getString("messages.wrongArgs", wrongArgs);
			alreadyOffBoostOther = language.getString("messages.alreadyOffBoostOther", alreadyOffBoostOther);
			alreadyOnBoostOther = language.getString("messages.alreadyOnBoostOther", alreadyOnBoostOther);
			toggledOffBoostOther= language.getString("messages.toggledOffBoostOther", toggledOffBoostOther);
			toggledOnBoostOther = language.getString("messages.toggledOnBoostOther", toggledOnBoostOther);
			try {
				description = language.getStringList("lands.flag.description");
				ArrayList<String> temp = new ArrayList<>();
				for (String s : description) {
					temp.add(ChatColor.translateAlternateColorCodes('&', s));
				}
				description = temp;
			} catch (Exception ignored){}
		}else {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cCannot find " + languageFile.getName()));
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cCheck Trails/lang/ folder and make sure that"));
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cyou have a filename in that folder that matches"));
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cLanguage setting in config.yml"));
		}	
	}
}
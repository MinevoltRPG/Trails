package me.ccrama.Trails.configs;

import java.io.File;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.ccrama.Trails.Trails;

public class Language
{
	private File languageFile;
	private FileConfiguration language;
	private File languageFolder;
	
	public String languageType = "en-US";
	public String command = "trails";
	public String pluginPrefix = "&7[&eTrails&7]&r";
	public String noPerm = "%plugin_prefix% &cYou do not have permission to toggle trails";
	public String noPermOthers = "%plugin_prefix% &cYou do not have permission to toggle other peoples' Trails";
	public String consoleSpecify = "%plugin_prefix% &cYou must specify a &fname &cwhen running from &7Console";
	public String toggledOn = "%plugin_prefix% &fYour &eTrails &fhave been toggle &aOn!";
	public String toggledOff = "%plugin_prefix% &fYour &eTrails &fhave been toggle &cOff!";
	public String toggledOnOther = "%plugin_prefix% %name%'s &eTrails &fhave been toggle &aOn!";
	public String toggledOffOther = "%plugin_prefix% %name%'s &eTrails &fhave been toggle &cOff!";
	public String notPlayedBefore = "%plugin_prefix% %name% has not Played on this server before.";
	public String cantCreateTrails = "%plugin_prefix% You can't create trails here.";
	public String saveMessage = "%plugin_prefix% has successfully saved data!";
	public Material material = Material.DIRT_PATH;
	public String displayName = "&eTrails Flag";
	public List<String> description;
	
	private Trails plugin;
	
	
	public Language(Trails plugin){
		this.plugin = plugin;
		languageType = plugin.getConfigManager().langType;
		initLanguageFile();
	}
	
	public void initLanguageFile(){
		saveDefaultLanguageFile();
		loadLanguageFile();
	}
	
	public void saveDefaultLanguageFile() {
		this.languageFolder = new File(this.plugin.getDataFolder().toString() + "/lang");
		if (!this.languageFolder.exists())
		      this.languageFolder.mkdir();
		if (languageFile == null) {
			languageFile = new File(this.languageFolder, languageType + ".yml");
	    }
	    if (!languageFile.exists()) {           
	    	plugin.saveResource("lang/" + languageType + ".yml", false);
	    }   
	}
	  
	public void loadLanguageFile(){
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
	}
}
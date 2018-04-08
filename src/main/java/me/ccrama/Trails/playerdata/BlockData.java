package me.ccrama.Trails.playerdata;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ccrama.Trails.Main;
import me.ccrama.Trails.WrappedLocation;
import me.ccrama.Trails.utils.SerializeLocation;


public class BlockData{
	
	private File dataFile;
	private File dataFolder;
	private FileConfiguration data;
	private Main plugin;
	public HashMap<WrappedLocation, Integer> walkedOver;
	
	public BlockData(Main plugin){		
		this.plugin = plugin;
		dataFolder = new File(this.plugin.getDataFolder().toString()+"/data");
	}
		
	public void initLists(){
		saveDefaultBlockList();
		loadBlockList();
	}
	
    ////////////////////////////////////////////////////////////
	public void saveDefaultBlockList() {
		//pickup toggle data
		if(!(dataFolder.exists())){
			dataFolder.mkdir();
		}
	    if (dataFile == null) {
	        dataFile = new File(dataFolder, "blocks.yml");
	    }
	    if (!dataFile.exists()) {           
	        plugin.saveResource("data/blocks.yml", false);
	    }
    }
	  
	public void loadBlockList(){
		//pickup toggle data
		walkedOver = new HashMap<WrappedLocation, Integer>();
		data = YamlConfiguration.loadConfiguration(dataFile);
		for(String key : data.getKeys(false)){
			ConfigurationSection section = data.getConfigurationSection(key);
			if(section.getString("location")!=null && section.getString("location")!=""){
				if(section.getInt("walks")!=0){
					try {
						walkedOver.put(SerializeLocation.fromBase64(section.getString("location")), section.getInt("walks"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
	}
	  
	public void saveBlockList(){
		//pickup toggle data
		if(walkedOver!=null && !walkedOver.isEmpty())
		{
			int i = 0;
			for(WrappedLocation loc : walkedOver.keySet()){
				data.set(i + ".location", SerializeLocation.toBase64(loc));
				data.set(i + ".walks", walkedOver.get(loc));
				i++;
			}
		}
		if(dataFile.exists())
			dataFile.delete();
		try {
			data.save(dataFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dataFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
package me.ccrama.Trails.data;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.TrailBlock;
import me.ccrama.Trails.objects.WrappedLocation;


public class BlockDataManager{
	
	private File dataFile;
	private File dataFolder;
	private FileConfiguration data;
	private Trails plugin;
	public List<TrailBlock> walkedOver;
	
	public BlockDataManager(Trails plugin){		
		this.plugin = plugin;
		dataFolder = new File(this.plugin.getDataFolder().toString()+"/data");
		initLists();
	}
		
	private void initLists(){
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
		walkedOver = new ArrayList<TrailBlock>();
		data = YamlConfiguration.loadConfiguration(dataFile);
		for(String key : data.getKeys(false)){
			ConfigurationSection section = data.getConfigurationSection(key);
			if(section.getString("location")!=null && section.getString("location")!=""){
				if(section.getInt("walks")!=0){
					try {
						walkedOver.add(new TrailBlock(WrappedLocation.fromBase64(section.getString("location")), section.getInt("walks")));
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
			for(TrailBlock b : walkedOver){
				data.set(i + ".location", b.getWrappedLocation().toBase64());
				data.set(i + ".walks", b.getWalks());
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
	
	public List<TrailBlock> getTrailBlocks(){
		return walkedOver;
	}
	
	public void addTrailBlock(TrailBlock b) {
		walkedOver.add(b);
	}
	
	public void removeTrailBlock(TrailBlock b) {
		walkedOver.remove(b);
	}
}
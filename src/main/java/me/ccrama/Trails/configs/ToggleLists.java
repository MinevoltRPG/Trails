package me.ccrama.Trails.configs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ccrama.Trails.Trails;

public class ToggleLists{
	
	private File usersFile;
	private final File dataFolder;
	private FileConfiguration users;
	
	private List<String> toggledPlayers; 
	
	private final Trails plugin;
	
	public ToggleLists(Trails plugin){		
		this.plugin = plugin;
		dataFolder = new File(this.plugin.getDataFolder() +"/data");
	}
		
	public void initLists(){
		saveDefaultUserList();
		loadUserList();
	}
	
    ////////////////////////////////////////////////////////////
	public void saveDefaultUserList() {
		//pickup toggle users
		if(!(dataFolder.exists())){
			dataFolder.mkdir();
		}
	    if (usersFile == null) {
	        usersFile = new File(dataFolder, "toggles.yml");
	    }
	    if (!usersFile.exists()) {           
	        plugin.saveResource("data/toggles.yml", false);
	    }
    }
	  
	public void loadUserList(){
		//pickup toggle users
		toggledPlayers = new ArrayList<>();
		users = YamlConfiguration.loadConfiguration(usersFile);
		if(users.getStringList("DisabledPlayers")!=null && !users.getStringList("DisabledPlayers").isEmpty())
			toggledPlayers = users.getStringList("DisabledPlayers");
	}
	  
	public void saveUserList(){
		//pickup toggle users
		if(toggledPlayers!=null)
		{
			users.set("DisabledPlayers",toggledPlayers);
		}
		if(usersFile.exists())
			usersFile.delete();
		try {
			users.save(usersFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			usersFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isDisabled(@Nonnull Player p) {
		return isDisabled(p.getUniqueId().toString());
	}
	
	public boolean isDisabled(@Nonnull UUID id) {
		return isDisabled(id.toString());
	}
	
	private boolean isDisabled(@Nonnull String s) {
		return this.toggledPlayers.contains(s);
	}
	
	public void addPlayer(@Nonnull Player p) {
		addPlayer(p.getUniqueId().toString());
	}
	
    public void addPlayer(@Nonnull UUID id) {
		addPlayer(id.toString());
	}
    
    private void addPlayer(@Nonnull String s) {
		this.toggledPlayers.add(s);
	}
    
    public boolean removePlayer(@Nonnull Player p) {
    	return removePlayer(p.getUniqueId().toString());
    }
    
    public boolean removePlayer(@Nonnull UUID id) {
    	return removePlayer(id.toString());
    }
    
    private boolean removePlayer(@Nonnull String s) {
		if(isDisabled(s)) {
			this.toggledPlayers.remove(s);
			return true;
		}
		return false;
	}
	
}

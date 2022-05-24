package me.ccrama.Trails.data;

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
		initLists();
	}
		
	public void initLists(){
		saveDefaultUserList();
		loadUserList();
		//Start save task
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> saveUserList(), 20*60*plugin.getConfigManager().saveInterval, 20*60*plugin.getConfigManager().saveInterval);
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
		if(users.getStringList("EnabledPlayers")!=null && !users.getStringList("EnabledPlayers").isEmpty())
			toggledPlayers = users.getStringList("EnabledPlayers");
	}
	
	public void saveUserList() {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> saveUserListAsync(toggledPlayers, users, usersFile)); 
	}
	  
	private void saveUserListAsync(List<String> toggledPlayers, FileConfiguration users, File usersFile){
		//pickup toggle users
		if(toggledPlayers!=null)
		{
			users.set("EnabledPlayers",toggledPlayers);
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
		if(this.toggledPlayers == null)
			return true;
		return !this.toggledPlayers.contains(s);
	}
	
	public void enablePlayer(@Nonnull Player p) {
		enablePlayer(p.getUniqueId().toString());
	}
	
    public void enablePlayer(@Nonnull UUID id) {
		enablePlayer(id.toString());
	}
    
    private void enablePlayer(@Nonnull String s) {
		this.toggledPlayers.add(s);
	}
    
    public boolean disablePlayer(@Nonnull Player p) {
    	return disablePlayer(p.getUniqueId().toString());
    }
    
    public boolean disablePlayer(@Nonnull UUID id) {
    	return disablePlayer(id.toString());
    }
    
    private boolean disablePlayer(@Nonnull String s) {
		if(!isDisabled(s)) {
			this.toggledPlayers.remove(s);
			return true;
		}
		return false;
	}
	
}

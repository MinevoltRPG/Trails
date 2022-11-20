package me.ccrama.Trails.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nonnull;

import me.ccrama.Trails.listeners.MoveEventListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ccrama.Trails.Trails;

public class ToggleLists{
	
	private File usersFile;
	private final File dataFolder;
	private FileConfiguration users;
	
	private HashMap<String, HashMap<String, Object>> toggledPlayers;
	
	private final Trails plugin;
	
	public ToggleLists(Trails plugin){		
		this.plugin = plugin;
		dataFolder = new File(this.plugin.getDataFolder() + "");
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
	        usersFile = new File(dataFolder, "players.yml");
	    }
	    if (!usersFile.exists()) {
			try {
				usersFile.createNewFile();
			} catch (Exception ex){
				ex.printStackTrace();
			}

	    }
    }
	  
	public void loadUserList(){
		//pickup toggle users
		toggledPlayers = new HashMap<>();
		users = YamlConfiguration.loadConfiguration(usersFile);
		ConfigurationSection section = users.getConfigurationSection("players");
		if(section!=null){
			for(String uuid : section.getKeys(false)){
				HashMap<String, Object> temp = new HashMap<>();
				for(String key : section.getConfigurationSection(uuid).getKeys(false)){
					temp.put(key, section.get(uuid+"."+key));
				}
				toggledPlayers.put(uuid, temp);
			}
		}
	}
	
	public void saveUserList() {
		if(this.plugin.isEnabled())
		    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> saveUserListAsync(toggledPlayers, users, usersFile));
		else
			saveUserListAsync(toggledPlayers, users, usersFile);
	}
	  
	private void saveUserListAsync(HashMap<String, HashMap<String, Object>> toggledPlayers, FileConfiguration users, File usersFile){
		//pickup toggle users
		if(toggledPlayers!=null)
		{
			users.set("players",toggledPlayers);
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
	
	public boolean isDisabled(@Nonnull String uuid) {
		HashMap<String, Object> playerInfo = this.toggledPlayers.get(uuid);
		if(this.toggledPlayers == null || playerInfo == null || playerInfo.get("enable") == null) {
			return !plugin.getConfigManager().enabledDefault;
		}
		return !((boolean) playerInfo.get("enable"));
	}

	public boolean isBoost(@Nonnull String uuid) {
		HashMap<String, Object> playerInfo = this.toggledPlayers.get(uuid);
		if(this.toggledPlayers == null || playerInfo == null || playerInfo.get("boost") == null) {
			return plugin.getConfigManager().boostEnabledDefault;
		}
		return (boolean) playerInfo.get("boost");
	}
    
    public void enablePlayer(@Nonnull String s) {
		if(!this.toggledPlayers.containsKey(s) || this.toggledPlayers.get(s) == null){
			HashMap<String, Object> playerInfo = new HashMap<>();
			playerInfo.put("enable", true);
			this.toggledPlayers.put(s, playerInfo);
		} else {
			this.toggledPlayers.get(s).put("enable", true);
		}
	}
    
    public void disablePlayer(@Nonnull String s) {
		if(!this.toggledPlayers.containsKey(s) || this.toggledPlayers.get(s) == null){
			HashMap<String, Object> playerInfo = new HashMap<>();
			playerInfo.put("enable", false);
			this.toggledPlayers.put(s, playerInfo);
		} else {
			this.toggledPlayers.get(s).put("enable", false);
		}
	}

	public void enableBoost(@Nonnull String s) {
		if(!this.toggledPlayers.containsKey(s) || this.toggledPlayers.get(s) == null){
			HashMap<String, Object> playerInfo = new HashMap<>();
			playerInfo.put("boost", true);
			this.toggledPlayers.put(s, playerInfo);
		} else {
			this.toggledPlayers.get(s).put("boost", true);
		}
	}

	public void disableBoost(@Nonnull String s) {
		MoveEventListener.removeBoostedPlayer(UUID.fromString(s), true);
		if(!this.toggledPlayers.containsKey(s) || this.toggledPlayers.get(s) == null){
			HashMap<String, Object> playerInfo = new HashMap<>();
			playerInfo.put("boost", false);
			this.toggledPlayers.put(s, playerInfo);
		} else {
			this.toggledPlayers.get(s).put("boost", false);
		}
	}
	
}

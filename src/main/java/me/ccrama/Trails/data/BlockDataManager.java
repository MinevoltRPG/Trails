package me.ccrama.Trails.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.TrailBlock;
import me.ccrama.Trails.objects.WrappedLocation;


public class BlockDataManager {

    private File dataFile;
    private final File dataFolder;
    private FileConfiguration data;
    private final Trails plugin;
    public List<TrailBlock> walkedOver;

    public BlockDataManager(Trails plugin) {
        this.plugin = plugin;
        dataFolder = new File(this.plugin.getDataFolder() + "/data");
        initLists();
    }

    private void initLists() {
        saveDefaultBlockList();
        loadBlockList();
        //Start save task
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> saveBlockList(), 20*60*plugin.getConfigManager().saveInterval, 20*60*plugin.getConfigManager().saveInterval);
    }

    ////////////////////////////////////////////////////////////
    public void saveDefaultBlockList() {
        //pickup toggle data
        if (!(dataFolder.exists())) {
            dataFolder.mkdir();
        }
        if (dataFile == null) {
            dataFile = new File(dataFolder, "blocks.yml");
        }
        if (!dataFile.exists()) {
            plugin.saveResource("data/blocks.yml", false);
        }
    }

    public void loadBlockList() {
        //pickup toggle data
        walkedOver = new ArrayList<>();
        data = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : data.getKeys(false)) {
            ConfigurationSection section = data.getConfigurationSection(key);
            if (section.getString("location") != null && !Objects.equals(section.getString("location"), "")) {
                if (section.getInt("walks") != 0) {
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
    
    public void saveBlockList() {
    	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> saveBlockListAsync(walkedOver, data, dataFile));
    	Bukkit.getConsoleSender().sendMessage(plugin.getCommands().getFormattedMessage(Bukkit.getConsoleSender().getName(), plugin.getLanguage().saveMessage));
    }
    
    private void saveBlockListAsync(List<TrailBlock> walkedOver, FileConfiguration data, File dataFile) {
        if (walkedOver != null && !walkedOver.isEmpty()) {
            int i = 0;
            for (TrailBlock b : walkedOver) {
                data.set(i + ".location", b.getWrappedLocation().toBase64());
                data.set(i + ".walks", b.getWalks());
                i++;
            }
        }
        if (dataFile.exists())
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

    public List<TrailBlock> getTrailBlocks() {
        return walkedOver;
    }

    public void addTrailBlock(TrailBlock b) {
        walkedOver.add(b);
    }

    public void removeTrailBlock(TrailBlock b) {
        walkedOver.remove(b);
    }
}
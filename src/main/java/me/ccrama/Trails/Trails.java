package me.ccrama.Trails;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import me.ccrama.Trails.compatibility.towny.TownyHook;
import me.ccrama.Trails.compatibility.worldguard.WorldGuardHook;
import me.ccrama.Trails.configs.LinksConfig;
import me.ccrama.Trails.configs.ToggleLists;
import me.ccrama.Trails.data.BlockDataManager;
import me.ccrama.Trails.listeners.MoveEventListener;
import me.ccrama.Trails.util.Console;
import me.drkmatr1984.customevents.CustomEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Trails Plugin V0.7
 * - Updated to 1.15.2 by j10max
 * - Updated to 1.17.1 by Mr-Deej
 * - Created by ccrama & drkmatr1984
 */
public class Trails extends JavaPlugin {

    private TownyHook townyHook = null;
    private WorldGuardHook wgHook = null;
    private CustomEvents customEvents;
    private BlockDataManager blockData;
    private LinksConfig linksData;
    private ToggleLists toggle;
    public List<UUID> wgPlayers;

    /**
     * On Plugin Enable
     */
    @Override
    public void onEnable() {
        // Activate plugin config
        this.saveDefaultConfig();
        // Wrapper for custom bukkit events
        this.customEvents = new CustomEvents((JavaPlugin) this,
                false,
                false,
                true,
                false,
                false);
        // Initalise events
        this.customEvents.initializeLib();
        this.blockData = new BlockDataManager(this);
        this.linksData = new LinksConfig(this);
        this.toggle = new ToggleLists(this);
        // Towny hook
        if (Bukkit.getServer().getPluginManager().getPlugin("Towny") != null) {
            townyHook = new TownyHook(this);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails]" + ChatColor.GREEN + " hooked into Towny!");
        }
        // Register Move Listener
        Bukkit.getServer().getPluginManager().registerEvents(new MoveEventListener(this), this);
        // Register commands
        Objects.requireNonNull(getCommand("trails")).setExecutor(new Commands(this));
        // Console enabled message
        Console.sendConsoleMessage(String.format("Trails v%s", this.getDescription().getVersion()), "updated to 1.15.2 by j10max", "created by ccrama & drkmatr1984", ChatColor.GREEN + "Thank you");
    }
    
    @Override
    public void onLoad() {
    	// Worldguard Hook
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            wgHook = new WorldGuardHook();
            this.wgPlayers = new ArrayList<UUID>();
            Console.sendConsoleMessage(String.format(ChatColor.YELLOW + "[Trails]" + ChatColor.GREEN + " hooked into worldguard! Flag trails-flag registered. Set trails-flag = DENY to deny trails in regions."));
        }
    }

    /**
     * On plugin disable
     */
    public void onDisable() {
        this.blockData.saveBlockList();
    }

    public BlockDataManager getBlockDataManager() {
        return this.blockData;
    }

    public LinksConfig getConfigManager() {
        return this.linksData;
    }

    public WorldGuardHook getWorldGuardHook() {
        return this.wgHook;
    }

    public TownyHook getTownyHook() {
        return this.townyHook;
    }

    public ToggleLists getToggles() {
    	return this.toggle;
    }

}

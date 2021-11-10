package me.ccrama.Trails;

import java.util.ArrayList;
import java.util.UUID;

import me.ccrama.Trails.compatibility.towny.TownyHook;
import me.ccrama.Trails.compatibility.worldguard.WorldGuardHook;
import me.ccrama.Trails.data.BlockDataManager;
import me.ccrama.Trails.listeners.MoveEventListener;
import me.ccrama.Trails.util.Console;
import me.drkmatr1984.customevents.CustomEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    private ArrayList<String> off = new ArrayList<>();
    private CustomEvents customEvents;
    private BlockDataManager blockData;
    private ConfigHelper linksData;
    private CommandHelper framework;

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
        this.linksData = new ConfigHelper(this);
        this.framework = new CommandHelper(this);
        // Towny hook
        if (Bukkit.getServer().getPluginManager().getPlugin("Towny") != null) {
            townyHook = new TownyHook();
            //this.usingTowny = this.getConfig().getBoolean("HookTowny");
            //if(this.usingTowny) {
            //   System.out.println("Towny present and hooked into Trails.");
            //}
        }
        // Worldguard Hook
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            wgHook = new WorldGuardHook();
        }
        // Register Move Listener
        Bukkit.getServer().getPluginManager().registerEvents(new MoveEventListener(this), this);
        // Register commands
        this.framework.registerCommands(this);
        // Console enabled message
        Console.sendConsoleMessage(String.format("Trails v%s", this.getDescription().getVersion()), "updated to 1.15.2 by j10max", "created by ccrama & drkmatr1984", ChatColor.GREEN + "Thank you");
    }

    /**
     * On plugin disable
     */
    public void onDisable() {
        this.blockData.saveBlockList();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.framework.handleCommand(sender, label, command, args);
    }

    @CommandHelper.Command(
            name = "Trails",
            aliases = {"paths"},
            permission = "trails.off",
            noPerm = "You do not have permission for that command."
    )
    public void testSub(CommandHelper.CommandArgs args) {
        if (args.getArgs().length > 0) {
            if (args.getArgs()[0].equalsIgnoreCase("off")) {
                this.off.add(args.getPlayer().getName());
                args.getPlayer().sendMessage(ChatColor.RED + "[Trails] Your Trails are turned off." + ChatColor.GREEN + " Turn them back on with /Trails on");
            } else if (args.getArgs()[0].equalsIgnoreCase("on")) {
                this.off.remove(args.getPlayer().getName());
                args.getPlayer().sendMessage(ChatColor.GREEN + "[Trails] Your Trails are turned on");
            } else {
                args.getPlayer().sendMessage(ChatColor.RED + "[Trails] Invalid Argument. Do /trails on or /trails off");
            }
        } else {
            args.getPlayer().sendMessage(ChatColor.RED + "[Trails] You also need to type on or off");
        }

    }

    public BlockDataManager getBlockDataManager() {
        return this.blockData;
    }

    public ConfigHelper getConfigManager() {
        return this.linksData;
    }

    public WorldGuardHook getWorldGuardHook() {
        return this.wgHook;
    }

    public TownyHook getTownyHook() {
        return this.townyHook;
    }

    public boolean isToggledOff(UUID player) {
        return this.off.contains(player.toString());
    }

    public boolean isToggledOff(Player player) {
        return isToggledOff(player.getUniqueId());
    }

    public boolean isToggledOff(OfflinePlayer player) {
        return isToggledOff(player.getUniqueId());
    }
}

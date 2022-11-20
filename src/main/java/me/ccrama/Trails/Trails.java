package me.ccrama.Trails;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.ccrama.Trails.compatibility.CoreProtectHook;
import me.ccrama.Trails.compatibility.GriefPreventionHook;
import me.ccrama.Trails.compatibility.LandsAPIHook;
import me.ccrama.Trails.compatibility.LogBlockHook;
import me.ccrama.Trails.compatibility.PAPIHook;
import me.ccrama.Trails.compatibility.TownyHook;
import me.ccrama.Trails.compatibility.WorldGuardHook;
import me.ccrama.Trails.configs.Config;
import me.ccrama.Trails.configs.Language;
import me.ccrama.Trails.data.ToggleLists;
import me.ccrama.Trails.listeners.BreakBlockListener;
import me.ccrama.Trails.listeners.MoveEventListener;
import me.ccrama.Trails.listeners.PlayerInteractListener;
import me.ccrama.Trails.listeners.PlayerLeaveListener;
import me.ccrama.Trails.util.Console;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Trails Plugin V0.7
 * - Updated to 1.15.2 by j10max
 * - Updated to 1.17.1 by Mr-Deej
 * - Created by ccrama & drkmatr1984
 */
public class Trails extends JavaPlugin {

    private TownyHook townyHook = null;
    private LandsAPIHook landsHook = null;
    private GriefPreventionHook gpHook;
    private WorldGuardHook wgHook = null;
    private LogBlockHook lbHook = null;
    private CoreProtectHook cpHook = null;

    private ToggleLists toggle;
    private Config config;
    public List<UUID> messagePlayers;
    private Language language;
    private Commands commands;

	private static CommandMap cmap;   
    private CCommand command;

    private static Plugin plugin;
    private PluginManager pm;
    private MoveEventListener moveEventListener = null;
    private BreakBlockListener breakBlockListener = null;
    private PlayerLeaveListener playerLeaveListener = null;
    private PlayerInteractListener playerInteractListener = null;

    /**
     * On Plugin Enable
     */
    @Override
    public void onEnable() {
        plugin = this;
        // Wrapper for custom bukkit events
        //this.blockData = new BlockDataManager(this);
        this.toggle = new ToggleLists(this);
        // Register Move Listener
        if(moveEventListener == null) {
            moveEventListener = new MoveEventListener(this);
            pm.registerEvents(moveEventListener, this);
        }
        //Register Block Break listener
        if(breakBlockListener == null) {
            breakBlockListener = new BreakBlockListener(this);
            pm.registerEvents(breakBlockListener, this);
        }
        // Register plater quit listener
        if(playerLeaveListener == null) {
            playerLeaveListener = new PlayerLeaveListener(this);
            pm.registerEvents(playerLeaveListener, this);
        }

        if(playerInteractListener == null) {
            playerInteractListener = new PlayerInteractListener(this);
            pm.registerEvents(playerInteractListener, this);
        }
        // Register commands
        this.commands = new Commands(this);
        RegisterCommands();
        // Towny hook
        if (pm.getPlugin("Towny") != null && townyHook == null) {
            townyHook = new TownyHook(this);
        }
        // GriefPrevention Hook
        if (pm.getPlugin("GriefPrevention") != null && gpHook == null) {
            gpHook = new GriefPreventionHook(this);
        }
        // LogBlock Hook
        if(pm.getPlugin("LogBlock") != null && config.logBlock && lbHook == null) {
        	lbHook = new LogBlockHook(this);
        }
        // CoreProtect Hook
        if(pm.getPlugin("CoreProtect") != null && config.coreProtect && cpHook == null) {
        	cpHook = new CoreProtectHook(this);
        }
        // PlaceholderAPI Support
		if (pm.isPluginEnabled("PlaceholderAPI"))
		{
			if(new PAPIHook(this).register()) {
				getLogger().info("Successfully registered %trails_toggled_on%");
			}
		}
        // Console enabled message
        Console.sendConsoleMessage(String.format("Trails v%s", this.getDescription().getVersion()), "updated to 1.15.2 by j10max", "created by ccrama & drkmatr1984", ChatColor.GREEN + "Thank you");
    }
    
    @Override
    public void onLoad() {
        pm = Bukkit.getServer().getPluginManager();
        this.config = new Config(this);
        this.language = new Language(this);
    	// Worldguard Hook
        if (pm.getPlugin("WorldGuard") != null && getConfig().getBoolean("Plugin-Integration.WorldGuard.IntegrationEnabled", true) && wgHook == null) {
            wgHook = new WorldGuardHook(this);
            this.messagePlayers = new ArrayList<UUID>();
            Console.sendConsoleMessage(String.format(ChatColor.GRAY + "[" + ChatColor.YELLOW + "Trails" + ChatColor.GRAY + "]" 
            + ChatColor.GREEN + " hooked into worldguard! Flag trails-flag registered. Set trails-flag = DENY to deny trails in regions."));
        }
        // Lands Hook
        if (pm.getPlugin("Lands") != null && landsHook == null) {
            landsHook = new LandsAPIHook(this);
        }
    }

    /**
     * On plugin disable
     */
    public void onDisable() {
    	unRegisterCommands();
        this.getToggles().saveUserList();

        MoveEventListener.disableBoostTask();
    }
    
    public CommandMap getCommandMap() {
        return cmap;
    }
      
    public class CCommand extends Command {
        private CommandExecutor exe = null;
        
        protected CCommand(String name) {
          super(name);
        }
        
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (this.exe != null)
                this.exe.onCommand(sender, this, commandLabel, args); 
            return false;
        }
        
        public void setExecutor(CommandExecutor exe) {
            this.exe = exe;
        }
        
    }
    
    private void RegisterCommands() {
        String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
        try {
          Class<?> clazz = Class.forName(cbukkit);
          try {
            Field f = clazz.getDeclaredField("commandMap");
            f.setAccessible(true);
            cmap = (CommandMap)f.get(Bukkit.getServer());
            if (!language.command.equals(null)) {
              this.command = new CCommand(language.command);
              if(!cmap.register("paths", this.command)) {
            	  Bukkit.getConsoleSender().sendMessage(this.commands.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &aCommand " + language.command
            			  + " command has already been taken. Defaulting to 'paths' for Trails command.")));
              }else {
            	  Bukkit.getConsoleSender().sendMessage(this.commands.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &aCommand " + language.command + " command Registered!")));
              }
              this.command.setExecutor(this.commands);        
            } 
          } catch (Exception e) {
            e.printStackTrace();
          } 
        } catch (ClassNotFoundException e) {
          Bukkit.getConsoleSender().sendMessage(this.commands.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &ccould not be loaded, is this even Spigot or CraftBukkit?")));
          setEnabled(false);
        } 
      }
      
      private void unRegisterCommands() {
          String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
          try {
              Class<?> clazz = Class.forName(cbukkit);
              try {
                  Field f = clazz.getDeclaredField("commandMap");
                  f.setAccessible(true);
                  cmap = (CommandMap)f.get(Bukkit.getServer());
                  if (!this.command.equals(null)) {
                      this.command.unregister(cmap);
                      Bukkit.getConsoleSender().sendMessage(this.commands.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &aCommand " + language.command + " Unregistered!")));
                  } 
              } catch (Exception e) {
              e.printStackTrace();
              } 
          } catch (ClassNotFoundException e) {
              Bukkit.getConsoleSender().sendMessage(this.commands.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &ccould not be unloaded, is this even Spigot or CraftBukkit?")));
              setEnabled(false);
          } 
    }

    public static Plugin getInstance(){ return plugin; }

    public WorldGuardHook getWorldGuardHook() {
        return this.wgHook;
    }

    public TownyHook getTownyHook() {
        return this.townyHook;
    }

    public ToggleLists getToggles() {
    	return this.toggle;
    }
    
    public Config getConfigManager() {
    	return this.config;
    }
    
    public Language getLanguage() {
    	return language;
    }
    
    public Commands getCommands() {
		return this.commands;
	}

	public LogBlockHook getLbHook() {
		return lbHook;
	}

	public CoreProtectHook getCpHook() {
		return cpHook;
	}

	public LandsAPIHook getLandsHook() {
		return landsHook;
	}

	public GriefPreventionHook getGpHook() {
		return gpHook;
	}

}

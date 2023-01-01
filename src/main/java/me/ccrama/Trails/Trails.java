package me.ccrama.Trails;

import java.lang.reflect.Field;
import java.util.*;

import me.ccrama.Trails.compatibility.*;
import me.ccrama.Trails.configs.Config;
import me.ccrama.Trails.configs.Language;
import me.ccrama.Trails.data.ToggleLists;
import me.ccrama.Trails.listeners.*;
import me.ccrama.Trails.util.Console;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

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
    private DynmapAPI dynmapAPI = null;
    private PlayerPlotHook playerPlotHook = null;

    private ToggleLists toggle;
    private Config config;
    public List<UUID> messagePlayers;
    private Language language;
    private TrailCommand trailCommand;

	private static CommandMap cmap;   
    private CCommand command;

    private static Plugin plugin;
    private PluginManager pm;
    private MoveEventListener moveEventListener = null;
    private BreakBlockListener breakBlockListener = null;
    private PlayerLeaveListener playerLeaveListener = null;
    private PlayerInteractListener playerInteractListener = null;
    private BlockSpreadListener blockSpreadListener = null;
    private DecayTask decayTask;
    public static Map<Player, String> roadMap = new HashMap<>();

    /**
     * On Plugin Enable
     */
    @Override
    public void onEnable() {
        plugin = this;

        DecayTask.setPlugin(this);
        DecayTask.setTrailKey(new NamespacedKey(this, "n"));
        DecayTask.setWalksKey(new NamespacedKey(this, "w"));

        int pluginId = 16930;
        Metrics metrics = new Metrics(this, pluginId);

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

        if(blockSpreadListener == null) {
            blockSpreadListener = new BlockSpreadListener(this);
            pm.registerEvents(blockSpreadListener, this);
        }
        // Register commands
        this.trailCommand = new TrailCommand(this);
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
        if(pm.getPlugin("Dynmap") != null &&dynmapAPI == null){
            dynmapAPI = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("Dynmap");
        }
        // PlaceholderAPI Support
		if (pm.isPluginEnabled("PlaceholderAPI"))
		{
			if(new PAPIHook(this).register()) {
				getLogger().info("Successfully registered %trails_toggled_on%");
			}
		}
        // PlayerPlotAPI
        if(pm.isPluginEnabled("PlayerPlot") && playerPlotHook == null && config.playerPlotIntegration){
            playerPlotHook = new PlayerPlotHook(this);
        }
        // Console enabled message
        Console.sendConsoleMessage(String.format("Trails v%s", this.getDescription().getVersion()), "updated to 1.15.2 by j10max", "created by ccrama & drkmatr1984", ChatColor.GREEN + "Thank you");

        if(config.trailDecay) this.decayTask = new DecayTask(this);
        getCommand("road").setExecutor(new RoadCommand());
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
        if(this.decayTask != null){
            this.decayTask.stopTask();
            this.decayTask = null;
        }
    	unRegisterCommands();
        this.getToggles().saveUserList(false);

        MoveEventListener.disableBoostTask();
    }
    
    public CommandMap getCommandMap() {
        return cmap;
    }
      
    public class CCommand extends Command {
        private CommandExecutor exe = null;
        private TabCompleter tab = null;
        
        protected CCommand(String name) {
          super(name);
        }
        
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (this.exe != null)
                this.exe.onCommand(sender, this, commandLabel, args); 
            return false;
        }

        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
            List<String> tabs = new ArrayList<>();

                if (args.length == 0) {
                    tabs.add("on");
                    tabs.add("off");
                    tabs.add("boost");
                } else if (args.length == 1) {
                    if (sender.hasPermission("trails.other")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().indexOf(args[0]) == 0) tabs.add(p.getName());
                        }
                    }
                    boolean hasPermission = sender.hasPermission("trails.toggle");
                    if (hasPermission && "on".indexOf(args[0]) == 0) tabs.add("on");
                    if (hasPermission && "off".indexOf(args[0]) == 0) tabs.add("off");
                    if (sender.hasPermission("trails.toggle-boost") && "boost".indexOf(args[0]) == 0) tabs.add("boost");
                    if (sender.hasPermission("trails.reload") && "reload".indexOf(args[0]) == 0) tabs.add("reload");
                } else if (args.length == 2) {
                    if (sender.hasPermission("trails.other") && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().indexOf(args[1]) == 0) tabs.add(p.getName());
                        }
                    } else if (sender.hasPermission("trails.toggle-boost.other") && args[0].equalsIgnoreCase("boost")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().indexOf(args[1]) == 0) tabs.add(p.getName());
                        }
                    }
                    if(args[0].equalsIgnoreCase("boost")){
                        boolean hasPermission = sender.hasPermission("trails.toggle-boost");
                        if (hasPermission && "on".indexOf(args[1]) == 0) tabs.add("on");
                        if (hasPermission && "off".indexOf(args[1]) == 0) tabs.add("off");
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("boost") && (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) && sender.hasPermission("trails.toggle-boost.other")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().indexOf(args[2]) == 0) tabs.add(p.getName());
                        }
                    }
                }

            return tabs;
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
            if (language.command != null) {
              this.command = new CCommand(language.command);
              if(!cmap.register("paths", this.command)) {
            	  Bukkit.getConsoleSender().sendMessage(this.trailCommand.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &aCommand " + language.command
            			  + " command has already been taken. Defaulting to 'paths' for Trails command.")));
              }else {
            	  Bukkit.getConsoleSender().sendMessage(this.trailCommand.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &aCommand " + language.command + " command Registered!")));
              }
              this.command.setExecutor(this.trailCommand);
            } 
          } catch (Exception e) {
            e.printStackTrace();
          } 
        } catch (ClassNotFoundException e) {
          Bukkit.getConsoleSender().sendMessage(this.trailCommand.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &ccould not be loaded, is this even Spigot or CraftBukkit?")));
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
                  if (this.command != null) {
                      this.command.unregister(cmap);
                      Bukkit.getConsoleSender().sendMessage(this.trailCommand.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &aCommand " + language.command + " Unregistered!")));
                  } 
              } catch (Exception e) {
              e.printStackTrace();
              } 
          } catch (ClassNotFoundException e) {
              Bukkit.getConsoleSender().sendMessage(this.trailCommand.getFormattedMessage(Bukkit.getConsoleSender().getName(), (language.pluginPrefix + " &ccould not be unloaded, is this even Spigot or CraftBukkit?")));
              setEnabled(false);
          } 
    }

    public void triggerUpdate(Location location){
        try {
            if (dynmapAPI != null && config.dynmapRender)
                dynmapAPI.triggerRenderOfBlock(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        } catch (NullPointerException ex){
            getLogger().warning("World of the location is null.");
            ex.printStackTrace();
        }
        }


    public PlayerPlotHook getPlayerPlotHook() {
        return playerPlotHook;
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
    
    public TrailCommand getCommands() {
		return this.trailCommand;
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

    public MoveEventListener getMoveEventListener() {
        return moveEventListener;
    }
}

package me.ccrama.Trails;

import java.util.ArrayList;
import java.util.UUID;

import me.ccrama.Trails.CommandFramework;
import me.ccrama.Trails.compatibility.towny.TownyHook;
import me.ccrama.Trails.compatibility.worldguard.WorldGuardHook;
import me.ccrama.Trails.data.BlockDataManager;
import me.ccrama.Trails.listeners.MoveEventListener;
import me.drkmatr1984.customevents.CustomEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Trails extends JavaPlugin{
   private TownyHook townyHook = null;
   private WorldGuardHook wgHook = null;
   private ArrayList<String> off = new ArrayList<String>();
   private CustomEvents customEvents;
   private BlockDataManager blockData;
   private ConfigManager linksData;
   private CommandFramework framework;

   public void onEnable() {
      this.saveDefaultConfig();
      this.customEvents = new CustomEvents((JavaPlugin)this, false, false, true, false, false);
  	  this.customEvents.initializeLib();
  	  this.blockData = new BlockDataManager(this);
      this.linksData = new ConfigManager(this);    
      this.framework = new CommandFramework(this);
      if(Bukkit.getServer().getPluginManager().getPlugin("Towny") != null) {
         townyHook = new TownyHook();
    	 //this.usingTowny = this.getConfig().getBoolean("HookTowny");
         //if(this.usingTowny) {
         //   System.out.println("Towny present and hooked into Trails.");
         //}
      }
      if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
    	  wgHook = new WorldGuardHook();
      }
      Bukkit.getServer().getPluginManager().registerEvents(new MoveEventListener(this), this);
      this.framework.registerCommands(this);
      
      System.out.println("Trails v" + this.getDescription().getVersion() + " by ccrama & drkmatr1984 is enabled!");
   }
   
   public void onDisable(){
	   this.blockData.saveBlockList();
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      return this.framework.handleCommand(sender, label, command, args);
   }

   @CommandFramework.Command(
      name = "Trails",
      aliases = {"paths"},
      permission = "Trails.off",
      noPerm = "[Trails] You do not have permisison for that command."
   )
   public void testSub(CommandFramework.CommandArgs args) {
	  if(args.getArgs().length>0){
		  if(args.getArgs()[0].equalsIgnoreCase("off")) {
		         this.off.add(args.getPlayer().getName());
		         args.getPlayer().sendMessage(ChatColor.RED + "[Trails] Your Trails are turned off." + ChatColor.GREEN + " Turn them back on with /Trails on");
		  }else if(args.getArgs()[0].equalsIgnoreCase("on")) {
		         this.off.remove(args.getPlayer().getName());
		         args.getPlayer().sendMessage(ChatColor.GREEN + "[Trails] Your Trails are turned on");
		  }else{
			  args.getPlayer().sendMessage(ChatColor.RED + "[Trails] Invalid Argument. Do /trails on or /trails off");
		  }
	  }else{
    	 args.getPlayer().sendMessage(ChatColor.RED + "[Trails] You also need to type on or off");
      }

   }  
   
   public BlockDataManager getBlockDataManager() {
	   return this.blockData;
   }
   
   public ConfigManager getConfigManager() {
	   return this.linksData;
   }
   
   public WorldGuardHook getWorldGuardHook() {
	   return this.wgHook;
   }
   
   public TownyHook getTownyHook() {
	   return this.townyHook;
   }
   
   public boolean isToggledOff(UUID player) {
	   if(this.off.contains(player.toString())) {
		   return true;
	   }
	   return false;
   }
   
   public boolean isToggledOff(Player player) {
	   return isToggledOff(player.getUniqueId());
   }
   
   public boolean isToggledOff(OfflinePlayer player) {
	   return isToggledOff(player.getUniqueId());
   }
}

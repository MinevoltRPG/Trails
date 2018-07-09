package me.ccrama.Trails;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import me.ccrama.Trails.CommandFramework;
import me.ccrama.Trails.data.BlockDataManager;
import me.ccrama.Trails.data.LinksDataManager;
import me.ccrama.Trails.listeners.MoveEventListener;
import me.ccrama.Trails.worldguard.WorldGuardHook;
import me.drkmatr1984.customevents.CustomEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Trails extends JavaPlugin{
   public boolean usingTowny;
   public WorldGuardHook hook = null;
   public ArrayList<String> off = new ArrayList<String>();
   private BlockDataManager blockData;
   private LinksDataManager linksData;
   private CommandFramework framework;
   public ConcurrentHashMap<?, ?> times = new ConcurrentHashMap<Object, Object>();
   Random r = new Random();

   public void onEnable() {
      this.saveDefaultConfig();
      CustomEvents customEvents = new CustomEvents((JavaPlugin)this, false, false, true, false, false);
  	  customEvents.initializeLib();
  	  this.blockData = new BlockDataManager(this);
      this.linksData = new LinksDataManager(this);
      System.out.println("Trails v" + this.getDescription().getVersion() + " by ccrama & drkmatr1984 is enabled!");
      Bukkit.getServer().getPluginManager().registerEvents(new MoveEventListener(this), this);
      this.framework = new CommandFramework(this);
      if(Bukkit.getServer().getPluginManager().getPlugin("Towny") != null) {
         this.usingTowny = this.getConfig().getBoolean("HookTowny");
         if(this.usingTowny) {
            System.out.println("Towny present and hooked into Trails.");
         }
      }
      if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
    	  hook = new WorldGuardHook(this);
      }
      this.framework.registerCommands(this);
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
   
   public LinksDataManager getLinksDataManager() {
	   return this.linksData;
   }

}

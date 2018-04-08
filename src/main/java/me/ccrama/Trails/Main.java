package me.ccrama.Trails;

import com.palmergames.bukkit.towny.object.TownyUniverse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.ccrama.Trails.CommandFramework;
import me.ccrama.Trails.Link;
import me.ccrama.Trails.playerdata.BlockData;
import me.drkmatr1984.customevents.CustomEvents;
import me.drkmatr1984.customevents.moveEvents.SignificantPlayerMoveEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
   public boolean usingTowny;
   public WorldGuardHook hook = null;
   public ArrayList<String> off = new ArrayList<String>();
   public HashMap<String, Location> lastLoc = new HashMap<String, Location>();
   public BlockData blockData;
   public CommandFramework framework;
   public FileConfiguration config;
   public ConcurrentHashMap<?, ?> times = new ConcurrentHashMap<Object, Object>();
   Random r = new Random();

   public void onEnable() {
      this.saveDefaultConfig();
      this.blockData = new BlockData(this);
      this.blockData.initLists();
      CustomEvents customEvents = new CustomEvents((JavaPlugin)this, false, true, false);
  	  customEvents.initializeLib();
      System.out.println("Trails v" + this.getDescription().getVersion() + " by ccrama & drkmatr1984 is enabled!");
      Bukkit.getServer().getPluginManager().registerEvents(this, this);
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

      this.config = this.getConfig();
      this.createLinks();
      (new BukkitRunnable() {
         public void run() {
            Iterator<?> var2 = Main.this.times.entrySet().iterator();

            while(var2.hasNext()) {
               Entry<?, ?> t = (Entry<?, ?>)var2.next();
               Long currenttime = Long.valueOf(System.currentTimeMillis());
               if(currenttime.longValue() - ((Long)t.getKey()).longValue() >= 30000L) {
                  float chance = Main.this.r.nextFloat();
                  if(chance <= 0.3F) {
                     Main.this.checkBlock((Long)t.getKey());
                  }
               }
            }

         }
      }).runTaskTimer(this, 600L, 600L);
   }

   public void createLinks() {
      Iterator<?> var2 = this.config.getConfigurationSection("Trails").getKeys(false).iterator();

      while(var2.hasNext()) {
         String configs = (String)var2.next();
         String s = this.config.getString("Trails." + configs);
         String[] sarray = s.split(">");
         int numb = sarray.length - 1;
         Link lastlink = null;

         while(true) {
        	boolean legit = true;
        	Material mat;
        	byte dataValue = 0;
            String numbs = sarray[numb];
            String[] sarray2 = numbs.split(":");
            if(sarray2[0].contains(";")){
            	String[] matAndDataValue = sarray2[0].split(";");
            	mat = Material.getMaterial(matAndDataValue[0].toUpperCase());
            	dataValue = (byte) Integer.parseInt(matAndDataValue[1]);
            }else{
            	mat = Material.getMaterial(sarray2[0]);
            }
            for(TypeAndData block : Link.matLinks.keySet()){
            	if(block.mat == mat && block.dataValue == dataValue){
            		System.out.println("[Trails] ERROR(severe): Two Trails have the same material && data values. Please check the config and make sure no two trails have the same data values.");
                    numb = -1;
                    legit = false;
            	}
            }
            if(legit) {
            	int wearTimes = Integer.parseInt(sarray2[1]);
                int chance = Integer.parseInt(sarray2[2]);
                Link link2 = new Link(mat, dataValue, wearTimes - 1, chance, numb, lastlink);
                
                Link.matLinks.put(new TypeAndData(mat, dataValue), link2);
                lastlink = link2;
                System.out.println("[TRAILS] added: Link material = " + mat.name() + ":" + dataValue + " wear = " + wearTimes + " chance = " + chance + "%");
                --numb;
            }
            
            if(numb == -1) {
               break;
            }
         }
      }

   }
   
   public void onDisable(){
	   this.blockData.saveBlockList();
   }

   @SuppressWarnings("deprecation")
   public void checkBlock(Long l) {
      Location loc = (Location)this.times.get(l);
      Block b = loc.getBlock();
      if(b.getData() == 1 && b.getType() == Material.DIRT) {
         this.times.remove(l);
      } else {
         this.times.remove(l);
         b.setType(Material.GRASS);
      }
   }

   @EventHandler
   public void walk(SignificantPlayerMoveEvent e) {
      Player p = e.getPlayer();
      if(this.off.contains(p.getName())) {
    	  return;
      }
      if(this.usingTowny && !TownyUniverse.isWilderness(p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock()))
    	  return;
      if(hook!=null && !hook.canBuild(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D)))	
    	  return;          		
      this.makePath(e.getFrom().subtract(0.0D, 1.0D, 0.0D).getBlock());
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

   @SuppressWarnings("deprecation")
   public void makePath(Block block) {
      Material type = block.getType();
      byte dataValue = block.getData();
      for(TypeAndData typeData : Link.matLinks.keySet()){
      	if(typeData.mat == type && typeData.dataValue == dataValue){
      		Link links = (Link)Link.matLinks.get(typeData);
            double foo = Math.random() * 100.0D;
            if(foo <= (double)links.chanceOccurance()) {
           	 for(WrappedLocation loc : blockData.walkedOver.keySet()){
           		 if(WrappedLocation.compareLocations(loc, block.getLocation())){
           			 int walked = blockData.walkedOver.get(loc);
           			 if(walked >= links.decayNumber()){
           				 blockData.walkedOver.remove(loc);
           				 this.changeNext(block);
           				 return;
           			 }else{    				 
           				 blockData.walkedOver.replace(loc, (walked+1));
           				 return;
           			 }
           		 }
           	 }
           	blockData.walkedOver.put(new WrappedLocation(block.getLocation()), 1);
            }
      	}
      }  
   }

   @SuppressWarnings("deprecation")
   public void changeNext(Block block) {
      Material type = block.getType();
      byte dataValue = block.getData();
      for(TypeAndData typeData : Link.matLinks.keySet()){
        	if(typeData.mat == type && typeData.dataValue == dataValue){
        		if(((Link)Link.matLinks.get(typeData)).getNext() != null) {
        	         Material nextMat = ((Link)Link.matLinks.get(typeData)).getNext().getMat();
        	         byte dValue = ((Link)Link.matLinks.get(typeData)).getNext().getDataValue();
        	         block.setType(nextMat);
        	         block.setData(dValue);
        	         block.getState().update(true);
        	      }
        	}
      }     
   }
   
   public class TypeAndData{	   
	   public Material mat;
	   public byte dataValue;
	   
	   public TypeAndData(Material mat, byte dataValue){
		   this.mat = mat;
		   this.dataValue = dataValue;
	   }
   }
}

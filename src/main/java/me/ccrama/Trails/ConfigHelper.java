package me.ccrama.Trails;

import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.Links;
import me.ccrama.Trails.util.Console;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ConfigHelper {

    private final Trails main;
    private final Links links = new Links();
    private final boolean isPathsInWilderness;
    private final boolean isTownyPathsPerm;

    public ConfigHelper(Trails plugin) {
        this.main = plugin;
        this.createLinks();
        this.isPathsInWilderness = main.getConfig().getBoolean("Plugin-Integration.Towny.PathsInWilderness");
        this.isTownyPathsPerm = main.getConfig().getBoolean("Plugin-Integration.Towny.TownyPathsPerm");
    }

    private void createLinks() {

        for (String configs : this.main.getConfig().getConfigurationSection("Trails").getKeys(false)) {
            String s = this.main.getConfig().getString("Trails." + configs);
            String[] sarray = s.split(">");
            int numb = sarray.length - 1;
            Link lastlink = null;

            do {
                boolean legit = true;            
                String numbs = sarray[numb];
                String[] sarray2 = numbs.split(":");
                Material mat = Material.getMaterial(sarray2[0]);      
                for (Link link : links) {
                    if (link.getMat() == mat) {
                    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.RED +"ERROR: " + "Two Trails have the same materials. Please check the config and make sure no two trails have the same material values.");
                        numb = -1;
                        legit = false;
                    }
                }
                if (legit) {
                    int wearTimes = Integer.parseInt(sarray2[1]);
                    int chance = Integer.parseInt(sarray2[2]);
                    Link link2 = new Link(mat, wearTimes - 1, chance, numb, lastlink);

                    links.add(link2);
                    lastlink = link2;
                    if(mat != null)
                    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GREEN + "added: Link material = " + mat.name() + " wear = " + wearTimes + " chance = " + chance + " percent");
                    else
                    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.RED +"ERROR: " + ChatColor.WHITE + sarray2[0] + ChatColor.RED + " is not a valid Material name. Check "
                    			+ "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html for the proper names.");
                    --numb;
                }
            } while (numb != -1);
        }

    }

    public Links getLinks() {
        return this.links;
    }

    public boolean isPathsInWilderness() {
        return this.isPathsInWilderness;
    }

    public boolean isTownyPathsPerm() {
        return this.isTownyPathsPerm;
    }
}
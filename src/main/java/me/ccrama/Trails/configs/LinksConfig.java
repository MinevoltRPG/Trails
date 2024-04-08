package me.ccrama.Trails.configs;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.Links;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;

public class LinksConfig {

    private final Trails main;
    private final Links links = new Links();

    public LinksConfig(Trails plugin) {
        this.main = plugin;
        this.createLinks();
    }

    private void createLinks() {
        for (String trailName : this.main.getConfig().getConfigurationSection("Trails").getKeys(false)) {
            String linkString = this.main.getConfig().getString("Trails." + trailName);
            String[] linksArray = Arrays.stream(linkString.split(">")).map(String::trim).toArray(String[]::new);
            int linkLength = linksArray.length - 1;
            Link lastLink = null;

            do {
                String numbs = linksArray[linkLength];
                String[] currentLink = Arrays.stream(numbs.split(":")).map(String::trim).toArray(String[]::new);
                Material mat = Material.getMaterial(currentLink[0].toUpperCase());

                int wearTimes = 10;
                int chance = 100;
                float speedBoost = 1.0F;

                try {
                    wearTimes = Integer.parseInt(currentLink[1]);
                } catch (Exception ex){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GOLD +"WARN: " + "Trail with material "+mat.name()+" does not have defined wear times. Defaulting to 10!");
                }

                try{
                    chance = Integer.parseInt(currentLink[2]);
                } catch (Exception ex){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GOLD +"WARN: " + "Trail with material "+mat.name()+" does not have defined chance. Defaulting to 100%!");
                }

                try{
                    speedBoost = Float.parseFloat(currentLink[3]);
                } catch (Exception ex){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GOLD +"WARN: " + "Trail with material "+mat.name()+" does not have defined speed boost. Defaulting to 1.0!");
                }

                Link link2 = new Link(mat, wearTimes - 1, chance, speedBoost, linkLength, lastLink, trailName);
                links.add(link2);
                if(lastLink != null){
                    lastLink.setPrevious(link2);
                }
                lastLink = link2;
                if(mat != null)
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GREEN + "added: Link material = " + mat.name() + " wear = " + wearTimes + " chance = " + chance + " percent");
                else
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.RED +"ERROR: " + ChatColor.WHITE + currentLink[0] + ChatColor.RED + " is not a valid Material name. Check "
                            + "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html for the proper names.");
                --linkLength;
            } while (linkLength != -1);
        }

    }

    public Links getLinks() {
        return this.links;
    }

}
package me.ccrama.Trails.configs;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.Links;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class LinksConfig {

    private Trails main;
    private Links links = new Links();

    public LinksConfig(Trails plugin) {
        this.main = plugin;
        this.createLinks();
    }

    private void createLinks() {

        for (String configs : this.main.getConfig().getConfigurationSection("Trails").getKeys(false)) {
            String linkString = this.main.getConfig().getString("Trails." + configs);
            String[] linksArray = linkString.split(">");
            int numb = linksArray.length - 1;
            Link lastlink = null;

            do {
                String numbs = linksArray[numb];
                String[] sarray2 = numbs.split(":");
                Material mat = Material.getMaterial(sarray2[0]);

                if (links.getFromMat(mat) != null) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.RED +"ERROR: " + "Two Trails have the same materials. Please check the config and make sure no two trails have the same material values.");
                    numb = -1;
                    continue;
                }

                int wearTimes = 10;
                int chance = 100;
                float speedBoost = 1.0F;

                try {
                    wearTimes = Integer.parseInt(sarray2[1]);
                } catch (Exception ex){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GOLD +"WARN: " + "Trail with material "+mat.name()+" does not have defined wear times. Defaulting to 10!");
                }

                try{
                    chance = Integer.parseInt(sarray2[2]);
                } catch (Exception ex){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GOLD +"WARN: " + "Trail with material "+mat.name()+" does not have defined chance. Defaulting to 100%!");
                }

                try{
                    speedBoost = Float.parseFloat(sarray2[3]);
                } catch (Exception ex){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GOLD +"WARN: " + "Trail with material "+mat.name()+" does not have defined speed boost. Defaulting to 1.0!");
                }

                Link link2 = new Link(mat, wearTimes - 1, chance, speedBoost, numb, lastlink);
                links.add(link2);
                lastlink = link2;
                if(mat != null)
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.GREEN + "added: Link material = " + mat.name() + " wear = " + wearTimes + " chance = " + chance + " percent");
                else
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[Trails] " + ChatColor.RED +"ERROR: " + ChatColor.WHITE + sarray2[0] + ChatColor.RED + " is not a valid Material name. Check "
                            + "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html for the proper names.");
                --numb;
            } while (numb != -1);
        }

    }

    public Links getLinks() {
        return this.links;
    }

}
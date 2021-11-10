package me.ccrama.Trails;

import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.Links;
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
                Material mat;
                byte dataValue = 0;
                String numbs = sarray[numb];
                String[] sarray2 = numbs.split(":");
                if (sarray2[0].contains(";")) {
                    String[] matAndDataValue = sarray2[0].split(";");
                    mat = Material.getMaterial(matAndDataValue[0].toUpperCase());
                    dataValue = (byte) Integer.parseInt(matAndDataValue[1]);
                } else {
                    mat = Material.getMaterial(sarray2[0]);
                }
                for (Link link : links) {
                    if (link.getMat() == mat && link.getDataValue() == dataValue) {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Trails] ERROR: " + "Two Trails have the same material && data values. Please check the config and make sure no two trails have the same data values.");
                        numb = -1;
                        legit = false;
                    }
                }
                if (legit) {
                    int wearTimes = Integer.parseInt(sarray2[1]);
                    int chance = Integer.parseInt(sarray2[2]);
                    Link link2 = new Link(mat, dataValue, wearTimes - 1, chance, numb, lastlink);

                    links.add(link2);
                    lastlink = link2;
                    System.out.println(ChatColor.GREEN + "[TRAILS] added: Link material = " + mat.name() + ":" + dataValue + " wear = " + wearTimes + " chance = " + chance + "%");
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
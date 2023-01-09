package me.ccrama.Trails.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.antlr.v4.codegen.model.SrcOp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.ccrama.Trails.Trails;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class Language {
    public String alreadyMaxLevel = "%plugin_prefix% &fYour &eTrail &fis already max level!";
    private static File languageFile;
    private static FileConfiguration languageConfig;
    public static String command = "trails";
    public static String pluginPrefix = "&7[&eTrails&7]&r";
    public Material material = Material.DIRT_PATH;
    public String displayName = "&eTrails Flag";
    public List<String> description;
    private final Trails plugin;


    public Language(Trails plugin) {
        this.plugin = plugin;
        initLanguageFile();
    }

    public void initLanguageFile() {
        loadLanguageFile(saveDefaultLanguageFile(Trails.config.langType));
    }

    public File saveDefaultLanguageFile(String languageType) {
        languageFile = new File(this.plugin.getDataFolder().toString() + File.separator + "lang" + File.separator + languageType + ".yml");
        if (!languageFile.getParentFile().exists()) languageFile.getParentFile().mkdirs();

        if (!languageFile.exists()) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &aSaving default language files..."));
            Reflections reflections = new Reflections("lang", Scanners.Resources);
            Set<String> set = reflections.getResources(".*").stream().map(s -> '/' + s).collect(Collectors.toSet());
            String path = plugin.getDataFolder() + File.separator;
            for (String s : set) {
                copy(plugin.getClass().getResourceAsStream(s), (path + s));
            }
        }
        return languageFile;
    }

    public void copy(InputStream source, String destination) {
        try {
            File file = new File(destination);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                Files.copy(source, Paths.get(destination));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static List<String> getStringList(String path, Map<String, String> map, Player p) {
        try {
            List<String> strings = languageConfig.getStringList(path);
            String str = languageConfig.getString(path, null);
            if (strings.size() == 0 && str == null) {
                languageConfig.createSection(path);
                languageConfig.set(path, "no value: " + path + ". " + phString(map));
                languageConfig.save(languageFile);
                return new ArrayList<>();
            }
            if (str != null && strings.size() == 0) strings.add(str);
            List<String> newStrings = new ArrayList<>();
            for (String s : strings) {
                if (map != null) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        s = s.replace(entry.getKey(), entry.getValue());
                    }
                }
                if (Trails.getPapiHook() != null) s = Trails.getPapiHook().parse(s, p);
                if(p!=null)newStrings.add(translate(s, p.getName()));
                else newStrings.add(translate(s, "no_player"));
            }
            return newStrings;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    public static String getString(String path, Map<String, String> replace, Player p) {
        try {
            String s = languageConfig.getString(path);
            if (s == null) {
                languageConfig.createSection(path);
                languageConfig.set(path, "no value: " + path + ". " + phString(replace));
                languageConfig.save(languageFile);
                return path;
            }
            if (replace != null) {
                for (Map.Entry<String, String> entry : replace.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                }
            }
            if (Trails.getPapiHook() != null) s = Trails.getPapiHook().parse(s, p);
            if(p != null)return translate(s, p.getName());
            else return translate(s, "no_player");
        } catch (Exception ex) {
            ex.printStackTrace();
            return path;
        }
    }

    public static String getString(String path, String playerName) {
        try {
            String s = languageConfig.getString(path);
            if (s == null) {
                languageConfig.createSection(path);
                languageConfig.set(path, "no value: " + path);
                languageConfig.save(languageFile);
                return path;
            }
            return translate(s, playerName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return path;
        }
    }

    private static String phString(Map<String, String> map) {
        if (map == null || map.size() == 0) return "";
        StringBuilder s = new StringBuilder("(Placeholders: ");
        for (String tmp : map.keySet()) {
            s.append(tmp).append(", ");
        }
        s = new StringBuilder(s.substring(0, s.length() - 3));
        s.append(")");
        return s.toString();
    }

    public static String translate(String message, String playerName) {
        message = message.replace("%plugin_prefix%", pluginPrefix);
        if (playerName != null) message = message.replace("%name%", playerName);
        message = message.replace("%command%", command);
        // Do Colors
        message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }


    public void loadLanguageFile(File languageFile) {
        if (languageFile.exists()) {
            languageConfig = YamlConfiguration.loadConfiguration(languageFile);
            command = languageConfig.getString("command-name", command);
            pluginPrefix = languageConfig.getString("plugin-prefix", pluginPrefix);
            material = Material.matchMaterial(languageConfig.getString("lands.flag.icon-material", "DIRT_PATH").toUpperCase());
            displayName = ChatColor.translateAlternateColorCodes('&', languageConfig.getString("lands.flag.display-name", "Create trails"));
            try {
                description = languageConfig.getStringList("lands.flag.description");
                ArrayList<String> temp = new ArrayList<>();
                for (String s : description) {
                    temp.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                description = temp;
            } catch (Exception ignored) {
            }
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cCannot find " + languageFile.getName()));
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cCheck Trails/lang/ folder and make sure that"));
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cyou have a filename in that folder that matches"));
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Trails] &cLanguage setting in config.yml"));
        }
    }
}
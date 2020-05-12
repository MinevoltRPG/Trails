package me.ccrama.Trails.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

public class Console {

    public static void sendConsoleMessage(String... message) {
        ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
        int length = 0;
        // Find longest string
        for (String s : message) if (s.length() > length) length = s.length();
        List<String> strings = new ArrayList<String>();
        // Formulate header
        StringBuilder plus = new StringBuilder("++++");
        for (int i = 0; i < length; i++) plus.append("+");
        strings.add("==" + ChatColor.DARK_AQUA + plus.toString() + ChatColor.RESET + "==");
        // Messages
        for (String msg : message) strings.add("++ " + ChatColor.translateAlternateColorCodes('&', msg));
        strings.add("==" + ChatColor.DARK_AQUA + plus.toString() + ChatColor.RESET + "==");
        // Send message to console
        sender.sendMessage(strings.toArray(new String[0]));
    }

}
